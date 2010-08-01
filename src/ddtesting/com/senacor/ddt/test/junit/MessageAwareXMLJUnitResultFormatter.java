/*
 * Copyright (c) 2007 Senacor Technologies AG.
 *  
 * Based on Apache Ant's XMLJUnitResultFormatter, modified by Carl-Eric Menzel,
 * used under the following license:
 *
 *  ------------------------------------------------------------------------
 *  Copyright  2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ------------------------------------------------------------------------
 *  
 * The modifications are made available under the same license as the rest of DDT:
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.junit.FormatterElement;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitVersionHelper;
import org.apache.tools.ant.taskdefs.optional.junit.XMLConstants;
import org.apache.tools.ant.util.DOMElementWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.senacor.ddt.test.DataDrivenTestCase;
import com.senacor.ddt.test.ReportingTestCase;
import com.senacor.ddt.test.report.ReportLevel;
import com.senacor.ddt.test.report.ReportMessage;

/**
 * Prints XML output of the test to a specified Writer.
 * 
 * 
 * @see FormatterElement
 */
public class MessageAwareXMLJUnitResultFormatter implements JUnitResultFormatter, XMLConstants {
  private static final String GREEN = "green";
  
  private static final String RED = "red";
  
  private static final String YELLOW = "yellow";
  
  private static final String MESSAGE = "message";
  
  private static final String ATTR_MESSAGE_TYPE = "type";
  
  private static final String DESCRIPTION = "description";
  
  private static final String ATTR_TEST_ID = "test-id";
  
  private static int idSequence = 0;
  
  /**
   * The XML document.
   */
  private Document doc;
  
  /**
   * The wrapper for the whole test suite.
   */
  private Element rootElement;
  
  /**
   * Element for the current test.
   */
  private final Hashtable testElements = new Hashtable();
  
  /**
   * tests that failed.
   */
  private final Hashtable failedTests = new Hashtable();
  
  /**
   * Timing helper.
   */
  private final Hashtable testStarts = new Hashtable();
  
  /**
   * Where to write the log to.
   */
  private OutputStream out;
  
  private final Set seenTests = new HashSet();
  
  /**
   * Constructor
   */
  public MessageAwareXMLJUnitResultFormatter() {
    // nothing to be done
  }
  
  private static DocumentBuilder getDocumentBuilder() {
    try {
      return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (final Exception exc) {
      throw new ExceptionInInitializerError(exc);
    }
  }
  
  /**
   * @see org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter#setOutput(java.io.OutputStream)
   */
  public void setOutput(final OutputStream out) {
    this.out = out;
  }
  
  /**
   * @see org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter#setSystemOutput(java.lang.String)
   */
  public void setSystemOutput(final String out) {
    formatOutput(SYSTEM_OUT, out);
  }
  
  /**
   * @see org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter#setSystemError(java.lang.String)
   */
  public void setSystemError(final String out) {
    formatOutput(SYSTEM_ERR, out);
  }
  
  /**
   * The whole test suite started.
   * 
   * @param suite
   *          -
   */
  public void startTestSuite(final JUnitTest suite) {
    this.doc = getDocumentBuilder().newDocument();
    this.rootElement = this.doc.createElement(TESTSUITE);
    this.rootElement.setAttribute(ATTR_NAME, suite.getName());
    
    // Output properties
    final Element propsElement = this.doc.createElement(PROPERTIES);
    this.rootElement.appendChild(propsElement);
    
    final Properties props = suite.getProperties();
    if (props != null) {
      final Enumeration e = props.propertyNames();
      while (e.hasMoreElements()) {
        final String name = (String) e.nextElement();
        final Element propElement = this.doc.createElement(PROPERTY);
        propElement.setAttribute(ATTR_NAME, name);
        propElement.setAttribute(ATTR_VALUE, props.getProperty(name));
        propsElement.appendChild(propElement);
      }
    }
  }
  
  /**
   * The whole test suite ended.
   * 
   * @param suite
   *          -
   * @throws BuildException
   *           -
   */
  public void endTestSuite(final JUnitTest suite) throws BuildException {
    this.rootElement.setAttribute(ATTR_TESTS, "" + suite.runCount());
    this.rootElement.setAttribute(ATTR_FAILURES, "" + suite.failureCount());
    this.rootElement.setAttribute(ATTR_ERRORS, "" + suite.errorCount());
    this.rootElement.setAttribute(ATTR_TIME, "" + (suite.getRunTime() / 1000.0));
    if (this.out != null) {
      Writer wri = null;
      try {
        wri = new BufferedWriter(new OutputStreamWriter(this.out, "UTF8"));
        wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        (new DOMElementWriter()).write(this.rootElement, wri, 0, "  ");
        wri.flush();
      } catch (final IOException exc) {
        throw new BuildException("Unable to write log file", exc);
      } finally {
        if ((this.out != System.out) && (this.out != System.err)) {
          if (wri != null) {
            try {
              wri.close();
            } catch (final IOException e) {
              // ignore
            }
          }
        }
      }
    }
  }
  
  /**
   * Interface TestListener.
   * 
   * <p>
   * A new Test is started.
   * 
   * @param t
   *          -
   */
  public void startTest(final Test t) {
    this.testStarts.put(t, new Long(System.currentTimeMillis()));
  }
  
  /**
   * Interface TestListener.
   * 
   * <p>
   * A Test is finished.
   * 
   * @param test
   *          .
   */
  public void endTest(final Test test) {
    if (this.seenTests.contains(test)) {
      return;
    }
    
    // Fix for bug #5637 - if a junit.extensions.TestSetup is
    // used and throws an exception during setUp then startTest
    // would never have been called
    if (!this.testStarts.containsKey(test)) {
      startTest(test);
    }
    
    Element currentTest = null;
    if (!this.failedTests.containsKey(test)) {
      currentTest = this.doc.createElement(TESTCASE);
      currentTest.setAttribute(ATTR_NAME, JUnitVersionHelper.getTestCaseName(test));
      
      // a TestSuite can contain Tests from multiple classes,
      // even tests with the same name - disambiguate them.
      currentTest.setAttribute(ATTR_CLASSNAME, test.getClass().getName());
      this.rootElement.appendChild(currentTest);
      this.testElements.put(test, currentTest);
    } else {
      currentTest = (Element) this.testElements.get(test);
    }
    
    if (test instanceof ReportingTestCase) {
      final ReportingTestCase rtest = (ReportingTestCase) test;
      final Iterator iter = rtest.getReport().getMessages().iterator();
      while (iter.hasNext()) {
        final ReportMessage msg = (ReportMessage) iter.next();
        currentTest.appendChild(formatMessage(getMessageColor(msg), msg.getMessage()));
      }
    }
    if (test instanceof DataDrivenTestCase) {
      final DataDrivenTestCase dtest = (DataDrivenTestCase) test;
      final Element description = this.doc.createElement(DESCRIPTION);
      final Text descText = this.doc.createTextNode(dtest.getTestCaseData().getDescription());
      description.appendChild(descText);
      currentTest.appendChild(description);
    }
    
    currentTest.setAttribute(ATTR_TEST_ID, "" + (idSequence++));
    
    final Long l = (Long) this.testStarts.get(test);
    currentTest.setAttribute(ATTR_TIME, "" + ((System.currentTimeMillis() - l.longValue()) / 1000.0));
    this.seenTests.add(test);
  }
  
  private String getMessageColor(final ReportMessage msg) {
    if (ReportLevel.GREEN == msg.getLevel()) {
      return GREEN;
    } else if (ReportLevel.RED == msg.getLevel()) {
      return RED;
    } else if (ReportLevel.YELLOW == msg.getLevel()) {
      return YELLOW;
    } else {
      return YELLOW;
    }
  }
  
  private Element formatMessage(final String msgType, final String msg) {
    final Element msgElement = this.doc.createElement(MESSAGE);
    msgElement.setAttribute(ATTR_MESSAGE_TYPE, msgType);
    
    final Text msgTextElement = this.doc.createTextNode(msg);
    msgElement.appendChild(msgTextElement);
    
    return msgElement;
  }
  
  /**
   * Interface TestListener for JUnit &lt;= 3.4.
   * 
   * <p>
   * A Test failed.
   * 
   * @param test
   *          -
   * @param t
   *          -
   */
  public void addFailure(final Test test, final Throwable t) {
    formatError(FAILURE, test, t);
  }
  
  /**
   * Interface TestListener for JUnit &gt; 3.4.
   * 
   * <p>
   * A Test failed.
   * 
   * @param test
   *          -
   * @param t
   *          -
   */
  public void addFailure(final Test test, final AssertionFailedError t) {
    addFailure(test, (Throwable) t);
  }
  
  /**
   * Interface TestListener.
   * 
   * <p>
   * An error occurred while running the test.
   * 
   * @param test
   *          -
   * @param t
   *          -
   */
  public void addError(final Test test, final Throwable t) {
    formatError(ERROR, test, t);
  }
  
  private void formatError(final String type, final Test test, final Throwable t) {
    if (test != null) {
      endTest(test);
      this.failedTests.put(test, test);
    }
    
    final Element nested = this.doc.createElement(type);
    Element currentTest = null;
    if (test != null) {
      currentTest = (Element) this.testElements.get(test);
    } else {
      currentTest = this.rootElement;
    }
    
    currentTest.appendChild(nested);
    
    final String message = t.getMessage();
    if ((message != null) && (message.length() > 0)) {
      nested.setAttribute(ATTR_MESSAGE, t.getMessage());
    }
    nested.setAttribute(ATTR_TYPE, t.getClass().getName());
    
    final String strace = JUnitTestRunner.getFilteredTrace(t);
    final Text trace = this.doc.createTextNode(strace);
    nested.appendChild(trace);
  }
  
  private void formatOutput(final String type, final String output) {
    final Element nested = this.doc.createElement(type);
    this.rootElement.appendChild(nested);
    nested.appendChild(this.doc.createCDATASection(output));
  }
} // XMLJUnitResultFormatter
