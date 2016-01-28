/**
 * Project Name:movie-data-source File Name:CommonServiceImpl.java Package
 * Name:com.wx.movie.data.service.impl Date:2016年1月28日上午9:47:00
 *
 */

package com.wx.movie.data.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
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
  public void groupByUid(Map<String, List<String>> actionMap, String uid, String movieNo) {
    if (actionMap.containsKey(uid)) {// 如果actionMap中包扩了uid这个key
      List<String> movieIds = actionMap.get(uid);
      movieIds.add(movieNo);
      actionMap.put(uid, movieIds);
    } else {
      List<String> movieNos = Lists.newArrayList();
      movieNos.add(movieNo);
      actionMap.put(uid, movieNos);
    }
  }
}
