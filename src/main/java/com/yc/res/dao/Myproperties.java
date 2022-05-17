package com.yc.res.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Myproperties extends Properties {
	private static Myproperties mypro=null;  //对外提供的实例

	/**
	 * 单例的核心就是　　　构造方法私有化
	 */
	private Myproperties(){
		//比db.properties中读取所有的配置，
		//通过类的反射实例，获取classpath类路径下的资源文件db.properties，并建立一个流
		InputStream iis=this.getClass().getClassLoader().getResourceAsStream("db.properties");
		try {
			this.load(iis);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(iis!=null) {
				try {
					iis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 对外提供一个获取唯一实例的方法
	 * @return
	 */
	//TODO:这里只考虑了单线程场景，没有考虑多线程  double check   (synchronized, ...)
	public static Myproperties getMyproperties(){
		if(   mypro==null){
			mypro=new Myproperties();
		}
		return mypro;
	}
}
