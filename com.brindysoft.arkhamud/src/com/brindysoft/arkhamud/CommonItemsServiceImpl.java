package com.brindysoft.arkhamud;

import aQute.bnd.annotation.component.Component;

@Component
public class CommonItemsServiceImpl implements CommonItemService {

	@Override
	public CommonItem randomItem() {
		return new CommonItem() {
		};
	}

}
