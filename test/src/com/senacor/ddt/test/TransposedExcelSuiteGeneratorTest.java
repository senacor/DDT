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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;

import com.senacor.ddt.objectmatrix.excel.ExcelObjectMatrixFactory;
import com.senacor.ddt.test.junit.JUnitTestSuiteBuilder;

/**
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class TransposedExcelSuiteGeneratorTest extends TestSuiteBuilderTest {
  public static Test suite() throws NoSuchMethodException, IOException, InvocationTargetException,
      IllegalAccessException {
    ExcelObjectMatrixFactory omf =
        new ExcelObjectMatrixFactory("com/senacor/ddt/test/TestSuiteBuilderTest.xls", new String[] { "TestTransposed" });
    omf.setTransposed(true);
    
    TestSuiteConfiguration config = new TestSuiteConfiguration();
    config.setFirstTestCaseName("T2");
    config.setLastTestCaseName("T4");
    config.addTestCaseFilter(new BooleanRowTestFilter("testable"));
    
    JUnitTestSuiteBuilder gen = new JUnitTestSuiteBuilder(omf, config, MovedColumnsExcelSuiteGeneratorTest.class);
    
    return (Test) gen.buildSuite();
  }
}
