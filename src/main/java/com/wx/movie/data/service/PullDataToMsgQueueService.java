/**
 * Project Name:movie-data-source File Name:PullDataToCacheService.java Package
 * Name:com.wx.movie.data.service.impl Date:2016年1月26日下午10:34:53
 *
 */

package com.wx.movie.data.service;

/**
 * ClassName:PullDataToCacheService <br/>
 * 读取数据库中 用户关注影片，用户评论的影片消息队列中 
 * 读取日志文件 用户浏览影片，用户搜索的影片到消息队列中
 * 
 * @author dynamo
 * @version
 * @see
 */
public interface PullDataToMsgQueueService {

  public void pullDataToMsgQueue();

}
