package com.wx.movie.data.dao.entity.user;

import java.util.Date;

public class UserComment {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_comment.id
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_comment.u_id
     *
     * @mbggenerated
     */
    private Integer uId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_comment.movie_no
     *
     * @mbggenerated
     */
    private String movieNo;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_comment.comment
     *
     * @mbggenerated
     */
    private String comment;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column user_comment.operate_time
     *
     * @mbggenerated
     */
    private Date operateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_comment.id
     *
     * @return the value of user_comment.id
     *
     * @mbggenerated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_comment.id
     *
     * @param id the value for user_comment.id
     *
     * @mbggenerated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_comment.u_id
     *
     * @return the value of user_comment.u_id
     *
     * @mbggenerated
     */
    public Integer getuId() {
        return uId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_comment.u_id
     *
     * @param uId the value for user_comment.u_id
     *
     * @mbggenerated
     */
    public void setuId(Integer uId) {
        this.uId = uId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_comment.movie_no
     *
     * @return the value of user_comment.movie_no
     *
     * @mbggenerated
     */
    public String getMovieNo() {
        return movieNo;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_comment.movie_no
     *
     * @param movieNo the value for user_comment.movie_no
     *
     * @mbggenerated
     */
    public void setMovieNo(String movieNo) {
        this.movieNo = movieNo == null ? null : movieNo.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_comment.comment
     *
     * @return the value of user_comment.comment
     *
     * @mbggenerated
     */
    public String getComment() {
        return comment;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_comment.comment
     *
     * @param comment the value for user_comment.comment
     *
     * @mbggenerated
     */
    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column user_comment.operate_time
     *
     * @return the value of user_comment.operate_time
     *
     * @mbggenerated
     */
    public Date getOperateTime() {
        return operateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column user_comment.operate_time
     *
     * @param operateTime the value for user_comment.operate_time
     *
     * @mbggenerated
     */
    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}