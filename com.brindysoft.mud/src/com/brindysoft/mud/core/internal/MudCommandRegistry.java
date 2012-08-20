package com.brindysoft.mud.core.internal;

import com.brindysoft.mud.mpi.MudCommand;


public interface MudCommandRegistry {

	MudCommand find(String string);

}
