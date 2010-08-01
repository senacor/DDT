/*
 * Copyright (c) 2007-2008 Senacor Technologies AG.
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

/**
 * A SpecificTransformer is one element in the chain of transformers managed by {@link Transformer}, responsible for one
 * or several kinds of type transformations. Implement this interface if you need to provide transformations from or to
 * custom types.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public interface SpecificTransformer {
  /**
   * Transform the given object to a (possibly new) instance of the given target class.
   * 
   * @param object
   *          The object to transform
   * @param targetType
   *          The required target type
   * @return <ul>
   *         <li>An object of type <code>targetType</code>, derived from <code>object</code>, if the transformation
   *         succeeded. May be null if that is the result of the transformation.</li>
   *         <li>{@link #TRY_NEXT}, if this transformer was unable to transform the value. This signals the parent
   *         {@link Transformer} to try the next transformer in the chain.</li>
   *         </ul>
   * @throws TransformationFailedException
   *           If there was a serious failure and the current transformation attempt must be aborted. This stops the
   *           parent Transformer, which will propagate this exception to the external caller.
   */
  Object transform(Object object, Class targetType) throws TransformationFailedException;
  
  /**
   * Magic object token that, if returned from {@link #transform(Object, Class)}, signals the parent {@link Transformer}
   * to try the next transformer in the chain.
   */
  public static final Object TRY_NEXT = new Object();
}
