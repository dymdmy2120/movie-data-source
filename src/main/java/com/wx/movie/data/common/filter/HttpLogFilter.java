package com.wx.movie.data.common.filter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wx.movie.data.common.util.JsonMapperUtil;

@WebFilter("/*")
public class HttpLogFilter implements Filter {

  private static final Logger analysisLog = LoggerFactory.getLogger("analysis");
  private static final Logger log = LoggerFactory.getLogger(HttpLogFilter.class);

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void init(FilterConfig config) throws ServletException {
    // NOOP.
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    Date statDate = Calendar.getInstance().getTime();

    if (response.getCharacterEncoding() == null) {
      response.setCharacterEncoding("UTF-8");
    }

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String url = httpRequest.getRequestURL().toString();
    Map<String, String> parameterMap = getParamterMap(httpRequest);
    log.info("Recv request, method:{}, url:{}, param:{}", httpRequest.getMethod(), url, JsonMapperUtil
        .getInstance().toJson(parameterMap));

    HttpServletResponseCopier responseCopier =
        new HttpServletResponseCopier((HttpServletResponse) response);

    try {
      chain.doFilter(request, responseCopier);
    } catch (Exception e) {
      // logger.error("chain.doFilter error", e);
    }

    try {
      responseCopier.flushBuffer();
      String content = new String(responseCopier.getCopy(), response.getCharacterEncoding());
      log.info("Return response, reqUrl:{}, result:{}", url, content);
      writeHttpLog(httpRequest, statDate, parameterMap, content);
    } catch (Exception e) {
      analysisLog.error("writeHttpLog error", e);
    }
  }

  @Override
  public void destroy() {
    // NOOP.
  }

  private Map<String, String> getParamterMap(HttpServletRequest request) {
    Map<String, String> parameterMap = new HashMap<String, String>();
    Map<String, String[]> map = request.getParameterMap();
    Iterator<String> it = map.keySet().iterator();
    while (it.hasNext()) {
      String key;
      key = it.next();
      parameterMap.put(key, map.get(key)[0]);
    }
    return parameterMap;
  }

  private void writeHttpLog(HttpServletRequest request, Date statDate,
      Map<String, String> parameterMap, String content) {
    Map<String, String> headMap = new HashMap<String, String>();
    Enumeration<String> enu = request.getHeaderNames();
    while (enu.hasMoreElements()) {
      String headerName = enu.nextElement();
      String headerValue = request.getHeader(headerName);
      headMap.put(headerName, headerValue);
    }

    String url = request.getRequestURL().toString();
    String method = request.getMethod();

    headMap.put("QUERY_STRING", request.getQueryString());
    headMap.put("REQUEST_METHOD", method);
    headMap.put("REQUEST_URI", url);
    headMap.put("REMOTE_ADDR", request.getRemoteAddr());
    headMap.put("REMOTE_PORT", String.valueOf(request.getRemotePort()));
    headMap.put("SERVER_ADDR", request.getLocalAddr());
    headMap.put("SERVER_PORT", String.valueOf(request.getLocalPort()));
    headMap.put("SERVER_NAME", request.getLocalName());
    headMap.put("HTTP_HOST", headMap.get("host"));
    headMap.put("HTTP_COOKIE", "");
    headMap.put("REQUEST_TIME", String.valueOf(statDate.getTime()));
    headMap.put("REQUEST_TIME_FLOAT", "");

    Date endDate = Calendar.getInstance().getTime();

    Map<String, Object> logMap = new HashMap<String, Object>();

    logMap.put("TEXT", getTextObj(content));
    logMap.put("runtime", endDate.getTime() - statDate.getTime());
    logMap.put("runtime_float", "");

    Map<String, Object> rMap = new HashMap<String, Object>();
    if ("get".equals(method.toLowerCase())) {
      rMap.put("get", parameterMap);
      rMap.put("post", "");
    } else {
      rMap.put("get", "");
      rMap.put("post", parameterMap);
    }
    if (parameterMap.containsKey("uuid")) {
      rMap.put("uuid", parameterMap.get("uuid"));
    } else {
      rMap.put("uuid", "");
    }
    rMap.put("head", headMap);
    rMap.put("url_rule", url);
    rMap.put("log", logMap);

    analysisLog.info(JsonMapperUtil.getInstance().toJson(rMap));
  }

  @SuppressWarnings("rawtypes")
  private Object getTextObj(String content) {
    Map map = null;
    try {
      map = mapper.readValue(content, Map.class);
    } catch (Exception e) {}
    return map == null ? content : map;
  }

}
