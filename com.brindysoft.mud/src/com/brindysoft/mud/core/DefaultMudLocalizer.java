package com.brindysoft.mud.core;

import java.util.Locale;

import aQute.bnd.annotation.component.Component;

@Component
public class DefaultMudLocalizer implements MudLocalizer {

	@Override
	public String lookup(String message, Locale locale) {
		return message;
	}

}
