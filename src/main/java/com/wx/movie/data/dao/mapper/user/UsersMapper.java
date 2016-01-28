package com.wx.movie.data.dao.mapper.user;

import java.util.List;

import com.wx.movie.data.dao.entity.user.Users;

public interface UsersMapper {
 
  public List<Users> selectAllUser();
  
  /**
   *查询出比uid还要大的用户
   */
  public List<Users> selectGreaterThanUid(Integer uid);

}