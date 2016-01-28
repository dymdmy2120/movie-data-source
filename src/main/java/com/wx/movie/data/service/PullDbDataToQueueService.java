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
 *拉取数据库中用户关注，用户评论的数据
 * @author   dynamo
 * @version  
 * @see 	 
 */
public interface PullDbDataToQueueService {

  public void pullDbDataToQueue();
}
