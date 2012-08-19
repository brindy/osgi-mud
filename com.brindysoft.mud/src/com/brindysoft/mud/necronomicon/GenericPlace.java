package com.brindysoft.mud.necronomicon;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.brindysoft.mud.core.api.MudPlace;
import com.brindysoft.mud.core.api.MudUser;

public class GenericPlace implements MudPlace {

	private Map<String, MudPlace> connections;

	private Map<String, String> opposites;

	private Set<MudUser> users;

	private String description;

	@Override
	public String getDescription(MudUser user) {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public synchronized void addUser(MudUser user) {
		if (users == null) {
			users = new HashSet<MudUser>();
		}
		users.add(user);
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
	public MudPlace placeInDirection(String direction) {
		return null == connections ? null : connections.get(direction);
	}

	@Override
	public void removeUser(MudUser user) {
		users.remove(user);
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
	public String getOppositeExit(String direction) {
		return getOpposites().get(direction);
	}

	public void connect(GenericPlace otherPlace, String inDirection, String fromDirection) {
		getConnections().put(inDirection, otherPlace);
		getOpposites().put(inDirection, fromDirection);

		otherPlace.getConnections().put(fromDirection, this);
		otherPlace.getOpposites().put(fromDirection, inDirection);
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
