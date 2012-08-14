package com.brindysoft.mud.core.api;

import java.io.IOException;

public interface MudAuthenticator {

	MudUser authenticate(MudSocketHandler socket) throws IOException;

}
