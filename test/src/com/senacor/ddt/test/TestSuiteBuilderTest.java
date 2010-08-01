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
import java.util.Enumeration;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.senacor.ddt.objectmatrix.ObjectMatrixFactory;
import com.senacor.ddt.objectmatrix.excel.ExcelObjectMatrixFactory;
import com.senacor.ddt.test.junit.JUnitTestSuiteBuilder;

/**
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class TestSuiteBuilderTest extends TestCase implements DataDrivenTestCase {
  private TestCaseData testCaseData;
  
  public TestSuiteBuilderTest() {
    // nothing to be done
  }
  
  public static Test suite() throws Throwable {
    int testCaseCount = 0;
    List foundTestCases = new ArrayList();
    TestSuiteConfiguration config = new TestSuiteConfiguration();
    config.setFirstTestCaseName("T2");
    config.setLastTestCaseName("T4");
    config.addTestCaseFilter(new BooleanRowTestFilter("testable"));
    
    ObjectMatrixFactory omf =
        new ExcelObjectMatrixFactory(TestSuiteBuilderTest.class.getClassLoader().getResourceAsStream(
            "com/senacor/ddt/test/TestSuiteBuilderTest.xls"), new String[] { "Test" });
    ;
    JUnitTestSuiteBuilder gen = new JUnitTestSuiteBuilder(omf, config, TestSuiteBuilderTest.class);
    
    try {
      TestSuite suite = (TestSuite) gen.buildSuite();
      Enumeration tests = suite.tests();
      while (tests.hasMoreElements()) {
        TestSuiteBuilderTest test = (TestSuiteBuilderTest) tests.nextElement();
        foundTestCases.add(test.getTestCaseData().getTestCaseName());
        testCaseCount++;
      }
      assertEquals(4, testCaseCount);
      assertEquals(4, foundTestCases.size());
      assertTrue(foundTestCases.contains("T2"));
      assertTrue(foundTestCases.contains("T4"));
      
      return suite;
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }
  
  public void testTest() {
    String testCaseName = getTestCaseData().getTestCaseName();
    if (testCaseName.equals("T2")) {
      assertEquals("foo2", getTestCaseData().getString("a"));
      assertEquals("bar2", getTestCaseData().getString("b"));
    } else if (testCaseName.equals("T3")) {
      fail("T3 should be disabled");
    } else if (testCaseName.equals("T4")) {
      assertEquals("foo4", getTestCaseData().getString("a"));
      assertEquals("bar4", getTestCaseData().getString("b"));
    } else {
      fail("Testcase " + testCaseName + " shouldn't be included");
    }
  }
  
  public void setTestCaseData(TestCaseData tcd) {
    this.testCaseData = tcd;
  }
  
  public TestCaseData getTestCaseData() {
    return this.testCaseData;
  }
  
  public void testNoActiveTestcases() throws Exception {
    TestSuiteConfiguration config = new TestSuiteConfiguration();
    config.addTestCaseFilter(new TestCaseFilter() {
      
      public boolean isTestCaseAllowedToRun(TestCaseData tcd) {
        return false;
      }
      
    });
    try {
      ObjectMatrixFactory omf =
          new ExcelObjectMatrixFactory(TestSuiteBuilderTest.class.getClassLoader().getResourceAsStream(
              "com/senacor/ddt/test/TestSuiteBuilderTest.xls"), new String[] { "Test" });
      new JUnitTestSuiteBuilder(omf, config, TestSuiteBuilderTest.class).buildSuite();
      fail("should have thrown NoActiveTestCaseException");
    } catch (NoActiveTestCasesException e) {
      ; // expected
    }
  }
  
  public String getName() {
    return getTestCaseData().getTestId() + " -- " + super.getName();
  }
}
