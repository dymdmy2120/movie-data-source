package com.movie.data.source.test.service;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.movie.data.source.test.BaseTest;
import com.wx.movie.data.service.CommonService;

/**
 * Date:     2016年2月23日 下午10:26:57 <br/>
 * @author   dynamo
 */
public class CommonServiceTest extends BaseTest{
  @Autowired
private CommonService commonService;

  public void testGroupById(){
    Map<String,Set<String>> actionMap = Maps.newHashMap();
   String[] uIds = {"U1","U2","U3"};
   Object[] obj = new Object[3];
   obj[0] = new String[]{"M1"};
   obj[1] = new String[]{"M3","M2"};
   obj[2] = new String[]{"M1","M2","M3"};
  }
}
