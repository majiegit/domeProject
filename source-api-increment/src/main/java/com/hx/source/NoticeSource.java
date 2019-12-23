package com.hx.source;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import cn.hutool.core.util.ObjectUtil;
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
 * 通知source
 *
 * @author majie
 */
public class NoticeSource extends AbstractSource implements Configurable, PollableSource {
    private static Logger logger = LoggerFactory.getLogger(NoticeSource.class);

    /**
     * 服务器请求地址
     */
    private String requestUrl;
    /**
     * # 请求间隔时间  cron表达式
     */
    private String interval;
    /**
     * 中文字符匹配正则
     */
    private Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
    /**
     * 多个换行符匹配，替换成一个
     */
    private Pattern patternTwo = Pattern.compile("(\r?\n(\\s*\r?\n)+)");

    @Override
    public Status process() throws EventDeliveryException {
        String s = HttpUtil.get(requestUrl);
        JSONObject jsonObject = JSONUtil.parseObj(JSONUtil.parseObj(s).get("data"));
        String hostname = jsonObject.get("ip").toString();
        String username = jsonObject.get("username").toString();
        String password = jsonObject.get("password").toString();
        Integer port = 22;
        if (ObjectUtil.isNotNull(jsonObject.get("port"))) {
            port = (Integer) jsonObject.get("port");
        }
        Connection conn = ConnUtil.getConn(hostname, username, password, port);
        try {
            //开启一个Session
            Session sess = conn.openSession();
            //执行具体命令
            sess.requestPTY("bash");
            sess.startShell();
            //获取返回输出
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader stdoutReader = new BufferedReader(
                    new InputStreamReader(stdout));

            System.out.println("request conn success!!!");

            PrintWriter out = new PrintWriter(sess.getStdin());
            //进入通知文件存放的目录
            out.println("cd /bcuq/workdir/incoming/cmacast/resourcefile/notice");
            out.println("ls -lRt |grep -v ^d|awk '{print $9}'");
            out.println("exit");
            out.close();
            sess.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, 3000);
            ArrayList<String> fileNameList = new ArrayList<>();
            while (true) {
                String line = stdoutReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.matches("notice_.*.txt")) {
                    fileNameList.add(line);
                }
            }
            ArrayList<String> resultList = getResult(fileNameList,hostname,username,password,port);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("noticeList", resultList);
            String noticeList = JSONUtil.toJsonStr(map);
            HashMap<String, String> resultMap = new HashMap<>();
            resultMap.put("result", noticeList);
            System.out.println("noticeList: " + noticeList);
            //1.创建flume事件
            SimpleEvent event = new SimpleEvent();
            //2.将事件传给channel
            event.setHeaders(resultMap);
            getChannelProcessor().processEvent(event);
            //关闭Session
            sess.close();
            //关闭Connection
            conn.close();
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
    }

    @Override
    public synchronized void start() {
        super.start();
        logger.info("采集程序开始");
    }

    public ArrayList<String> getResult(List<String> fileNameList,String hostname,String username,String password ,Integer port) {
        // 保存结果
        ArrayList<String> resultList = new ArrayList<>();
        Connection conn = ConnUtil.getConn(hostname, username, password, port);
        try {
            for (String fileName : fileNameList) {
                //开启一个Session
                Session sess = conn.openSession();
                //执行具体命令
                sess.requestPTY("bash");
                sess.startShell();
                //获取返回输出
                InputStream stdout = new StreamGobbler(sess.getStdout());
                BufferedReader stdoutReader = new BufferedReader(
                        new InputStreamReader(stdout, "utf8"));
                PrintWriter out = new PrintWriter(sess.getStdin());
                //进入通知文件存放的目录
                out.println("cd /bcuq/workdir/incoming/cmacast/resourcefile/notice");
                out.println("cat " + fileName);
                out.println("exit");
                out.close();
                sess.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, 3000);
                StringBuffer stringBuffer = new StringBuffer();
                while (true) {
                    String line = stdoutReader.readLine();
                    if (line == null) {
                        break;
                    }
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        stringBuffer.append(line);
                    }
                    if (line.equals("")) {
                        stringBuffer.append("\r\n");
                    }
                }

                // 多个换行处理成一个换行
                Matcher matcher = patternTwo.matcher(stringBuffer.toString());
                String result = matcher.replaceAll("\r\n");
                if (!result.equals("\r\n")) {
                    resultList.add(result);
                }
                sess.close();
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
