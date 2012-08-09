package com.brindysoft.mud.core;

import java.io.IOException;

public interface MudAuthenticator {

	MudUser authenticate(MudIo engine) throws IOException;

}
