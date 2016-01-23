package com.wx.movie.data.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wx.movie.data.common.util.JsonMapperUtil;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {

  public static final String CODE_SUCCESS = "0";
  public static final String CODE_FAIL = "-1";
  public static final String MSG_SUCCESS = "success";
  public static final String MSG_FAIL = "fail";

  /**
   * 返回码
   */
  protected String ret;
  /**
   * 返回子码
   */
  protected String sub;
  /**
   * 返回消息
   */
  protected String msg;

  private T data;

  public CommonResponse() {
    this.ret = CODE_SUCCESS;
    this.sub = CODE_SUCCESS;
    this.msg = MSG_SUCCESS;
  }

  public CommonResponse(String ret, String sub, String msg) {
    this.ret = ret;
    this.sub = sub;
    this.msg = msg;
  }

  private CommonResponse(Builder<T> builder) {
    this.ret = builder.ret;
    this.sub = builder.sub;
    this.msg = builder.msg;
    this.data = builder.data;
  }

  @Override
  public String toString() {
    return JsonMapperUtil.getInstance().toJson(this);
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getRet() {
    return ret;
  }

  public void setRet(String ret) {
    this.ret = ret;
  }

  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public static <T> CommonResponse<T> success() {
    return new CommonResponse<T>(CODE_SUCCESS, CODE_SUCCESS, MSG_SUCCESS);
  }

  public static <T> CommonResponse<T> success(T data) {
    CommonResponse<T> resp = new CommonResponse<T>(CODE_SUCCESS, CODE_SUCCESS, MSG_SUCCESS);
    resp.setData(data);
    return resp;
  }

  public static <T>CommonResponse<T> fail() {
    return new CommonResponse<T>(CODE_FAIL, CODE_FAIL, MSG_FAIL);
  }

  @JsonIgnore
  public boolean isSucc() {
    return this.getRet() != null && this.getRet().equals(CommonResponse.CODE_SUCCESS);
  }

  public static class Builder<T> {
    private String ret;
    private String sub;
    private String msg;
    private T data;


    public Builder<T> ret(String ret) {
      this.ret = ret;
      return this;
    }

    public Builder<T> sub(String sub) {
      this.sub = sub;
      return this;
    }

    public Builder<T> msg(String msg) {
      this.msg = msg;
      return this;
    }

    public Builder<T> data(T data) {
      this.data = data;
      return this;
    }

    public Builder<T> success() {
      this.ret = CODE_SUCCESS;
      this.sub = CODE_SUCCESS;
      this.msg = MSG_SUCCESS;
      return this;
    }

    public Builder<T> fail() {
      this.ret = CODE_FAIL;
      this.sub = CODE_FAIL;
      this.msg = MSG_FAIL;
      return this;
    }

    public CommonResponse<T> build() {
      validate();
      return new CommonResponse<T>(this);
    }

    private void validate() {}
  }

}
