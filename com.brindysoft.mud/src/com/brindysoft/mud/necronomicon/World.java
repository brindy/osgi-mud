package com.brindysoft.mud.necronomicon;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.db4o.api.Db4oService;
import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudWorld;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

@Component
public class World implements MudWorld {

	private static final String STARTING_PLACE = "starting-place";

	private Logger logger;

	private ObjectContainer db;

	private Db4oService service;

	private MudPlace startingPlace;

	@Reference
	public void setDb4oService(Db4oService service) {
		this.service = service;
	}

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Activate
	public void start() {
		logger.debug("%s#start() - IN", getClass().getSimpleName());
		db = service.getDatabase("necronomicon");

		ObjectSet<MudPlace> query = db.query(MudPlace.class);
		if (query.isEmpty()) {
			createEmptyWorld();
		}

		logger.debug("%s#start() - OUT", getClass().getSimpleName());
	}

	@Deactivate
	public void stop() {
		logger.debug("%s#stop() - IN", getClass().getSimpleName());
		db = null;
		logger.debug("%s#stop() - OUT", getClass().getSimpleName());
	}

	@Override
	public void addUser(MudUser user) {

		MudPlace place = findPlaceContaining(user);
		if (null == place) {
			place = findStartingPlace();
		}

		place.broadcast("{text:green}%s{text} appears!", user.getName());
		place.addUser(user);

		db.commit();
	}

	@Override
	public MudPlace findPlaceContaining(Object object) {
		ObjectSet<MudPlace> query = db.query(new FindPlaceContainingObjectPredicate(object));
		MudPlace place = query.isEmpty() ? null : query.get(0);
		return place;
	}

	@Override
	public boolean move(MudUser user, String direction) {
		MudPlace place = findPlaceContaining(user);
		MudPlace otherPlace = place.placeInDirection(direction);
		if (null != otherPlace) {
			place.removeUser(user);
			place.broadcast("%s heads %s", user.getName(), direction);

			otherPlace.broadcast("%s arrives from the %s", user.getName(), place.getOppositeExit(direction));
			otherPlace.addUser(user);

			db.commit();
			return true;
		}
		return false;
	}

	private void createEmptyWorld() {
		logger.debug("%s#createEmptyWorld() - IN", getClass().getSimpleName());

		// create key places
		SimplePlace busstop = new SimplePlace();
		busstop.setTag(STARTING_PLACE);
		busstop.setDescription("This is the bus stop at Dean's Corners.  "
				+ "The main road to Aylesbury (5 miles) and Arkham (45 miles) runs east to west respectively.  ");

		SimplePlace dunwichTurnoff = new SimplePlace();
		dunwichTurnoff.setTag("turnoff");
		dunwichTurnoff.setDescription("This is the Dunwich Turnoff.  "
				+ "The main road to Aylesbury (5 miles) and Arkham (45 miles) runs east to west respectively.  "
				+ "The road to Dunwich, little more than an improved dirt road, heads north.");

		SimplePlace aBridge = new SimplePlace();
		aBridge.setTag("dunwich-eastcreek-bridge");
		aBridge.setDescription("This is covered bridge made mainly of wooden planks.  "
				+ "Dunwich Road continues to the north and south.  " + "A river flows east to west below the bridge.");

		SimplePlace cornerOfEastCreekAndDunwich = new SimplePlace();
		cornerOfEastCreekAndDunwich.setTag("eastcreek-dunwich-corner");
		cornerOfEastCreekAndDunwich
				.setDescription("Dunwich Road turns sharply from the east to the north.  "
						+ "East Creek Road leads off to the west.");

		// connect places
		connect(busstop, dunwichTurnoff, "The main road to Aylesbury (5 miles) and Arkham (45 miles), "
				+ "running east to west respectively.", "ee");
		
		// TODO make this longer.
		connect(dunwichTurnoff, aBridge, "Dunwich Road between the turnoff and the bridge.", "nwn");
		
		// TODO make this longer
		connect(aBridge, cornerOfEastCreekAndDunwich, "Dunwich Road between the bridge and the East Creek junction.",
				"wnw");

		db.commit();

		logger.debug("%s#createEmptyWorld() - OUT", getClass().getSimpleName());
	}

	private void connect(SimplePlace start, SimplePlace end, String description, String directions) {
		char direction = 0;
		for (int i = 0; i < directions.length() - 1; i++) {
			direction = directions.charAt(i);
			SimplePlace next = new SimplePlace();
			next.setDescription(description);
			connect(start, next, direction);
			start = next;
		}
		direction = directions.charAt(directions.length() - 1);
		connect(start, end, direction);
	}

	private void connect(SimplePlace start, SimplePlace next, char direction) {
		switch (direction) {
		case 'n':
			start.connect(next, "north", "south");
			break;

		case 'e':
			start.connect(next, "east", "west");
			break;

		case 's':
			start.connect(next, "south", "north");
			break;

		case 'w':
			start.connect(next, "west", "east");
			break;
		}
		db.store(next);
		db.store(start);
	}

	private MudPlace findStartingPlace() {
		if (null == startingPlace) {
			SimplePlace example = new SimplePlace();
			example.setTag(STARTING_PLACE);
			startingPlace = (MudPlace) db.queryByExample(example).get(0);
		}
		return startingPlace;
	}

	static class FindPlaceContainingObjectPredicate extends Predicate<MudPlace> {

		private static final long serialVersionUID = 1L;

		private final Object object;

		public FindPlaceContainingObjectPredicate(Object object) {
			this.object = object;
		}

		@Override
		public boolean match(MudPlace place) {
			return place.contains(object);
		}

	}

}
