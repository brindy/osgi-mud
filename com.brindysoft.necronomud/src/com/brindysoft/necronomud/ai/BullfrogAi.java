package com.brindysoft.necronomud.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.mud.mpi.MudPlace;
import com.brindysoft.mud.mpi.MudPlace.AbstractListener;
import com.brindysoft.mud.mpi.MudBackgroundTask;
import com.brindysoft.mud.mpi.MudUser;
import com.brindysoft.mud.mpi.MudWorld;
import com.brindysoft.necronomud.User;
import com.brindysoft.necronomud.World;
import com.brindysoft.necronomud.objects.Bullfrog;
import com.brindysoft.necronomud.objects.Bullfrog.UserData;

// TODO why doesn't this get activated??

@Component
public class BullfrogAi extends AbstractListener implements MudBackgroundTask {

	private Set<Bullfrog> bullfrogs = new HashSet<Bullfrog>();

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
		logger.debug("%s#stop() - IN", getClass().getSimpleName());

		for (MudPlace place : patrolRoute) {
			for (Bullfrog frog : bullfrogs) {
				place.removeObject(frog);
			}
		}

		logger.debug("%s#stop() - OUT", getClass().getSimpleName());
	}

	@Override
	public void tick() {

		if (users <= 0) {
			return;
		}

		long time = System.currentTimeMillis();

		synchronized (bullfrogs) {
			logger.debug("%s#tick()", getClass().getName());
			for (Bullfrog frog : bullfrogs) {
				for (UserData data : new HashSet<UserData>(frog.getAllUserData())) {
					if (time - data.lastInteractionTime > 30000) {
						frog.removeUserData(data.userName);
					}
				}
			}
		}

	}

	@Override
	public long delay() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void onUserAdded(MudPlace place, MudUser user) {
		logger.debug("%s#onUserAdded() %s", getClass().getSimpleName(), user.getName());
		users++;
	}

	@Override
	public void onUserRemoved(MudPlace place, MudUser user) {
		logger.debug("%s#onUserRemoved() %s", getClass().getSimpleName(), user.getName());
		users--;
	}
	
	@Override
	public void onUserDisconnected(MudPlace place, MudUser user) {
		logger.debug("%s#onUserDisconnected() %s", getClass().getSimpleName(), user.getName());
		users--;
	}

	public void examinedBy(Bullfrog bullfrog, MudUser mudUser) {
		User user = (User) mudUser;
		UserData data = bullfrog.getUserData(user.getName());

		switch (data.examineCount) {
		case 0:
			user.println("The bullfrog croaks loudly and you think you see its eyes flash red for a moment.  "
					+ "Maybe you should leave it alone?");
			move(bullfrog);
			break;

		default:
			user.println("The bullfrog appears to squint, and then its long, strangely tentacle like tongue "
					+ "flicks from its mouth, narrowly missing your eye.  It looks really annoyed.");

			if (!user.getPropertyAsBoolean("bullfrog.tongue")) {
				user.setProperty("bullfrog.tongue", true);
				user.incInsanity(1);
			}

			move(bullfrog);
			break;
		}

		data.examineCount++;
	}

	private void spawn() {
		if (patrolRoute.size() > 0) {
			Collections.shuffle(patrolRoute);
			MudPlace current = patrolRoute.get(0);
			current.addObject(new Bullfrog(this));
			current.broadcast("A bullfrog emerges from the pond.  Something about it isn't quite right.");
		}
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

}
