package com.brindysoft.necronomud.ai;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;

import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

import com.brindysoft.logging.api.Logger;

@Component(immediate = true)
public class AiTicker implements Runnable, UncaughtExceptionHandler {

	private Logger logger;

	private Map<Heart, Long> hearts = new HashMap<Heart, Long>();

	private Thread thread;

	@Reference
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Reference(optional = true, multiple = true, dynamic = true)
	public void addHeart(Heart heart) {
		logger.debug("%s#addHeart(%s) - IN, hearts = %s", getClass().getSimpleName(), heart, hearts);
		hearts.put(heart, 0L);
	}

	public void removeHeart(Heart heart) {
		logger.debug("%s#removeHeart(%s) - IN, hearts = %s", getClass().getSimpleName(), heart, hearts);
		hearts.remove(heart);
	}

	@Activate
	public void start() {
		logger.debug("%s#start() - IN, hearts = %s", getClass().getSimpleName(), hearts);
		thread = new Thread(this);
		thread.setUncaughtExceptionHandler(this);
		thread.start();
		logger.debug("%s#start() - OUT", getClass().getSimpleName());
	}

	@Override
	public void run() {
		logger.debug("%s#run() - IN", getClass().getSimpleName());

		while (null != thread) {
			long time = System.currentTimeMillis();
			for (Map.Entry<Heart, Long> entry : hearts.entrySet()) {
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

	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		logger.error(throwable, "%s#uncaughtException(%s, %s) - IN", getClass().getSimpleName(), thread, throwable);
	}

	static interface Heart {

		long delay();

		void tick();

	}

}
