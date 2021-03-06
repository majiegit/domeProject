package com.hx.source;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import cn.hutool.core.date.DateUtil;
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


public class SystemResourceSource extends AbstractSource implements Configurable, PollableSource {
    private static Logger logger = LoggerFactory.getLogger(NoticeSource.class);

    /**
     * 服务器请求地址
     */
    private String requestUrl;

    /**
     * # 请求间隔时间  cron表达式
     */
    private String interval;

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
        Session sess = null;
        PrintWriter out = null;
        InputStream stdout = null;
        BufferedReader stdoutReader = null;
        try {
            /**=================处理CPU================*/
            //开启一个Session

            sess = conn.openSession();
            //执行具体命令
            sess.requestPTY("bash");
            sess.startShell();
            //获取返回输出
            stdout = new StreamGobbler(sess.getStdout());
            stdoutReader = new BufferedReader(new InputStreamReader(stdout, "utf8"));
            System.out.println("System Resource request conn success!!!");
            out = new PrintWriter(sess.getStdin());
            // CPU
            out.println("top -n 1 | head -n 3");
            out.println("exit");
            out.close();
            sess.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, 3000);
            StringBuffer stringBuffer = new StringBuffer();
            while (true) {
                String line = stdoutReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.matches("Cpu.*")) {
                    stringBuffer.append(line);
                }
            }
            // CPU 使用率
            ArrayList<String> cpuUsAndSy = getCpuUsAndSy(stringBuffer.toString());
            float cpuRate = 0.0f;
            for (String string : cpuUsAndSy) {
                String regx = "(\\d+(\\.\\d+)?)";
                Pattern patternTwo = Pattern.compile(regx);
                Matcher matcher = patternTwo.matcher(string);
                if (matcher.find()) {
                    String group = matcher.group();
                    cpuRate += Float.parseFloat(group);
                }
            }
            String cpuRateResult = cpuRate + "%";
            sess.close();
            /**=================处理内存================*/
            //开启一个Session
            sess = conn.openSession();
            //执行具体命令
            sess.requestPTY("bash");
            sess.startShell();
            //获取返回输出
            stdout = new StreamGobbler(sess.getStdout());
            stdoutReader = new BufferedReader(new InputStreamReader(stdout, "utf8"));
            out = new PrintWriter(sess.getStdin());
            // 内存
            out.println("free -t | head -2");
            out.println("exit");
            out.close();
            sess.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, 3000);
            List<String> list = new ArrayList<>();
            while (true) {
                String line = stdoutReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.indexOf("Mem") != -1) {
                    String[] split = line.split(" ");
                    for (String string : split) {
                        if (!string.equals("")) {
                            list.add(string);
                        }
                    }
                }
            }
            // 总内存
            int total = Integer.parseInt(list.get(1));
            // 使用内存
            float used = Float.parseFloat(list.get(2));
            float i = (used / total) * 100;
            int round = Math.round(i);
            // 内存使用率
            String memoryResult = round + "%";
            sess.close();
            /**=================处理分区百分比================*/
            //开启一个Session
            sess = conn.openSession();
            //执行具体命令
            sess.requestPTY("bash");
            sess.startShell();
            //获取返回输出
            stdout = new StreamGobbler(sess.getStdout());
            stdoutReader = new BufferedReader(new InputStreamReader(stdout, "utf8"));
            out = new PrintWriter(sess.getStdin());
            // 分区百分比
            out.println(" df -h");
            out.println("exit");
            out.close();
            sess.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, 3000);
            ArrayList<ArrayList<String>> lists = new ArrayList<>();
            while (true) {
                String line = stdoutReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.indexOf("/") != -1) {
                    ArrayList<String> dflist = new ArrayList<>();
                    String[] split = line.split(" ");
                    for (String string : split) {
                        if (!string.equals("")) {
                            dflist.add(string);
                        }
                    }
                    lists.add(dflist);
                }
            }
            Map<String, String> map = new LinkedHashMap<>();
            map.put("cpuPate", cpuRateResult);
            map.put("memory", memoryResult);
            for (ArrayList<String> dflist : lists) {
                String name = dflist.get(dflist.size() - 1);
                String value = dflist.get(dflist.size() - 2);
                map.put(name, value);
            }
            map.put("updateTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            sess.close();

            //1.创建flume事件
            SimpleEvent event = new SimpleEvent();
            //2.将事件传给channel
            event.setHeaders(map);
            getChannelProcessor().processEvent(event);
            Thread.sleep(120000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭Connection
            conn.close();
            try {
                stdout.close();
                stdoutReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    @Override
    public void configure(Context context) {
        requestUrl = context.getString("requestUrl");
        Preconditions.checkNotNull(requestUrl, "主机请求地址不能为空");
        interval = context.getString("interval");
        Preconditions.checkNotNull(interval, "数据请求请求频率不能为空 ");
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
    public synchronized void start() {
        super.start();
        logger.info("采集程序开始");
    }

    public ArrayList<String> getCpuUsAndSy(String cpuLine) {
        ArrayList<String> usAndSyCpuList = new ArrayList<>();
        String[] split = cpuLine.split(",");
        for (String string : split) {
            if (string.indexOf("us") != -1) {
                usAndSyCpuList.add(string);
            }
        }
        return usAndSyCpuList;
    }
}