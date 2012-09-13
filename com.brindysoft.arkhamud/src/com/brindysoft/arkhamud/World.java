package com.brindysoft.arkhamud;

import java.util.Arrays;

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
import com.brindysoft.oodb.api.QueryResult;

@Component
public class World implements MudWorld {

	public static final String PLACES_DB = "mem://arkham-places.db4o";

	private Logger logger;

	private Database db;

	private DatabaseService service;

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
		db = service.getDatabase(PLACES_DB);
		
		// TODO create the places

		logger.debug("%s#start() - OUT", getClass().getSimpleName());
	}

	@Deactivate
	public void stop() {
		logger.debug("%s#stop() - IN", getClass().getSimpleName());
		db = null;
		logger.debug("%s#stop() - OUT", getClass().getSimpleName());
	}

	@Override
	public void connectUser(MudUser user) {

		MudPlace place = findPlaceContaining(user);
		if (null == place) {

			// random place

		}

		place.broadcastByUser(user, "{text:green}%s{text} appears!", user.getName());
		place.addUser(user);

		db.commit();
	}

	@Override
	public MudPlace findPlaceContaining(MudUser user) {
		return findPlaceByTag(((ArkhamCharacter) user).getPlaceTag());
	}

	@Override
	public MudPlace findPlaceByTag(String tag) {
		QueryResult<Place> result = db.queryByExample(new Place(null, tag, null));
		return result.size() > 0 ? result.get(0) : null;
	}

	@Override
	public void save(Object... objects) {
		logger.debug("%s#save(%s)", getClass().getName(), Arrays.asList(objects));
		for (Object o : objects) {
			db.store(o);
		}
		db.commit();
	}

	@Override
	public void disconnectUser(MudUser mudUser) {
		ArkhamCharacter user = (ArkhamCharacter) mudUser;
		Place place = (Place) findPlaceByTag(user.getPlaceTag());
		if (null != place) {
			place.disconnectUser(user);
		}
	}

}