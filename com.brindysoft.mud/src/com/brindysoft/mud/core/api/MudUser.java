package com.brindysoft.mud.core.api;

import java.util.Locale;

public interface MudUser extends MudIo {

	Locale getLocale();

	void attachToSocket(MudSocketHandler socket);

	boolean isAttached();

	String getName();

}
