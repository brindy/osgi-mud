package com.brindysoft.necronomud;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudPlaceProvider;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudWorld;
import com.brindysoft.oodb.api.Database;
import com.brindysoft.oodb.api.DatabaseService;
import com.brindysoft.oodb.api.QueryPredicate;
import com.brindysoft.oodb.api.QueryResult;

@Component
public class World implements MudWorld {

	public static final String PLACES_DB = "mem://necro-places.db4o";

	private Logger logger;

	private Database db;

	private DatabaseService service;

	private Set<MudPlaceProvider> providers = new HashSet<MudPlaceProvider>();

	private MudPlace startingPlace;

	@Reference(optional = false, multiple = true, target = "(world=necro)")
	public void addPlaceProvider(MudPlaceProvider provider) {
		providers.add(provider);
	}

	public void removePlaceProvider(MudPlaceProvider provider) {
		providers.remove(provider);
	}

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

		// put all places in to memory
		Map<String, MudPlace> tags = new HashMap<String, MudPlace>();
		for (MudPlaceProvider provider : providers) {
			for (MudPlace place : provider.getPlaces()) {

				if (null != tags.put(place.getTag(), place)) {
					logger.error("MudPlace with tag %s already exists", place.getTag());
					throw new RuntimeException("MudPlace with tag " + place.getTag() + " already exists");
				}

				if ("0001".equals(place.getTag())) {
					startingPlace = place;
				}

				db.store(place);
			}
		}

		if (null == startingPlace) {
			logger.error("%s#start() No place with tag '0001'", getClass().getSimpleName());
			throw new RuntimeException("No place with tag '0001'");
		}

		// make connections
		for (MudPlaceProvider provider : providers) {
			for (MudPlaceProvider.Connection connection : provider.getConnections()) {
				Place destination = (Place) tags.get(connection.connectedTo);
				destination.connect((Place) connection.place, connection.fromDirection, connection.toDirection);
			}
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
			place = startingPlace;
		}

		place.broadcastByUser(user, "{text:green}%s{text} appears!", user.getName());
		place.addUser(user);

		db.commit();
	}

	@Override
	public MudPlace findPlaceContaining(MudUser user) {
		QueryResult<MudPlace> query = db.query(new FindPlaceContainingUserPredicate((User) user));
		MudPlace place = query.isEmpty() ? null : query.get(0);
		return place;
	}

	@Override
	public void save(Object... objects) {
		logger.debug("%s#save(%s)", getClass().getName(), Arrays.asList(objects));
		for (Object o : objects) {
			db.store(o);
		}
		db.commit();
	}

	static class FindPlaceContainingUserPredicate implements QueryPredicate<MudPlace> {

		private final User user;

		public FindPlaceContainingUserPredicate(User user) {
			this.user = user;
		}

		@Override
		public boolean match(MudPlace place) {
			return place.containsUser(user) || place.getTag().equals(user.getPlaceTag());
		}

	}

	public MudPlace findPlaceByTag(String tag) {
		QueryResult<Place> result = db.queryByExample(new Place(tag, null));
		return result.size() > 0 ? result.get(0) : null;
	}

}
