/*
 * Copyright (c) 2009 Senacor Technologies AG.
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
package com.senacor.ddt.objectmatrix.beanfiller;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.senacor.ddt.objectmatrix.csv.CsvObjectMatrixFactory;

public class LineSortingTest extends TestCase {
  private BeanFiller bf;
  
  protected void setUp() throws Exception {
    Bar.setterSequence.clear();
    final CsvObjectMatrixFactory omf =
        new CsvObjectMatrixFactory(new InputStreamReader(getClass().getResourceAsStream("LineSortingTest.csv.txt")),
            ';', "test");
    this.bf = new BeanFiller(omf.create()[0].getObjectMapForColumn("Test"));
  }
  
  public void testLineSorting() throws Exception {
    this.bf.fillBean("foo", new Foo());
    assertEquals(3, Bar.setterSequence.size());
    assertEquals(new Integer(1), Bar.setterSequence.get(0));
    assertEquals(new Integer(2), Bar.setterSequence.get(1));
    assertEquals(new Integer(3), Bar.setterSequence.get(2));
  }
  
  public static class Foo {
    public List bar;
  }
  
  public static class Bar {
    public static List setterSequence = new ArrayList();
    private Integer val;
    
    public Integer getVal() {
      return this.val;
    }
    
    public void setVal(final Integer val) {
      setterSequence.add(val);
      this.val = val;
    }
  }
}
