/**
 * Project Name:movie-data-source
 * File Name:UserOperate.java
 * Package Name:com.wx.movie.data.common.enums
 * Date:2016年1月27日下午5:42:29
 *
*/

package com.wx.movie.data.common.enums;
/**
 * 用户行为操作
 * Date:     2016年1月27日 下午5:42:29 <br/>
 * @author   dynamo
 */
public interface UserOperate {
  
  String SEARCHE_MOVIE = "search_movie"; //搜索影片操作
  String BROWSE_MOVIE ="browse_movie";//浏览影片的操作
  
  String ATTENTION_MOVIE = "attention_movie";//关注的影片
  String COMMENT_MOVIE = "comment_movie";//评论的影片
}
