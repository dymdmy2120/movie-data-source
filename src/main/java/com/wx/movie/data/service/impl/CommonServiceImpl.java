/**
 * Project Name:movie-data-source File Name:CommonServiceImpl.java Package
 * Name:com.wx.movie.data.service.impl Date:2016年1月28日上午9:47:00
 *
 */

package com.wx.movie.data.service.impl;

import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.google.common.collect.Sets;
import com.wx.movie.data.service.CommonService;
/**
 * 
 * @author dynamo
 * @version
 * @see
 */
@Service
public class CommonServiceImpl implements CommonService {

  /**
   * 对用户行为操作集合进行分组，拆分成Map<Integer,List<String>> key：uid value:影片id的集合
   * 
   * @author dynamo
   * @param uid
   * @param movieId
   */
  @Override
  public void groupByUid(Map<String, Set<String>> actionMap, String uid, String movieNo) {
    if (actionMap.containsKey(uid)) {// 如果actionMap中包扩了uid这个key
      Set<String> movieIds = actionMap.get(uid);
      movieIds.add(movieNo);
      actionMap.put(uid, movieIds);
    } else {
      Set<String> movieNos = Sets.newHashSet();
      movieNos.add(movieNo);
      actionMap.put(uid, movieNos);
    }
  }
  
  /**
   * 对用户行为操作集合进行分组，拆分成Map<Integer,List<String>> key：moiveNo value:用户id的集合
   * 
   * @author dynamo
   * @param uid
   * @param movieId
   */
  @Override
  public void groupByMovieNo(Map<String, Set<String>> actionMap, String uid, String movieNo) {
    if (actionMap.containsKey(movieNo)) {// 如果actionMap中包扩了movieNo这个key
      Set<String> uIds = actionMap.get(movieNo);
      uIds.add(uid);
      actionMap.put(movieNo, uIds);
    } else {
      Set<String> uIds = Sets.newHashSet();
      uIds.add(uid);
      actionMap.put(movieNo, uIds);
    }
  }
}
