/**
 * Project Name:movie-data-source
 * File Name:TestController.java
 * Package Name:com.wx.movie.data.web
 * Date:2016年1月23日下午9:48:30
 *
*/

package com.wx.movie.data.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wx.movie.data.common.exception.CommonResponse;
import com.wx.movie.data.service.PullDataToCacheService;
import com.wx.movie.data.service.PullDataToMsgQueueService;

/**
 * 定时任务 Controller
 * 定时拉取用户列表，影片列表，用户喜欢影片数据 存入缓存中
 * 定时从数据库读取用户关注影片，评论影片数据，从日志中读取浏览和搜索影片的数据 存入到队列中
 */
@Controller
@RequestMapping("/jobs")
public class CrontabController {
  @Autowired
  private PullDataToCacheService pullDataToCacheService;
  
  @Autowired
  private PullDataToMsgQueueService pullDataToMsgQueueService;

  
  @RequestMapping("/pullDataToCache")
  @ResponseBody
  public CommonResponse<?>  pullDataToCache(){
    pullDataToCacheService.pullDataToCache();
    return CommonResponse.success();
  }
  
  @RequestMapping("/pullDataToQueue")
  @ResponseBody
  public CommonResponse<?> pullDateToQueue(){
    pullDataToMsgQueueService.pullDataToMsgQueue();
    return CommonResponse.success();
  }
}
