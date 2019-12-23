package com.hx.source;

import ch.ethz.ssh2.*;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Preconditions;
import com.hx.util.ConnUtil;
import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 接收状态
 *
 * @author majie
 */
public class AcceptingStateSource extends AbstractSource implements Configurable, PollableSource {
    private static Logger logger = LoggerFactory.getLogger(AcceptingStateSource.class);
    /**
     * 服务器请求地址
     */
    private String requestUrl;

    /**
     * # 请求间隔时间  cron表达式
     */
    private String interval;
    /**
     * 成功接收日志文件接口
     */
    private String fileConfigUrl;

    /**
     * 保存本地文件地址
     */
    private String filePath;


    @Override
    public Status process() throws EventDeliveryException {


        /**==============处理成功接收文件==============*/
        // 结果封装
        HashMap<String, String> resultMap = new HashMap<>();
        // 获取成功接收状态配置文件
        String success = getResult("成功接收日志", fileConfigUrl);
        // 获取失败接收状态配置文件
        String error = getResult("失败接收日志", fileConfigUrl);
        resultMap.put("successNumber", success);
        resultMap.put("errorNumber", error);
        resultMap.put("updateTime",DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        //1.创建flume事件
        SimpleEvent event = new SimpleEvent();
        //2.将事件传给channel
        event.setHeaders(resultMap);
        getChannelProcessor().processEvent(event);
        //关闭Connection
        try {
            Thread.sleep(120000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Status.READY;
    }

    @Override
    public long getBackOffSleepIncrement() {
        return 0;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return 0;
    }

    @Override
    public void configure(Context context) {
        requestUrl = context.getString("requestUrl");
        Preconditions.checkNotNull(requestUrl, "服务器请求地址不能为空");
        interval = context.getString("interval");
        Preconditions.checkNotNull(interval, "数据请求请求频率不能为空");
        fileConfigUrl = context.getString("fileConfigUrl");
        Preconditions.checkNotNull(fileConfigUrl, "日志文件请求地址不能为空");
        filePath = context.getString("filePath");
        Preconditions.checkNotNull(filePath, "本地保存文件路径不能为空");
    }

    @Override
    public synchronized void start() {
        super.start();
        logger.info("采集程序开始");
    }

    /**
     * 获取请求结果
     */
    public String getResult(String params, String url) {
        String s = HttpUtil.get(requestUrl);
        JSONObject json = JSONUtil.parseObj(JSONUtil.parseObj(s).get("data"));
        String hostname = json.get("ip").toString();
        String username = json.get("username").toString();
        String password = json.get("password").toString();
        Integer port = 22;
        if (ObjectUtil.isNotNull(json.get("port"))) {
            port = (Integer) json.get("port");
        }
        // 获取成功接收状态配置文件
        Map<String, Object> map = new HashMap<>();
        map.put("name", params);
        HttpResponse execute = HttpRequest.get(url).form(map).execute();
        String body = execute.body();
        JSONObject jsonObject = JSONUtil.parseObj(body);
        JSONObject result = (JSONObject) jsonObject.get("data");
        String path = result.get("path").toString();
        String time = DateUtil.format(new Date(), "yyyyMMdd");
        path = path.replace("yyyyMMdd", time);
        Connection conn = ConnUtil.getConn(hostname, username, password, port);
        System.out.println("AcceptingState request conn success!!!");
        FileReader fr = null;
        BufferedReader br = null;
        int line = 0;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
            SCPClient client = new SCPClient(conn);

            client.get(path, filePath);
            String fileName = filePath + "/" + path.substring(path.lastIndexOf("/"));
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            while (br.readLine() != null) {
                line++; // 每读一行，则变量x累加1
            }
            if (fr != null) {
                fr.close();
            }
            if (br != null) {
                br.close();
            }
            File file1 = new File(fileName);
            if (file1.exists()) {
                file1.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        String string = Integer.toString(line);
        return string;
    }

}