package com.yc.res.bean;

import java.io.Serializable;
import java.util.List;

//处理分页结果
public class PageBean<T> implements Serializable {
    private List<T> data;

    private Integer pagenum;   //当前第几页

    private Integer totals;  //总记录数
    private Integer pagesize;  //每页多少条

    private Integer totalpages; // 总页数             setTotalpages    getTotalpages()
    private Integer prepage;  //前一页的页号       //   setPrepage()      getPrepage();   javabean
    private Integer nextpage; //后一页的页号;      //   setNextpage()     getNextpage()

    public void setTotalpages(Integer totalpages) {
        this.totalpages = totalpages;
    }

    public void setPrepage(Integer prepage) {
        this.prepage = prepage;
    }

    public void setNextpage(Integer nextpage) {
        this.nextpage = nextpage;
    }

    public Integer getNextpage(){
        getTotalpages();
        if(  pagenum>=totalpages  ){
            nextpage=totalpages;
        }else{
            nextpage=pagenum+1;
        }
        return nextpage;
    }

    //下一页
    public Integer getPrepage(){
        if(  pagenum<=1){
            prepage=1;
        }else{
            prepage=pagenum-1;
        }
        return prepage;
    }


    //标准javabean的封装的方法  get
    //业务规则
    public Integer getTotalpages(){  // gson
        if(   pagesize==0  ){
            pagesize=5;
        }
        if(   totals%pagesize==0){
            totalpages= totals/pagesize;
        }else{
            totalpages= totals/pagesize+1;
        }
        return totalpages;
    }


    public void setTotals(Integer totals) {
        this.totals = totals;
    }

    public Integer getTotals() {
        return totals;
    }

    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setPagenum(Integer pagenum) {
        this.pagenum = pagenum;
    }

    public Integer getPagenum() {
        return pagenum;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
