package com.brindysoft.mud.necronomicon;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.db4o.api.Db4oService;
import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.core.mpi.AbstractMudPlace;
import com.brindysoft.mud.core.mpi.MudPlace;
import com.brindysoft.mud.core.mpi.MudUser;
import com.brindysoft.mud.core.mpi.MudWorld;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

@Component
public class World implements MudWorld {

	private Logger logger;

	private ObjectContainer db;

	private Db4oService service;

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
			place = db.query(MudPlace.class).get(0);
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

		// create places
		AbstractMudPlace dunwichTurnoff = new SimplePlace();
		dunwichTurnoff.setDescription("This is the Dunwich Turnoff.  "
				+ "The main road to Arkham and Aylesbury runs east to west.  " + "The road to Dunwich heads north.");
		db.store(dunwichTurnoff);

		AbstractMudPlace busstop = new SimplePlace();
		busstop.setDescription("You are next to to the bus stop at Dean's Corners.");
		db.store(busstop);

		// connect places
		busstop.connect(dunwichTurnoff, "east", "west");
		db.store(busstop);
		db.store(dunwichTurnoff);
		db.commit();

		assert (dunwichTurnoff.getExits().size() == 1);
		assert (busstop.getExits().size() == 1);
		assert (db.query(MudPlace.class).size() == 2);

		logger.debug("%s#createEmptyWorld() - OUT", getClass().getSimpleName());
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
