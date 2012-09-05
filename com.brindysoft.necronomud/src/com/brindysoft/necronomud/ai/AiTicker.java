package com.brindysoft.necronomud.ai;

import java.util.HashMap;
import java.util.Map;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;
import com.brindysoft.necronomud.Tickable;

@Component(immediate = true, properties = { "spawn=true" })
public class AiTicker implements Runnable {

	private Logger logger;

	private Map<Tickable, Long> hearts = new HashMap<Tickable, Long>();

	private Thread thread;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference(optional = true, multiple = true, dynamic = true)
	public void add(Tickable heart) {
		hearts.put(heart, 0L);
	}

	public void remove(Tickable heart) {
		hearts.remove(heart);
	}

	@Activate
	public void start() {
		logger.debug("%s#start(), hearts = %s", getClass().getSimpleName(), hearts);
	}

	@Override
	public void run() {
		logger.debug("%s#run(%s) - IN", getClass().getSimpleName(), Thread.currentThread());

		thread = Thread.currentThread();

		while (null != thread) {
			long time = System.currentTimeMillis();
			for (Map.Entry<Tickable, Long> entry : hearts.entrySet()) {
				if (time > entry.getValue()) {
					entry.getKey().tick();
					entry.setValue(entry.getKey().delay() + System.currentTimeMillis());
				}
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}

		logger.debug("%s#run() - OUT", getClass().getSimpleName());
	}

	@Deactivate
	public void stop() {
		logger.debug("%s#stop() - IN", getClass().getSimpleName());

		Thread thread = this.thread;
		this.thread = null;
		thread.interrupt();

		logger.debug("%s#stop() - OUT", getClass().getSimpleName());
	}

}
