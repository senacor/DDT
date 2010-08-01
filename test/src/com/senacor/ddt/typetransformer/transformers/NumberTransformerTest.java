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

package com.senacor.ddt.typetransformer.transformers;

import java.math.BigDecimal;
import java.math.BigInteger;

import junit.framework.TestCase;

import org.apache.commons.beanutils.ConvertUtils;

import com.senacor.ddt.typetransformer.NoSuccessfulTransformerException;
import com.senacor.ddt.typetransformer.Transformer;

public class NumberTransformerTest extends TestCase {
  private Transformer transformer;
  
  private static final String TWENTY_THREE = "23";
  
  private static final byte TWENTY_THREE_INT = 23;
  
  private static final byte MINUS_FORTY_TWO_INT = -42;
  
  private static final String MINUS_FORTY_TWO = "-42";
  
  private static final float TWO_POINT_ONE_FLOAT = 2.1f;
  
  private static final String TWO_POINT_ONE = "2.1";
  
  private static final float MINUS_THREE_POINT_SIX_FLOAT = -3.6f;
  
  private static final String MINUS_THREE_POINT_SIX = "-3.6";
  
  protected void setUp() throws Exception {
    this.transformer = new Transformer();
    ConvertUtils.deregister(Integer.class);
    ConvertUtils.deregister(Short.class);
    ConvertUtils.deregister(Number.class);
    ConvertUtils.deregister(BigDecimal.class);
    ConvertUtils.deregister(BigInteger.class);
    ConvertUtils.deregister(Byte.class);
    ConvertUtils.deregister(Double.class);
    ConvertUtils.deregister(Float.class);
    ConvertUtils.deregister(Long.class);
    
  }
  
  public void testDouble() throws Exception {
    assertEquals(TWO_POINT_ONE_FLOAT, ((Double) this.transformer.transform(TWO_POINT_ONE, Double.class)).doubleValue(),
        0.01);
    assertEquals(MINUS_THREE_POINT_SIX_FLOAT,
        ((Double) this.transformer.transform(MINUS_THREE_POINT_SIX, Double.class)).doubleValue(), 0.01);
  }
  
  public void testBigDecimal() throws Exception {
    assertEquals(new BigDecimal(TWO_POINT_ONE), this.transformer.transform(TWO_POINT_ONE, BigDecimal.class));
    assertEquals(new BigDecimal(MINUS_THREE_POINT_SIX), this.transformer.transform(MINUS_THREE_POINT_SIX,
        BigDecimal.class));
  }
  
  public void testFloat() throws Exception {
    assertEquals(new Float(TWO_POINT_ONE_FLOAT), this.transformer.transform(TWO_POINT_ONE, Float.class));
    assertEquals(new Float(MINUS_THREE_POINT_SIX_FLOAT), this.transformer.transform(MINUS_THREE_POINT_SIX, Float.class));
  }
  
  public void testBigInteger() throws Exception {
    assertEquals(new BigInteger(TWENTY_THREE), this.transformer.transform(TWENTY_THREE, BigInteger.class));
    assertEquals(new BigInteger(MINUS_FORTY_TWO), this.transformer.transform(MINUS_FORTY_TWO, BigInteger.class));
  }
  
  public void testInteger() throws Exception {
    assertEquals(new Integer(TWENTY_THREE_INT), this.transformer.transform(TWENTY_THREE, Integer.class));
    assertEquals(new Integer(MINUS_FORTY_TWO_INT), this.transformer.transform(MINUS_FORTY_TWO, Integer.class));
  }
  
  public void testShort() throws Exception {
    assertEquals(new Short(TWENTY_THREE_INT), this.transformer.transform(TWENTY_THREE, Short.class));
    assertEquals(new Short(MINUS_FORTY_TWO_INT), this.transformer.transform(MINUS_FORTY_TWO, Short.class));
  }
  
  public void testByte() throws Exception {
    assertEquals(new Byte(TWENTY_THREE_INT), this.transformer.transform(TWENTY_THREE, Byte.class));
    assertEquals(new Byte(MINUS_FORTY_TWO_INT), this.transformer.transform(MINUS_FORTY_TWO, Byte.class));
  }
  
  public void testLong() throws Exception {
    assertEquals(new Long(TWENTY_THREE_INT), this.transformer.transform(TWENTY_THREE, Long.class));
    assertEquals(new Long(MINUS_FORTY_TWO_INT), this.transformer.transform(MINUS_FORTY_TWO, Long.class));
  }
  
  public void testFailsCorrectly() throws Exception {
    try {
      this.transformer.transform(new Object(), Exception.class);
      fail("should have failed");
    } catch (final NoSuccessfulTransformerException e) {
      ; // expected
    }
  }
}
