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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.objectmatrix.ObjectMatrix;
import com.senacor.ddt.objectmatrix.ObjectMatrixFactory;
import com.senacor.ddt.objectmatrix.StringMatrix;
import com.senacor.ddt.util.ParamChecker;

/**
 * Abstract superclass for TestSuiteBuilders. This class provides the methods to collect test case data from object
 * matrices provides test suite configuration data, test case filtering and registers converters. Subclasses must call
 * {@link #prepareSuite()} to receive the pre-filtered array of applicable TestCaseData instances and then construct an
 * appropriate test suite over those instances.
 * <p>
 * Instances of this class should be used once and then discarded, since not all ObjectMatrixFactories can be re-used.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public abstract class AbstractTestSuiteBuilder {
  private static final Log log = LogFactory.getLog(AbstractTestSuiteBuilder.class);
  
  static {
    // load version number and infotext to print them on INFO level. all errors that happen here
    // will be logged and
    // discarded, so the tests will operate normally in any case.
    final Properties p = new Properties();
    try {
      final URL propertiesUrl = AbstractTestSuiteBuilder.class.getResource("/ddt-release-version.properties");
      if (propertiesUrl != null) {
        final InputStream inputStream = propertiesUrl.openStream();
        p.load(inputStream);
        log.info(p.getProperty("ddt-info-text"));
      } else {
        log.warn("ddt-release-version.properties not found. DON'T PANIC! Your program is not affected.");
      }
    } catch (final Exception e) {
      log.warn("Error while loading ddt-release-version.properties. DON'T PANIC! Your program is not affected.", e);
    }
  }
  
  private final ObjectMatrixFactory matrixFactory;
  
  private final TestSuiteConfiguration config;
  
  /**
   * Construct a new TestSuiteBuilder.
   * 
   * @param matrixFactory
   *          An {@link ObjectMatrixFactory} that will provide the object matrices from which this builder will extract
   *          the {@link TestCaseData} instances. Must not be null.
   * @param config
   *          The configuration for the test suite to be built. If this parameter is null, the default values in
   *          {@link TestSuiteConfiguration} will be used.
   */
  protected AbstractTestSuiteBuilder(final ObjectMatrixFactory matrixFactory, final TestSuiteConfiguration config) {
    ParamChecker.notNull("matrixFactory", matrixFactory);
    this.matrixFactory = matrixFactory;
    if (config == null) {
      log.info("No TestSuiteConfiguration given, using defaults.");
      this.config = new TestSuiteConfiguration();
    } else {
      this.config = config;
    }
  }
  
  /**
   * Prepares the test suite. Converters available in the TestSuiteConfiguration are registered. If necessary, a
   * {@link NameRangeTestCaseFilter} is created and added to the filters in the TestSuiteConfiguration. Object matrices
   * are retrieved from the factory, {@link TestCaseData} instances are collected and returned to the calling subclass.
   * 
   * @return an array of all TestCaseData instances that are applicable to the current configuration.
   */
  protected final synchronized TestCaseData[] prepareSuite() {
    createFiltersIfNecessary();
    
    final ObjectMatrix[] matrices = this.matrixFactory.create();
    assert matrices.length > 0 : "matrixFactory returned empty array!";
    
    final TestCaseData[] testCaseDatas = collectActiveTestCaseData(matrices);
    if (testCaseDatas.length == 0) {
      throw new NoActiveTestCasesException();
    }
    
    return testCaseDatas;
  }
  
  /**
   * Create an array of TestCaseData instances over the columns of the object matrices. Apply the filters in the
   * TestSuiteConfiguration and return those that pass.
   */
  private TestCaseData[] collectActiveTestCaseData(final ObjectMatrix[] matrices) {
    log.debug("walking through objectmatrices to collect data for active test cases");
    
    final List collectedTestCaseData = new ArrayList();
    for (int matrixIndex = 0; matrixIndex < matrices.length; matrixIndex++) {
      final ObjectMatrix currentMatrix = matrices[matrixIndex];
      assert currentMatrix != null : "matrix at index " + matrixIndex + " is null";
      if (log.isDebugEnabled()) {
        log.debug("looking at matrix '" + currentMatrix.getMatrixIdentifier() + "'");
      }
      
      final Iterator testCaseNames = currentMatrix.getColNames().iterator();
      while (testCaseNames.hasNext()) {
        final String currentTestCaseName = (String) testCaseNames.next();
        if ((currentTestCaseName.length() == 0) || StringMatrix.Tokens.RESERVED.equals(currentTestCaseName)) {
          // we don't take columns that are reserved or don't have a name. all valid test cases have
          // a name.
          continue;
        } else {
          // column is valid, create TCD...
          final TestCaseData currentTestCaseData = new TestCaseData(currentMatrix, currentTestCaseName, this.config);
          
          // ...but only add it to the result if they pass the filters of Khazad-D�m.
          if (isTestAllowedToRun(currentTestCaseData)) {
            collectedTestCaseData.add(currentTestCaseData);
            if (log.isDebugEnabled()) {
              log.debug("added TestCase '" + currentTestCaseName + "'");
            }
          }
        }
      }
    }
    
    return (TestCaseData[]) collectedTestCaseData.toArray(new TestCaseData[collectedTestCaseData.size()]);
  }
  
  /**
   * If either of the first/lastTestCaseName properties in TestSuiteConfiguration is specified, add a
   * NameRangeTestCaseFilter to the configuration.
   */
  private void createFiltersIfNecessary() {
    assert this.config != null;
    if ((this.config.getFirstTestCaseName() != null) || (this.config.getLastTestCaseName() != null)) {
      log.debug("adding NameRangeTestCaseFilter");
      this.config.addTestCaseFilter(new NameRangeTestCaseFilter(this.config.getFirstTestCaseName(), this.config
          .getLastTestCaseName()));
    } else {
      log.debug("no test case name range defined, skipping NameRangeTestCaseFilter");
    }
  }
  
  private boolean isTestAllowedToRun(final TestCaseData tcd) {
    assert this.config != null;
    
    final Iterator iter = this.config.getFilters().iterator();
    while (iter.hasNext()) {
      final TestCaseFilter filter = (TestCaseFilter) iter.next();
      if (!filter.isTestCaseAllowedToRun(tcd)) {
        if (log.isInfoEnabled()) {
          log.info("TestCase '" + tcd.getTestCaseName() + "' rejected by filter '" + filter.toString() + "'");
        }
        
        return false;
      }
    }
    
    return true;
  }
  
  protected TestSuiteConfiguration getConfig() {
    return this.config;
  }
}
