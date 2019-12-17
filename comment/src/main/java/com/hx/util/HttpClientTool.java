package com.hx.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientTool {
	static Logger logger = LoggerFactory.getLogger(HttpClientTool.class);
	
    public static void sendData(String contents, String sendUrl) throws Exception{
		logger.info("开始发送数据.....");
		logger.info(contents);
		logger.info("发送数据字节数(b)："+ contents.getBytes("UTF-8").length +"");
		logger.info(sendUrl);
		HttpURLConnection connection = null;
		DataOutputStream out = null;
		try {
			 URL conUrl = new URL(sendUrl);  
			 connection = (HttpURLConnection) conUrl.openConnection();  
	         // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在   
	         // http正文内，因此需要设为true, 默认情况下是false;
	         connection.setDoOutput(true);
	         // 设置是否从httpUrlConnection读入，默认情况下是true;
	         connection.setDoInput(true);  
	         //POST请求
	         connection.setRequestMethod("POST");
	         // Post 请求不能使用缓存
	         connection.setUseCaches(false);
	         //设置本次连接是否自动处理重定向
	         connection.setInstanceFollowRedirects(true);
	         //设置30秒的连接超时
	         connection.setConnectTimeout(30000);
	         //设置30秒读取超时
	         connection.setReadTimeout(30000);
	         connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
	         connection.connect();
	         
	         out = new DataOutputStream(connection.getOutputStream()); 
	         //提交数据
	         out.write(contents.getBytes("UTF-8"));
	         out.flush(); 
	         out.close();
	         //判断是否访问成功
	         if(connection.getResponseCode() != 200){
	        	 logger.error("提交数据失败; 接口返回码："+ connection.getResponseCode());
	         }
	         logger.info("数据提交成功。。。返回码为："+ connection.getResponseCode());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}finally {
			if(connection != null){
				connection.disconnect();
			}
			if(out != null){
				out.flush(); 
				out.close();
			}
		}
	}
    
    public static void main(String[] args) {
		String url = "http://10.20.69.144:80/store/openapi/v2/logs/push_batch?apikey=e10adc3949ba59abbe56e057f2gg88dd";
		String apiKey="";
		url=url+apiKey;
		JSONObject fields = new JSONObject();
		fields.put("channelCapacity", 2000);
		fields.put("channelFillPercentage", 1);
		fields.put("channelSize", 1);
		fields.put("current_time", 1516244553711l);
		fields.put("name", "gaoxingnengLogChannel");
		fields.put("start_time", 1515987374371l);
		fields.put("stop_time", 0);
		fields.put("type", "channel");
		JSONObject result = new JSONObject();
		result.put("message", "FLUME自监控数据");
		result.put("name", "FLUME自监控数据");
		result.put("type", "ZIJIANKONG.DATA");
		result.put("fields", fields);
		JSONArray array = new JSONArray();
		array.add(result);
		try {
			sendData(array.toJSONString(),url);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}

