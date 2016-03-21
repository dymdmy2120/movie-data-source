package com.wx.movie.data.dao.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.wx.movie.data.dao.entity.user.OpenBaseMovie;

public interface OpenBaseMovieMapper {
  
    public List<OpenBaseMovie> selectAllMovie();
    
    /**
     *查询出比id还要大的影片
     */
    public List<OpenBaseMovie> selectGreaterThanId(Integer mid);
    
    public OpenBaseMovie selectByMoiveNo(@Param("movieNo") String movieNo);
}