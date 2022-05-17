package com.yc.res.utils;


import com.yc.res.bean.Resfood;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BeanUtils {
    /**
     * 将一个Map转为Bean
     */
    public static <T> T parseMapToObject(Map<String,String> map,   Class<T> cls     ) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T t=cls.newInstance();
        //1. 到map中找有几个键,
        Set<String> keys= map.keySet();
        Iterator<String> its=keys.iterator();
        //获取cls 所有的方法
        Method[] ms=cls.getDeclaredMethods();
        while(  its.hasNext() ) {
            //2. 循环这些键 ，到cls中找对应的   setXxx方法
            String key=its.next();
            String value=   map.get(key);
            Method setMethod=findSetMethod(ms, key);
            //3.     激活setXxx方法，设置值
            //没有这个set方法,则循环下一个属性
            if(   setMethod==null ){
                continue;
            }
            // 查看一下 setMethod的参数类型( 注意，参数有可能有多个，我们只考虑一个的情况  )
            Class[]   parameterTypeClass=setMethod.getParameterTypes();
            if(   parameterTypeClass==null || parameterTypeClass.length<=0){
                continue;
            }
            //对于标准javabean而言，一个set方法只有一个参数
            Class parameterType=parameterTypeClass[0];
            String parameterTypeName=    parameterType.getName();   //set方法第一个参数的类型名
            if(  "int".equals( parameterTypeName)|| "java.lang.Integer".equals(parameterTypeName)  ){
                setMethod.invoke(  t,  Integer.parseInt(  value ) );
            }else  if(  "double".equals( parameterTypeName)|| "java.lang.Double".equals(parameterTypeName)  ){
                setMethod.invoke(  t,  Double.parseDouble(  value ) );
            }else  if(  "float".equals( parameterTypeName)|| "java.lang.Float".equals(parameterTypeName)  ){
                setMethod.invoke(  t,  Float.parseFloat(  value ) );
            }else  if(  "long".equals( parameterTypeName)|| "java.lang.Long".equals(parameterTypeName)  ){
                setMethod.invoke(  t,  Long.parseLong(  value ) );
            }else  if(  "short".equals( parameterTypeName)|| "java.lang.Short".equals(parameterTypeName)  ){
                setMethod.invoke(  t,  Short.parseShort(  value ) );
            }else  if(  "char".equals( parameterTypeName)|| "java.lang.Character".equals(parameterTypeName)  ){
                setMethod.invoke(  t,    value.substring(0,1)    );
            }else {
                setMethod.invoke(  t,   value);
            }
        }
        return t;
    }

    private static Method findSetMethod(   Method[] ms,  String key  ){
        if(   ms==null || ms.length<=0){
            return null;
        }
        for( Method m:ms){
            //标准javabean的set方法约定
            String methodName=   "set"+    key.substring(0,1).toUpperCase()+  key.substring(1);
            if(    methodName.equals(  m.getName()  )){
                return m;
            }
        }
        return null;
    }

    private static Method findGetMethod(   Method[] ms,  String key  ){
        if(   ms==null || ms.length<=0){
            return null;
        }
        for( Method m:ms){
            //标准javabean的set方法约定
            String methodName=   "get"+    key.substring(0,1).toUpperCase()+  key.substring(1);
            if(    methodName.equals(  m.getName()  )){
                return m;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String,String> map=new HashMap<String,String>();
        map.put("fid","1");
        map.put("fname","肉");
        map.put("realprice","60");
        map.put("normprice","100");

       Resfood rf= BeanUtils.parseMapToObject( map,   Resfood.class  );
       System.out.println(    rf );
    }
}
