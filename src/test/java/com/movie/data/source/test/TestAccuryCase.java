package com.movie.data.source.test;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wx.movie.data.common.enums.RedisKey;
import com.wx.movie.data.dao.entity.user.OpenBaseMovie;
import com.wx.movie.data.dao.entity.user.Users;
import com.wx.movie.data.redis.RedisUtils;
import com.wx.movie.data.service.PullLogDataToQueueService;

public class TestAccuryCase extends BaseTest {
	  @Autowired
	  RedisUtils redisUtils;
	  @Autowired
	  private PullLogDataToQueueService pullLogDataToQueue;
 @Test
	public void testCase(){
		 setUserLike();
		  setUser();
		  setMovie();
		  pullLogDataToQueue.pullLogDataToQueue();
	}

 private void setUserLike(){
	  Integer uid1 = 1;
	  Set<String> set1 = Sets.newHashSet();
	  set1.add("M1");
     redisUtils.setT(String.format(RedisKey.USER_LIKE_MAP,uid1),set1);

     Integer uid2 = 2;
	  Set<String> set2 = Sets.newHashSet();
	  set2.add("M2");
     redisUtils.setT(String.format(RedisKey.USER_LIKE_MAP,uid2),set2);

     Integer uid3 = 3;
	  Set<String> set3 = Sets.newHashSet();
	  set3.add("M3");
     redisUtils.setT(String.format(RedisKey.USER_LIKE_MAP,uid3),set3);
 }
 private void setUser(){
	  List<Users> lists = Lists.newArrayList();
	  Users user1 = new Users();
	  user1.setUid(1);

	  Users user2 = new Users();
	  user2.setUid(2);

	  Users user3 = new Users();
	  user3.setUid(3);
	  lists.add(user1);
	  lists.add(user2);
	  lists.add(user3);
	  redisUtils.setList(RedisKey.USERLIST, lists);

 }
 private void setMovie(){
	  List<OpenBaseMovie> lists = Lists.newArrayList();

	  OpenBaseMovie openBaseMovie1 = new OpenBaseMovie();
	  openBaseMovie1.setMovieno("M1");

	  OpenBaseMovie openBaseMovie2 = new OpenBaseMovie();
	  openBaseMovie2.setMovieno("M2");

	  OpenBaseMovie openBaseMovie3 = new OpenBaseMovie();
	  openBaseMovie3.setMovieno("M3");

	  OpenBaseMovie openBaseMovie4 = new OpenBaseMovie();
	  openBaseMovie4.setMovieno("M4");
	  lists.add(openBaseMovie1);
	  lists.add(openBaseMovie2);
	  lists.add(openBaseMovie3);
	  lists.add(openBaseMovie4);
	  redisUtils.setList(RedisKey.MOVIELIST, lists);
 }
}
