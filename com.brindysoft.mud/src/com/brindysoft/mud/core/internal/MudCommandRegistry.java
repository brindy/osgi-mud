package com.brindysoft.mud.core.internal;

import com.brindysoft.mud.mpi.MudCommand;
import com.brindysoft.mud.mpi.MudUser;

public interface MudCommandRegistry {

	MudCommand find(String string, MudUser user);

}
