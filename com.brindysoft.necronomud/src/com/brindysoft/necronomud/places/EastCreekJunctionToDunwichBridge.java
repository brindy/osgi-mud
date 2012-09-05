package com.brindysoft.necronomud.places;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;

import com.brindysoft.mud.mpi.MudPlaceProvider;
import com.brindysoft.necronomud.Place;

@Component(provide = MudPlaceProvider.class, properties = "world=necro")
public class EastCreekJunctionToDunwichBridge extends AbstractPlaceProvider {

	@Activate
	public void start() {

		Place northOfJunction = new Place(this);
		northOfJunction.setTag("0009");
		northOfJunction.setDescription("East Creek Junction is to the south.  The road to Dunwich continues north.");
		places.add(northOfJunction);

		Place southOfDunwichBridge = new Place(this);
		southOfDunwichBridge.setTag("0010");
		southOfDunwichBridge
				.setDescription("To the north is a dangerous looking, rackety old wooden bridge which crosses Miskatonic River in to Dunwich.  "
						+ "The road south heads to East Creek Junction.  South River Road runs east to west.");
		places.add(southOfDunwichBridge);

		connect(northOfJunction, southOfDunwichBridge, "The road between Dunwich and East Creek Junction.",
				"nneenwwnneeennwwn");

		connections.add(new Connection(northOfJunction, "0005", "north", "south"));

	}

}
