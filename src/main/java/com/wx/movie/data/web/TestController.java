/**
 * Project Name:movie-data-source
 * File Name:TestController.java
 * Package Name:com.wx.movie.data.web
 * Date:2016年1月23日下午9:48:30
 *
*/

package com.wx.movie.data.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 测试 Controller
 */
@Controller
public class TestController {
  
  @RequestMapping("/test/index")
  @ResponseBody
  public String index(){
    return "hello";
  }
}
