package com.brindysoft.mud.core;

import java.util.Locale;

public interface MudUser {

	void attach(MudIo engine);

	Locale getLocale();

}
