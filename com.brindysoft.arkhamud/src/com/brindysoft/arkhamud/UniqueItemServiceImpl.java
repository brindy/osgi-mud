package com.brindysoft.arkhamud;

import aQute.bnd.annotation.component.Component;

@Component
public class UniqueItemServiceImpl implements UniqueItemService {

	@Override
	public UniqueItem randomItem() {
		return new UniqueItem() {
		};
	}

}
