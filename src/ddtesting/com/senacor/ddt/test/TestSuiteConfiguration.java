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
import java.util.List;

import com.senacor.ddt.objectmatrix.beanfiller.BeanFiller;
import com.senacor.ddt.test.junit.JUnitTestSuiteBuilder;
import com.senacor.ddt.util.ParamChecker;

/**
 * Container for various configuration options for building a test suite.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class TestSuiteConfiguration {
  private String firstTestCaseName;
  
  private String lastTestCaseName;
  
  private boolean ignoreAllNulls;
  
  private int numberOfThreads = 1;
  
  private final List filters = new ArrayList();
  
  private BeanAccessStrategyFactory beanAccessStrategyFactory;
  
  /**
   * @return first test case name
   */
  public String getFirstTestCaseName() {
    return this.firstTestCaseName;
  }
  
  /**
   * Convenience shortcut for adding a {@link NameListTestCaseFilter} to the configuration.
   * <p>
   * Implementation detail: This is handled by {@link AbstractTestSuiteBuilder}, which reads this property and adds the
   * filter if necessary.
   * 
   * @param firstTestCaseName
   *          The first allowed test case name.
   */
  public void setFirstTestCaseName(final String firstTestCaseName) {
    this.firstTestCaseName = firstTestCaseName;
  }
  
  /**
   * @return last test case name
   */
  public String getLastTestCaseName() {
    return this.lastTestCaseName;
  }
  
  /**
   * Convenience shortcut for adding a {@link NameListTestCaseFilter} to the configuration.
   * <p>
   * Implementation detail: This is handled by {@link AbstractTestSuiteBuilder}, which reads this property and adds the
   * filter if necessary.
   * 
   * @param lastTestCaseName
   *          The last allowed test case name.
   */
  public void setLastTestCaseName(final String lastTestCaseName) {
    this.lastTestCaseName = lastTestCaseName;
  }
  
  /**
   * @return number of threads
   */
  public int getNumberOfThreads() {
    return this.numberOfThreads;
  }
  
  /**
   * Set the number of test threads to run the test cases in parallel. Defaults to 1 (no parallel execution). This might
   * not be supported by all test suite builders. {@link JUnitTestSuiteBuilder} supports this property.
   * 
   * @param numberOfThreads
   *          number of threads
   */
  public void setNumberOfThreads(final int numberOfThreads) {
    this.numberOfThreads = numberOfThreads;
  }
  
  /**
   * Add a {@link TestCaseFilter}.
   * 
   * @param filter
   *          filter
   * @return <code>this</code> for method chaining
   */
  public TestSuiteConfiguration addTestCaseFilter(final TestCaseFilter filter) {
    ParamChecker.notNull("filter", filter);
    this.filters.add(filter);
    return this;
  }
  
  /**
   * @return the filters
   */
  public List getFilters() {
    return this.filters;
  }
  
  /**
   * @see BeanFiller#isIgnoreAllNulls()
   * @return ignore flag
   */
  public boolean getIgnoreAllNulls() {
    return this.ignoreAllNulls;
  }
  
  /**
   * @param ignoreAllNullRows
   *          ignore flag
   * @see BeanFiller#setIgnoreAllNulls(boolean)
   */
  public void setIgnoreAllNulls(final boolean ignoreAllNullRows) {
    this.ignoreAllNulls = ignoreAllNullRows;
  }
  
  /**
   * @return the strategy
   */
  public BeanAccessStrategyFactory getBeanAccessStrategyFactory() {
    return this.beanAccessStrategyFactory;
  }
  
  /**
   * Set a {@link BeanAccessStrategyFactory} for this configuration. All {@link BeanFiller} instances created with this
   * configuration will use the strategy returned by the given factory. Defaults to
   * {@link JavaBeanAccessStrategyFactory}.
   * 
   * @param beanAccessStrategyFactory
   *          -
   */
  public void setBeanAccessStrategyFactory(final BeanAccessStrategyFactory beanAccessStrategyFactory) {
    this.beanAccessStrategyFactory = beanAccessStrategyFactory;
  }
}
