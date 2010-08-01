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

package com.senacor.ddt.objectmatrix;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;

/**
 * Abstract superclass for {@link ObjectMatrixFactory} implementations that creates {@link DelegatingObjectMatrix}
 * instances when given {@link StringMatrix} instances. Note: Usually new factory implementations will extend
 * {@link AbstractDefaultStringMatrixBasedObjectMatrixFactory} instead. This class is intended for the rare cases where
 * a {@link DefaultStringMatrix.StringMatrixReader} implementation does not meet the requirements. This might, for
 * example, be the case when reading from a database.
 * 
 * @author Carl-Eric Menzel
 */
public abstract class AbstractDelegatingOMBasedObjectMatrixFactory implements ObjectMatrixFactory {
  private Transformer localTransformer;
  
  /**
   * Set a local master transformer. This transformer will be passed to the newly created {@link DelegatingObjectMatrix}
   * instances.
   * 
   * @param localTransformer
   *          A transformer instance. If null, the global transformer will be used.
   * @see DelegatingObjectMatrix#DelegatingObjectMatrix(AnnotatedStringMatrix, Transformer)
   */
  public void setLocalTransformer(final Transformer localTransformer) {
    this.localTransformer = localTransformer;
  }
  
  /**
   * A simple wrapper around {@link URL#openStream()}. This is used to facilitate a null check on the URL object. The
   * constructor calling this method can't do it, since it is not allowed to place any statements before constructor
   * delegation via this().
   * 
   * @param paramName
   *          Name of the parameter. Used for the IllegalArgumentException thrown by {@link ParamChecker} if url is
   *          null.
   * @param url
   *          URL to open. Must not be null.
   * @return The result of url.openStream().
   * @throws IOException
   *           If the call to url.openStream() fails.
   * @throws IllegalArgumentException
   *           If the url parameter is null.
   */
  protected static InputStream nullsafeUrlToStream(final String paramName, final URL url) throws IOException,
      IllegalArgumentException {
    ParamChecker.notNull(paramName, url);
    
    return url.openStream();
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMatrixFactory#create()
   */
  public final ObjectMatrix[] create() {
    final AnnotatedStringMatrix[] stringMatrices = createStringMatrices();
    
    final ObjectMatrix[] objectMatrices = new ObjectMatrix[stringMatrices.length];
    
    // walk over the readers and create the matrices
    for (int i = 0; i < objectMatrices.length; i++) {
      final AnnotatedStringMatrix stringMatrix = stringMatrices[i];
      final DelegatingObjectMatrix objectMatrix;
      if (this.localTransformer == null) {
        objectMatrix = new DelegatingObjectMatrix(stringMatrix);
      } else {
        objectMatrix = new DelegatingObjectMatrix(stringMatrix, this.localTransformer);
      }
      objectMatrices[i] = objectMatrix;
    }
    
    return objectMatrices;
  }
  
  /**
   * Provide all required {@link StringMatrix} instances. This factory will call this method to receive from concrete
   * subclasses the StringMatrices required to create the ObjectMatrices.
   * 
   * @return An array of StringMatrix instances. Must not be null.
   */
  protected abstract AnnotatedStringMatrix[] createStringMatrices();
}
