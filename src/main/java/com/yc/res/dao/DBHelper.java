package com.yc.res.dao;

import com.yc.res.utils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO:将数据库更换成sql server
public class DBHelper {
    //在静态块中jvm一加载就会运行且只运行一次
    static {
        try {
            Myproperties mp = Myproperties.getMyproperties();
            Class.forName(mp.getProperty("driverClass"));
            //第二种方案(二者的区别就在于前面的不管这个OracleDriver类存不存在都可以通过编译，而后者则必须OracleDriver类存在不然就通不过编译)
            //DriverManager.registerDriver(new OracleDriver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        //TODO:系统中用的属性存储方案:1.注册表  2.系统的环境变量  3.properties 文件
        Connection coon = null;
        try {
            Myproperties mp = Myproperties.getMyproperties();
            coon = DriverManager.getConnection(mp.getProperty("url"), mp.getProperty("user"), mp.getProperty("password"));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return coon;
    }
    /*
     * 事务的定义: 多条sql要执行，要不同时成功，要不同时失败
     * 事务是一次数据库操作最小的单元.
     *    ACID:
     *       A: 原子性：多条语句不可分
     *       C: 一致性
     *       I: 隔离性
     *       D: 持久性：存到硬盘上去了
     *  commit
     *  savepoing p1
     *  rollback to xxx
     */

    /**
     * 更新的重载方法   ，   jdbc的更新都隐务事务提交,即不需要手工  commit/rollback.
     *
     * @param sql
     * @return
     */
    public int doupdate(String sql, Object... parms) {
        Connection con = getConnection();
        PreparedStatement pstmt = null;
        int result = -1;
        try {
            pstmt = con.prepareStatement(sql);
            setparms(pstmt, parms);      //调用setprms方法
            result = pstmt.executeUpdate();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            closeAll(con, pstmt);
        }
        return result;
    }
    /*
     * 事务的定义: 多条sql要执行，要不同时成功，要不同时失败
     * 事务是一次数据库操作最小的单元.
     *    ACID:
     *       A: 原子性
     *       C: 一致性
     *       I: 隔离性
     *       D: 持久性
     *  commit
     *  savepoint p1
     *  rollback to xxx
     *
     *  标准jdbc中的更新操作DML是隐式事务处理，即自动提交，所以在多条sql语句为同一事务时，要修改原有的代码，关闭隐式事务 。
     *
     *  所以要使用显式事务处理
     */

    /**
     * 带事务带?的修改，
     *
     * @param sqls 　　　要执行的sql语句集
     * @return　成功返回true
     */
    public void doUpdate(List<String> sqls, List<Object[]> params) {
        Connection con = this.getConnection();
        PreparedStatement pstmt = null;
        try {
            con.setAutoCommit(false); //   ***:  关闭自动提交( 关闭隐式事务 )
            if (sqls != null && sqls.size() > 0) {
                for (int i = 0; i < sqls.size(); i++) {
                    pstmt = con.prepareStatement(sqls.get(i));
                    if (params != null && params.size() > 0 && params.get(i) != null) {
                        this.setparms(pstmt, params.get(i));
                    }
                    pstmt.executeUpdate();
                }
            }
            // 当所有语句执行完后没有出现错误，
            con.commit(); // *** 手动提交修改
        } catch (SQLException e) {
            try {
                con.rollback();   //  ***   自动回滚
            } catch (SQLException e1) {
                e1.printStackTrace();
            } // 说明执行过程中出错，那么就回滚数据
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);    //   *** 恢复现场， 恢复默认值。
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.closeAll(con, pstmt); // 只有查询才有结果集，更新没有结果集
        }

    }

    public void setparms(PreparedStatement pstmt, Object... parms) {
        //预编译语句占位符?
        if (parms != null && parms.length > 0) {
            for (int i = 0; i < parms.length; i++) {
                //TODO:可以扩展成根据param的数据类型来判断调用的setxxx()方法
                try {
                    pstmt.setString(i + 1, parms[i].toString());
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public int doupdate(String sql, List<Object> parms) {
        if (parms == null || parms.size() <= 0) {
            return doupdate(sql, new Object[]{});//边界条件
        }
        return doupdate(sql, parms.toArray());
    }

    public <T> List<T> doselect(Class<T> cls, String sql, Object... params) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Map<String, String>> list = doselect(sql, params);
        if (list == null || list.size() <= 0) {
            return null;
        }
        List<T> result = new ArrayList<T>();
        for (Map<String, String> map : list) {
            T t = BeanUtils.parseMapToObject(map, cls);
            result.add(t);
        }
        return result;
    }


    public List<Map<String, String>> doselect(String sql, Object... parms) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Connection con = getConnection();
        PreparedStatement p = null;
        ResultSet rs = null;
        try {
            p = con.prepareStatement(sql);
            setparms(p, parms);
            rs = p.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();//通过获取结果集列元素
            int count = rsmd.getColumnCount();
            String[] columnName = new String[count];
            for (int i = 0; i < count; i++) {
                columnName[i] = rsmd.getColumnLabel(i + 1);
            }
            while (rs.next()) {
                Map<String, String> map = new HashMap<String, String>();
                for (String cn : columnName) {
                    map.put(cn, rs.getString(cn));
                }
                list.add(map);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            closeAll(con, p, rs);
        }
        return list;
    }

    public void closeAll(Connection con) {
        closeAll(con, null, null);
    }

    //为什么不一次性捕获异常?因为如果这样做了一旦前面的有一个关闭出现了异常后面的就会直接跳过没有执行他们的关闭代码
    public void closeAll(Connection con, PreparedStatement pstmt) {
        closeAll(con, pstmt, null);
    }

    public void closeAll(Connection con, PreparedStatement pstmt, ResultSet set) {
        if (set != null) {
            try {
                set.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
