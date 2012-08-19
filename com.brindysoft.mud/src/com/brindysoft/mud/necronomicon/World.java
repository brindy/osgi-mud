package com.brindysoft.mud.necronomicon;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.db4o.api.Db4oService;
import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.core.api.MudPlace;
import com.brindysoft.mud.core.api.MudUser;
import com.brindysoft.mud.core.api.MudWorld;
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

		place.addUser(user);
		db.store(place);
		db.commit();
	}

	@Override
	public MudPlace findPlaceContaining(Object object) {
		ObjectSet<MudPlace> query = db.query(new FindPlaceContainingObjectPredicate(object));
		return query.isEmpty() ? null : query.get(0);
	}

	private void createEmptyWorld() {
		logger.debug("%s#createEmptyWorld() - IN", getClass().getSimpleName());

		GenericPlace first = new GenericPlace();
		first.setDescription("Welcome to Dunwich.  The Miskatonic river flows under the nearby "
				+ "bridge leading out of the village.");
		db.store(first);
		db.commit();

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
