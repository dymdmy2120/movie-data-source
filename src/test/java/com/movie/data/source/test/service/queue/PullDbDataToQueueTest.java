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
public class PullDbDataToQueueTest extends BaseTest {

  @Autowired
  private PullDbDataToQueueService pullDbDataToQueueServcie;
  
  @Test
  public void testPullLogData(){
    pullDbDataToQueueServcie.pullDbDataToQueue();
  }
}
