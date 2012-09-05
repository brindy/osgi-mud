package com.brindysoft.necronomud.places;

import java.util.ArrayList;
import java.util.List;

import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudPlaceProvider;
import com.brindysoft.necronomud.Place;

public class AbstractPlaceProvider implements MudPlaceProvider {

	protected List<MudPlace> places = new ArrayList<MudPlace>();

	protected List<Connection> connections = new ArrayList<Connection>();

	@Override
	public MudPlace[] getPlaces() {
		return places.toArray(new MudPlace[places.size()]);
	}

	@Override
	public Connection[] getConnections() {
		return connections.toArray(new Connection[connections.size()]);
	}

	protected void connect(Place start, Place end, String description, String directions) {
		String startTag = start.getTag();
		String endTag = end.getTag();

		for (int i = 0; i < directions.length() - 1; i++) {
			char direction = directions.charAt(i);
			Place next = new Place(this);
			next.setTag(startTag + "-" + i + "-" + endTag);

			next.setDescription(description);

			connect(start, next, direction);
			start = next;
		}
		char direction = directions.charAt(directions.length() - 1);
		connect(start, end, direction);
	}

	protected void connect(Place start, Place next, char direction) {
		switch (direction) {
		case 'n':
			start.connect(next, "north", "south");
			break;

		case 'e':
			start.connect(next, "east", "west");
			break;

		case 's':
			start.connect(next, "south", "north");
			break;

		case 'w':
			start.connect(next, "west", "east");
			break;
		}

	}

}
