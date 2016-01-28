/**
 * Project Name:movie-data-source
 * File Name:RedisKey.java
 * Package Name:com.wx.movie.data.common.enums
 * Date:2016年1月23日上午12:08:00
 *
*/

package com.wx.movie.data.common.enums;
/**
 * @author   dynamo
 */
public interface RedisKey {
  String USER_LIKE = "user_like_uid_%s";//用户喜欢的影片列表 value：List<UserLike>
  String  USERLIST = "user_list";//用户列表，value为List 集合
  String MOVIELIST = "movie_list";//影片列表 ,value为List集合
}
