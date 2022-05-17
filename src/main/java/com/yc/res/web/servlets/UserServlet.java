package com.yc.res.web.servlets;


import com.yc.res.bean.JsonModel;
import com.yc.res.bean.Resuser;
import com.yc.res.dao.DBHelper;
import com.yc.res.utils.Encrypt;
import com.yc.res.utils.YcConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/user.action")
public class UserServlet extends BaseServlet {

    private DBHelper db = new DBHelper();


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //请求分发...
        if ("checkUname".equals(op)) {
            checkUnameOp(request, response);
        } else if ("login".equals(op)) {
            loginOp(request, response);
        } else if ("logout".equals(op)) {
            logoutOp(request, response);
        } else if ("reg".equals(op)) {
            regOp(request, response);
        }else if("checkLogin".equals(op)){
            checkLogin(   request, response);
        } else {
            show404Common(request, response);
        }
    }
    private void logoutOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        session.removeAttribute("resuser");
        JsonModel jm=new JsonModel();
        jm.setCode(1);
        super.writeJson(response, jm );
    }

    private void loginOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jm=new JsonModel();
        String uname = request.getParameter("username");
        String upwd = request.getParameter("pwd");
        upwd= Encrypt.md5(   upwd );
        String valcode=request.getParameter("valcode");
        HttpSession session=request.getSession();
        String validateCode=(String)session.getAttribute("validateCode");
        if(   !validateCode.equals(    valcode)   ){
            jm.setCode(0);
            jm.setObj("验证码不一致,请确认后重新登录....");
            super.writeJson(   response, jm);
            return;
        }
        List<Map<String,String>> list= db.doselect("select * from resuser where username=? and pwd=?",uname, upwd);
        if(  list!=null&& list.size()>0){
            Map<String, String> map=list.get(0);
            Resuser resuser=new Resuser();
            resuser.setUserid(   Integer.parseInt(  map.get("userid")   ) );
            resuser.setEmail(   map.get("email") );
            resuser.setUsername(   uname );
            session.setAttribute(YcConstants.LOGINUSER, resuser );
            jm.setCode(1);
            jm.setObj(   resuser  );
        }else{
            jm.setCode(0);
        }
        super.writeJson(  response, jm);
    }

    //检查当前客户端是否已经登录
    private void checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        JsonModel jm=new JsonModel();
        if(  session.getAttribute("resuser")==null){
            jm.setCode(0);
            jm.setMsg("用户暂未登录....");
        }else{
            Resuser resuser=(Resuser)session.getAttribute("resuser");
            jm.setCode(1);
            jm.setObj(    resuser   );
        }
        super.writeJson(   response, jm);
    }

    private void regOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //接参数
        String uname = request.getParameter("uname");
        String upwd = request.getParameter("upwd");
        upwd= Encrypt.md5(upwd);
        String sql="insert into testuser(uname, upwd) values(  ?,?   )";
        JsonModel jm=new JsonModel();
        int result=db.doupdate(sql, uname, upwd);
        if( result>0){
            jm.setCode(1);
        }else{
           jm.setCode(0);
        }
        super.writeJson(response,jm);
    }



    private void checkUnameOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // System.out.println("aaa");
        String uname = request.getParameter("uname");
        String sql = "select * from testuser where uname=?";
        List<Map<String, String>> list = db.doselect(sql, uname);
        JsonModel jm=new JsonModel();
        if(  list!=null&&list.size()>0) {
            jm.setCode( 1 );
        }else{
            jm.setCode(0);
        }
        super.writeJson(    response, jm);
    }


}
