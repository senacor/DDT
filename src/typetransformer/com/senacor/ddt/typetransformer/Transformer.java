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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.typetransformer.transformers.BooleanTransformer;
import com.senacor.ddt.typetransformer.transformers.CharacterTransformer;
import com.senacor.ddt.typetransformer.transformers.ClassTransformer;
import com.senacor.ddt.typetransformer.transformers.NumberTransformer;
import com.senacor.ddt.typetransformer.transformers.ObjectToStringTransformer;
import com.senacor.ddt.typetransformer.transformers.PackageClassTransformer;
import com.senacor.ddt.typetransformer.transformers.RelativeDateTransformer;
import com.senacor.ddt.typetransformer.transformers.StringPatternDateTransformer;
import com.senacor.ddt.util.ParamChecker;

/**
 * A generalized engine for transforming objects from one type to another. The scope is similar to that of Jakarta's
 * ConvertUtils, but slightly broader - arbitrary types can be converted, not just to/from String.
 * <p>
 * This class only provides the entry point. For each required type/target combination there must be a suitable
 * implementation of {@link SpecificTransformer}. While ConvertUtils performs a lookup to select a suitable Converter
 * instance for a given conversion, Transfomer uses the GoF pattern Chain of Responsibility, where the available
 * SpecificTransformers are tried one after another until one successfully performs the transformation. If no suitable
 * transformer is found, an exception is thrown. This approach may be slightly slower than a simple Map-lookup, but it
 * allows for much greater flexibility when creating custom transformers (see below).
 * <p>
 * There is one global Transformer instance, available via <code>{@link Transformer#get()}</code>. It is generally
 * recommended to use the global instance, unless custom transformer chains are required. In the latter case, simply
 * instantiate a new Transformer instance and add your SpecificTransformers to it.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class Transformer {
  private static final Log log = LogFactory.getLog(Transformer.class);
  
  /**
   * Maps the primitives to their boxed types.
   * 
   * @see #transform(Object, Class)
   */
  public static final Map BOXED_TYPES = new HashMap() {
    {
      put(Integer.TYPE, Integer.class);
      put(Long.TYPE, Long.class);
      put(Short.TYPE, Short.class);
      put(Byte.TYPE, Byte.class);
      put(Character.TYPE, Character.class);
      put(Double.TYPE, Double.class);
      put(Float.TYPE, Float.class);
      put(Boolean.TYPE, Boolean.class);
    }
  };
  
  /**
   * The transformer chain.
   * 
   * @see #transform(Object, Class)
   */
  private final List transformers = new ArrayList();
  
  /**
   * The static instance returned from {@link #get()}.
   */
  private static final Transformer GLOBAL_TRANSFORMER = createPreFilledTransformer();
  
  public static final Transformer get() {
    return GLOBAL_TRANSFORMER;
  }
  
  /**
   * Create a Transformer chain pre-filled with commonly useful transformers:
   * <ul>
   * <li>{@link RelativeDateTransformer}</li>
   * <li>
   * {@link StringPatternDateTransformer#DATETRANSFORMER_ISO_8601_DATE_ONLY}</li>
   * <li>
   * {@link StringPatternDateTransformer#DATETRANSFORMER_ISO_8601_DATETIME_UTC}</li>
   * <li>
   * {@link StringPatternDateTransformer#DATETRANSFORMER_ISO_8601_FULL_UTC}</li>
   * <li>
   * {@link StringPatternDateTransformer#DATETRANSFORMER_YEAR_ONLY}</li>
   * </ul>
   * 
   * These transformers are available in addition to the ones already created by the {@link #Transformer() default
   * constructor}.
   * 
   * @return A pre-filled Transformer chain
   */
  public static Transformer createPreFilledTransformer() {
    final Transformer t = new Transformer();
    t.addTransformer(RelativeDateTransformer.INSTANCE);
    t.addTransformer(StringPatternDateTransformer.DATETRANSFORMER_YEAR_ONLY);
    t.addTransformer(StringPatternDateTransformer.DATETRANSFORMER_ISO_8601_DATE_ONLY);
    t.addTransformer(StringPatternDateTransformer.DATETRANSFORMER_ISO_8601_DATETIME_UTC);
    t.addTransformer(StringPatternDateTransformer.DATETRANSFORMER_ISO_8601_FULL_UTC);
    return t;
  }
  
  /**
   * Construct a new Transformer instance, with the following minimum set of Transformers:
   * <ul>
   * <li>{@link ObjectToStringTransformer#INSTANCE}</li>
   * <li>{@link JakartaConvertUtilsTransformer#INSTANCE}</li>
   * <li>
   * {@link CharacterTransformer#INSTANCE}</li>
   * <li>{@link BooleanTransformer#INSTANCE}</li>
   * <li>
   * {@link NumberTransformer#INSTANCE}</li>
   * </ul>
   */
  public Transformer() {
    addTransformer(ObjectToStringTransformer.INSTANCE);
    addTransformer(JakartaConvertUtilsTransformer.INSTANCE);
    addTransformer(ClassTransformer.INSTANCE);
    addTransformer(PackageClassTransformer.DEFAULT_JAVA_PACKAGES_INSTANCE);
    addTransformer(CharacterTransformer.INSTANCE);
    addTransformer(BooleanTransformer.INSTANCE);
    addTransformer(NumberTransformer.INSTANCE);
  }
  
  /**
   * Transform the given object into a new object of the given target type, if possible. This method tries the available
   * SpecificTransformer instances one after another, until one of them is able to perform the transformation. If no
   * transformer succeeds, a CannotTransformException is thrown.
   * 
   * @see #addTransformer(SpecificTransformer)
   * @param object
   *          The object to transform. May be null.
   * @param targetType
   *          The desired new type. Must not be null. If this is one of the primitive types, it will be automatically
   *          changed to the corresponding boxed type.
   * @throws NoSuccessfulTransformerException
   *           If no Transformer is able to perform the transformation.
   * @throws TransformationFailedException
   *           If the transformation fails in a way that is serious enough to disrupt the entire chain.
   * @throws TransformationException
   *           If an internal error occurs
   * @return The result of the transformation. May be null. Is always null if <code>object</code> was null.
   */
  public Object transform(final Object object, Class targetType) throws NoSuccessfulTransformerException,
      TransformationFailedException, TransformationException {
    if (this.transformers.isEmpty()) {
      throw new TransformationException("No transformers have been registered with this Transformer instance");
    }
    ParamChecker.notNull("targetType", targetType);
    if (targetType.isPrimitive()) {
      // we can't work with primitives, so we find the corresponding boxed type and use it as
      // transformation target
      targetType = findBoxedType(targetType);
    }
    if (object == null) {
      return null;
    }
    try {
      // - What? Class.forName when we already have a class object?
      // - Such is the nature of the Java platform we are running on, young student. We have to do
      // this.
      // - I don't understand this, Master.
      // - Listen and learn, novice. What do you know about Class.forName?
      // - Well, it loads the named class and gives us an object representing that class.
      // - Good, but what else does it do?
      // - I'm not sure, Master... Oh! It also initializes the class it loads!
      // - Very well. You are getting closer to understanding this interesting problem.
      // - But Master, we already have a class object, why would we need to reload it?
      // - It is not the loading that we need, but the initializing.
      // - The initializing? How can that be important when we already *have* the class?
      // - When you look at the class object, can you tell whether it was initialized already?
      // - Oh.
      // - You are wondering now from where we would receive a class that has not been initialized.
      // - Yes, Master.
      // - Look at BeanFiller#fillBean and DelegatingObjectMatrix#getObject for enlightenment, my
      // student.
      // - Oh. They use reflection to get to an object's field... and then they use Field#getType to
      // get at the class object. But... this means Field#getType would give us uninitialized
      // classes? How very strange!
      // - You have learned well.
      Class.forName(targetType.getName());
    } catch (final ClassNotFoundException e) {
      throw new Error("This should never happen: Caught CNFE while Class.forName()ing an existing class!", e);
    }
    final Object transformed = runTransformerChain(object, targetType);
    return transformed;
  }
  
  /**
   * Run the transformer chain, asking each transformer in turn to perform the transformation, until one succeeds.
   * 
   * @param object
   *          The object to transform.
   * @param targetType
   *          The target type.
   * @return The transformation result.
   */
  private Object runTransformerChain(final Object object, final Class targetType) {
    final Iterator iter = this.transformers.iterator();
    // iterate over the transformers...
    while (iter.hasNext()) {
      final SpecificTransformer currentTransformer = (SpecificTransformer) iter.next();
      // ...and simply try each one.
      final Object transformed = currentTransformer.transform(object, targetType);
      if (transformed == SpecificTransformer.TRY_NEXT) {
        // this transformer was unable to help us
        // eat this exception and try the next transformer
        if (log.isDebugEnabled()) {
          log.debug("The transformer " + currentTransformer.toString() + " was unable to transform '" + object
              + "' to type " + targetType.getName());
        }
      } else {
        if ((transformed != null) && !targetType.isAssignableFrom(transformed.getClass())) {
          throw new TransformationException("Transformer '" + currentTransformer + "' returned object of type '"
              + transformed.getClass().getName() + "' instead of the required '" + targetType.getName() + "'!");
        } else {
          return transformed;
        }
        
      }
    }
    // no transformer worked - give up
    throw new NoSuccessfulTransformerException(object, targetType);
  }
  
  private Class findBoxedType(final Class type) {
    Class result = (Class) BOXED_TYPES.get(type);
    if (result == null) {
      result = type;
    }
    return result;
  }
  
  public synchronized void addTransformer(final SpecificTransformer newTransformer) {
    ParamChecker.notNull("newTransformer", newTransformer);
    ParamChecker.require("Transformers may not be added twice!", !this.transformers.contains(newTransformer));
    
    // insert at first position so the most recent transformers are the ones that run first
    this.transformers.add(0, newTransformer);
    if (newTransformer instanceof RecursiveTransformer) {
      ((RecursiveTransformer) newTransformer).setMasterTransformer(this);
    }
  }
}
