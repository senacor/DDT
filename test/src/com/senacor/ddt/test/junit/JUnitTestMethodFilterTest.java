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
package com.senacor.ddt.test.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.senacor.ddt.objectmatrix.excel.ExcelObjectMatrixFactory;
import com.senacor.ddt.test.DataDrivenTestCase;
import com.senacor.ddt.test.TestCaseData;
import com.senacor.ddt.test.TestCaseFilter;
import com.senacor.ddt.test.TestSuiteConfiguration;
import com.senacor.ddt.util.StringFilter;

public class JUnitTestMethodFilterTest extends TestCase implements DataDrivenTestCase {
  private final class OnlyOnceFilter implements TestCaseFilter {
    private int count = 0;
    
    public boolean isTestCaseAllowedToRun(TestCaseData tcd) {
      return (count++) == 0; // only accept the first so that we only have one test column. makes
      // calculations for number of
      // tests easier.
    }
    
    public OnlyOnceFilter() {
      // nothing to do
    }
  }
  
  public void testFoo() throws Exception {
    // foo
  }
  
  public void testBar() throws Exception {
    // bar
  }
  
  public void testFooBar() throws Exception {
    // foobar
  }
  
  public void testFilter() throws Exception {
    JUnitTestSuiteBuilder builder =
        new JUnitTestSuiteBuilder(
            new ExcelObjectMatrixFactory("com/senacor/ddt/test/TestSuiteBuilderTest.xls", "Test"),
            new TestSuiteConfiguration().addTestCaseFilter(new OnlyOnceFilter()), JUnitTestMethodFilterTest.class);
    assertContainsOnly(new String[] { "testFoo", "testBar", "testFilter", "testFooBar" }, builder.buildSuite());
    StringFilter filter = new StringFilter();
    builder =
        new JUnitTestSuiteBuilder(
            new ExcelObjectMatrixFactory("com/senacor/ddt/test/TestSuiteBuilderTest.xls", "Test"),
            new TestSuiteConfiguration().addTestCaseFilter(new OnlyOnceFilter()), JUnitTestMethodFilterTest.class);
    filter.addIncludingFilter("testFoo");
    filter.addIncludingFilter("testBar");
    builder.setTestMethodFilter(filter);
    assertContainsOnly(new String[] { "testFoo", "testBar" }, builder.buildSuite());
    builder =
        new JUnitTestSuiteBuilder(
            new ExcelObjectMatrixFactory("com/senacor/ddt/test/TestSuiteBuilderTest.xls", "Test"),
            new TestSuiteConfiguration().addTestCaseFilter(new OnlyOnceFilter()), JUnitTestMethodFilterTest.class);
    filter.clear();
    builder.setTestMethodFilter(filter);
    filter.addExcludingFilter("testBar");
    filter.addIncludingFilter("testFoo");
    filter.addIncludingFilter("testBar");
    assertContainsOnly(new String[] { "testFoo" }, builder.buildSuite());
    builder =
        new JUnitTestSuiteBuilder(
            new ExcelObjectMatrixFactory("com/senacor/ddt/test/TestSuiteBuilderTest.xls", "Test"),
            new TestSuiteConfiguration().addTestCaseFilter(new OnlyOnceFilter()), JUnitTestMethodFilterTest.class);
    builder.setTestMethodFilter(filter);
    filter.clear();
    filter.addExcludingFilter("testFoo");
    assertContainsOnly(new String[] { "testBar", "testFilter", "testFooBar" }, builder.buildSuite());
    builder =
        new JUnitTestSuiteBuilder(
            new ExcelObjectMatrixFactory("com/senacor/ddt/test/TestSuiteBuilderTest.xls", "Test"),
            new TestSuiteConfiguration().addTestCaseFilter(new OnlyOnceFilter()), JUnitTestMethodFilterTest.class);
    builder.setTestMethodFilter(filter);
    filter.clear();
    filter.addExcludingFilter("testFoo.*");
    assertContainsOnly(new String[] { "testBar", "testFilter", }, builder.buildSuite());
  }
  
  private void assertContainsOnly(String[] testNames, TestSuite suite) {
    List list = new ArrayList(Arrays.asList(testNames));
    Enumeration tests = suite.tests();
    while (tests.hasMoreElements()) {
      TestCase test = (TestCase) tests.nextElement();
      assertTrue("suite contained unexpected test '" + test.getName() + "'", list.remove(test.getName()));
    }
    assertTrue("suite did not contain expected test", list.isEmpty());
  }
  
  public TestCaseData getTestCaseData() {
    return null;
  }
  
  public void setTestCaseData(TestCaseData tcd) {
    ; // ignore
  }
}
