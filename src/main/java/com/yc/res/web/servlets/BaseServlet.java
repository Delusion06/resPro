package com.yc.res.web.servlets;

import com.google.gson.Gson;
import com.yc.res.bean.JsonModel;
import com.yc.res.utils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用servlet处理类
 */
public abstract class BaseServlet extends HttpServlet {
    protected String op;


    protected <T> T getTFromRequest(  Class<T> cls,  HttpServletRequest request     ) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        //取出request中所有的键 值对.
        Map<String, String[]> map= request.getParameterMap();
        //为了简化操作，将Map<String,String[]>   转为Map<String,String>
        Map<String,String> result=new HashMap<String,String>();
        for(  Map.Entry<String,String[]> entry:   map.entrySet()  ){
            String key=entry.getKey();
            String[] values=entry.getValue();
            if(  values==null||values.length<=0){
                continue;
            }
            result.put(   key,   values[0] );
        }
       T t= BeanUtils.parseMapToObject(   result, cls    );
        return t;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        op=req.getParameter("op");
        if(  op==null||"".equalsIgnoreCase(op) ){
            show404Common(   req, resp);
            return;
        }
        super.service( req,resp);    //  判断 req中的method是   get还是post,  -> doGet()   /doPost()
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(  request,response);
    }

    protected void show404Common(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out=response.getWriter();
        out.println("<html>");
        out.println("<body>");
        out.println("404<br /><hr />查无此种操作...");
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    protected void writeJson(  HttpServletResponse response, JsonModel jm  ) throws IOException {
        Gson g=new Gson();
        String jsonString=g.toJson(    jm );
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out=response.getWriter();
        out.println(    jsonString );
        out.flush();
        out.close();
    }
}
