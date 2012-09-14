package com.brindysoft.arkhamud.impl;

import com.brindysoft.arkhamud.UniqueItem;
import com.brindysoft.arkhamud.UniqueItemService;

import aQute.bnd.annotation.component.Component;

@Component
public class UniqueItemServiceImpl implements UniqueItemService {

	@Override
	public UniqueItem randomItem() {
		return new UniqueItem() {
		};
	}

}
