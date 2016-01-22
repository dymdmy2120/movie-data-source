package com.wx.movie.data.common.exception;

/**
 * 商品中心的异常处理类
 */
public class DataException extends RuntimeException {

  private static final long serialVersionUID = -8876379099262898617L;
  private CommonResponse<?> commonResponse;

  public DataException() {
    super();
  }

  public DataException(Throwable cause) {
    super(cause);
  }

  public DataException(String message) {
    super(message);
  }

  public DataException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataException(CommonResponse<?> commonResponse) {
    super(commonResponse.getMsg());
    this.commonResponse = commonResponse;
  }

  public DataException(CommonResponse<?> commonResponse, Throwable cause) {
    super(commonResponse.getMsg(), cause);
    this.commonResponse = commonResponse;
  }

  public DataException(ResponseMessage responseMessage) {
    super(responseMessage.getResponse().getMsg());
    this.commonResponse = responseMessage.getResponse();
  }

  public DataException(ResponseMessage responseMessage, Throwable cause) {
    super(responseMessage.getResponse().getMsg(), cause);
    this.commonResponse = responseMessage.getResponse();
  }

  public CommonResponse<?> getCommonResponse() {
    return commonResponse;
  }
}
