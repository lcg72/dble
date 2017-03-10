package io.mycat.plan.common.item.function.strfunc;

import java.util.List;

import io.mycat.plan.common.item.Item;


public class ItemFuncReplace extends ItemStrFunc {

	public ItemFuncReplace(List<Item> args) {
		super(args);
	}

	@Override
	public final String funcName() {
		return "replace";
	}

	@Override
	public String valStr() {
		String old = args.get(0).valStr();
		String from = args.get(1).valStr();
		String to = args.get(2).valStr();
		if (args.get(0).isNull() || args.get(1).isNull() || args.get(2).isNull()) {
			this.nullValue = true;
			return EMPTY;
		}
		return old.replace(from, to);
	}
}
