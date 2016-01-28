/**
 * Project Name:movie-data-source File Name:WritingUserActionLog.java Package Name:com.wx.movie.data
 * Date:2016年1月28日下午3:23:48
 *
 */

package com.movie.data.source.test.enchance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.movie.data.source.test.BaseTest;
import com.wx.movie.data.common.util.JsonMapperUtil;
import com.wx.movie.data.dao.entity.user.OpenBaseMovie;
import com.wx.movie.data.dao.entity.user.Users;
import com.wx.movie.data.dao.mapper.user.OpenBaseMovieMapper;
import com.wx.movie.data.dao.mapper.user.UserCommentMapper;
import com.wx.movie.data.dao.mapper.user.UsersMapper;

/**
 * 向用户日志文件中写入数据
 * 
 * @author dynamo
 */
public class WritingUserActionLog extends BaseTest implements InitializingBean {

  @Value("${logRootPath}")
  private String logRootPath;

  @Value("${user.action.json}")
  private String userActionJson;

  @Autowired
  private UsersMapper userMapper;

  @Autowired
  private OpenBaseMovieMapper openBaseMovieMapper;

  @Autowired
  private UserCommentMapper commentMapper;

  @Autowired
  @Qualifier("pullDataToQueueExecutor")
  private ThreadPoolTaskExecutor writeLogExecutor;

  private String[] userActions;

  private int logCount = 500;// 产生日志的记录数

  private List<Users> usersList;
  private List<OpenBaseMovie> moviesList;
  private Logger logger = LoggerFactory.getLogger(WritingUserActionLog.class);

  @Test
  public void writeActionLog() {
    if (userActions == null || userActions.length == 0) {
      logger.warn("UserAction Json File Is Not Found or Empty File");
      return;
    }
    initData();
    for (final String action : userActions) {
      writeLogExecutor.execute(new Runnable() {
        @Override
        public void run() {
          writeData(construtLogPath(action));
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
   * 构建用户操作日志路径 时间_操作名
   */
  private String construtLogPath(String action) {
    DateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
    String currentDate = fomat.format(Calendar.getInstance().getTime());
    StringBuilder builder = new StringBuilder();
    return builder.append(logRootPath).append(currentDate).append("_" + action).toString();
  }

  private void writeData(String logPath) {
    Stopwatch timer = Stopwatch.createStarted();
    
    Random random = new Random();
    int scope = Math.min(usersList.size(), moviesList.size());

    OutputStream os = null;
    Map<String, String> map = Maps.newHashMap();

    try {
      os = new FileOutputStream(logPath, true);
      for (int i = 0; i < logCount; i++) {
        Users user = usersList.get(random.nextInt(scope));
        OpenBaseMovie movie = moviesList.get(random.nextInt(scope));

        map.put("uid", String.valueOf(user.getUid()));
        map.put("movieNo", movie.getMovieno());

        byte[] bytes = JsonMapperUtil.getInstance().toJsonByBytes(map);
        os.write(bytes);
        os.write(System.getProperty("line.separator").getBytes());
      }
      logger.info("write data to Log File Path : {} , take time:{}", logPath, timer.stop());
    } catch (Exception e) {
      logger.error("write data to log file error path:{}", logPath, e);
    } finally {
      if (os != null) try {
        os.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void initData() {
    usersList = userMapper.selectAllUser();
    moviesList = openBaseMovieMapper.selectAllMovie();
  }
}
