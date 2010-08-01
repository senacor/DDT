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

package com.senacor.ddt.objectmatrix.csv;

import junit.framework.TestCase;

import java.io.InputStreamReader;

public class CsvStringMatrixReaderTest extends TestCase {
  private static final String IDENTIFIER =
      "com.senacor.ddt.objectmatrix.reader.csv.CsvStringMatrixReaderTest.IDENTIFIER";
  
  private CsvStringMatrixReader reader;
  
  protected void setUp() throws Exception {
    reader =
        new CsvStringMatrixReader(new InputStreamReader(this.getClass().getResourceAsStream("/test.csv")), ';',
            IDENTIFIER);
  }
  
  public void testReadFields() throws Exception {
    assertEquals("foo", reader.getString(0, 0));
    assertEquals("quux", reader.getString(1, 1));
    assertEquals("", reader.getString(3, 0));
  }
  
  public void testSize() throws Exception {
    assertEquals(6, reader.getNumberOfColumns());
    assertEquals(8, reader.getNumberOfRows()); // must not break over empty lines
  }
  
  public void testIdentifier() throws Exception {
    assertEquals(IDENTIFIER, reader.getIdentifier());
  }
  
  public void testQuoted() throws Exception {
    assertEquals("foo;bar", reader.getString(0, 5));
    assertEquals("foo\nbar", reader.getString(0, 6));
    assertEquals("foo\"bar", reader.getString(0, 7));
  }
}
