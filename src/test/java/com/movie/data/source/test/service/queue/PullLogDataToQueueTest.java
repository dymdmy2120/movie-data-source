/**
 * Project Name:movie-data-source
 * File Name:PullLogDataToQueueTest.java
 * Package Name:com.movie.data.source.test.service
 * Date:2016年1月28日下午7:43:47
 *
*/

package com.movie.data.source.test.service.queue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.movie.data.source.test.BaseTest;
import com.wx.movie.data.service.PullDbDataToQueueService;
import com.wx.movie.data.service.PullLogDataToQueueService;

/**
 * 从日志文件中读取日志的测试
 * @author   dynamo
 */
public class PullLogDataToQueueTest extends BaseTest {

  @Autowired
  private PullLogDataToQueueService pullLogDataToQueueServcie;
  
  @Test
  public void testPullLogData(){
    /**
     * case1: /test/resource 中不存在 user_json文件
     * case2: /test/resource中存在user_json文件
     * case3: user_json存在但是格式写错
     * case4: user_json格式正确但是故意增加一个或减少一个用户操作
     * case5:读取的用户日志文件不存时
     */
    pullLogDataToQueueServcie.pullLogDataToQueue();
  }
}
