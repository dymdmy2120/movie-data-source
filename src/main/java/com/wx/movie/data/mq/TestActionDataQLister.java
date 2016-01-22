package com.wx.movie.data.mq;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import com.wx.movie.data.common.util.JsonMapper;

public class TestActionDataQLister implements ChannelAwareMessageListener {
  @Autowired
  ZoListenerHelper mqHelper;
  
  private static final Logger logger = LoggerFactory.getLogger(TestActionDataQLister.class);

  private TypeReference<Map<String, List<String>>> tr;

  @PostConstruct
  public void init() {
    tr = new TypeReference<Map<String, List<String>>>() {};
  }

  @Override
  public void onMessage(Message message, Channel channel) {
    try {
      Map<String, List<String>> map = JsonMapper.getInstance().fromJson(message.getBody(), tr);
      logger.info("Rec User Action Data :{}", JsonMapper.getInstance().toJson(map));
    } catch (Throwable e) {
      logger.error("DrawLotteryQListener process message failed.", e);
    } finally {
      mqHelper.sendAck(message, channel);
    }
  }
}
