package com.brindysoft.mud.mpi;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.brindysoft.mud.necronomud.User;

public abstract class AbstractMudPlace implements MudPlace {

	protected Set<String> userNames;

	protected Set<MudObject> objects;

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
			user.println(message, args);
		}
	}

	@Override
	public void broadcastByUser(MudUser user, String message, Object... args) {
		for (MudUser recipient : getUsers()) {
			if (user == recipient) {
				continue;
			}
			recipient.println(message, args);
		}
	}

	@Override
	public Set<String> getExits() {
		return null == connections ? Collections.<String> emptySet() : Collections.unmodifiableSet(new HashSet<String>(
				connections.keySet()));
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
		broadcast("%s arrives from the %s.", user.getName(), opposites.get(direction));
		addUser(user);
	}

	public void connect(AbstractMudPlace otherPlace, String inDirection, String fromDirection) {
		getConnections().put(inDirection, otherPlace);
		getOpposites().put(inDirection, fromDirection);

		otherPlace.getConnections().put(fromDirection, this);
		otherPlace.getOpposites().put(fromDirection, inDirection);
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

		((User) user).setPlaceTag(getTag());
	}

	@Override
	public boolean contains(Object object) {
		return null == users ? false : users.contains(object);
	}

	@Override
	public Set<MudUser> getUsers() {
		return null == users ? Collections.<MudUser> emptySet() : Collections.unmodifiableSet(new HashSet<MudUser>(
				this.users));
	}

	@Override
	public void removeUser(MudUser user) {
		users.remove(user);
		userNames.remove(user.getName());
		((User) user).setPlaceTag(null);
	}

	@Override
	public synchronized void userLeaves(MudUser user, String direction) {
		users.remove(user);
		broadcast("%s heads %s.", user.getName(), direction);
	}

	@Override
	public void addObject(MudObject object) {
		if (null == objects) {
			objects = new HashSet<MudObject>();
		}

		objects.add(object);
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

}
