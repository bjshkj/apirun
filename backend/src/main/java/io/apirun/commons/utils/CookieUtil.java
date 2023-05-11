package io.apirun.commons.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie工具类
 * @author lixin1-s@360.cn
 *
 */
public class CookieUtil {
	 /**
	  *  根据名称读取cookie
	  */
	public static Object getCookie(HttpServletRequest request,String name) {
		Cookie[] cookie = request.getCookies();
		Object object = null;
		if(cookie!=null && cookie.length>0){
			for (int i = 0; i < cookie.length; i++) {
				if (cookie[i].getName().equals(name) == true) {
					object = cookie[i].getValue();
					break;
				}
			}
		}
		return object;
	}
	 
	 /**
	  * @name       设置cookie名称
	  * @value      设置cookie的值
	  * @cookieTime 设置cookie的存活时间 
	  * @domain     设置cookie的域名
	  * @path       设置cookie
	  */
	public static void setCookie(HttpServletResponse response,String name, String value, int cookieTime,String domain, String path) {
		Cookie _cookie = new Cookie(name, value);
		_cookie.setMaxAge(cookieTime);
		_cookie.setDomain(domain);
		_cookie.setPath(path);
		response.addCookie(_cookie);
	}
	
	/**
	 * 清空所有cookie
	 */
	public static void clearCokie(String domain, String path,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		if(cookies!=null && cookies.length>0){
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = new Cookie(cookies[i].getName(), null);  
	            cookie.setMaxAge(0); 
	            cookie.setPath(path);//根据你创建cookie的路径进行填写
	            cookie.setDomain(domain);
				response.addCookie(cookie);
			}
		}
	}
	
	/**
	 * 清空cookie
	 */
	public static void clearCokie(String name,String domain, String path,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();
		if(cookies!=null && cookies.length>0){
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(name)) {
					Cookie cookie = new Cookie(cookies[i].getName(), null);  
		            cookie.setMaxAge(0); 
		            cookie.setPath(path);//根据你创建cookie的路径进行填写
		            cookie.setDomain(domain);
					response.addCookie(cookie);
					break;
				}
			}
		}
	}
}