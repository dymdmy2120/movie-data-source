/**
 * Project Name:movie-data-source File Name:PullDataToCacheServiceImpl.java Package
 * Name:com.wx.movie.data.service.impl Date:2016年1月26日下午10:39:14
 *
 */

package com.wx.movie.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wx.movie.data.service.PullDataToMsgQueueService;
import com.wx.movie.data.service.PullDbDataToQueueService;
import com.wx.movie.data.service.PullLogDataToQueueService;

/**
 * @author dynamo
 */
@Service
public class PullDataToMsgQueueServiceImpl implements PullDataToMsgQueueService {
  
  @Autowired
  private PullDbDataToQueueService pullDbDataToQueueService;
  
  @Autowired
  private PullLogDataToQueueService pullLogDataToQueueService;
  
  @Override
  public void pullDataToMsgQueue() {
    //拉取数据库中的用户行为数据到队列中
    pullDbDataToQueueService.pullDbDataToQueue();
    
    //拉取日志文件中用户行为数据到队列中
    pullLogDataToQueueService.pullLogDataToQueue();
  }


}
