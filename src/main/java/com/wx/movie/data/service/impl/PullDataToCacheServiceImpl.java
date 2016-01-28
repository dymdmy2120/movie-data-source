/**
 * Project Name:movie-data-source File Name:PullDataToCacheServiceImpl.java Package
 * Name:com.wx.movie.data.service.impl Date:2016年1月26日下午10:39:14
 *
 */

package com.wx.movie.data.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.base.Stopwatch;
import com.wx.movie.data.common.enums.RedisKey;
import com.wx.movie.data.common.util.JsonMapperUtil;
import com.wx.movie.data.dao.entity.user.OpenBaseMovie;
import com.wx.movie.data.dao.entity.user.UserLike;
import com.wx.movie.data.dao.entity.user.Users;
import com.wx.movie.data.dao.mapper.user.OpenBaseMovieMapper;
import com.wx.movie.data.dao.mapper.user.UserLikeMapper;
import com.wx.movie.data.dao.mapper.user.UsersMapper;
import com.wx.movie.data.redis.RedisUtils;
import com.wx.movie.data.service.PullDataToCacheService;

/**
 * @author dynamo
 */
@Service
public class PullDataToCacheServiceImpl implements PullDataToCacheService {

  @Autowired
  private RedisUtils redisUtils;
  private UsersMapper usersMapper;
  private UserLikeMapper userLikeMapper;
  private OpenBaseMovieMapper openBaseMovieMapper;
  private Logger logger = LoggerFactory.getLogger(PullDataToCacheService.class);

  @Override
  @Async("pullDataToCacheExecutor")
  public void pullDataToCache() {
    Stopwatch timer = Stopwatch.createStarted();
    //拉取用户列表
    List<Users> userList = pullUsersToCache();
    
    //拉取影片数据
    pullMovieToCache();
    
    //拉取用户喜欢影片的数据
    pullUserLikeToCache(userList);
    
    logger.info(" Async PullDataToCacheServiceImpl.pullDataToCache  take total time：{}",
        timer.stop());
  }

  /**
   * 从数据库中查询出所有用户列表 TODO:1、先从缓存中查，若不存在 则查询所有用户，2、若存在获得用户集合中最后一个User的uid(保证用户表是以uid排序)
   * 然后作为条件，查询出大于uid的用户，最后存放到缓存中
   */
  private List<Users> pullUsersToCache() {
    Stopwatch timer = Stopwatch.createStarted();
    List<Users> retUsers = null;
    List<Users> userFromCache = redisUtils.getList(Users.class, RedisKey.USERLIST);

    if (userFromCache != null) {// 如果缓存中存在用户列表
      Integer lastUid = userFromCache.get(userFromCache.size() - 1).getUid();
      List<Users> partUsers = usersMapper.selectGreaterThanUid(lastUid);
      userFromCache.addAll(partUsers);
      retUsers = userFromCache;
    } else {
      retUsers = usersMapper.selectAllUser();
    }
    if(CollectionUtils.isEmpty(retUsers)){
      logger.warn("pollUsersToCache but user is null ");
      return null;
    }
    //存入缓存
    redisUtils.setList(RedisKey.USERLIST, retUsers);
    
    logger.debug("pull user list  result :{}",JsonMapperUtil.getInstance().toJson(retUsers));
    logger.info("PullDataToCacheServiceImpl.pullDataToCache query User list take time：{}",
        timer.stop());
    return retUsers;
  }

  /**
   * 获取影片数据列表
   */
  private List<OpenBaseMovie> pullMovieToCache() {
    Stopwatch timer = Stopwatch.createStarted();
    List<OpenBaseMovie> retMovies = null;
    List<OpenBaseMovie> movieFromCache =
        redisUtils.getList(OpenBaseMovie.class, RedisKey.MOVIELIST);

    if (movieFromCache != null) {// 如果缓存中存影片列表
      Integer lastMid = movieFromCache.get(movieFromCache.size() - 1).getId();
      List<OpenBaseMovie> partMovies = openBaseMovieMapper.selectGreaterThanId(lastMid);
      movieFromCache.addAll(partMovies);
      retMovies = movieFromCache;
    } else {
      retMovies = openBaseMovieMapper.selectAllMovie();
    }
    if(CollectionUtils.isEmpty(retMovies)){
      logger.warn("pollMovieToCache but movie is null ");
      return null;
    }
   //存入到缓存
    redisUtils.setList(RedisKey.MOVIELIST, retMovies);
    
    logger.debug("pull movie list result :{}",JsonMapperUtil.getInstance().toJson(retMovies));
    logger.info("PullDataToCacheServiceImpl.pullDataToCache query Movie list take time：{}",
        timer.stop());
    return retMovies;
  }

  /**
   * 1、获取用户喜欢的影片列表
   * 2、根据uid为key查询缓存 若缓存中有，则得到集合中最后一个UserLike，获取operate_time
   * 然后根据用户uid和operate_time到user_like查询出用户为uid并且操作时间大于operate_time记录
   *3、若缓存没有则直接从库中查询所有的用户喜欢影片的记录
   * @param userList 用户列表，直接使用内存中的用户列表
   * 
   */
  private List<UserLike> pullUserLikeToCache(List<Users> users) {
    Stopwatch timer = Stopwatch.createStarted();
    List<Users> userList = users;
    List<UserLike> retUserLike = null;

    if (userList == null) {
      userList = usersMapper.selectAllUser();
    }
    for (Users user : userList) {
      String rKey = String.format(RedisKey.USER_LIKE, user.getUid());
      List<UserLike> userLikeFromCache = redisUtils.getList(UserLike.class, rKey);
      if (userLikeFromCache != null) {
        // 获得最后一个UserLike
        UserLike userLike = userLikeFromCache.get(userLikeFromCache.size() - 1);
        List<UserLike> partUserLike =
            userLikeMapper.selectByUidAdOperTime(userLike.getUid(), userLike.getOperateTime());
        userLikeFromCache.addAll(partUserLike);
        retUserLike = userLikeFromCache;
      } else {
        retUserLike = userLikeMapper.selectByUidAdOperTime(user.getUid(), null);
      }
      if(CollectionUtils.isEmpty(retUserLike)){
        logger.warn("pollUserLikeToCache but userlike is null, uid:{} ",user.getUid());
        continue;
      }
      //存入缓存
      redisUtils.setList(rKey, retUserLike);
      logger.debug("pull userlike  list result :{}",JsonMapperUtil.getInstance().toJson(retUserLike));
    }
    logger.info("PullDataToCacheServiceImpl.pullDataToCache query UserLike list take time：{}",
        timer.stop());
    return retUserLike;
  }
}
