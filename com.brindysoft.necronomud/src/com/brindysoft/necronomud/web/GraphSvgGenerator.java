package com.brindysoft.necronomud.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.brindysoft.mud.mpi.MudPlaceProvider;
import com.brindysoft.necronomud.web.SvgModel.Element;
import com.brindysoft.necronomud.web.SvgModel.SimpleElement;
import com.brindysoft.necronomud.web.SvgModel.TextElement;

public class GraphSvgGenerator {

	private static final String[] COLOURS = { "#cdc8b1", "#f0fff0", "#bdb76b", "#eee9e9", "#cdc9c9", "#8b8989",
			"#f8f8ff", "#f5f5f5", "#fffaf0", "#fdf5e6", "#faf0e6", "#faebd7", "#eedfcc", "#cdc0b0", "#8b8378",
			"#ffdead", "#ffefd5", "#ffebcd", "#ffe4c4", "#eed5b7", "#cdb79e", "#8b7d6b", "#ffdab9", "#eecbad",
			"#cdaf95", "#8b7765", "#ffe4b5", "#fff8dc", "#eee8dc", "#8b8878", "#fffff0", "#eeeee0", "#cdcdc1",
			"#8b8b83", "#fffacd", "#fff5ee", "#eee5de", "#8b8682", "#e0eee0", "#c1cdc1", "#838b83", "#f5fffa",
			"#fffafa", "#f0ffff", "#f0f8ff", "#e6e6fa", "#fff0f5", "#ffe4e1", "#ffffff", "#66cdaa", "#7fffd4",
			"#006400", "#556b2f", "#8fbc8f", "#2e8b57", "#3cb371", "#20b2aa", "#98fb98", "#00ff7f", "#7cfc00",
			"#7fff00", "#00fa9a", "#adff2f", "#32cd32", "#9acd32", "#228b22", "#cdc5bf", "#6b8e23", "#f0e68c", };

	private static final int PADDING = 20;
	private static final int SIZE = 50;

	public SvgModel generate(MapGraph graph, int offsetX, int offsetY) {
		Set<Set<MapNode>> connections = new HashSet<Set<MapNode>>();
		Map<MudPlaceProvider, String> colours = new HashMap<MudPlaceProvider, String>();

		SvgModel model = new SvgModel();

		int boundsX = 0;
		int boundsY = 0;

		for (int i = 0; i < graph.size(); i++) {
			MapNode node = graph.get(i);

			int x = offsetX + node.x * (SIZE + PADDING);
			int y = offsetY + node.y * (SIZE + PADDING);

			boundsX = Math.max(x, boundsX);
			boundsY = Math.max(y, boundsY);

			Element e = new SimpleElement("rect");

			if (node.place.getTag() != null) {
				e.setAttribute("id", "tag" + node.place.getTag().replaceAll("-", ""));
			}

			e.setAttribute("fill", colorForProvider(colours, node));
			e.setAttribute("stroke", "#000");
			e.setAttribute("stroke-weight", "3");
			e.setAttribute("class", "mud-place");
			e.setAttribute("width", "50");
			e.setAttribute("height", "50");
			e.setAttribute("x", x);
			e.setAttribute("y", y);
			model.add(e);

			if (node.place.getTag() != null) {

				if (node.place.getTag().length() == 4) {
					TextElement t = new TextElement(node.place.getTag());
					t.setAttribute("x", x + 6);
					t.setAttribute("y", y + (SIZE / 2) + 5);
					t.setAttribute("class", "mud-label");
					t.setAttribute("style", "font-family: tahoma;");
					model.add(t);
				}
				
				TextElement tip = new TextElement(node.place.getDescription(null));
				tip.setAttribute("id", "tip" + node.place.getTag().replace("-", ""));
				tip.setAttribute("x", 5);
				tip.setAttribute("y", y + (SIZE / 2) + 15);
				tip.setAttribute("visibility", "hidden");
				tip.addSetter("visibility", "hidden", "visible", "tag" + node.place.getTag().replaceAll("-", "")
						+ ".mouseover", "tag" + node.place.getTag().replaceAll("-", "") + ".mouseout");
				model.add(tip);
			}

			addLinesForConnections(graph, connections, model, node, "north");
			addLinesForConnections(graph, connections, model, node, "east");
			addLinesForConnections(graph, connections, model, node, "south");
			addLinesForConnections(graph, connections, model, node, "west");

		}

		model.setWidth(boundsX + SIZE + 10);
		model.setHeight(boundsY + SIZE + 10);

		return model;
	}

	private String colorForProvider(Map<MudPlaceProvider, String> colours, MapNode node) {
		MudPlaceProvider provider = node.place.getProvider();

		String colour = colours.get(provider);
		if (null == colour) {
			colours.put(node.place.getProvider(), colour = COLOURS[colours.size()]);
		}

		return colour;
	}

	private void addLinesForConnections(MapGraph graph, Set<Set<MapNode>> connections, SvgModel model, MapNode node,
			String exit) {
		if (null != node.place.getExit(exit)) {

			// find the destination
			MapNode destination = graph.find(node.place.getExit(exit));

			Set<MapNode> connection = new HashSet<MapNode>();
			connection.add(node);
			connection.add(destination);

			// has the line already been added
			if (connections.add(connection)) {
				// no it hasn't
				model.add(lineBetween(node, destination));
			}

		}
	}

	private Element lineBetween(MapNode source, MapNode destination) {
		Element line = new SimpleElement("line");
		line.setAttribute("stroke", "#000");
		line.setAttribute("stroke-weight", "5");
		line.setAttribute("class", "mud-direction");

		int x1 = (source.x * (SIZE + PADDING)) + (SIZE / 2);
		int y1 = (source.y * (SIZE + PADDING)) + (SIZE / 2);
		int x2 = (destination.x * (SIZE + PADDING)) + (SIZE / 2);
		int y2 = (destination.y * (SIZE + PADDING)) + (SIZE / 2);

		line.setAttribute("x1", x1);
		line.setAttribute("y1", y1);

		line.setAttribute("x2", x2);
		line.setAttribute("y2", y2);

		return line;
	}

}
