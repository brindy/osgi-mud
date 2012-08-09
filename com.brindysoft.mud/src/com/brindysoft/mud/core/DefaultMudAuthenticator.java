package com.brindysoft.mud.core;

import java.io.IOException;

import aQute.bnd.annotation.component.Component;

@Component
public class DefaultMudAuthenticator implements MudAuthenticator {

	@Override
	public MudUser authenticate(MudIo engine) throws IOException {
		engine.print("Enter your desired username: ");
		engine.readLine();
		return null;
	}

}
