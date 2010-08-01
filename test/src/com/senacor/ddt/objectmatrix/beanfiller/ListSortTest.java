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
import java.util.List;

import junit.framework.TestCase;

import com.senacor.ddt.objectmatrix.ObjectMatrix;
import com.senacor.ddt.objectmatrix.csv.CsvObjectMatrixFactory;

public class ListSortTest extends TestCase {
  private BeanFiller bf;
  
  protected void setUp() throws Exception {
    final ObjectMatrix matrix =
        new CsvObjectMatrixFactory(new InputStreamReader(getClass().getResourceAsStream("ListSortTest.csv")), ';',
            "test").create()[0];
    this.bf = new BeanFiller(matrix.getObjectMapForColumn("test"));
  }
  
  public void testCorrectValuesAtHighIndices() throws Exception {
    final Bean bean = (Bean) this.bf.fillBean("foo", new Bean());
    for (int i = 0; i <= 11; i++) {
      assertEquals("" + i, i, ((Item) bean.list.get(i)).val);
      assertEquals("" + i, 11 - i, ((Item) bean.list.get(i)).val2);
    }
  }
  
  public static class Bean {
    public List list;
  }
  
  public static class Item {
    public int val;
    public int val2;
  }
}
