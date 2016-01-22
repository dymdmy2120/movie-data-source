package com.wx.movie.data.common.exception;

public enum ResponseMessage implements MessageCodes {

  // 参数校验失败
  SE10001(E00001, "00001", "请求参数为空或非法"),

   // 数据或逻辑异常
  SE20001(E00002, "00001", "不存在的订单"),

   // 系统异常
  SE30002(E00003, "00002", "数据库操作异常"),
  SE30003(E00003, "00003", "消息队列系统异常");

  private CommonResponse<?> response = null;

  private ResponseMessage() {
    response = new CommonResponse<Object>();
  }

  private ResponseMessage(String ret, String sub, String msg) {
    response = new CommonResponse<Object>(ret, sub, msg);
  }

  /**
   * 定制message信息.
   */
  public ResponseMessage withMsg(String msg) {
    this.response.setMsg(msg);
    return this;
  }

  /**
   * 定制sub信息.
   */
  public ResponseMessage withSub(String sub) {
    this.response.setSub(sub);;
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> CommonResponse<T> getResponse() {
    return (CommonResponse<T>) this.response;
  }
}
