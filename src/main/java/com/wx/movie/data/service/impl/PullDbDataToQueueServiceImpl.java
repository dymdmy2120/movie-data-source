/**
 * Project Name:movie-data-source File Name:PullDataToCacheServiceImpl.java Package
 * Name:com.wx.movie.data.service.impl Date:2016年1月26日下午10:39:14
 *
 */

package com.wx.movie.data.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.wx.movie.data.common.enums.RabbitMqName;
import com.wx.movie.data.common.enums.UserOperate;
import com.wx.movie.data.dao.entity.user.UserAttention;
import com.wx.movie.data.dao.entity.user.UserComment;
import com.wx.movie.data.dao.mapper.user.UserAttentionMapper;
import com.wx.movie.data.dao.mapper.user.UserCommentMapper;
import com.wx.movie.data.pojo.UserActionData;
import com.wx.movie.data.service.CommonService;
import com.wx.movie.data.service.PullDbDataToQueueService;

/**
 * @author dynamo
 */
@Service
public class PullDbDataToQueueServiceImpl implements PullDbDataToQueueService,InitializingBean {
  
@Autowired
private UserAttentionMapper userAttentionMapper;

@Autowired
private UserCommentMapper userCommentMapper;

@Autowired
private CommonService commonService;

@Value("${user.action.time}")
private String pullActionTime;

@Autowired
@Qualifier("pullDataToQueueExecutor")
private ThreadPoolTaskExecutor pullDataToQueueExecutor;

@Autowired
@Qualifier("jsonRabbitTemplate")
 private AmqpTemplate amqpTemplate;

private Date criteriaTime;
private Logger logger = LoggerFactory.getLogger(PullDbDataToQueueService.class);

/**
 * 由于在同一个类中，一个无Async注解的方法，调用一个有Async注解的方法，那么此方法启动另一个方法
 * 异步执行失效，由于代理的原因，那么@Transactional也是一样
 */
  @Async("pullDataToQueueExecutor")
  @Override
  public void pullDbDataToQueue() {
    //将用户关注影片放在队列中
    pullUserAttentionToQueue();
    
   //将用户评论影片放在队列中
    pullUserCommentToQueue();
  }
  
  /**
   * 将用户关注影片的数据加入到队列中
   */
 private void pullUserAttentionToQueue(){
pullDataToQueueExecutor.execute(new Thread(){
  @Override
  public void run() {
    Stopwatch timer = Stopwatch.createStarted();
    List<UserAttention> usreAttentions = userAttentionMapper.selectByOperateTime(criteriaTime);
    if(CollectionUtils.isEmpty(usreAttentions)){
      logger.warn("pullUserAttentionToQueue UserAttention is null");
      return;
    }
    UserActionData userAction = new UserActionData();
    Map<String,Set<String>> actionMap = Maps.newHashMap();
    
    for(UserAttention userAttention : usreAttentions){
      commonService.groupByUid(actionMap, String.valueOf(userAttention.getUid()), userAttention.getMovieNo());
    }
    userAction.setAction(UserOperate.ATTENTION_MOVIE);
    userAction.setUserActionMap(actionMap);
    //发送到消息队列中
    amqpTemplate.convertAndSend(RabbitMqName.USER_ACTION_DATA_QUEUE.name(), userAction);
    logger.info("pullUserAttentionToQueue take time:{}",timer.stop());
  }
});
 }
 
 /**
  * 将用户评论的影片的数据加入到队列中
  */
private void pullUserCommentToQueue(){
pullDataToQueueExecutor.execute(new Thread(){
 @Override
 public void run() {
   Stopwatch timer = Stopwatch.createStarted();
   List<UserComment> usreComments = userCommentMapper.selectByOperateTime(criteriaTime);
   if(CollectionUtils.isEmpty(usreComments)){
     logger.warn("pullUserCommentToQueue UserComment is null");
     return;
   }
   UserActionData userAction = new UserActionData();
   Map<String,Set<String>> actionMap = Maps.newHashMap();
   
   for(UserComment userComment : usreComments){
     commonService.groupByUid(actionMap, String.valueOf(userComment.getUid()), userComment.getMovieNo());
   }
   userAction.setAction(UserOperate.COMMENT_MOVIE);
   userAction.setUserActionMap(actionMap);
   //发送到消息队列中
   amqpTemplate.convertAndSend(RabbitMqName.USER_ACTION_DATA_QUEUE.name(), userAction);
   logger.info("pullUserAttentionToQueue take time:{}",timer.stop());
 }
});
}

@Override
public void afterPropertiesSet() throws Exception {
  DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
  try{
  criteriaTime = format.parse(pullActionTime);
  }catch(Exception e){
    logger.error("parse time fail, source is {}",pullActionTime,e);
    throw e;
  }
}


}
