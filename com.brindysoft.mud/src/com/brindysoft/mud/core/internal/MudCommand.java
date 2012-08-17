package com.brindysoft.mud.core.internal;

import com.brindysoft.mud.core.api.MudUser;

public interface MudCommand {

	boolean invoke(String[] args, MudUser user);

	String[] getVerbs();

}
