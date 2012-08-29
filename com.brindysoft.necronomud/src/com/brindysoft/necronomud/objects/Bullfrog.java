package com.brindysoft.necronomud.objects;

import com.brindysoft.mud.mpi.MudObject;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.necronomud.ai.BullfrogAi;

public class Bullfrog implements MudObject, MudObject.Examinable, MudObject.Listable {

	private BullfrogAi ai;

	public Bullfrog(BullfrogAi ai) {
		this.ai = ai;
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

}
