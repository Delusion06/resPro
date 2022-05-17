package com.yc.res.biz;

import com.yc.res.bean.CartItem;
import com.yc.res.bean.Resorder;

import java.sql.SQLException;
import java.util.Map;


public interface ResorderBiz {
	
	public void  completeOrder(Resorder resorder, Map<Integer, CartItem> shopCart  ) throws SQLException;
}
