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

import junit.framework.TestCase;
import jxl.Workbook;

import com.senacor.ddt.objectmatrix.excel.JExcelStringMatrixReader;

public class MultiColumnCellTest extends TestCase {
  public void testMultiColumnCells() throws Exception {
    StringMatrix matrix =
        new DefaultStringMatrix(new JExcelStringMatrixReader(Workbook.getWorkbook(getClass().getResourceAsStream(
            "MultiColumnCellTest.xls")), "MultiColumnCellTest", false));
    validate(matrix);
  }
  
  public void testMultiColumnCellsTransposed() throws Exception {
    StringMatrix matrix =
        new DefaultStringMatrix(new JExcelStringMatrixReader(Workbook.getWorkbook(getClass().getResourceAsStream(
            "MultiColumnCellTest.xls")), "MultiColumnCellTestTransposed", true));
    validate(matrix);
  }
  
  private void validate(StringMatrix matrix) {
    assertEquals("I'm in 1", matrix.getString("Col1", "Row1"));
    assertEquals("I'm in 2", matrix.getString("Col2", "Row1"));
    assertEquals("I'm in both columns", matrix.getString("Col1", "Row2"));
    assertEquals("I'm in both columns", matrix.getString("Col2", "Row2"));
    assertEquals("I'm in both rows", matrix.getString("Col3", "Row1"));
    assertEquals("I'm in both rows", matrix.getString("Col3", "Row2"));
    assertEquals("I'm square", matrix.getString("Col4", "Row1"));
    assertEquals("I'm square", matrix.getString("Col5", "Row1"));
    assertEquals("I'm square", matrix.getString("Col4", "Row2"));
    assertEquals("I'm square", matrix.getString("Col5", "Row2"));
  }
}
