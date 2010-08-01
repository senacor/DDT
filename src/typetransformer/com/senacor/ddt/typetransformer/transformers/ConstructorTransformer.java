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

import java.lang.reflect.Constructor;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.typetransformer.AbstractGuardedTransformer;
import com.senacor.ddt.typetransformer.RecursiveTransformer;
import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;
import com.senacor.ddt.util.StateChecker;

/**
 * A DDT Transformer that converts a multi-field string (containing data separated by an arbitrary delimiter) into an
 * instance of a given class by searching for an applicable constructor.
 * <p>
 * Yes, this is magic.
 * <p>
 * Suppose you have a bean that offers two constructors:
 * 
 * <pre>
 * public MyBean(String foo, Long bar, Date baz)
 * public MyBean(String foo, Long bar)
 * </pre>
 * 
 * You can now define a new ConstructorConverter:
 * 
 * <pre>
 * ConvertUtils.register(MyBean.class, new ConstructorConverter(&quot;:&quot;));
 * </pre>
 * 
 * The new converter will use the ":" character as a field delimiter. Given the string "quux:123:2006-05-01" it would
 * find the first constructor and call it with the string "quux", the Long "123" and the Date "2006-05-01" as arguments.
 * With the string "fizzle:456" it would find the second constructor and call it appropriately.
 * <p>
 * If no constructor with the correct number of arguments and suitable argument types can be found, a
 * CannotTransformException is thrown, to signal the parent Transformer to try the next transformer in the chain.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class ConstructorTransformer extends AbstractGuardedTransformer implements RecursiveTransformer {
  private static final Log log = LogFactory.getLog(ConstructorTransformer.class);
  
  private final String delimiterRegex;
  
  private final Class targetClass;
  
  private final boolean trySubclasses;
  
  private Transformer master;
  
  /**
   * Construct a new ConstructorConverter using the given regular expression as a field delimiter.
   * 
   * @param delimiterRegex
   *          Regular expression that will be used to split the received string into separate fields
   */
  public ConstructorTransformer(final String delimiterRegex, final Class targetClass, final boolean trySubclasses) {
    ParamChecker.notNull("targetClass", targetClass);
    
    this.targetClass = targetClass;
    this.trySubclasses = trySubclasses;
    this.delimiterRegex = delimiterRegex;
  }
  
  public ConstructorTransformer(final String delimiterRegex, final Class targetClass) {
    this(delimiterRegex, targetClass, false);
  }
  
  protected Object doTransform(final Object value, final Class theClass) throws ConversionException {
    StateChecker.notNull("master", this.master);
    ParamChecker.notNull("theClass", theClass);
    
    final String valueAsString = (String) value;
    final int numberOfParams;
    final String[] parameterStrings;
    if ((valueAsString == null) || (valueAsString.length() == 0)) {
      // no parameters given: we'll try no-argument constructors.
      numberOfParams = 0;
      parameterStrings = new String[0];
    } else if ((this.delimiterRegex == null) || (this.delimiterRegex.length() == 0)) {
      // no delimiter given: we'll just try it as a single parameter
      parameterStrings = new String[] { valueAsString };
      numberOfParams = 1;
    } else {
      parameterStrings = valueAsString.split(this.delimiterRegex, -1);
      numberOfParams = parameterStrings.length;
    }
    
    final Constructor[] allConstructors = theClass.getConstructors();
    
    // walk throuth the constructors, looking for an appropriate one to use
    for (int constructorIndex = 0; constructorIndex < allConstructors.length; constructorIndex++) {
      final Constructor currentConstructor = allConstructors[constructorIndex];
      final Class[] parameterTypes = currentConstructor.getParameterTypes();
      final Object[] convertedParameters = new Object[numberOfParams];
      
      if (parameterTypes.length == numberOfParams) {
        // if the constructor takes the same number of parameters that we can offer, it might be the
        // one we need
        
        // this will be set to false if an error occurs while converting the parameters
        boolean convertedAllParametersWithoutError = true;
        for (int paramIndex = 0; paramIndex < numberOfParams; paramIndex++) {
          try {
            final String parameterString = parameterStrings[paramIndex];
            final Object convertedParameter;
            final Class paramType = parameterTypes[paramIndex];
            if ("".equals(parameterString)) {
              convertedParameter = null;
            } else {
              convertedParameter = this.master.transform(parameterString, paramType);
            }
            
            // a valid conversion returns one of:
            // - null (unless our demanded type is primitive)
            // - an instance of the type we demanded
            // - an instance of the corresponding boxed version of the type we demanded
            // if we get one of those, we use the converted value. if it's something else, we move
            // on.
            if (((convertedParameter == null) && !paramType.isPrimitive())
                || paramType.isAssignableFrom(convertedParameter.getClass())
                || Transformer.BOXED_TYPES.get(paramType).equals(convertedParameter.getClass())) {
              convertedParameters[paramIndex] = convertedParameter;
            } else {
              convertedAllParametersWithoutError = false;
              
              break;
            }
          } catch (final RuntimeException e) {
            // } catch (final CannotTransformException e) {
            if (log.isDebugEnabled()) {
              log.debug("Error while trying to convert parameter string '" + parameterStrings[paramIndex]
                  + "' to parameter number " + paramIndex + " of constructor '" + currentConstructor.toString()
                  + "'. Trying next constructor.", e);
            }
            convertedAllParametersWithoutError = false;
            
            break;
          }
        }
        if (convertedAllParametersWithoutError) {
          // we managed to convert all parameters, so now we try to invoke this constructor and
          // return the created object:
          try {
            return currentConstructor.newInstance(convertedParameters);
          } catch (final Exception e) {
            if (log.isDebugEnabled()) {
              log.debug("Error while calling Constructor '" + currentConstructor + "'. Trying next constructor.", e);
            }
            
            // this constructor didn't work, let's try the next one:
            continue;
          }
        }
      }
    }
    // give up
    return TRY_NEXT;
  }
  
  protected boolean canTransform(final Class sourceType, final Class targetType) {
    if (String.class.equals(sourceType)) {
      if (this.trySubclasses) {
        return this.targetClass.isAssignableFrom(targetType);
      } else {
        return this.targetClass.equals(targetType);
      }
    } else {
      return false;
    }
  }
  
  public RecursiveTransformer setMasterTransformer(final Transformer master) {
    this.master = master;
    return this;
  }
}
