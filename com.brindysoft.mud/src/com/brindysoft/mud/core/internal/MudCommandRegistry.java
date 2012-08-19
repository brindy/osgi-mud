package com.brindysoft.mud.core.internal;

import com.brindysoft.mud.core.api.MudCommand;


public interface MudCommandRegistry {

	MudCommand find(String string);

}
