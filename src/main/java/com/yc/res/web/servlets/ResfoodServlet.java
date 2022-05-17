package com.yc.res.web.servlets;

import com.yc.res.bean.JsonModel;
import com.yc.res.bean.PageBean;
import com.yc.res.bean.Resfood;
import com.yc.res.bean.Resuser;
import com.yc.res.biz.ResfoodBizImpl;
import com.yc.res.dao.Myproperties;
import com.yc.res.utils.YcConstants;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@WebServlet("/resfood.action")
public class ResfoodServlet extends BaseServlet {
    private ResfoodBizImpl rbi = new ResfoodBizImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if ("findResfoodByPage".equals(op)) {
                findResfoodByPageOp(request, response);
            } else if ("recordTrace".equals(op)) {
                recordTrace(request, response);
            } else {
                show404Common(request, response);
            }
        } catch (Exception ex) {
            JsonModel jm = new JsonModel();
            jm.setObj(0);
            jm.setMsg(ex.getMessage());   //给用户看
            ex.printStackTrace();    //给程 序员
        }
    }

    private void recordTrace(HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
        JsonModel jm = new JsonModel();

        HttpSession session = request.getSession();
        if (session.getAttribute(YcConstants.LOGINUSER) == null) {
            jm.setCode(0);
            jm.setMsg("用户没有登录");
            super.writeJson(response, jm);
            return;
        }
        Resuser ru = (Resuser) session.getAttribute(YcConstants.LOGINUSER);
        //方案一:
        // int fid=Integer.parseInt( request.getParameter("fid") );
        //方案二:
        Resfood rf = super.getTFromRequest(Resfood.class, request);
        int fid = rf.getFid();

        Myproperties mp = Myproperties.getMyproperties();
        // Jedis jedis = new Jedis(mp.getProperty("redis_host"), Integer.parseInt(mp.getProperty("redis_port") ));
        //Date d=new Date();
        // jedis.zadd(   ru.getUserid()+"_view",  d.getTime()      ,  fid+""         );
        // jedis.close();

    }

    private void findResfoodByPageOp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int pagenum = 1;
        if (request.getParameter("pagenum") != null) {
            pagenum = Integer.parseInt(request.getParameter("pagenum"));
        }
        int pagesize = 5;
        if (request.getParameter("pagesize") != null) {
            pagesize = Integer.parseInt(request.getParameter("pagesize"));
        }
        PageBean pb = rbi.findResfoodByPage(pagenum, pagesize);
        JsonModel jm = new JsonModel();
        jm.setCode(1);
        jm.setObj(pb);
        super.writeJson(response, jm);
    }

}
