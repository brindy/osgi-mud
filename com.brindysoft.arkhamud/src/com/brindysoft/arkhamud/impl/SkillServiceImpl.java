package com.brindysoft.arkhamud.impl;

import com.brindysoft.arkhamud.Skill;
import com.brindysoft.arkhamud.SkillService;

import aQute.bnd.annotation.component.Component;

@Component
public class SkillServiceImpl implements SkillService {

	@Override
	public Skill randomItem() {
		return new Skill() {
		};
	}

}
