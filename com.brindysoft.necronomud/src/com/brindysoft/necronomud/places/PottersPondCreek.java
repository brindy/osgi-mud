package com.brindysoft.necronomud.places;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

import com.brindysoft.mud.mpi.MudPlaceProvider;
import com.brindysoft.necronomud.Place;

@Component(provide = MudPlaceProvider.class, properties = "world=necro")
public class PottersPondCreek extends AbstractPlaceProvider {

	@Activate
	public void start() {

		Place startOfDirtTrack = createStartOfDirtTrack();
		Place endOfDirtTrack = createEndOfDirtTrack();
		Place southEastPond = new Place(this, "0011",
				"A path leads around Potter's Pond.  The sound of bullfrogs is almost deafening.");

		connect(southEastPond, southEastPond,
				"The path around Potter's Pond.  The sound of bullfrogs is almost deafening.", "wnes");

		endOfDirtTrack.connect(southEastPond, "north", "south");
		
		connect(startOfDirtTrack, endOfDirtTrack, "A dirt track between the bridge and Potter's Pond Creek.",
				"esesennwn");
		
		connections.add(new Connection(startOfDirtTrack, "0004", "east", "west"));
		
	}

	private Place createEndOfDirtTrack() {
		Place place = new Place(this);
		place.setTag("0006");
		place.setDescription("The dirt track to the south ends abruptly.  "
				+ "The creek continues to flow north towards the sound of bullfrogs.");
		places.add(place);
		return place;
	}

	private Place createStartOfDirtTrack() {
		Place place = new Place(this);
		place.setTag("0008");
		place.setDescription("The start of a dirt track which heads east.  There is a road to the west.");
		places.add(place);
		return place;
	}

}
