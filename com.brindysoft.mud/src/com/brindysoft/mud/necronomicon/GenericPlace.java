package com.brindysoft.mud.necronomicon;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.brindysoft.mud.core.api.MudPlace;
import com.brindysoft.mud.core.api.MudUser;

public class GenericPlace implements MudPlace {

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
		for (MudUser otherUser : users) {
			if (otherUser == user || !otherUser.isAttached()) {
				continue;
			}
			otherUser.println("{text:green}%s{text} has just appeared here.", user.getName());
		}
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
}
