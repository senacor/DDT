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

import java.util.Set;

public class NameRangeFilterTest extends AbstractFilterTestCase {
  public void testNoBounds() throws Exception {
    try {
      NameRangeTestCaseFilter filter = new NameRangeTestCaseFilter(null, null);
      fail("at least one parameter must be given, should have thrown exception");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
  
  public void testOpenLeft() throws Exception {
    NameRangeTestCaseFilter filter = new NameRangeTestCaseFilter(null, "col2");
    Set found = runThroughFilter(filter);
    assertTrue(found.size() == 3);
    assertTrue(found.contains(tcds[0]));
    assertTrue(found.contains(tcds[1]));
    assertTrue(found.contains(tcds[2]));
  }
  
  public void testOpenRight() throws Exception {
    NameRangeTestCaseFilter filter = new NameRangeTestCaseFilter("col2", null);
    Set found = runThroughFilter(filter);
    assertTrue(found.size() == 3);
    assertTrue(found.contains(tcds[2]));
    assertTrue(found.contains(tcds[3]));
    assertTrue(found.contains(tcds[4]));
  }
  
  public void testRealRange() throws Exception {
    NameRangeTestCaseFilter filter = new NameRangeTestCaseFilter("col1", "col3");
    Set found = runThroughFilter(filter);
    assertTrue(found.size() == 3);
    assertTrue(found.contains(tcds[1]));
    assertTrue(found.contains(tcds[2]));
    assertTrue(found.contains(tcds[3]));
  }
  
  public void testFirstIsLast() throws Exception {
    NameRangeTestCaseFilter filter = new NameRangeTestCaseFilter("col1", "col1");
    Set found = runThroughFilter(filter);
    assertTrue(found.size() == 1);
    assertTrue(found.contains(tcds[1]));
  }
  
  public void testInverseRange() throws Exception {
    NameRangeTestCaseFilter filter = new NameRangeTestCaseFilter("col2", "col0");
    Set found = runThroughFilter(filter);
    assertTrue(found.size() == 3);
    assertTrue(found.contains(tcds[2]));
    assertTrue(found.contains(tcds[3]));
    assertTrue(found.contains(tcds[4]));
  }
  
  public void testInvalidFirst() throws Exception {
    NameRangeTestCaseFilter filter = new NameRangeTestCaseFilter("foobar", "col3");
    Set found = runThroughFilter(filter);
    assertTrue(found.size() == 0);
  }
  
  public void testInvalidLast() throws Exception {
    NameRangeTestCaseFilter filter = new NameRangeTestCaseFilter("col2", "foobar");
    Set found = runThroughFilter(filter);
    assertTrue(found.size() == 3);
    assertTrue(found.contains(tcds[2]));
    assertTrue(found.contains(tcds[3]));
    assertTrue(found.contains(tcds[4]));
  }
}
