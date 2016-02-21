package com.wx.movie.data.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;
import com.wx.movie.data.common.util.JsonMapperUtil;
import com.wx.movie.data.pojo.UserActionData;

public class TestActionDataQLister implements ChannelAwareMessageListener {
  @Autowired
  ZoListenerHelper mqHelper;
  
  private static final Logger logger = LoggerFactory.getLogger(TestActionDataQLister.class);
/**
 * 返回的是一个 UserActionData 对象json字符串
 * 有用户操作行为属性，List<String,List<String>> 用户对那些影片操作过
 */

  @Override
  public void onMessage(Message message, Channel channel) {
    try {
 /*     UserActionData userAction = JsonMapperUtil.getInstance().fromJson(message.getBody(), UserActionData.class);
      logger.info("Rec User Action Data :{}", JsonMapperUtil.getInstance().toJson(userAction));*/
      String userAction = new String(message.getBody(),"UTF-8");
      logger.debug("Rec User Action Data :{}",userAction);
    } catch (Throwable e) {
      logger.error("TestActionDataQLister process message failed.", e);
    } finally {
      mqHelper.sendAck(message, channel);
    }
  }
}
