package com.brindysoft.necronomud.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudPlace.AbstractListener;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudWorld;
import com.brindysoft.necronomud.World;
import com.brindysoft.necronomud.ai.AiTicker.Heart;
import com.brindysoft.necronomud.objects.Bullfrog;

@Component(immediate = true)
public class BullfrogAi extends AbstractListener implements Heart {

	private Map<Bullfrog, Map<String, Data>> bullfrogData = new HashMap<Bullfrog, Map<String, Data>>();

	private List<MudPlace> patrolRoute = new ArrayList<MudPlace>(4);

	private int users;
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

			for (MudPlace patrolPlace : patrolRoute) {
				patrolPlace.addListener(this);
			}

		}

		logger.debug("%s#start() OUT - route has %d places", getClass().getSimpleName(), patrolRoute.size());

		spawn();
	}

	@Deactivate
	public void stop() {
		logger.debug("%s#stop()", getClass().getSimpleName());
	}

	@Override
	public void tick() {

		long time = System.currentTimeMillis();

		synchronized (bullfrogData) {
			Set<Map<String, Data>> toRemove = new HashSet<Map<String, Data>>();
			for (Map<String, Data> userData : bullfrogData.values()) {
				for (Data data : userData.values()) {
					if (time - data.lastInteractionTime > 30000) {
						toRemove.add(userData);
					}
				}
			}
			bullfrogData.values().removeAll(toRemove);
		}

	}

	@Override
	public long delay() {
		return 5000;
	}

	@Override
	public void onUserAdded(MudPlace place, MudUser user) {
		users++;
	}

	@Override
	public void onUserLeaves(MudPlace place, MudUser user, String toDirection) {
		users--;
	}

	private void spawn() {
		Collections.shuffle(patrolRoute);
		MudPlace current = patrolRoute.get(0);
		current.addObject(new Bullfrog(this));
		current.broadcast("A bullfrog emerges from the pond.  Something about it isn't quite right.");
	}

	public void examinedBy(Bullfrog bullfrog, MudUser user) {
		Data data = getDataFor(bullfrog, user);

		switch (data.examineCount) {
		case 0:
			user.println("The bullfrog croaks loudly and you think you see its eyes flash red for a moment.  "
					+ "Maybe you should leave it alone?");
			move(bullfrog);
			break;

		default:
			user.println("The bullfrog appears to squint, and then its long, strangely tentacle like tongue "
					+ "flicks from its mouth, narrowly missing your eye.  It looks annoyed.");
			move(bullfrog);
			break;
		}

		data.examineCount++;
	}

	private void move(Bullfrog bullfrog) {

		for (MudPlace place : patrolRoute) {
			if (place.containsObject(bullfrog)) {

				MudPlace destination = null;
				String direction;
				do {
					List<String> exits = new LinkedList<String>(place.getExits());
					Collections.shuffle(exits);
					destination = place.getExit(direction = exits.get(0));
				} while (!destination.getTag().contains("0011"));

				place.broadcast("The bullfrog heads %s.", direction);
				place.removeObject(bullfrog);
				destination.addObject(bullfrog);
				destination.broadcast("A bullfrog arrives from the %s.", place.getOppositeExit(direction));

				break;
			}
		}

	}

	private Data getDataFor(Bullfrog bullfrog, MudUser user) {
		Map<String, Data> userData = bullfrogData.get(bullfrog);
		if (null == userData) {
			userData = new HashMap<String, Data>();
			bullfrogData.put(bullfrog, userData);
		}

		Data data = userData.get(user.getName());
		if (null == data) {
			data = new Data();
			userData.put(user.getName(), data);
		}

		data.lastInteractionTime = System.currentTimeMillis();
		return data;
	}

	class Data {

		long lastInteractionTime;
		int examineCount;

		public Data() {
		}

	}

}
