package com.yc.res.biz;

import com.yc.res.bean.CartItem;
import com.yc.res.bean.Resorder;
import com.yc.res.dao.DBHelper;
import com.yc.res.utils.HttpRequest;
import sun.net.www.http.HttpClient;

import java.net.HttpCookie;
import java.sql.*;
import java.util.Map;


public class ResorderBizImpl implements ResorderBiz {

	@Override
	public void completeOrder(Resorder resorder, Map<Integer, CartItem> shopCart) throws SQLException {
		DBHelper db=new DBHelper();
		int roid=0;
		Connection con=null;
		//                                                     下订时间是now()   mysql的一个时间函数
		String sql1="insert into resorder( userid,address,tel,ordertime,deliverytime,ps,status) values( ?,?,?,now(),?,?,?)";
		try {
			con=db.getConnection();
			//关闭隐式事务提交:不允许一次提交一条sql语句到数据库执行
			con.setAutoCommit(false);

			//PreparedStatement pstmt=con.prepareStatement(sql1);
			//TODO:    Statement.RETURN_GENERATED_KEYS
			PreparedStatement pstmt=con.prepareStatement( sql1,     Statement.RETURN_GENERATED_KEYS );
			
			pstmt.setString(1, String.valueOf(resorder.getUserid()));
			pstmt.setString(2, String.valueOf(resorder.getAddress()));
			pstmt.setString(3, String.valueOf(resorder.getTel()));
			pstmt.setString(4, String.valueOf(resorder.getDeliverytime()));  //   "2021-07-02"   ->  mysql  date
			pstmt.setString(5, String.valueOf(resorder.getPs()));
			pstmt.setString(6, String.valueOf(resorder.getStatus()));
			pstmt.executeUpdate();

			//  TODO:  自动获取自动生成的   resorder表的  id 值.
			ResultSet rs = pstmt.getGeneratedKeys();

			String s = HttpRequest.sendGet("http://localhost:1234/makeId/getId", "type=NONE");
			String result="\"result\":\"";
			int index1=s.indexOf(result);
			index1+=result.length();
			int index2=s.lastIndexOf("\"");
			String str=s.substring(index1,index2);
			int uniqueid=Integer.parseInt(str);
			roid = uniqueid;

//			sql1="select max( roid ) from resorder";
//			pstmt=con.prepareStatement(sql1);
//			ResultSet rs=pstmt.executeQuery();
//			if( rs.next() ){
//				roid=rs.getInt(1);
//			}


			if( shopCart!=null&& shopCart.size()>0){
				for(   Map.Entry<Integer, CartItem> entry: shopCart.entrySet()){
					//TODO:   roid   订单号
					sql1="insert into resorderitem(roid,fid,dealprice,num) values( ?,?,?,?)";
					pstmt=con.prepareStatement(sql1);
					pstmt.setString(1, String.valueOf( roid ));
					pstmt.setString(2, String.valueOf( entry.getKey()));
					pstmt.setString(3, String.valueOf(entry.getValue().getFood().getRealprice()));
					pstmt.setString(4, String.valueOf(entry.getValue().getNum()));
					pstmt.executeUpdate();
				}
			}
			con.commit();   //提交事务.
		} catch (SQLException e) {
			if( con!=null ){
				con.rollback();   //回滚.
			}
			e.printStackTrace();
			throw e;
		}finally{
			if( con!=null ){
				con.setAutoCommit(true);   //  恢复现场.
				con.close();
			}
		}
	}


}
