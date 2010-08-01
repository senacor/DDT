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

import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kohsuke.junit.ParallelTestSuite;

import com.senacor.ddt.objectmatrix.ObjectMatrixFactory;
import com.senacor.ddt.test.AbstractTestSuiteBuilder;
import com.senacor.ddt.test.DataDrivenTestCase;
import com.senacor.ddt.test.TestCaseData;
import com.senacor.ddt.test.TestSuiteConfiguration;
import com.senacor.ddt.util.ParamChecker;
import com.senacor.ddt.util.StringFilter;

/**
 * Test Suite builder for JUnit 3.8 test suites. This class takes an {@link ObjectMatrixFactory}, a
 * {@link TestSuiteConfiguration} and a test class and creates a DDT-enabled test suite at runtime.
 * <p>
 * Typical usage with static suite() method in your test class:
 * 
 * <pre>
 *              public static {@link Test} suite() {
 *                ObjectMatrixFactory omf = ...;
 *                TestSuiteConfiguration config = new TestSuiteConfiguration();
 *                ...set config options...
 *                return new JUnitTestSuiteBuilder(omf, config, MyTest.class).buildSuite(); 
 *              }
 * </pre>
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class JUnitTestSuiteBuilder extends AbstractTestSuiteBuilder {
  private static final Log log = LogFactory.getLog(JUnitTestSuiteBuilder.class);
  
  private final Class testClass;
  
  private StringFilter testMethodFilter;
  
  /**
   * Construct a builder.
   * 
   * @param matrixFactory
   *          Factory. Must not be null.
   * @param config
   *          Configuration.
   * @param testClass
   *          The class of your test case. Must implement {@link DataDrivenTestCase}. Must not be null.
   * @see AbstractTestSuiteBuilder#AbstractTestSuiteBuilder(ObjectMatrixFactory, TestSuiteConfiguration)
   */
  public JUnitTestSuiteBuilder(final ObjectMatrixFactory matrixFactory, final TestSuiteConfiguration config,
      final Class testClass) {
    super(matrixFactory, config);
    ParamChecker.notNull("testClass", testClass);
    ParamChecker.require("testClass must implement DataDrivenTestCase", DataDrivenTestCase.class
        .isAssignableFrom(testClass));
    this.testClass = testClass;
  }
  
  /**
   * Construct a builder with the configuration set to defaults.
   * 
   * @param matrixFactory
   *          Factory. May not be null.
   * @param testClass
   *          The class of your test case. Must implement {@link DataDrivenTestCase}. Must not be null.
   * @see #JUnitTestSuiteBuilder(ObjectMatrixFactory, TestSuiteConfiguration, Class)
   */
  public JUnitTestSuiteBuilder(final ObjectMatrixFactory matrixFactory, final Class testClass) {
    this(matrixFactory, null, testClass);
  }
  
  /**
   * Create a JUnit 3.8 test suite from the given information.
   * 
   * @return A full test suite, ready to run.
   */
  public TestSuite buildSuite() {
    assert this.testClass != null;
    final TestCaseData[] testCaseDatas = prepareSuite();
    TestSuite masterSuite; // this one will be returned after we fill it with all the tests we
    // found
    if (getConfig().getNumberOfThreads() > 1) {
      if (log.isInfoEnabled()) {
        log.info("creating ParallelTestSuite with " + getConfig().getNumberOfThreads() + " threads");
      }
      masterSuite = new ParallelTestSuite(this.testClass.getName(), getConfig().getNumberOfThreads());
    } else {
      log.info("only one thread specified, creating regular TestSuite");
      masterSuite = new TestSuite(this.testClass.getName());
    }
    
    log.debug("iterating through testcasedata array");
    for (int tcdIndex = 0; tcdIndex < testCaseDatas.length; tcdIndex++) {
      final TestCaseData currentTcd = testCaseDatas[tcdIndex];
      assert currentTcd != null : "TCD at index " + tcdIndex + " is null!";
      if (log.isDebugEnabled()) {
        log.debug("creating tests for testcase '" + currentTcd.getTestId() + "'");
      }
      
      final TestSuite currentTestSuite = new TestSuite(this.testClass); // this contains all the
      // tests JUnit finds for the
      // test
      // class
      currentTestSuite.setName(currentTcd.getTestId());
      
      final Enumeration tests = currentTestSuite.tests();
      // walk over the tests and inject the test case data
      while (tests.hasMoreElements()) {
        final Test currentTest = (Test) tests.nextElement();
        if (isAcceptedByFilters(currentTest)) {
          if (currentTest instanceof DataDrivenTestCase) {
            ((DataDrivenTestCase) currentTest).setTestCaseData(new TestCaseData(currentTcd));
          }
          // don't inject currentTestSuite, but simply flatten the suite by adding all individual
          // tests to the master suite
          masterSuite.addTest(currentTest);
        }
      }
    }
    
    return masterSuite;
  }
  
  private boolean isAcceptedByFilters(final Test test) {
    final boolean accepted;
    if (this.testMethodFilter == null) {
      // we have no filters => accept
      accepted = true;
    } else if (test instanceof TestCase) {
      final TestCase testCase = (TestCase) test;
      final String testName = testCase.getName();
      accepted = this.testMethodFilter.accepts(testName);
      if ((accepted == false) && log.isInfoEnabled()) {
        log.info("Test case '" + testName + "' rejected by filters");
      }
    } else {
      // we have a Test that is not a TestCase => we can't see the test's name, so we have to accept
      // it blindly
      accepted = true;
    }
    return accepted;
  }
  
  /**
   * @return The active test method filter. May be null.
   */
  public StringFilter getTestMethodFilter() {
    return this.testMethodFilter;
  }
  
  /**
   * Set a StringFilter that will be used to filter test methods by name.
   * 
   * @param filter
   *          The filter. May be null to disable all filtering.
   * @return <code>this</code>, for method chaining.
   */
  public JUnitTestSuiteBuilder setTestMethodFilter(final StringFilter filter) {
    this.testMethodFilter = filter;
    return this;
  }
}
