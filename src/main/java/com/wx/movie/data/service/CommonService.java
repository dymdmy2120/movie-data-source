package com.wx.movie.data.service;

import java.util.Map;
import java.util.Set;

/**
 * 公共的Service
 * 
 * @author dynamo
 */
public interface CommonService {
  /**
   * 对用户行为操作集合进行分组，拆分成Map<Integer,List<String>> key：uid value:影片id的集合
   */
  public void groupByUid(Map<String, Set<String>> actionMap, String uid, String movieNo);
}
