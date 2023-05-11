package io.apirun.common.util;

import io.apirun.common.exception.HttpRequestException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;

public class HttpUtil {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(HttpUtil.class);
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String USER_AGENT = 
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36";
	
	
    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     *            如果param为空，则只请求改url，不带参数
     *            编码自行处理
     * @param headers
     * 			  http header的map
     * @return URL 所代表远程资源的响应结果
     */
    public static String get(String url, String param, Map<String, String> headers) {
        return get(url, param, headers, -1, -1);
    }

	/**
	 *
	 * @param url
	 * @param param
	 * @param headers
	 * @param connectTimeout  milliseconds
	 * @param readTimeout  milliseconds
	 * @return
	 */
	public static String get(String url, String param, Map<String, String> headers, int connectTimeout, int readTimeout) {
		BufferedReader in = null;
		StringBuilder builder = new StringBuilder();
		try {
			String urlNameString = url;
			if (param != null) {
				urlNameString += "?" + param;
			}
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", USER_AGENT);

			if (connectTimeout > 0) {
				conn.setConnectTimeout(connectTimeout);
			}
			if (readTimeout > 0) {
				conn.setReadTimeout(readTimeout);
			}
			if (headers != null) {
				for (Entry<String, String> entry: headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// 定义 BufferedReader输入流来读取URL的响应
				in = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), DEFAULT_CHARSET));
				String line;
				while ((line = in.readLine()) != null) {
					builder.append(line).append("\n");
				}
			}
		} catch (Exception e) {
			LOGGER.error("HttpUtil.get error.", e);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				LOGGER.error("HttpUtil.get finally error.", e2);
			}
		}
		return builder.toString();
	}



	public static String getWithParamMap(String url, Map<String, Object> param, Map<String, String> headers) {
		BufferedReader in = null;
		StringBuilder builder = new StringBuilder();
		try {
			String paramStr = "";
			if(!param.isEmpty()) {
				List<String> list = new ArrayList<String>();
				for(String key : param.keySet()) {
					list.add(String.format("%s=%s", key, param.get(key)));
				}
				if(!list.isEmpty()) {
					paramStr = StringUtils.join(list, "&");
				}
			}
			String urlNameString = url;
			if (StringUtils.isNotEmpty(paramStr)) {
				urlNameString += "?" + paramStr;
			}
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", USER_AGENT);
			if (headers != null) {
				for (Entry<String, String> entry: headers.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// 定义 BufferedReader输入流来读取URL的响应
				in = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), DEFAULT_CHARSET));
				String line;
				while ((line = in.readLine()) != null) {
					builder.append(line).append("\n");
				}
			}
		} catch (Exception e) {
			LOGGER.error("HttpUtil.get error.", e);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				LOGGER.error("HttpUtil.get finally error.", e2);
			}
		}
		return builder.toString();
	}
    /**
     * 向指定URL发送GET方法的请求(不带参数)
     * @param url
     * @return
     */
    public static String get(String url) {
    	return get(url, null, null);
    }
    
    public static String getWithException(String url, String param) {
    	return getWithException(url, param, null);
    }
    
    public static String getWithException(String url, String param, Map<String, String> headers) {
        BufferedReader in = null;
        StringBuilder builder = new StringBuilder();
        try {
            String urlNameString = url;
            if (param != null) {
            	urlNameString += "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", USER_AGENT);
            if (headers != null) {
            	for (Entry<String, String> entry: headers.entrySet()) {
            		conn.setRequestProperty(entry.getKey(), entry.getValue());
            	}
            }
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            // 定义 BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(new InputStreamReader(
	                    conn.getInputStream(), DEFAULT_CHARSET));
	            String line;
	            while ((line = in.readLine()) != null) {
	            	builder.append(line).append("\n");
	            }
            } else {
            	throw new HttpRequestException(String.format("get request fail, responseCode : %s, message : %s",
            			conn.getResponseCode(), conn.getResponseMessage()));
            }
        } catch (IOException e) {
        	throw new HttpRequestException("get request fail", e);
		}
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
            	LOGGER.error("HttpUtil.get finally error.", e2);
            }
        }
        return builder.toString();
    }
    
    /**
     * 向指定URL发送GET方法的请求(不带参数，可以设置头信息)
     * @param url
     * @param headers
     * @return
     */
    public static String get(String url, Map<String, String> headers) {
    	return get(url, null, headers);
    }
    
    /**
     * 向指定URL发送GET方法的请求(不可设置头信息)
     * @param url
     * @param param
     * @return
     */
    public static String get(String url, String param) {
    	return get(url, param, null);
    }
    
    /**
     * 获取目标资源的长度,返回值单位：字节
     */
    public static Long getContentLength(String url) {
        Long result = (long) -1;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", USER_AGENT);
            result = (long) connection.getContentLength();
        } catch (Exception e) {
        	LOGGER.error("HttpUtil.getContentLength error.", e);
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     *            编码自行处理
     * @param headers
     * 			  http header的map
     * @return 所代表远程资源的响应结果
     */
    public static String post(String url, String param, Map<String, String> headers) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder builder = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", USER_AGENT);
            conn.setRequestMethod("POST");
            if (headers != null) {
            	for (Entry<String, String> entry: headers.entrySet()) {
            		conn.setRequestProperty(entry.getKey(), entry.getValue());
            	}
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(new BufferedOutputStream(conn.getOutputStream()));

            // 发送请求参数
			LOGGER.info("HttpUtil.post param:{}", param);
            out.write(param);
            // flush输出流的缓冲
            out.flush();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            // 定义BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream(), DEFAULT_CHARSET));
	            String line;
	            while ((line = in.readLine()) != null) {
	            	builder.append(line).append("\n");
	            }
            } else {
            	LOGGER.info("HttpUtil.post conn code:{}", conn.getResponseCode());
			}
        } catch (Exception e) {
        	LOGGER.error("HttpUtil.post error.", e);
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException e2){
            	LOGGER.error("HttpUtil.post finally error.", e2);
            }
        }
        
        int end = builder.length();
        if (end >= "\n".length()) {
        	end = end - "\n".length();
        }
        return builder.substring(0, end);
    }   
    
    public static String postWithException(String url, String param) {
    	return postWithException(url, param, null);
    }

//    public static String postWithFile(String url, File file, )
    
    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     *            编码自行处理
     * @param headers
     * 			  http header的map
     * @return 所代表远程资源的响应结果
     */
    public static String postWithException(String url, String param, Map<String, String> headers) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder builder = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection)realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
			conn.setUseCaches(false);
            conn.setRequestProperty("user-agent", USER_AGENT);
            if (headers != null) {
            	for (Entry<String, String> entry: headers.entrySet()) {
            		conn.setRequestProperty(entry.getKey(), entry.getValue());
            	}
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
	            // 定义BufferedReader输入流来读取URL的响应
	            in = new BufferedReader(
	                    new InputStreamReader(conn.getInputStream(), DEFAULT_CHARSET));
	            String line;
	            while ((line = in.readLine()) != null) {
	            	builder.append(line).append("\n");
	            }
            } else if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
            	throw new HttpRequestException("http post fail, response code : " + responseCode);
            }
        } catch (Exception e) {
        	LOGGER.error("HttpUtil.post error.", e);
        	throw new HttpRequestException("http post error", e);
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException e2){
            	LOGGER.error("HttpUtil.post finally error.", e2);
            }
        }
        return builder.substring(0, builder.length() - "\n".length());
    }   
    
    /**
     * 向指定 URL 发送POST方法的请求(不可设置头信息)
     * @param url
     * @param param
     * @return
     */
    public static String post(String url, String param) {
    	return post(url, param, null);
    }
    
    /**
     * multiPart Post请求
     * @param url
     * @param params
     * @param filesMap
     * @param headers
     * @return
     */
    public static String multiPartPost(String url, Map<String, Object> params, Map<String, String> filesMap, Map<String, String> headers) {
		HttpClient httpclient = new DefaultHttpClient();
		StringBuilder builder = new StringBuilder();
		try {
			HttpPost httppost = new HttpPost(url);
			if (headers != null) {
				for (Entry<String, String> entry: headers.entrySet()) {
					httppost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			MultipartEntity reqEntity = new MultipartEntity();
			if(params != null){
				for(String key : params.keySet()){
					reqEntity.addPart(key, new StringBody(params.get(key).toString(), Charset.forName(DEFAULT_CHARSET)));
				}
			}
			if(filesMap != null){
				for(String file : filesMap.keySet()){
					reqEntity.addPart(file, new FileBody(new File(filesMap.get(file))));
				}
			}
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
//			int statusCode = response.getStatusLine().getStatusCode();
//			if (statusCode == HttpStatus.SC_OK) {
//				System.out.println("服务器正在响应......");
			HttpEntity resEntity = response.getEntity();
			builder.append(EntityUtils.toString(resEntity));
				// System.out.println(EntityUtils.toString(resEntity));//
				// httpclient自带的工具读取返回数据
//			}
		} catch (Exception e) {
			LOGGER.error("multiPartPost error, {}", e);
			throw new RuntimeException(e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return builder.toString();
	}
    
    
    /**
     * multiPart Post请求
     * @param url
     * @param params
     * @param filesMap
     * @param headers
     * @return
     */
    public static String multiPartPostWithException(String url, Map<String, Object> params, Map<String, String> filesMap, Map<String, String> headers) {
		HttpClientBuilder httpclientBuilder = HttpClientBuilder.create();
		StringBuilder builder = new StringBuilder();
		try {
			HttpPost httppost = new HttpPost(url);
			if (headers != null) {
				for (Entry<String, String> entry: headers.entrySet()) {
					httppost.addHeader(entry.getKey(), entry.getValue());
				}
			}
			MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
			if(params != null){
				for(String key : params.keySet()){
					reqEntity.addTextBody(key, params.get(key).toString(), ContentType.DEFAULT_TEXT);
				}
			}
			if(filesMap != null){
				for(String file : filesMap.keySet()){
					reqEntity.addPart(file, new FileBody(new File(filesMap.get(file))));
				}
			}
			httppost.setEntity(reqEntity.build());
			HttpClient httpClient = httpclientBuilder.build();
			HttpResponse response = httpClient.execute(httppost);
			int responseCode = response.getStatusLine().getStatusCode();
			if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
				throw new HttpRequestException("http multiPartPost fail, response code : " + responseCode);
			}
			HttpEntity resEntity = response.getEntity();
			builder.append(EntityUtils.toString(resEntity));
		} catch (Exception e) {
			LOGGER.error("multiPartPost error, {}", e);
			throw new HttpRequestException("http multiPartPost error", e);
		} finally {
			httpclientBuilder = null;
		}
		return builder.toString();
	}
    
    /**
     * multiPart Post请求(不可设置头信息)
     * @param url
     * @param params
     * @param filesMap
     * @return
     */
    public static String multiPartPost(String url, Map<String, Object> params, Map<String, String> filesMap) {
    	return multiPartPost(url, params, filesMap, null);
    }
    
    public static String genFormData(Map<String, Object> data){
    	if(data == null || data.size() == 0){
    		return null;
    	}
    	StringBuffer result = new StringBuffer();
    	for(String key : data.keySet()){
    		try {
				result.append(String.format("%s=%s&", key, URLEncoder.encode(data.get(key).toString(), DEFAULT_CHARSET)));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	result.delete(result.length() - 1, result.length());
    	return result.toString();
    }
    
    /**
     * 用于purge varnish的cache 
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static String purge(String url, Map<String, String> params, Map<String, String> headers) {
    	if (params != null && params.size() > 0) {
    		Set<String> keys = params.keySet();
    		StringBuilder urlBuilder = new StringBuilder(url + "?");
    		for (String key : keys) {
    			urlBuilder.append(key).append("=").append(params.get(key)).append(
    					"&");
    		}
    		urlBuilder.delete(urlBuilder.length() - 1, urlBuilder.length());
    		url = urlBuilder.toString();
    	}
    	CloseableHttpClient httpClient = HttpClients.createDefault();
    	CloseableHttpResponse response = null;
    	try {
    		HttpUriRequest request = RequestBuilder.create("PURGE").setUri(url).build();
        	if (headers != null) {
        		for (Entry<String, String> entry: headers.entrySet()) {
        			request.setHeader(entry.getKey(), entry.getValue());
    			}
        	}
    		response = httpClient.execute(request);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				return EntityUtils.toString(resEntity);
			} else {
				return null;
			}
		} catch (Exception e) {
			LOGGER.error("purge error.", e);
			return null;
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
				}
			}
			try {
				httpClient.close();
			} catch (IOException e) {
			}
		}
    }
    
    public static String purge(String url) {
    	return purge(url, null, null);
    }

}
