/**
 * Project Name:movie-data-source
 * File Name:PullDataToCacheService.java
 * Package Name:com.wx.movie.data.service.impl
 * Date:2016年1月26日下午10:34:53
 *
*/

package com.wx.movie.data.service;
/**
 * ClassName:PullDataToCacheService <br/>
 * 从日志文件中拉取 用户搜索，用户浏览影片的数据
 * @author   dynamo
 * @version  
 * @see 	 
 */
public interface PullLogDataToQueueService {

  public void pullLogDataToQueue();
}
