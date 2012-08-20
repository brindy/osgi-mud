package com.brindysoft.mud.core.internal;

import java.io.IOException;

import com.brindysoft.mud.core.mpi.MudUser;

public interface MudEngine {

	void run(MudUser user) throws IOException;

}
