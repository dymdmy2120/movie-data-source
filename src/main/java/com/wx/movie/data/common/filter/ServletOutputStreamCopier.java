package com.wepiao.goods.common.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * ClassName:ServletOutputStreamCopier <br/>
 * Date: 2015年10月15日 下午7:11:05 <br/>
 *
 * @author Huapenpen
 */
public class ServletOutputStreamCopier extends ServletOutputStream {

  private OutputStream outputStream;
  private ByteArrayOutputStream copy;

  public ServletOutputStreamCopier(OutputStream outputStream) {
    this.outputStream = outputStream;
    this.copy = new ByteArrayOutputStream(256);
  }

  @Override
  public void write(int b) throws IOException {
    outputStream.write(b);
    copy.write(b);
  }

  public byte[] getCopy() {
    return copy.toByteArray();
  }

}
