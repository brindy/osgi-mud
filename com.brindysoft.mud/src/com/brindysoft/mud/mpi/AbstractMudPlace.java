package com.brindysoft.mud.mpi;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMudPlace implements MudPlace {

	protected transient Set<MudPlace.Listener> listeners = new HashSet<MudPlace.Listener>();

	protected transient Set<MudObject> objects;

	protected Set<String> userNames;

	protected Set<MudUser> users;

	protected Map<String, MudPlace> connections;

	protected Map<String, String> opposites;

	protected String tag;

	protected String description;

	@Override
	public String getDescription(MudUser user) {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public MudPlace placeInDirection(String direction) {
		return null == connections ? null : connections.get(direction);
	}

	@Override
	public synchronized void broadcast(String message, Object... args) {
		for (MudUser user : getUsers()) {
			if (user.isAttached()) {
				user.println(message, args);
			}
		}
	}

	@Override
	public synchronized void broadcastByUser(MudUser user, String message, Object... args) {
		Set<MudUser> users = getUsers();
		users.removeAll(Arrays.asList(user));
		for (MudUser recipient : users) {
			if (recipient.isAttached()) {
				recipient.println(message, args);
			}
		}
	}

	@Override
	public Set<String> getExits() {
		return null == connections ? Collections.<String> emptySet() : new HashSet<String>(connections.keySet());
	}

	@Override
	public MudPlace getExit(String direction) {
		return getConnections().get(direction);
	}

	@Override
	public String getOppositeExit(String direction) {
		return getOpposites().get(direction);
	}

	@Override
	public synchronized void userArrives(MudUser user, String direction) {

		synchronized (listeners) {
			for (Listener listener : listeners) {
				listener.onUserArrives(this, user, direction, opposites.get(direction));
			}
		}

		broadcast("{text:green}%s{text} arrives from the %s.", user.getName(), opposites.get(direction));
		addUser(user);
	}

	@Override
	public synchronized void userLeaves(MudUser user, String direction) {
		removeUser(user);
		broadcast("{text:green}%s{text} heads %s.", user.getName(), direction);

		synchronized (listeners) {
			for (Listener listener : listeners) {
				listener.onUserLeaves(this, user, direction);
			}
		}
	}

	public void connect(AbstractMudPlace otherPlace, String inDirection, String fromDirection) {
		getConnections().put(inDirection, otherPlace);
		getOpposites().put(fromDirection, inDirection);

		otherPlace.getConnections().put(fromDirection, this);
		otherPlace.getOpposites().put(inDirection, fromDirection);
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public synchronized void addUser(MudUser user) {
		if (userNames == null) {
			userNames = new HashSet<String>();
		}
		userNames.add(user.getName());

		if (users == null) {
			users = new HashSet<MudUser>();
		}
		users.add(user);

		user.setPlaceTag(getTag());
		synchronized (listeners) {
			for (Listener listener : listeners) {
				listener.onUserAdded(this, user);
			}
		}
	}

	@Override
	public boolean containsUser(MudUser user) {
		if (null != users && users.contains(user)) {
			return true;
		}

		if (null != userNames && userNames.contains(user.getName())) {
			return true;
		}

		return false;
	}

	@Override
	public Set<MudUser> getUsers() {
		return null == users ? Collections.<MudUser> emptySet() : new HashSet<MudUser>(this.users);
	}

	@Override
	public void removeUser(MudUser user) {
		users.remove(user);
		userNames.remove(user.getName());
		user.setPlaceTag(null);
	}

	@Override
	public void addObject(MudObject object) {
		if (null == objects) {
			objects = new HashSet<MudObject>();
		}

		objects.add(object);
	}

	@Override
	public void removeObject(MudObject object) {
		objects.remove(object);
	}

	@Override
	public boolean containsObject(MudObject object) {
		return null == objects ? false : objects.contains(object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends MudObject> Set<T> getObjects(Class<T> type) {
		if (null == objects) {
			return Collections.<T> emptySet();
		}

		Set<T> objects = new HashSet<T>();
		for (MudObject o : this.objects) {
			if (type.isInstance(o)) {
				objects.add((T) o);
			}
		}
		return objects;
	}

	@Override
	public void addListener(Listener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(Listener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	private Map<String, MudPlace> getConnections() {
		if (null == connections) {
			connections = new HashMap<String, MudPlace>();
		}
		return connections;
	}

	private Map<String, String> getOpposites() {
		if (null == opposites) {
			opposites = new HashMap<String, String>();
		}
		return opposites;
	}

	@Override
	public String toString() {
		return String.format("MudPlace, %s, '%s'", tag, description);
	}

}
