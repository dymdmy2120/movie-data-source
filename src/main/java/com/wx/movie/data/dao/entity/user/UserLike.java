package com.wx.movie.data.dao.entity.user;

import java.util.Date;

public class UserLike {
  private Integer id;

  private Integer uid;
  private String movieNo;
  private Date operateTime;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public Integer getUid() {
    return uid;
  }

  public void setUid(Integer uid) {
    this.uid = uid;
  }

  public String getMovieNo() {
    return movieNo;
  }

  public void setMovieNo(String movieNo) {
    this.movieNo = movieNo == null ? null : movieNo.trim();
  }

  public Date getOperateTime() {
    return operateTime;
  }

  public void setOperateTime(Date operateTime) {
    this.operateTime = operateTime;
  }
}
