/**
 * Project Name:movie-data-source
 * File Name:Test.java
 * Package Name:com.movie.data.source.test.enchance
 * Date:2016年1月29日上午10:36:47
 *
*/

package com.movie.data.source.test.enchance;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.movie.data.source.test.BaseTest;
import com.wx.movie.data.dao.mapper.user.OpenBaseMovieMapper;

/**
 * ClassName:Test <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年1月29日 上午10:36:47 <br/>
 * @author   dynamo
 * @version  
 * @see 	 
 */
public class TestMapper  extends BaseTest{

  @Autowired
 private OpenBaseMovieMapper mapper;
  
  @Autowired
  @Qualifier("pullDataToQueueExecutor")
  private ThreadPoolTaskExecutor pullDataToQueueExecutor;
  
  
  @Test
  @Async("pullDataToQueueExecutor")
  public void test(){
    t1();
    t2();
  }
  private void t1(){
    pullDataToQueueExecutor.execute(new Runnable() {
      
      @Override
      public void run() {
        for(int i = 0;i<1000;i++){
          mapper.selectAllMovie();
        }        
      }
    });
  }
  private void t2(){
    pullDataToQueueExecutor.execute(new Runnable() {
      
      @Override
      public void run() {
        for(int i = 0;i<1000;i++){
          mapper.selectAllMovie();
        }        
      }
    });
  }
}
