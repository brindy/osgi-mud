package com.brindysoft.mud.necronomud;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudWorld;
import com.brindysoft.oodb.api.Database;
import com.brindysoft.oodb.api.DatabaseService;
import com.brindysoft.oodb.api.QueryPredicate;
import com.brindysoft.oodb.api.QueryResult;

@Component
public class World implements MudWorld {

	private Logger logger;

	private Database db;

	private DatabaseService service;

	private MudPlace startingPlace;

	@Reference
	public void setDatabaseService(DatabaseService service) {
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

		QueryResult<MudPlace> query = db.query(MudPlace.class);
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
	public MudPlace findPlaceContaining(MudUser user) {
		QueryResult<MudPlace> query = db.query(new FindPlaceContainingUserPredicate(user));
		MudPlace place = query.isEmpty() ? null : query.get(0);
		return place;
	}

	@Override
	public void save(Object... objects) {
		for (Object o : objects) {
			db.store(o);
		}
		db.commit();
	}

	private void createEmptyWorld() {
		logger.debug("%s#createEmptyWorld() - IN", getClass().getSimpleName());

		try {
			WorldBuilder.createWorld(db);
		} catch (Exception e) {
			logger.error(e, "Failed to create world.");
		}

		logger.debug("%s#createEmptyWorld() - OUT", getClass().getSimpleName());
	}

	private MudPlace findStartingPlace() {
		if (null == startingPlace) {
			SimplePlace example = new SimplePlace();
			example.setTag(WorldBuilder.STARTING_PLACE);
			startingPlace = (MudPlace) db.queryByExample(example).get(0);
		}
		return startingPlace;
	}

	static class FindPlaceContainingUserPredicate implements QueryPredicate<MudPlace> {

		private final MudUser user;

		public FindPlaceContainingUserPredicate(MudUser user) {
			this.user = user;
		}

		@Override
		public boolean match(MudPlace place) {
			return place.contains(user);
		}

	}

}
