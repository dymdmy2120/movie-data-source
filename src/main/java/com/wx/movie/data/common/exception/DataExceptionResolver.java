package com.wx.movie.data.common.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.wx.movie.data.common.util.JsonMapperUtil;

@Component
public class DataExceptionResolver implements HandlerExceptionResolver {

  private final Logger logger = LoggerFactory.getLogger(DataExceptionResolver.class);

  @Override
  public ModelAndView resolveException(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Object o, Exception e) {
    CommonResponse<?> response = null;
    // 暂时只考虑异步请求
    if (e instanceof DataException) {
      logger.error("DataExceptionResolver resolve DataException", e);
      DataException goodsException = (DataException) e;
      response = goodsException.getCommonResponse();
      if (response == null) {
        response = CommonResponse.fail();
      }

    } else {
      logger.error("DataExceptionResolver resolve exception", e);
      response = CommonResponse.fail();
    }
    try {
      httpServletResponse.setContentType("application/json");
      httpServletResponse.setCharacterEncoding("UTF-8");
      httpServletResponse.getWriter().print(new JsonMapperUtil().toJson(response));
    } catch (IOException e1) {
      logger.error("DataExceptionResolver write error", e1);
    }
    return null;
  }
}
