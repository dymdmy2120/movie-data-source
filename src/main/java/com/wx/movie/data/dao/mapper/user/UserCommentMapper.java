package com.wx.movie.data.dao.mapper.user;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wx.movie.data.dao.entity.user.UserComment;

public interface UserCommentMapper {
  
  public List<UserComment> selectByOperateTime(@Param("operateTime")Date operateTime);
  
}