package com.brindysoft.necronomud.places;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

import com.brindysoft.mud.mpi.MudPlaceProvider;
import com.brindysoft.necronomud.Place;
import com.brindysoft.necronomud.objects.BusStop;

@Component(immediate = true, provide = MudPlaceProvider.class, properties = "world=necro")
public class DunwichRoadToEastCreekJunction extends AbstractPlaceProvider {

	@Activate
	public void start() {

		// create key places
		Place busStop = createBusStop();
		Place dunwichTurnoff = createDunwichTurnOff();
		Place aBridge = createBridgeBetweenDunwichAndTurnOff();
		Place northOfTheBridge = createNorthOfTheBridge();
		Place cornerOfEastCreekAndDunwich = createCornerOfEastCreedAndDunwich();

		// create connections
		busStop.connect(dunwichTurnoff, "east", "west");
		connect(dunwichTurnoff, aBridge, "Dunwich Road, between the turnoff and the bridge.", "nwn");
		aBridge.connect(northOfTheBridge, "north", "south");
		connect(northOfTheBridge, cornerOfEastCreekAndDunwich,
				"Dunwich Road, between the bridge and East Creek Road junction.", "wnnw");

	}

	private Place createBusStop() {
		Place place = new Place();
		place.setTag("0001");
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

}
