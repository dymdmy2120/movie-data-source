package com.wx.movie.data.pojo;

import java.util.List;
import java.util.Map;

/**
 * 用户行为数据(关于用户浏览影片，搜索影片，关注，评论等行为)
 * @author   dynamo
 */
public class UserActionData {
  /**
   * 用户行为
   */
private String action;
/**
 * 所有用户行为的map
 */
private  Map<String,List<String>> userActionMap;

public String getAction() {
  return action;
}
public void setAction(String action) {
  this.action = action;
}
public Map<String, List<String>> getUserActionMap() {
  return userActionMap;
}
public void setUserActionMap(Map<String, List<String>> userActionMap) {
  this.userActionMap = userActionMap;
}

}
