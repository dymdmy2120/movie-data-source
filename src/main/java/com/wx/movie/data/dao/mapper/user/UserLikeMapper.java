package com.wx.movie.data.dao.mapper.user;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wx.movie.data.dao.entity.user.UserLike;

public interface UserLikeMapper {

  /**
   * 查询出用户喜欢的影片列表
   */
  /*public List<UserLike> selectByUid(Integer uid);*/

  /**
   * 查询出用户uid加入喜欢影片操作时间之后的记录
   */
  public List<UserLike> selectByUidAdOperTime(@Param("uid") Integer uid,
      @Param("operateTime") Date operateTime);
}
