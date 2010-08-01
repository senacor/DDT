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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.senacor.ddt.objectmatrix.ObjectMap;
import com.senacor.ddt.objectmatrix.ObjectMatrix;
import com.senacor.ddt.objectmatrix.StringMatrix;
import com.senacor.ddt.objectmatrix.beanfiller.BeanFiller;
import com.senacor.ddt.util.ParamChecker;
import com.senacor.ddt.util.StringFilter;

/**
 * Facade class uniting the methods of ObjectMatrix and BeanFiller for convenient use in a DataDrivenTestCase.
 * 
 * See {@link ObjectMatrix} and {@link BeanFiller} for details on the various methods.
 * 
 * @author Carl-Eric Menzel
 * @author Silvia Scheurich
 * @version $Id$
 */
public final class TestCaseData {
  private final String testCaseName;
  
  private final String testId;
  
  private final ObjectMap objectMap;
  
  private final StringFilter beanFillerKeyFilter;
  
  private TestSuiteConfiguration config;
  
  private boolean ignoreAllNulls;
  
  /**
   * @param testCaseName
   *          Name of the test case (usually the name of the column)
   * @param objectMatrix
   *          the underlying ObjectMatrix implementation
   * @param config
   *          The configuration
   */
  public TestCaseData(final ObjectMatrix objectMatrix, final String testCaseName, final TestSuiteConfiguration config) {
    ParamChecker.notNull("objectMatrix", objectMatrix);
    ParamChecker.notNull("testCaseName", testCaseName);
    ParamChecker.notNull("config", config);
    if (objectMatrix.getColNames().contains(testCaseName)) {
      this.testCaseName = testCaseName;
      this.objectMap = objectMatrix.getObjectMapForColumn(testCaseName);
      this.config = config;
      this.testId = "(Test '" + getTestCaseName() + "' on Object Matrix '" + objectMatrix.getMatrixIdentifier() + "') ";
      this.ignoreAllNulls = config.getIgnoreAllNulls();
    } else {
      throw new IllegalArgumentException("The test case named '" + testCaseName
          + "' doesn't exist in the given object matrix.");
    }
    this.beanFillerKeyFilter = new StringFilter();
  }
  
  private BeanFiller createBeanFiller() {
    BeanFiller bf;
    if (this.config.getBeanAccessStrategyFactory() != null) {
      bf = new BeanFiller(this.objectMap, this.config.getBeanAccessStrategyFactory().create());
    } else {
      bf = new BeanFiller(this.objectMap);
    }
    bf.setIgnoreAllNulls(this.ignoreAllNulls);
    bf.setKeyFilter(this.beanFillerKeyFilter);
    return bf;
  }
  
  /**
   * Copy Constructor
   * 
   * @param original
   */
  public TestCaseData(final TestCaseData original) {
    this.testCaseName = original.testCaseName;
    this.objectMap = original.objectMap;
    this.config = original.config;
    this.beanFillerKeyFilter = new StringFilter(original.beanFillerKeyFilter);
    this.testId = original.testId;
    this.ignoreAllNulls = original.ignoreAllNulls;
  }
  
  /**
   * @see BeanFiller#fillBean(java.lang.String, java.lang.Object)
   */
  public Object fillBean(final Object bean, final String beanName) {
    return createBeanFiller().fillBean(beanName, bean);
  }
  
  /**
   * @see BeanFiller#fillBean(Object)
   */
  public Object fillBean(final Object bean) {
    return createBeanFiller().fillBean(bean);
  }
  
  /**
   * @see ObjectMap#getBoolean(java.lang.String)
   */
  public Boolean getBoolean(final String rowName) {
    return this.objectMap.getBoolean(rowName);
  }
  
  /**
   * @see ObjectMap#getString(java.lang.String)
   */
  public String getString(final String rowName) {
    return this.objectMap.getString(rowName);
  }
  
  /**
   * @see ObjectMap#getBigDecimal(java.lang.String)
   */
  public BigDecimal getBigDecimal(final String rowName) {
    return this.objectMap.getBigDecimal(rowName);
  }
  
  /**
   * @see ObjectMap#getInteger(java.lang.String)
   */
  public Integer getInteger(final String rowName) {
    return this.objectMap.getInteger(rowName);
  }
  
  /**
   * @see ObjectMap#getLong(java.lang.String)
   */
  public Long getLong(final String rowName) {
    return this.objectMap.getLong(rowName);
  }
  
  /**
   * @see ObjectMap#getDouble(java.lang.String)
   */
  public Double getDouble(final String rowName) {
    return this.objectMap.getDouble(rowName);
  }
  
  /**
   * @see ObjectMap#getDate(java.lang.String)
   */
  public Date getDate(final String rowName) {
    return this.objectMap.getDate(rowName);
  }
  
  /**
   * @return This test case's name. Usually the title of the corresponding column in the underlying ObjectMatrix.
   */
  public String getTestCaseName() {
    return this.testCaseName;
  }
  
  /**
   * @return A unique identifier for this testcase. This consists of the {@link #getTestCaseName() testcase's name} and
   *         the underlying ObjectMatrix's {@link StringMatrix#getMatrixIdentifier() identifier}.
   */
  public String getTestId() {
    return this.testId;
  }
  
  /**
   * @see ObjectMap#getObject(java.lang.String,java.lang.Class)
   */
  public Object getObject(final Class type, final String rowName) {
    return this.objectMap.getObject(rowName, type);
  }
  
  /**
   * Returns a textual description for this testcase if the underlying ObjectMatrix has one. This description must be
   * contained in the row "description".
   * 
   * @return The description, or an empty String if there is none.
   */
  public String getDescription() {
    if (this.objectMap.getKeys().contains("description")) {
      return getString("description");
    } else {
      return "";
    }
  }
  
  /**
   * Returns the list of all available row names for the underlying matrix.
   * 
   * @return the list
   */
  public List getRowNames() {
    return this.objectMap.getKeys();
  }
  
  /**
   * @see BeanFiller#fillList(java.lang.String, java.util.List, java.lang.Class)
   */
  public List fillList(final String rowNamePrefix, final List list, final Class elementType) {
    return createBeanFiller().fillList(rowNamePrefix, list, elementType);
  }
  
  /**
   * @see BeanFiller#fillMap(java.lang.String, java.util.Map, java.lang.Class)
   */
  public Map fillMap(final String rowNamePrefix, final Map map, final Class elementType) {
    return createBeanFiller().fillMap(rowNamePrefix, map, elementType);
  }
  
  /**
   * @see BeanFiller#setIgnoreAllNulls(boolean)
   */
  public TestCaseData setIgnoreAllNullRows(final boolean ignoreAllNullRows) {
    this.ignoreAllNulls = ignoreAllNullRows;
    return this;
  }
  
  /**
   * @see ObjectMap#getAnnotation(String)
   */
  public Properties getAnnotation(final String rowName) {
    return this.objectMap.getAnnotation(rowName);
  }
  
  /**
   * @see BeanFiller#createAndFillBean(String)
   */
  public Object createAndFillBean(final String beanName) {
    return createBeanFiller().createAndFillBean(beanName);
  }
  
  /**
   * @return The key filter used while bean filling.
   * @see BeanFiller#setKeyFilter(StringFilter)
   */
  public StringFilter getBeanFillerKeyFilter() {
    return this.beanFillerKeyFilter;
  }
}
