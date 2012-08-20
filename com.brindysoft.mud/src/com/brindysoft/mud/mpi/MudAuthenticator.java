package com.brindysoft.mud.mpi;

import java.io.IOException;

import com.brindysoft.mud.api.MudSocketHandler;


public interface MudAuthenticator {

	MudUser authenticate(MudSocketHandler socket) throws IOException;

}
