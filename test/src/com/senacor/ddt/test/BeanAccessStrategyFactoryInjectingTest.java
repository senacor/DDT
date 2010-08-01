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

import java.util.Properties;

import junit.framework.TestCase;

import com.senacor.ddt.objectmatrix.ObjectMatrixFactory;
import com.senacor.ddt.objectmatrix.beanfiller.BeanAccessStrategy;
import com.senacor.ddt.objectmatrix.beanfiller.PropertyAccessException;
import com.senacor.ddt.objectmatrix.beanfiller.PropertyNotFoundException;
import com.senacor.ddt.objectmatrix.excel.ExcelObjectMatrixFactory;
import com.senacor.ddt.typetransformer.Transformer;

public class BeanAccessStrategyFactoryInjectingTest extends TestCase {
  public class YesItsWorkingException extends RuntimeException {
    // no implementation
  }
  
  public void testInjectBASFactory() throws Exception {
    final TestSuiteConfiguration config = new TestSuiteConfiguration();
    config.setBeanAccessStrategyFactory(new BeanAccessStrategyFactory() {
      
      public BeanAccessStrategy create() {
        return new BeanAccessStrategy() {
          
          public Object writeProperty(final Object bean, final String propertyName, final Object propertyValue,
              final Properties annotations) throws PropertyNotFoundException, PropertyAccessException {
            throw new YesItsWorkingException();
          }
          
          public Object readProperty(final Object bean, final String propertyName, final Properties annotations)
              throws PropertyNotFoundException, PropertyAccessException {
            throw new YesItsWorkingException();
          }
          
          public Object instantiateAndSet(final Object bean, final String propertyName, final Class expectedType,
              final Properties annotations) throws PropertyNotFoundException, PropertyAccessException {
            throw new YesItsWorkingException();
          }
          
          public Object instantiate(final Class type) {
            throw new YesItsWorkingException();
          }
          
          public Class getPropertyType(final Object bean, final String propertyName, final Properties annotation)
              throws PropertyNotFoundException, PropertyAccessException {
            throw new YesItsWorkingException();
          }
          
          public boolean doesObjectImplement(final Class typeToImplement, final Object objectToCheck) {
            throw new YesItsWorkingException();
          }
          
          public void setTransformer(final Transformer transformer) {
            throw new YesItsWorkingException();
          }
          
          public ObjectHolder createHolder(final Object bean, final Properties annotation) {
            throw new YesItsWorkingException();
          }
          
          public Class getConcreteType(final Class type) {
            throw new YesItsWorkingException();
          }
          
        };
      }
    });
    final ObjectMatrixFactory omf =
        new ExcelObjectMatrixFactory(getClass().getResourceAsStream("BeanAccessStrategyFactoryInjectingTest.xls"),
            new String[] { "Test" });
    final TestCaseData tcd = new TestCaseData(omf.create()[0], "Col1", config);
    try {
      tcd.fillBean(new Object(), "foo");
      fail("if the strategy was used it would have thrown a YesItsWorkingException!");
    } catch (final YesItsWorkingException e) {
      ; // expected
    }
  }
}
