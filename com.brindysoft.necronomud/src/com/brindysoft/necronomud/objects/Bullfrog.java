package com.brindysoft.necronomud.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.brindysoft.mud.mpi.MudObject;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.necronomud.ai.BullfrogAi;

public class Bullfrog implements MudObject, MudObject.Examinable, MudObject.Listable, MudObject.HasCommandSynonyms {

	private Map<String, UserData> users = new HashMap<String, Bullfrog.UserData>();

	private BullfrogAi ai;
	private Map<String, String> synonyms;

	public Bullfrog(BullfrogAi ai) {
		this.ai = ai;
		synonyms = new HashMap<String, String>();
		synonyms.put("touch", "examine");
		synonyms.put("take", "examine");
	}

	@Override
	public void examine(MudUser user) {
		ai.examinedBy(this, user);
	}

	@Override
	public String[] getAliases() {
		return new String[] { "bullfrog", "bull frog", "frog" };
	}

	@Override
	public String getListName() {
		return "A bullfrog";
	}

	@Override
	public String getSynonymFor(String string) {
		return synonyms.get(string);
	}

	public class UserData {

		public long lastInteractionTime;
		public int examineCount;
		public final String userName;

		public UserData(String userName) {
			this.userName = userName;
			lastInteractionTime = System.currentTimeMillis();
		}

	}

	public UserData getUserData(String name) {
		UserData data = users.get(name);
		if (null == data) {
			data = new UserData(name);
			users.put(name, data);
		}
		return data;
	}

	public void removeUserData(String userName) {
		users.remove(userName);
	}

	public Collection<? extends UserData> getAllUserData() {
		return users.values();
	}

}
