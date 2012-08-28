package com.brindysoft.necronomud.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudWorld;
import com.brindysoft.necronomud.World;
import com.brindysoft.necronomud.ai.AiTicker.Heart;

@Component(immediate = true)
public class BullfrogAi implements Heart {

	private List<MudPlace> patrolRoute = new ArrayList<MudPlace>(4);

	private World world;
	private Logger logger;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference
	public void setWorld(MudWorld world) {
		this.world = (World) world;
	}

	@Activate
	public void start() {
		logger.debug("%s#start()", getClass().getSimpleName());

		MudPlace place = world.findPlaceByTag("0011");

		if (place != null) {
			patrolRoute.add(place);
			patrolRoute.add(world.findPlaceByTag("0011-0-0011"));
			patrolRoute.add(world.findPlaceByTag("0011-1-0011"));
			patrolRoute.add(world.findPlaceByTag("0011-2-0011"));
		}

		logger.debug("%s#start() OUT - route has %d places", getClass().getSimpleName(), patrolRoute.size());

		spawn();
	}

	private void spawn() {
		Collections.shuffle(patrolRoute);

	}

	@Deactivate
	public void stop() {
		logger.debug("%s#stop()", getClass().getSimpleName());
	}

	@Override
	public void tick() {

		for (MudPlace pond : patrolRoute) {
			logger.debug("%s#tick() - checking %s", getClass().getSimpleName(), pond.getTag());
		}

	}

	@Override
	public long delay() {
		return 5000;
	}

}
