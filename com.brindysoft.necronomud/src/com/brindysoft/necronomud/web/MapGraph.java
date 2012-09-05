package com.brindysoft.necronomud.web;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.brindysoft.mud.mpi.MudPlace;

public class MapGraph {

	private List<MapNode> list = new LinkedList<MapNode>();
	private Map<String, MudPlace> places = new HashMap<String, MudPlace>();

	public int size() {
		return list.size();
	}

	public MapNode get(int i) {
		return list.get(i);
	}

	public void add(MapNode mapNode) {
		list.add(mapNode);
		if (null != places.put(mapNode.x + "," + mapNode.y, mapNode.place)) {
			throw new RuntimeException("Overlap! " + mapNode);
		}
	}

	public boolean contains(MudPlace place) {
		return places.values().contains(place);
	}

	public boolean contains(int x, int y) {
		String key = x + "," + y;
		System.out.println("*** contains? " + key);
		
		return places.containsKey(key);
	}

	public void shiftColumn(int x, int xMod) {

		List<MapNode> nodes = new LinkedList<MapNode>(this.list);

		places.clear();
		list.clear();
		for (MapNode node : nodes) {
			if (node.x == x) {
				node.x += xMod;
			}
			add(node);
		}

	}

	public void shiftRow(int y, int yMod) {

		List<MapNode> nodes = new LinkedList<MapNode>(this.list);

		places.clear();
		list.clear();
		for (MapNode node : nodes) {
			if (node.y == y) {
				node.y += yMod;
			}
			add(node);
		}

	}

	public void normalise() {

		int lowestX = 0;
		int lowestY = 0;

		for (MapNode node : list) {
			lowestX = Math.min(lowestX, node.x);
			lowestY = Math.min(lowestY, node.y);
		}

		int xMod = 0;
		if (lowestX < 0) {
			xMod = -lowestX;
		}

		int yMod = 0;
		if (lowestY < 0) {
			yMod = -lowestY;
		}

		for (MapNode node : list) {
			node.x += xMod;
			node.y += yMod;
		}
		
	}

	public MapNode find(MudPlace place) {
		for (MapNode node : list) {
			if (node.place == place) {
				return node;
			}
		}
		return null;
	}

}
