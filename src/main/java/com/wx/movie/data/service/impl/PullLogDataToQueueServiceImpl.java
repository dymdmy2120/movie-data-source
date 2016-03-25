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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wx.movie.data.common.enums.RabbitMqName;
import com.wx.movie.data.common.util.JsonMapperUtil;
import com.wx.movie.data.pojo.UserActionData;
import com.wx.movie.data.service.CommonService;
import com.wx.movie.data.service.PullLogDataToQueueService;

/**
 * @author dynamo
 */
@Service
public class PullLogDataToQueueServiceImpl implements PullLogDataToQueueService, InitializingBean {

  @Value("${user.log.logRootPath}")
  private String logRootPath;

  @Value("${user.action.json}")
  private String userActionJson;

  @Value("${user.action.time}")
  private String parseLogTime;

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
    if (userActions == null || userActions.length == 0) {
      logger.warn("UserAction Json File Is Not Found or Empty File");
      return;
    }
    for (final String action : userActions) {
      // 异步将用户行为操作发送到消息队列中
      pullDataToQueueExecutor.execute(new Runnable() {
        @Override
        public void run() {
          Stopwatch timer = Stopwatch.createStarted();
          List<Map<String, Set<String>>> actionMaps = parseUserLog(action);

          if (CollectionUtils.isEmpty(actionMaps)) {
            return;
          }
          // 发送到消息到基于用户推荐的队列中
          UserActionData bseUserAction = new UserActionData(action, actionMaps.get(0));
          amqpTemplate.convertAndSend(RabbitMqName.USER_ACTION_BSEUSR_QUEUE.name(), bseUserAction);

          // 发送到消息到基于影片推荐的队列中
          UserActionData bseMovieAction = new UserActionData(action, actionMaps.get(1));
          amqpTemplate.convertAndSend(RabbitMqName.USER_ACTION_BSEMOVIE_QUEUE.name(),
              bseMovieAction);
          logger.info("pullLogDataToQueue action is {} , take time is {}", action, timer.stop());
        }
      });
    }
  }

  /**
   * 解析日志文件成特定的数据结构，Map<String,List<String>>
   *
   * @paran action 用户行为，搜索或者浏览等操作
   */
  private List<Map<String, Set<String>>> parseUserLog(String action) {
    Stopwatch timer = Stopwatch.createStarted();
    String logPath = construtLogPath(action);
    BufferedReader br = null;

    try {
      File file = new File(logPath);
      if (!file.exists()) {
        logger.error("User Operate Log Path is Not Found path:{}", logPath);
        return null;
      }
      List<Map<String, Set<String>>> lists = Lists.newArrayList();
      Map<String, Set<String>> bseMovieActionMap = Maps.newHashMap();// 基于影片特征向量映射
      Map<String, Set<String>> bseUsrActionMap = Maps.newHashMap();// 基于用户特征向量映射
      br = new BufferedReader(new FileReader(file));
      String line = null;

      while ((line = br.readLine()) != null) {
        // TODO 考虑日志内容 日志内容格式为{}json格式，每次读取一行然后转换成Map对象，是否耗时？
        @SuppressWarnings("unchecked")
        Map<String, String> userLogMap = JsonMapperUtil.getInstance().fromJson(line, Map.class);
        commonService.groupByUid(bseUsrActionMap, userLogMap.get("uid"),
            userLogMap.get("movieNo"));
        commonService.groupByMovieNo(bseMovieActionMap, userLogMap.get("uid"),
            userLogMap.get("movieNo"));
      }
      lists.add(bseUsrActionMap);
      lists.add(bseMovieActionMap);

      logger.info("parseUserLog logPath is :{} , take time:{}", logPath, timer.stop());
      logger.debug("commonService.groupByUid BaseMovieActionMap is {}", JsonMapperUtil
          .getInstance().toJson(bseMovieActionMap));
      logger.debug("commonService.groupByMovieNo BaseUserActionMap is {}", JsonMapperUtil
          .getInstance().toJson(bseUsrActionMap));

      return lists;
    } catch (Exception e) {
      logger.error("parseUserLog erro logPath is :{}", logPath, e);
    } finally {
      try {
        if (br != null) {
          br.close();
        }
      } catch (IOException e) {
        logger.error("parseUserLog close BufferedReader fail ", e);
      }
    }
    return null;
  }

  /**
   * 构建用户操作日志路径 时间_操作名
   */
  private String construtLogPath(String action) {
    // DateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
    // String currentDate = fomat.format(Calendar.getInstance().getTime());
    // 解析什么时间的日志可以在文件中配置
    StringBuilder builder = new StringBuilder();
    return builder.append(logRootPath).append(parseLogTime).append("_" + action).toString();
  }


  @Override
  public void afterPropertiesSet() throws Exception {
    FileInputStream fis = null;
    try {
      // 得到classpath目录路径
      //String path = Thread.currentThread().getContextClassLoader().getResource("/").toURI().getPath();
    	System.out.println(this.getClass().getClassLoader().getResource(""));
    	String path = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
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
      if (fis != null) {
        fis.close();
      }
    }
  }
}
