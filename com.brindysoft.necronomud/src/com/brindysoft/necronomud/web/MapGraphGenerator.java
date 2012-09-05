package com.brindysoft.necronomud.web;

import com.brindysoft.mud.mpi.MudPlace;

public class MapGraphGenerator {

	public MapGraph generate(MudPlace place) {
		MapGraph mapGraph = new MapGraph();

		add(place, 0, 0, 0, 0, mapGraph);

		return mapGraph;
	}

	private void add(MudPlace place, int x, int y, int xMod, int yMod, MapGraph mapGraph) {

		if (null == place) {
			return;
		}

		if (mapGraph.contains(place)) {
			return;
		}

		if (mapGraph.contains(x + xMod, y + yMod)) {
			if (xMod != 0) {
				mapGraph.shiftColumn(x, -xMod);
			} else if (yMod != 0) {
				mapGraph.shiftRow(y, -yMod);
			}
		} else {
			x += xMod;
			y += yMod;
		}

		mapGraph.add(new MapNode(place, x, y));

		add(place.getExit("north"), x, y, 0, -1, mapGraph);
		add(place.getExit("east"), x, y, 1, 0, mapGraph);
		add(place.getExit("south"), x, y, 0, 1, mapGraph);
		add(place.getExit("west"), x, y, -1, 0, mapGraph);

	}
}
