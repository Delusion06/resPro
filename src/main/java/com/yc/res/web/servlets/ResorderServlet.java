package com.yc.res.web.servlets;


import com.yc.res.bean.CartItem;
import com.yc.res.bean.JsonModel;
import com.yc.res.bean.Resfood;
import com.yc.res.dao.DBHelper;
import com.yc.res.utils.BeanUtils;
import com.yc.res.utils.YcConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@WebServlet("/resorder.action")
public class ResorderServlet extends BaseServlet {

    private static final long serialVersionUID = -977974500768761840L;

    private DBHelper db=new DBHelper();  // controller层 ->   (biz层)   ->   dao层


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            if ("order".equals(op)) {
                orderOp(request, response);
            }else if("clearAll".equals(op)){
                clearAllOp(  request, response);
            } else if ("getCartInfo".equals(op)) {
                getCartInfoOp(request, response);
            }else {
                show404Common(request, response);
            }
        }catch( Exception ex){
            JsonModel jm =new JsonModel();
            jm.setObj(0);
            jm.setMsg(    ex.getMessage() );   //给用户看
            ex.printStackTrace();    //给程 序员
        }

    }

    private void getCartInfoOp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session=req.getSession();
        JsonModel jm = new JsonModel();
        Map<Integer, CartItem> cart = new HashMap<Integer, CartItem>();
        if (session.getAttribute( "cart") != null) {
            jm.setCode(1);
            cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        } else {
            jm.setCode(0);
        }
        jm.setObj(    cart.values()    );
        super.writeJson( resp,   jm );
    }

    private void clearAllOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        JsonModel jm = new JsonModel();
        session.removeAttribute("cart");
        jm.setCode(1);
        super.writeJson( response,jm);
    }


    //用户是否登录
    // 1. 取出num, fid
    // 2. 根据fid查到要购买的菜
    // 3. 存到购物车(
    // a.如果购物车中有，则加数量
    // b.如果没有，存到购物车
    // )
    // 4. 将结果( 购物车数据以json回传前台  )   **
    private void orderOp(HttpServletRequest request, HttpServletResponse response) throws IOException, IllegalAccessException, InvocationTargetException, InstantiationException {
        JsonModel jm=new JsonModel();
        //用户是否登录
        HttpSession session=request.getSession();
        if( session.getAttribute(YcConstants.LOGINUSER)==null){
            jm.setCode(-1);
            super.writeJson(response,jm);
            return;
        }
        //TODO:   ...
        int fid = Integer.parseInt(request.getParameter("fid"));
        int num = Integer.parseInt(request.getParameter("num"));

        //TODO:
        List<Map<String,String>> list= db.doselect("select * from resfood where fid=?", fid);
        if(   list==null|| list.size()<=0){
            jm.setCode(0);
            jm.setMsg("查无此商品");
            super.writeJson(  response, jm);
            return;
        }
        Map<String,String> resfood=list.get(0);

        // 购物车跟用户相关，所以存 session
        //  商品编号, 购物项
        Map<Integer, CartItem> cart = null;
        if (session.getAttribute("cart") != null) {
            cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        } else {
            cart = new HashMap<Integer, CartItem>();
        }
        // 看这个购物车是否有 fid
        CartItem ci = null;
        if (cart.containsKey(fid)) {
            // 证明用户已经购买了这个菜，则数量增加
            ci = cart.get(fid);
            int newnum = ci.getNum() + num;
            ci.setNum(newnum);
        } else {
            // 还没有买过, 则创建 一个cartItem存到map中
            ci = new CartItem();

            // Map  ->   Resfood
           // Resfood food=new Resfood(    );
            //food.setFid(    Integer.parseInt( resfood.get("fid") ) );
           // food.setRealprice(  Double.parseDouble(  resfood.get("realprice")));
            //  ... 4条
            Resfood food= BeanUtils.parseMapToObject(     resfood,   Resfood.class   );

            ci.setFood(food);
            ci.setNum(num);
        }
        if (ci.getNum() <= 0) {
            cart.remove(fid);
        } else {
            ci.getSmallCount(); // 计算小计.
            // 将cartitem存到map中
            cart.put(fid, ci);
        }
        // 将cart存到 session中
        session.setAttribute("cart", cart);

        jm.setCode(1);

        jm.setObj(     cart.values()    );
        super.writeJson( response, jm);
    }



}
