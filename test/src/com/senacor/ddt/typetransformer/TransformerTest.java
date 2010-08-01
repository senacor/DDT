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

package com.senacor.ddt.typetransformer;

import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import com.senacor.ddt.typetransformer.transformers.RelativeDateTransformer;
import com.senacor.ddt.typetransformer.transformers.StringPatternDateTransformer;

public class TransformerTest extends TestCase {
  private Transformer transformer;
  
  protected void setUp() throws Exception {
    super.setUp();
    this.transformer = new Transformer();
  }
  
  public void testUninitializedClasses() throws Exception {
    // See comment inside Transformer#transform to learn what this does.
    this.transformer.addTransformer(new AbstractTransformer() {
      public Object transform(final Object object, final Class targetType) {
        final String value = (String) object;
        return BeanOne.getBean(value);
      }
    });
    
    final BeanOne beanOne =
        ((BeanOne) this.transformer.transform("foo", Class.forName("com.senacor.ddt.typetransformer.BeanTwo", false,
            getClass().getClassLoader())));
    assertNotNull(beanOne);
    assertEquals("foo", beanOne.value);
  }
  
  public void testObjectToString() throws Exception {
    final String TEST_STRING = "0xDEADBEEF";
    // this is supposed to be managed by the ObjectToStringTransformer that is created automatically in Transformer
    final String result = (String) this.transformer.transform(new Object() {
      public String toString() {
        return TEST_STRING;
      }
    }, String.class);
    assertEquals(TEST_STRING, result);
  }
  
  public void testCannotTransformWithoutTransformers() throws Exception {
    try {
      new Transformer().transform(new Object(), Number.class);
      fail("no suitable transformers are registered, should have thrown exception");
    } catch (final NoSuccessfulTransformerException e) {
      ; // expected;
    }
  }
  
  public void testRecursiveTransformerInjection() throws Exception {
    final RecursiveTestTransformer rt = new RecursiveTestTransformer();
    this.transformer.addTransformer(rt);
    assertEquals(this.transformer, rt.master);
  }
  
  public void testCannotAddSameTransformerInstance() throws Exception {
    final SpecificTransformer trans = new StringPatternDateTransformer("foo");
    this.transformer.addTransformer(trans);
    try {
      this.transformer.addTransformer(trans);
      fail("transformer was already added, should have thrown IllegalArgumentException");
    } catch (final IllegalArgumentException e) {
      ; // expected
    }
  }
  
  public void testFindsCorrectTransformer_Single() throws Exception {
    this.transformer.addTransformer(new PassThroughTransformer() {
      
      protected boolean canTransform(final Class sourceType, final Class targetType) {
        return (sourceType.equals(String.class));
      }
    });
    assertEquals("foo", this.transformer.transform("foo", String.class));
    try {
      this.transformer.transform("bar", Byte.class);
      fail("wrong type, should have thrown exception");
    } catch (final TransformationException e) {
      ; // expected
    }
  }
  
  public void testTransformerChain() throws Exception {
    this.transformer.addTransformer(new AbstractTransformer() {
      public Object transform(final Object object, final Class targetType) {
        return "A";
      }
    });
    this.transformer.addTransformer(RelativeDateTransformer.INSTANCE);
    this.transformer.addTransformer(StringPatternDateTransformer.DATETRANSFORMER_ISO_8601_DATE_ONLY);
    assertEquals("A", this.transformer.transform(new Object(), String.class));
  }
  
  public void testJakartaConverter() throws Exception {
    ConvertUtils.register(new Converter() {
      
      public Object convert(final Class targetType, final Object value) {
        return new TestBean(((String) value).toUpperCase());
      }
    }, TestBean.class);
    
    final TestBean bean = (TestBean) this.transformer.transform("foo", TestBean.class);
    assertEquals("FOO", bean.string);
    try {
      this.transformer.transform("foo", TestCase.class);
      fail("no suitable transformer was registered, should have thrown exception");
    } catch (final NoSuccessfulTransformerException e) {
      ; // expected
    }
    
    // try whether a non-jakarta transformer still works
    this.transformer.addTransformer(new PassThroughTransformer() {
      
      protected boolean canTransform(final Class sourceType, final Class targetType) {
        return (targetType.equals(Long.class));
      }
    });
    assertEquals(new Long(4711), this.transformer.transform(new Long(4711), Long.class));
  }
  
  public void testTransformPrimitive() throws Exception {
    final Double d = (Double) Transformer.get().transform("0.5", Double.TYPE);
    assertEquals(Double.valueOf("0.5"), d);
  }
  
  public void testReusingTransformers() throws Exception {
    final SpecificTransformer returningA = new AbstractTransformer() {
      public Object transform(final Object object, final Class targetType) {
        return "A";
      }
    };
    
    final SpecificTransformer returningB = new AbstractTransformer() {
      public Object transform(final Object object, final Class targetType) {
        return "B";
      }
    };
    
    final SpecificTransformer dateTrans = new RelativeDateTransformer();
    
    final Transformer shouldReturnA = new Transformer();
    shouldReturnA.addTransformer(returningB);
    shouldReturnA.addTransformer(returningA);
    shouldReturnA.addTransformer(dateTrans);
    final Transformer shouldReturnB = new Transformer();
    shouldReturnB.addTransformer(returningA);
    shouldReturnB.addTransformer(returningB);
    shouldReturnB.addTransformer(dateTrans);
    assertEquals("A", shouldReturnA.transform(new Object(), String.class));
    assertEquals("B", shouldReturnB.transform(new Object(), String.class));
  }
  
  public void testDateTransformer() throws Exception {
    this.transformer = Transformer.createPreFilledTransformer();
    assertEquals(new Date(108, 0, 1), this.transformer.transform("2008", Date.class));
  }
  
  public void testFailureGetsThrough() throws Exception {
    this.transformer.addTransformer(new SpecificTransformer() {
      public Object transform(final Object object, final Class targetType) throws TransformationFailedException {
        throw new TransformationFailedException(object, targetType, "foo", null);
      }
    });
    try {
      this.transformer.transform(new Object(), String.class);
      fail("should have thrown TransformationFailedException");
    } catch (final TransformationFailedException e) {
      ; // expected
    }
  }
  
  private final class RecursiveTestTransformer extends AbstractTransformer implements RecursiveTransformer {
    public Transformer master;
    
    public RecursiveTestTransformer() {
      // nothing to do
    }
    
    public RecursiveTransformer setMasterTransformer(final Transformer master) {
      this.master = master;
      return this;
    }
    
    public Object transform(final Object object, final Class targetType) {
      throw new UnsupportedOperationException("This method is not implemented (yet)");
    }
  }
  
  public abstract class PassThroughTransformer extends AbstractGuardedTransformer implements SpecificTransformer {
    protected Object doTransform(final Object object, final Class targetType) {
      return object;
    }
  }
  
  public class TestBean {
    public TestBean(final String string) {
      this.string = string;
    }
    
    String string;
  }
  
  public void testTransformEqualTypes() throws Exception {
    this.transformer.addTransformer(new SpecificTransformer() {
      public Object transform(final Object object, final Class targetType) throws TransformationFailedException {
        if ("foo".equals(object)) {
          return "bar";
        } else {
          return TRY_NEXT;
        }
      }
    });
    
    assertEquals("bar", this.transformer.transform("foo", String.class));
  }
}
