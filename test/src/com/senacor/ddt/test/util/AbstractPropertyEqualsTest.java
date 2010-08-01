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
package com.senacor.ddt.test.util;

import junit.framework.TestCase;

public abstract class AbstractPropertyEqualsTest extends TestCase {
  protected final TestBean BEAN;
  
  protected final TestBean BEAN_EQUAL_ALL;
  
  protected final TestBean BEAN_EQUAL_FOO;
  
  protected final TestBean BEAN_EQUAL_BAR;
  
  protected final TestBean BEAN_UNEQUAL;
  
  protected AbstractPropertyEqualsTest() {
    BEAN = new TestBean();
    BEAN.setFoo("foo");
    BEAN.setBar(1);
    BEAN_EQUAL_ALL = new TestBean();
    BEAN_EQUAL_ALL.setFoo("foo");
    BEAN_EQUAL_ALL.setBar(1);
    BEAN_EQUAL_FOO = new TestBean();
    BEAN_EQUAL_FOO.setFoo("foo");
    BEAN_EQUAL_FOO.setBar(2);
    BEAN_EQUAL_BAR = new TestBean();
    BEAN_EQUAL_BAR.setFoo("foobar");
    BEAN_EQUAL_BAR.setBar(1);
    BEAN_UNEQUAL = new TestBean();
  }
  
  public class TestBean {
    private String foo;
    
    private int bar;
    
    public int getBar() {
      return bar;
    }
    
    public void setBar(int bar) {
      this.bar = bar;
    }
    
    public String getFoo() {
      return foo;
    }
    
    public void setFoo(String foo) {
      this.foo = foo;
    }
  }
}
