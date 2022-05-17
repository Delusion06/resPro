package com.yc.res.web.filters;


import com.google.gson.Gson;
import com.yc.res.bean.JsonModel;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//@WebFilter("/message.action")
public class RightFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {

    }

    protected void writeJson(HttpServletResponse response, JsonModel jm  ) throws IOException {
        Gson g=new Gson();
        String jsonString=g.toJson(    jm );
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out=response.getWriter();
        out.println(    jsonString );
        out.flush();
        out.close();
    }



}
