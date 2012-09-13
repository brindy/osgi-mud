package com.brindysoft.arkhamud;

import com.brindysoft.mud.mpi.AbstractMudPlace;
import com.brindysoft.mud.mpi.MudPlaceProvider;

public class Place extends AbstractMudPlace {

	public Place(MudPlaceProvider provider) {
		super(provider);
	}

	public Place(MudPlaceProvider provider, String tag, String description) {
		this(provider);
		this.tag = tag;
		this.description = description;
	}

}
