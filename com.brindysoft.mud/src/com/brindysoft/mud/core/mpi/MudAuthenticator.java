package com.brindysoft.mud.core.mpi;

import java.io.IOException;

import com.brindysoft.mud.core.api.MudSocketHandler;


public interface MudAuthenticator {

	MudUser authenticate(MudSocketHandler socket) throws IOException;

}
