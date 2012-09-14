package com.brindysoft.arkhamud.impl;

import com.brindysoft.arkhamud.CommonItem;
import com.brindysoft.arkhamud.CommonItemService;

import aQute.bnd.annotation.component.Component;

@Component
public class CommonItemsServiceImpl implements CommonItemService {

	@Override
	public CommonItem randomItem() {
		return new CommonItem() {
		};
	}

}
