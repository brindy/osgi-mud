package com.brindysoft.necronomud.web;

import com.brindysoft.mud.mpi.MudPlace;

public class MapNode {

	public int x;
	public int y;
	public MudPlace place;

	public MapNode(MudPlace place, int x, int y) {
		this.place = place;
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return x + "," + y + ",[" + place + "]";
	}

}
