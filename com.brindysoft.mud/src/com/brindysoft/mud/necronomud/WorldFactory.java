package com.brindysoft.mud.necronomud;

import com.brindysoft.mud.necronomud.objects.BusStop;
import com.db4o.ObjectContainer;

public class WorldFactory {

	public static final String STARTING_PLACE = "bus stop";

	public static void init(ObjectContainer db) {
		// create key places
		SimplePlace busStop = createBusStop();
		SimplePlace dunwichTurnoff = createDunwichTurnOff();
		SimplePlace aBridge = createBridgeBetweenDunwichAndTurnOff();
		SimplePlace cornerOfEastCreekAndDunwich = createCornerOfEastCreedAndDunwich();

		// connect places
		WorldFactory.connect(db, busStop, dunwichTurnoff, "The main road to Aylesbury and Arkham, "
				+ "running east to west respectively.", "e");

		// TODO make this longer
		WorldFactory.connect(db, dunwichTurnoff, aBridge, "Dunwich Road, between the turnoff and the bridge.", "nwn");

		// TODO make this longer
		WorldFactory.connect(db, aBridge, cornerOfEastCreekAndDunwich,
				"Dunwich Road, between the bridge and East Creek Road junction.", "wnw");

		db.store(busStop);
		db.store(dunwichTurnoff);
		db.store(aBridge);
		db.store(cornerOfEastCreekAndDunwich);
		db.commit();
	}

	public static SimplePlace createBusStop() {
		SimplePlace busstop = new SimplePlace();
		busstop.setTag(STARTING_PLACE);
		busstop.setDescription("This is the bus stop at Dean's Corners.  "
				+ "The main road to Aylesbury and Arkham runs east to west respectively.  ");
		
		busstop.addObject(new BusStop());
		
		return busstop;
	}

	public static SimplePlace createDunwichTurnOff() {
		SimplePlace dunwichTurnoff = new SimplePlace();
		dunwichTurnoff.setTag("turnoff");
		dunwichTurnoff.setDescription("This is the Dunwich Turnoff.  "
				+ "The main road to Aylesbury and Arkham runs east to west respectively.  "
				+ "The road to Dunwich, little more than an improved dirt road, heads north.");
		return dunwichTurnoff;
	}

	public static SimplePlace createBridgeBetweenDunwichAndTurnOff() {
		SimplePlace aBridge = new SimplePlace();
		aBridge.setTag("turnoff-dunwich-bridge");
		aBridge.setDescription("This is a covered bridge made mainly of wooden planks.  "
				+ "Dunwich Road continues to the north and south.  " + "A river flows east to west below the bridge.");
		return aBridge;
	}

	public static SimplePlace createCornerOfEastCreedAndDunwich() {
		SimplePlace cornerOfEastCreekAndDunwich = new SimplePlace();
		cornerOfEastCreekAndDunwich.setTag("eastcreek-dunwich-corner");
		cornerOfEastCreekAndDunwich.setDescription("Dunwich Road turns sharply from the east to the north.  "
				+ "East Creek Road leads off to the west.");
		return cornerOfEastCreekAndDunwich;
	}

	public static void connect(ObjectContainer db, SimplePlace start, SimplePlace end, String description,
			String directions) {
		for (int i = 0; i < directions.length() - 1; i++) {
			char direction = directions.charAt(i);
			SimplePlace next = new SimplePlace();
			next.setDescription(description);
			db.store(next);

			connect(db, start, next, direction);
			start = next;
		}
		char direction = directions.charAt(directions.length() - 1);
		connect(db, start, end, direction);
	}

	public static void connect(ObjectContainer db, SimplePlace start, SimplePlace next, char direction) {
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

		db.store(start);
		db.store(next);
	}

}
