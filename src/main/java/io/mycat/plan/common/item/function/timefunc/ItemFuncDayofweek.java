package io.mycat.plan.common.item.function.timefunc;

import java.math.BigInteger;
import java.util.List;

import io.mycat.plan.common.item.Item;
import io.mycat.plan.common.item.function.ItemFunc;
import io.mycat.plan.common.item.function.primary.ItemIntFunc;
import io.mycat.plan.common.time.MySQLTime;
import io.mycat.plan.common.time.MyTime;


public class ItemFuncDayofweek extends ItemIntFunc {

	public ItemFuncDayofweek(List<Item> args) {
		super(args);
	}
	
	@Override
	public final String funcName(){
		return "dayofweek";
	}

	@Override
	public BigInteger valInt() {
		MySQLTime ltime = new MySQLTime();
		if (getArg0Date(ltime, MyTime.TIME_FUZZY_DATE)) {
			return BigInteger.ZERO;
		} else {
			java.util.Calendar cal = ltime.toCalendar();
			return BigInteger.valueOf(cal.get(java.util.Calendar.DAY_OF_WEEK));
		}
	}

	@Override
	public void fixLengthAndDec() {
		maxLength = (1); /* 1..31 */
		maybeNull = true;
	}

	@Override
	public ItemFunc nativeConstruct(List<Item> realArgs) {
		return new ItemFuncDayofweek(realArgs);
	}
}
