package io.metersphere.node.util;

import io.metersphere.node.http.QHttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtil {

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36";

    private static Map<String, String> getHeaders(HttpURLConnection conn) {
        Map<String, List<String>> connHeaders = conn.getHeaderFields();
        Map<String, String> result = new HashMap<String, String>();
        if (connHeaders != null) {
            for (Entry<String, List<String>> entry : connHeaders.entrySet()) {
                result.put(entry.getKey(), StringUtils.join(entry.getValue(), "\n"));
            }
        }
        return result;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url     发送请求的URL
     * @param param   请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     *                如果param为空，则只请求改url，不带参数
     *                编码自行处理
     * @param headers http header的map
     * @return QHttpResponse
     */
    public static QHttpResponse get(String url, String param, Map<String, String> headers) {
        BufferedReader in = null;
        StringBuilder builder = new StringBuilder();
        try {
            String urlNameString = url;
            if (param != null) {
                urlNameString += "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", USER_AGENT);
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.connect();
            QHttpResponse response = new QHttpResponse();
            response.setStatusCode(conn.getResponseCode());
            in = new BufferedReader(new InputStreamReader(
                    conn.getResponseCode() == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream(),
                    DEFAULT_CHARSET));
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line).append("\n");
            }

            response.setBody(builder.toString());
            response.setHeaders(getHeaders(conn));
            return response;
        } catch (Exception e) {
            LogUtil.error("HttpUtil.get error.", e);
            return null;
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                LogUtil.error("HttpUtil.get finally error.", e);
            }
        }
    }

    /**
     * 向指定URL发送GET方法的请求(不带参数)
     *
     * @param url
     * @return
     */
    public static QHttpResponse get(String url) {
        return get(url, null, null);
    }

    /**
     * 向指定URL发送GET方法的请求(不带参数，可以设置头信息)
     *
     * @param url
     * @param headers
     * @return
     */
    public static QHttpResponse get(String url, Map<String, String> headers) {
        return get(url, null, headers);
    }

    /**
     * 向指定URL发送GET方法的请求(不可设置头信息)
     *
     * @param url
     * @param param
     * @return
     */
    public static QHttpResponse get(String url, String param) {
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
            LogUtil.error("HttpUtil.getContentLength error.", e);
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url     发送请求的 URL
     * @param param   请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     *                编码自行处理
     * @param headers http header的map
     * @return QHttpResponse
     */
    public static QHttpResponse post(String url, String param, Map<String, String> headers) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder builder = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", USER_AGENT);
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
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
            QHttpResponse response = new QHttpResponse();
            response.setStatusCode(conn.getResponseCode());
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(
                            conn.getResponseCode() == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream(),
                            DEFAULT_CHARSET));
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line).append("\n");
            }
            response.setBody(builder.toString());
            response.setHeaders(getHeaders(conn));
            return response;
        } catch (Exception e) {
            LogUtil.error("HttpUtil.post error.", e);
            return null;
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LogUtil.error("HttpUtil.post finally error.", e);
            }
        }
    }

    /**
     * 向指定 URL 发送POST方法的请求(不可设置头信息)
     *
     * @param url
     * @param param
     * @return
     */
    public static QHttpResponse post(String url, String param) {
        return post(url, param, null);
    }

    /**
     * multiPart Post请求
     *
     * @param url
     * @param params
     * @param filesMap
     * @param headers
     * @return
     */
    public static QHttpResponse multiPartPost(String url, Map<String, Object> params, Map<String, String> filesMap, Map<String, String> headers) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(url);
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        try {
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addPart(key, new StringBody(params.get(key).toString(), ContentType.TEXT_PLAIN));
                }
            }
            if (filesMap != null) {
                for (String file : filesMap.keySet()) {
                    builder.addPart(file, new FileBody(new File(filesMap.get(file)), ContentType.APPLICATION_OCTET_STREAM, new File(filesMap.get(file)).getName()));
                }
            }
            HttpEntity reqEntity = builder.build();
            httpPost.setEntity(reqEntity);
            response = httpClient.execute(httpPost);
            return QHttpResponse.parse(response);
        } catch (Exception e) {
            LogUtil.error("multiPartPost error.", e);
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

    /**
     * multiPart Post请求
     *
     * @param url
     * @param params
     * @param filesMap
     * @param headers
     * @return
     */
    public static QHttpResponse multiPartPostByByte(String url, Map<String, Object> params, Map<String, byte[]> filesMap, Map<String, String> headers) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        HttpPost httpPost = new HttpPost(url);
        if (headers != null) {
            for (Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        try {
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addPart(key, new StringBody(params.get(key).toString(), ContentType.TEXT_PLAIN));
                }
            }
            if (filesMap != null) {
                for (String fileName : filesMap.keySet()) {
                    builder.addPart(fileName, new ByteArrayBody(filesMap.get(fileName), fileName));
                }
            }
            HttpEntity reqEntity = builder.build();
            httpPost.setEntity(reqEntity);
            response = httpClient.execute(httpPost);
            return QHttpResponse.parse(response);
        } catch (Exception e) {
            LogUtil.error("multiPartPost error.", e);
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

    /**
     * multiPart Post请求(不可设置头信息)
     *
     * @param url
     * @param params
     * @param filesMap
     * @return
     */
    public static QHttpResponse multiPartPost(String url, Map<String, Object> params, Map<String, String> filesMap) {
        return multiPartPost(url, params, filesMap, null);
    }

    public static String genFormData(Map<String, Object> data) {
        if (data == null || data.size() == 0) {
            return null;
        }
        StringBuffer result = new StringBuffer();
        for (String key : data.keySet()) {
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
     * Json方式 Post请求
     *
     * @param url
     * @param param
     * @param headers
     * @return
     */
    public static QHttpResponse postJson(String url, String param, Map<String, String> headers) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder builder = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", USER_AGENT);
            conn.setRequestProperty("Content-Type", "application/json");
            if (headers != null) {
                for (Entry<String, String> entry : headers.entrySet()) {
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
            QHttpResponse response = new QHttpResponse();
            response.setStatusCode(conn.getResponseCode());
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(
                            conn.getResponseCode() == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream(),
                            DEFAULT_CHARSET));
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line).append("\n");
            }
            response.setBody(builder.toString());
            response.setHeaders(getHeaders(conn));
            return response;
        } catch (Exception e) {
            LogUtil.error("HttpUtil.post error.", e);
            return null;
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LogUtil.error("HttpUtil.post finally error.", e);
            }
        }
    }

    /**
     * 发起Get请求获取二进制数据
     * @param url
     * @return
     */
    public static byte[] doGetRequestForFile(String url, String param) {

        InputStream is = null;
        ByteArrayOutputStream os = null;
        byte[] buff = new byte[1024];
        int len = 0;
        try {
            String urlNameString = url;
            if (param != null) {
                urlNameString += "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            //URL url = new URL(UriUtils.encodePath(urlStr, DEFAULT_CHARSET));
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("Content-Type", "plain/text;charset=" + DEFAULT_CHARSET);
            conn.setRequestProperty("charset", DEFAULT_CHARSET);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.connect();
            is = conn.getInputStream();
            os = new ByteArrayOutputStream();
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
        } catch (IOException e) {
            LogUtil.error("发起请求出现异常:", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LogUtil.error("【关闭流异常】");
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    LogUtil.error("【关闭流异常】");
                }
            }
        }
        return os.toByteArray();
    }
}
