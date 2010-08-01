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

package com.senacor.ddt.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class NameListFilterTest extends AbstractFilterTestCase {
  public void testWithStringArrayConstructor() throws Exception {
    NameListTestCaseFilter filter = new NameListTestCaseFilter(new String[] { "col1", "col3" });
    Set found = runThroughFilter(filter);
    assertEquals(2, found.size());
    assertFalse(found.contains(tcds[0]));
    assertTrue(found.contains(tcds[1]));
    assertFalse(found.contains(tcds[2]));
    assertTrue(found.contains(tcds[3]));
    assertFalse(found.contains(tcds[4]));
  }
  
  public void testWithCollectionConstructor() throws Exception {
    Collection names = new ArrayList();
    names.add("col1");
    names.add("col2");
    names.add("col3");
    
    NameListTestCaseFilter filter = new NameListTestCaseFilter(names);
    Set found = runThroughFilter(filter);
    assertEquals(3, found.size());
    assertFalse(found.contains(tcds[0]));
    assertTrue(found.contains(tcds[1]));
    assertTrue(found.contains(tcds[2]));
    assertTrue(found.contains(tcds[3]));
    assertFalse(found.contains(tcds[4]));
  }
  
  public void testWithDefaultConstructor() throws Exception {
    NameListTestCaseFilter filter = new NameListTestCaseFilter();
    filter.addName("col1");
    filter.addName("col2");
    filter.addName("col3");
    
    Set found = runThroughFilter(filter);
    assertEquals(3, found.size());
    assertFalse(found.contains(tcds[0]));
    assertTrue(found.contains(tcds[1]));
    assertTrue(found.contains(tcds[2]));
    assertTrue(found.contains(tcds[3]));
    assertFalse(found.contains(tcds[4]));
  }
  
  public void testExcludingWithDefaultConstructor() throws Exception {
    ExcludingNameListTestCaseFilter filter = new ExcludingNameListTestCaseFilter();
    filter.addName("col1");
    filter.addName("col2");
    filter.addName("col3");
    
    Set found = runThroughFilter(filter);
    assertEquals(2, found.size());
    assertTrue(found.contains(tcds[0]));
    assertFalse(found.contains(tcds[1]));
    assertFalse(found.contains(tcds[2]));
    assertFalse(found.contains(tcds[3]));
    assertTrue(found.contains(tcds[4]));
  }
  
  public void testWithStringArrayConstructorAndAddNames() throws Exception {
    NameListTestCaseFilter filter = new NameListTestCaseFilter(new String[] { "col2" });
    filter.addName("col1");
    filter.addName("col3");
    
    Set found = runThroughFilter(filter);
    assertEquals(3, found.size());
    assertFalse(found.contains(tcds[0]));
    assertTrue(found.contains(tcds[1]));
    assertTrue(found.contains(tcds[2]));
    assertTrue(found.contains(tcds[3]));
    assertFalse(found.contains(tcds[4]));
  }
}
