package com.brindysoft.mud.mpi;

import com.brindysoft.mud.api.MudIo;
import com.brindysoft.mud.api.MudSocketHandler;

public interface MudUser extends MudIo {

	void attachToSocket(MudSocketHandler socket);

	boolean isAttached();

	String getName();

}
