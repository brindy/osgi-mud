package com.brindysoft.mud.core.api;

/**
 * Not intended to be implemented by clients.
 * 
 * @author brindy
 */
public interface MudSocketHandler extends MudIo {

	void close();

	boolean isAlive();

}
