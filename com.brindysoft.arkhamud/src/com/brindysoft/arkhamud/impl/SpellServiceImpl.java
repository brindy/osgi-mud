package com.brindysoft.arkhamud.impl;

import aQute.bnd.annotation.component.Component;

import com.brindysoft.arkhamud.Spell;
import com.brindysoft.arkhamud.SpellService;

@Component
public class SpellServiceImpl implements SpellService {

	@Override
	public Spell randomItem() {
		return new Spell() {
		};
	}

}
