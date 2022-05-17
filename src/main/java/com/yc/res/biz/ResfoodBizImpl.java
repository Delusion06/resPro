package com.yc.res.biz;


import com.yc.res.bean.PageBean;
import com.yc.res.dao.DBHelper;

import java.util.List;
import java.util.Map;

public class ResfoodBizImpl {
    private DBHelper db = new DBHelper();

    public PageBean<Map<String, String>> findResfoodByPage(int pagenum, int pagesize) {

        PageBean<Map<String, String>> pageBean=new PageBean<Map<String, String>>();
        if (pagenum <= 0) {
            pagenum = 1;
        }
        if (pagesize < 1) {
            pagesize = 5;
        }
        int start = (pagenum - 1) * pagesize;
        String sql = "select * from resfood   order by fid desc limit "+ start+","+pagesize;
        List<Map<String, String>> list = db.doselect(sql);

        String sql2="select count(*) as totals from resfood";
        List<Map<String,String>> totalMap=db.doselect(sql2);
        int totals= Integer.parseInt( totalMap.get(0).get("totals") );

        pageBean.setPagenum(pagenum);
        pageBean.setPagesize(pagesize);
        pageBean.setTotals( totals  );
        pageBean.setData(  list  );

        pageBean.getTotalpages();
        pageBean.getPrepage();
        pageBean.getNextpage();


        return pageBean;


    }
}
