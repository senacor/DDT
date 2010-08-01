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
package com.senacor.ddt.objectmatrix.excel;

import java.util.Iterator;
import java.util.List;

import com.senacor.ddt.objectmatrix.KeyNotFoundException;

/**
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class ExcelStringMatrixTest extends AbstractExcelStringMatrixTestCase {
  
  public void testRowColumnNames() {
    final List cols = this.stringMatrix.getColNames();
    Iterator i = cols.iterator();
    assertEquals("Reserved", i.next());
    assertEquals("", i.next());
    assertEquals("Col 1", i.next());
    assertEquals("Col 2", i.next());
    
    final List rows = this.stringMatrix.getRowNames();
    i = rows.iterator();
    assertEquals("Reserved", i.next());
    assertEquals("", i.next());
    assertEquals("Row 1", i.next());
    assertEquals("Row 2", i.next());
  }
  
  public void testComboBoxAccess() {
    assertEquals("combo2", this.stringMatrix.getString("Col 1", "comboBox"));
  }
  
  public void testNameAccess() {
    assertEquals("Bar2", this.stringMatrix.getString("Col 1", "Row 2"));
    assertEquals("Baz2", this.stringMatrix.getString("Col 2", "Row 2"));
    try {
      this.stringMatrix.getString("Col 1", "$JUNIT");
      fail("should have thrown exception");
    } catch (final KeyNotFoundException e) {
      // expected
      assertTrue(e.getMessage().indexOf("$JUNIT") > -1);
      assertTrue(e.getMessage().indexOf("Row") > -1);
    }
    try {
      this.stringMatrix.getString("$JUNIT", "Row 1");
      fail("should have thrown exception");
    } catch (final KeyNotFoundException e) {
      // expected
      assertTrue(e.getMessage().indexOf("$JUNIT") > -1);
      assertTrue(e.getMessage().indexOf("Col") > -1);
    }
  }
  
  public void testFilterRowNames() throws Exception {
    List result = this.stringMatrix.filterRowNames("myBean", null, null);
    assertEquals(43, result.size());
    result = this.stringMatrix.filterRowNames(null, "Formula", null);
    assertEquals(2, result.size());
    result = this.stringMatrix.filterRowNames(null, null, "theString");
    dumpList(result);
    assertEquals(10, result.size());
    result = this.stringMatrix.filterRowNames(null, "uninitialized", "theString");
    assertEquals(3, result.size());
    result = this.stringMatrix.filterRowNames("foo", "uninitialized", "theString");
    assertEquals(0, result.size());
  }
  
  protected static void dumpList(final List result) {
    final Iterator iter = result.iterator();
    while (iter.hasNext()) {
      System.out.println(iter.next());
    }
  }
}
