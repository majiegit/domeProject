package com.hx.sink;


import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Preconditions;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AcceptingStateSink extends AbstractSink implements Configurable {
    private static Logger logger = LoggerFactory.getLogger(AcceptingStateSink.class);

    private String url;

    @Override
    public Status process() throws EventDeliveryException {
        Status status = null;
        //1. 获取channel
        Channel channel = getChannel();
        //2. 获取
        Transaction transaction = channel.getTransaction();
        transaction.begin();
        try {
            Event event = channel.take();
            // 处理事件
            if (event != null) {
                Map<String, String> headers = event.getHeaders();
                JSON parse = JSONUtil.parse(headers);
                HttpRequest body = HttpRequest.post(url).body(parse);
                body.execute().body();
                System.out.println(" accepting State 成功推送！");
            } else {
                logger.info("even 为空 ！");
            }
            transaction.commit();
            status = Status.READY;
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
            status = Status.BACKOFF;
        } finally {
            if (transaction != null) {
                transaction.close();
            }

        }
        return status;
    }

    @Override
    public void configure(Context context) {
        url = context.getString("url");
        Preconditions.checkNotNull(url, "sink入库接口url不能为空");
    }
}