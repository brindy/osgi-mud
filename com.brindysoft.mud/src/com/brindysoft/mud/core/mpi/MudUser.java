package com.brindysoft.mud.core.mpi;

import java.util.Locale;

import com.brindysoft.mud.core.api.MudIo;
import com.brindysoft.mud.core.api.MudSocketHandler;


public interface MudUser extends MudIo {

	Locale getLocale();

	void attachToSocket(MudSocketHandler socket);

	boolean isAttached();

	String getName();

}
