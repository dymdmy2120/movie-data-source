package com.wx.movie.data.mq;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

@Service
public class ZoListenerHelper {

  private static final Logger logger = LoggerFactory.getLogger(ZoListenerHelper.class);

  @Autowired
  private ConnectionFactory factory;
  
  @PostConstruct
  public void init() {  
    try {
      logger.info("Asking rabbitMQ to resend unacknowledged messages.");
      Connection connection = factory.createConnection();
      Channel channel = connection.createChannel(false);
      channel.basicRecover();
      channel.close();
      connection.close();
      logger.info("Asked rabbitMQ to resend unacknowledged messages.");
    } catch (Throwable e) {
      logger.error("Ask rabbitMQ to resend unacknowledged messages failed", e);
    }

  }

  public void sendAck(Message message, Channel channel){
    try {
      channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    } catch (Throwable e) {
      logger.error("MQ listener send ACK error", e);
    }
  }
}
