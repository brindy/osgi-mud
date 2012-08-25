package com.brindysoft.mud.necronomud;

import com.brindysoft.mud.necronomud.objects.BusStop;
import com.brindysoft.oodb.api.Database;

public class WorldBuilder {

	public static final String STARTING_PLACE = "bus stop";
	private final Database db;

	private WorldBuilder(Database db) {
		this.db = db;
	}

	public static void createWorld(Database db) {
		new WorldBuilder(db).createWorld();
	}

	public void createWorld() {

		// create key places
		SimplePlace dunwichTurnoff = createDunwichTurnOff();
		SimplePlace aBridge = createBridgeBetweenDunwichAndTurnOff();
		SimplePlace cornerOfEastCreekAndDunwich = createCornerOfEastCreedAndDunwich();
		SimplePlace busStop = createBusStop();

		// TODO make this longer
		connect(aBridge, cornerOfEastCreekAndDunwich, "Dunwich Road, between the bridge and East Creek Road junction.",
				"nwnnw");

		// TODO make this longer
		connect(dunwichTurnoff, aBridge, "Dunwich Road, between the turnoff and the bridge.", "nwn");

		// connect places
		connect(busStop, dunwichTurnoff, "The main road to Aylesbury and Arkham, "
				+ "running east to west respectively.", "e");

		db.store(busStop);
		db.commit();
	}

	private SimplePlace createBusStop() {
		SimplePlace place = new SimplePlace();
		place.setTag(STARTING_PLACE);
		place.setDescription("This is the bus stop at Dean's Corners.  "
				+ "The main road to Aylesbury and Arkham runs east to west respectively.  ");

		BusStop busStopObject = new BusStop();
		place.addObject(busStopObject);
		return place;
	}

	private SimplePlace createDunwichTurnOff() {
		SimplePlace place = new SimplePlace();
		place.setTag("turnoff");
		place.setDescription("This is the Dunwich Turnoff.  "
				+ "The main road to Aylesbury and Arkham runs east to west respectively.  "
				+ "The road to Dunwich, little more than an improved dirt road, heads north.");
		return place;
	}

	private SimplePlace createBridgeBetweenDunwichAndTurnOff() {
		SimplePlace place = new SimplePlace();
		place.setTag("turnoff-dunwich-bridge");
		place.setDescription("This is a covered bridge made mainly of wooden planks.  "
				+ "Dunwich Road continues to the north and south.  " + "A river flows east to west below the bridge.");
		return place;
	}

	private SimplePlace createCornerOfEastCreedAndDunwich() {
		SimplePlace place = new SimplePlace();
		place.setTag("eastcreek-dunwich-corner");
		place.setDescription("Dunwich Road turns sharply from the east to the north.  "
				+ "East Creek Road leads off to the west.");
		return place;
	}

	private void connect(SimplePlace start, SimplePlace end, String description, String directions) {
		for (int i = 0; i < directions.length() - 1; i++) {
			char direction = directions.charAt(i);
			SimplePlace next = new SimplePlace();
			next.setDescription(description);

			connect(start, next, direction);
			start = next;
		}
		char direction = directions.charAt(directions.length() - 1);
		connect(start, end, direction);
	}

	private void connect(SimplePlace start, SimplePlace next, char direction) {
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
