package com.yc.res.web.servlets;


import com.yc.res.bean.CartItem;
import com.yc.res.bean.JsonModel;
import com.yc.res.bean.Resorder;
import com.yc.res.bean.Resuser;
import com.yc.res.biz.ResorderBiz;
import com.yc.res.biz.ResorderBizImpl;
import com.yc.res.utils.YcConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/custOp.action")
public class CustOpServlet extends BaseServlet {
    private static final long serialVersionUID = 7884358627459967791L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            if ("confirmOrder".equals(op)) {
                confirmOrderOp(req, resp);
            } else {
                show404Common(req, resp);
            }
        } catch (Exception ex) {
            JsonModel jm = new JsonModel();
            jm.setObj(0);
            jm.setMsg(ex.getMessage());   //给用户看
            ex.printStackTrace();    //给程 序员
        }
    }

    private void confirmOrderOp(HttpServletRequest req, HttpServletResponse resp) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException {
        // 从session中取出  用户id, cart
        HttpSession session = req.getSession();
        JsonModel jm = new JsonModel();
        // address, tel, ps, deliverytime
        Resorder resorder = super.getTFromRequest(Resorder.class, req);
//		Resorder resorder=new Resorder();
//
//		resorder.setAddress(     req.getParameter("address"));
//		resorder.setPs(req.getParameter("ps"));
//		resorder.setTel( req.getParameter("tel"));
//		resorder.setDeliverytime(   req.getParameter("deliverytime"));

        resorder.setStatus(0);

        if (session.getAttribute(YcConstants.LOGINUSER) == null) {
            jm.setCode(0);
            jm.setMsg("user has not been logined....");
            super.writeJson(resp, jm);
            return;
        }
        //查询用户id    从session中取出登录 用户.
        Resuser resuser = (Resuser) session.getAttribute(YcConstants.LOGINUSER);
        resorder.setUserid(resuser.getUserid());

        //准备   Resorderitem数据
        if (session.getAttribute("cart") == null) {
            jm.setCode(0);
            jm.setMsg("you have not buy any thing....");
            super.writeJson(resp, jm);
            return;
        }
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");

        try {
            //调用后台业务层，完成事务处理..
            ResorderBiz rb = new ResorderBizImpl();
            rb.completeOrder(resorder, cart);

            session.removeAttribute("cart");
            //TODO: 付款  -> 调用支付模块
            jm.setCode(1);

            super.writeJson(resp, jm);
        } catch (Exception e) {
            jm.setCode(0);
            jm.setMsg("order failed ,please contact the administrator....QQ:12334455");
            super.writeJson(resp, jm);
        }


    }
}
