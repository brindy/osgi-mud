package com.brindysoft.mud.necronomud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.mud.mpi.AbstractMudPlace;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudPlaceProvider;
import com.brindysoft.mud.mpi.MudUserManager;
import com.brindysoft.mud.necronomud.objects.BusStop;

@Component(immediate=true)
public class WorldBuilder implements MudPlaceProvider {

	public static final String STARTING_PLACE = "0001";

	private List<MudPlace> places = new ArrayList<MudPlace>();

	private MudUserManager userManager;

	@Reference
	public void setUserManager(MudUserManager userManager) {
		this.userManager = userManager;
	}

	@Override
	public MudPlace[] getPlaces() {
		return places.toArray(new MudPlace[places.size()]);
	}

	@Activate
	public void start() {

		// create key places
		Place busStop = createBusStop();
		Place dunwichTurnoff = createDunwichTurnOff();
		Place aBridge = createBridgeBetweenDunwichAndTurnOff();
		Place northOfTheBridge = createNorthOfTheBridge();
		Place cornerOfEastCreekAndDunwich = createCornerOfEastCreedAndDunwich();
		Place endOfDirtTrack = createEndOfDirtTrackNearPottersPondCreek();

		// create connections
		busStop.connect(dunwichTurnoff, "east", "west");
		connect(dunwichTurnoff, aBridge, "Dunwich Road, between the turnoff and the bridge.", "nwn");
		aBridge.connect(northOfTheBridge, "north", "south");
		connect(northOfTheBridge, cornerOfEastCreekAndDunwich,
				"Dunwich Road, between the bridge and East Creek Road junction.", "wnnw");
		connect(northOfTheBridge, endOfDirtTrack, "A dirt track between the bridge and Potter's Pond Creek.",
				"eesesennwn");

		Map<String, AbstractMudPlace> places = new HashMap<String, AbstractMudPlace>();
		for (MudPlace place : this.places) {
			places.put(place.getTag(), (AbstractMudPlace) place);
		}

		// reconnect users
		for (User user : ((UserManager) userManager).allUsers()) {
			String tag = user.getPlaceTag();
			AbstractMudPlace place = places.get(tag);
			if (null != place) {
				place.addUser(user);
			}
		}

	}

	private Place createBusStop() {
		Place place = new Place();
		place.setTag(STARTING_PLACE);
		place.setDescription("This is the bus stop at Dean's Corners.  "
				+ "The main road to Aylesbury and Arkham runs east to west respectively.  ");

		BusStop busStopObject = new BusStop();
		place.addObject(busStopObject);
		places.add(place);
		return place;
	}

	private Place createDunwichTurnOff() {
		Place place = new Place();
		place.setTag("0002");
		place.setDescription("This is the Dunwich Turnoff.  "
				+ "The main road to Aylesbury and Arkham runs east to west respectively.  "
				+ "The road to Dunwich, little more than an improved dirt road, heads north.");
		places.add(place);
		return place;
	}

	private Place createBridgeBetweenDunwichAndTurnOff() {
		Place place = new Place();
		place.setTag("0003");
		place.setDescription("This is a covered bridge made mainly of wooden planks.  "
				+ "Dunwich Road continues to the north and south.  " + "A river flows east to west below the bridge.");
		places.add(place);
		return place;
	}

	private Place createNorthOfTheBridge() {
		Place place = new Place();
		place.setTag("0004");
		place.setDescription("The road to Dunwich continues west.  A dirt track heads east.");
		places.add(place);
		return place;
	}

	private Place createCornerOfEastCreedAndDunwich() {
		Place place = new Place();
		place.setTag("0005");
		place.setDescription("Dunwich Road turns sharply from the east to the north.  "
				+ "East Creek Road leads off to the west.");
		places.add(place);
		return place;
	}

	private Place createEndOfDirtTrackNearPottersPondCreek() {
		Place place = new Place();
		place.setTag("0006");
		place.setDescription("The dirt track to the south ends abruptly.  "
				+ "The creek continues to flow north towards the sound of bullfrogs.");
		places.add(place);
		return place;
	}

	private void connect(Place start, Place end, String description, String directions) {
		for (int i = 0; i < directions.length() - 1; i++) {
			char direction = directions.charAt(i);
			Place next = new Place();
			next.setTag(start.getTag() + "-" + i + "-" + end.getTag());

			next.setDescription(description);

			connect(start, next, direction);
			start = next;
		}
		char direction = directions.charAt(directions.length() - 1);
		connect(start, end, direction);
	}

	private void connect(Place start, Place next, char direction) {
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
