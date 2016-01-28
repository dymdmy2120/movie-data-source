/**
 * Project Name:movie-data-source File Name:PullDataToCacheServiceImpl.java Package
 * Name:com.wx.movie.data.service.impl Date:2016年1月26日下午10:39:14
 *
 */

package com.wx.movie.data.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.wx.movie.data.common.enums.RabbitMqName;
import com.wx.movie.data.common.util.JsonMapperUtil;
import com.wx.movie.data.pojo.UserActionData;
import com.wx.movie.data.service.CommonService;
import com.wx.movie.data.service.PullLogDataToQueueService;

/**
 * @author dynamo
 */
public class PullLogDataToQueueServiceImpl implements PullLogDataToQueueService, InitializingBean {

  @Value("logRootPath")
  private static String logRootPath;

  @Value("user.action.json")
  private String userActionJson;

  @Autowired
  private CommonService commonService;
  
  @Autowired
  @Qualifier("pullDataToQueueExecutor")
  private ThreadPoolTaskExecutor pullDataToQueueExecutor;
  
  @Autowired
  @Qualifier("jsonRabbitTemplate")
  private AmqpTemplate amqpTemplate;
  
  private String[] userActions;
  private Logger logger = LoggerFactory.getLogger(PullLogDataToQueueService.class);

  @Async("pullDataToQueueExecutor")
  @Override
  public void pullLogDataToQueue() {
    if(userActions == null || userActions.length == 0){
      logger.warn("UserAction Json File Is Not Found or Empty File");
      return;
    }
    final UserActionData userAction = new UserActionData();
    
    for(final String action : userActions){
       final Map<String,List<String>> actionMap = parseUserLog(action);
      if(actionMap == null){
        continue;
      }
      //异步将用户行为操作发送到消息队列中
      pullDataToQueueExecutor.execute(new Runnable(){
        @Override
        public void run() {
          userAction.setAction(action);
          userAction.setUserActionMap(actionMap);
          //发送到队列中
          amqpTemplate.convertAndSend(RabbitMqName.USER_ACTION_DATA_QUEUE.name(), userAction);
        }
      });
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    FileInputStream fis = null;
    try {
      // 得到classpath目录路径
      String path = this.getClass().getResource("/").toURI().getPath();
      path = path + userActionJson;
      logger.info("配置用户操作文件路径：" + path);
      File jsonFile = new File(path);
      if (!jsonFile.exists()) {
        jsonFile = new File(userActionJson);
      }
      fis = new FileInputStream(jsonFile);
      Long filelength = jsonFile.length();
      byte[] fileContent = new byte[filelength.intValue()];
      fis.read(fileContent);
      String actionJson = new String(fileContent, "UTF-8");
      userActions = JsonMapperUtil.getInstance().fromJson(actionJson, String[].class);
      logger.info("获取用户行为操作：{}", actionJson);
    } catch (Exception e) {
      logger.error("parse user_action.json fail", e);
    } finally {
      if (fis != null) fis.close();
    }
  }

  /**
   * 解析日志文件成特定的数据结构，Map<String,List<String>>
   * @paran action 用户行为，搜索或者浏览等操作
   */
  private Map<String, List<String>> parseUserLog(String action) {
    Stopwatch timer = Stopwatch.createStarted();
    String logPath = construtLogPath(action);
    BufferedReader br = null;

    try {
      File file = new File(logPath);
      if (!file.exists()) {
        logger.error("User Operate Log Path is Not Found path:{}", logPath);
        return null;
      }
      Map<String, List<String>> actionMap = Maps.newHashMap();
      br = new BufferedReader(new FileReader(file));
      String line = null;

      while ((line = br.readLine()) != null) {
        //TODO 考虑日志内容 日志内容格式为{}json格式，每次读取一行然后转换成Map对象，是否耗时？
        @SuppressWarnings("unchecked")
        Map<String, String> userLogMap = JsonMapperUtil.getInstance().fromJson(line, Map.class);
        commonService.groupByUid(actionMap, userLogMap.get("uid"), userLogMap.get("userLogMap"));
      }
      logger.info("parseUserLog logPath is :{} , take time:{}",logPath,timer.stop());
      return actionMap;
    } catch (Exception e) {
      logger.error("parseUserLog erro logPath is :{}", logPath, e);
    } finally {
      try {
        if (br != null) br.close();
      } catch (IOException e) {
        logger.error("parseUserLog close BufferedReader fail ", e);
      }
    }
    return null;
  }

  /**
   * 构建用户操作日志路径 时间_操作名
   */
  private static String construtLogPath(String action) {
    DateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
    String currentDate = fomat.format(Calendar.getInstance().getTime());
    StringBuilder builder = new StringBuilder();
    return builder.append(logRootPath).append(currentDate).append(action).toString();
  }
}
