/*
 * Copyright (c) 2007 Senacor Technologies AG.
 *  
 * All rights reserved. Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided that the following
 * conditions are met: 
 *
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer. 
 *
 * Redistributions in binary form must reproduce the above copyright 
 * notice, this list of conditions and the following disclaimer in the 
 * documentation and/or other materials provided with the distribution. 
 *
 * Neither the name of Senacor Technologies AG nor the names of its 
 * contributors may be used to endorse or promote products derived from 
 * this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS 
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED 
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A 
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER 
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

package com.senacor.ddt.objectmatrix;

import java.io.InputStreamReader;

import junit.framework.TestCase;

import com.senacor.ddt.objectmatrix.beanfiller.BeanFiller;
import com.senacor.ddt.objectmatrix.beanfiller.PrimitiveNullException;
import com.senacor.ddt.objectmatrix.csv.CsvObjectMatrixFactory;
import com.senacor.ddt.test.util.UrlUtil;

public class PrimitiveValuesTest extends TestCase {
  public static final class TestBean {
    private boolean primitiveBoolean;
    
    private Boolean objectBoolean;
    
    private int primitiveInt;
    
    private Integer objectInteger;
    
    public Boolean getObjectBoolean() {
      return objectBoolean;
    }
    
    public void setObjectBoolean(Boolean objectBoolean) {
      this.objectBoolean = objectBoolean;
    }
    
    public Integer getObjectInteger() {
      return objectInteger;
    }
    
    public void setObjectInteger(Integer objectInteger) {
      this.objectInteger = objectInteger;
    }
    
    public boolean getPrimitiveBoolean() {
      return primitiveBoolean;
    }
    
    public void setPrimitiveBoolean(boolean primitiveBoolean) {
      this.primitiveBoolean = primitiveBoolean;
    }
    
    public int getPrimitiveInt() {
      return primitiveInt;
    }
    
    public void setPrimitiveInt(int primitiveInt) {
      this.primitiveInt = primitiveInt;
    }
  }
  
  private ObjectMatrix matrix;
  
  protected void setUp() throws Exception {
    super.setUp();
    CsvObjectMatrixFactory comf =
        new CsvObjectMatrixFactory(new InputStreamReader(UrlUtil.getClassnameBasedUrlInPackage(
            PrimitiveValuesTest.class, ".csv").openStream()), ';', "default");
    matrix = comf.create()[0];
  }
  
  public void testNoNull() throws Exception {
    TestBean bean = fillTestBean("nonull");
    assertEquals(1, bean.getPrimitiveInt());
    assertEquals(new Integer(1), bean.getObjectInteger());
    assertTrue(bean.getPrimitiveBoolean());
    assertEquals(Boolean.TRUE, bean.getObjectBoolean());
  }
  
  public void testNulltokenInteger() throws Exception {
    try {
      fillTestBean("nulltokenInteger");
      fail("attempt to set primitive property to null should have thrown exception");
    } catch (PrimitiveNullException e) {
      // expected
      assertTrue(e.getMessage().indexOf("primitiveInt") > -1);
    }
  }
  
  public void testNulltokenBoolean() throws Exception {
    try {
      fillTestBean("nulltokenBoolean");
      fail("attempt to set primitive property to null should have thrown exception");
    } catch (PrimitiveNullException e) {
      // expected
      assertTrue(e.getMessage().indexOf("primitiveBoolean") > -1);
    }
  }
  
  private TestBean fillTestBean(String column) {
    return (TestBean) new BeanFiller(matrix.getObjectMapForColumn(column)).fillBean("bean", new TestBean());
  }
}
