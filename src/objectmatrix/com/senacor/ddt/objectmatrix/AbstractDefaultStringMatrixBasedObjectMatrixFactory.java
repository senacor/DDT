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

import com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader;

/**
 * An abstract {@link ObjectMatrixFactory} implementation based on {@link DefaultStringMatrix}. Subclasses will only
 * have to provide the appropriate {@link DefaultStringMatrix.StringMatrixReader StringMatrixReader} implementations,
 * this class will create {@link DelegatingObjectMatrix} instances on top of {@link DefaultStringMatrix
 * DefaultStringMatrices} using the given readers.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public abstract class AbstractDefaultStringMatrixBasedObjectMatrixFactory extends
    AbstractDelegatingOMBasedObjectMatrixFactory {
  private int rowContainingColumnTitles = 0;
  
  private int columnContainingRowTitles = 0;
  
  /**
   * @return -
   * @see com.senacor.ddt.objectmatrix.ObjectMatrixFactory#create()
   */
  protected final AnnotatedStringMatrix[] createStringMatrices() {
    // get the readers
    final StringMatrixReader[] readers = createReaders();
    
    // we'll have one matrix for each reader
    final AnnotatedStringMatrix[] matrices = new AnnotatedStringMatrix[readers.length];
    
    // walk over the readers and create the matrices
    for (int i = 0; i < matrices.length; i++) {
      final StringMatrixReader reader = readers[i];
      final DefaultStringMatrix stringMatrix =
          new DefaultStringMatrix(reader, this.columnContainingRowTitles, this.rowContainingColumnTitles);
      matrices[i] = new EmbeddedAnnotationMatrixDecorator(stringMatrix);
    }
    
    return matrices;
  }
  
  /**
   * Create the appropriate number of {@link DefaultStringMatrix.StringMatrixReader}s of the correct type. This method
   * will be called during {@link #create()}. The implementation depends on the requirements of the StringMatrixReaders
   * that are returned.
   * 
   * @return an array of StringMatrixReaders
   */
  protected abstract StringMatrixReader[] createReaders();
  
  /**
   * Index of the column containing the embedded row titles. Defaults to 0.
   * 
   * @param columnContainingRowTitles
   *          column index
   * 
   * @see DefaultStringMatrix#DefaultStringMatrix(StringMatrixReader, int, int)
   */
  public void setColumnContainingRowTitles(final int columnContainingRowTitles) {
    this.columnContainingRowTitles = columnContainingRowTitles;
  }
  
  /**
   * Index of the row containing the embedded column titles. Defaults to 0.
   * 
   * @param rowContainingColumnTitles
   *          row index
   * 
   * @see DefaultStringMatrix#DefaultStringMatrix(StringMatrixReader, int, int)
   */
  public void setRowContainingColumnTitles(final int rowContainingColumnTitles) {
    this.rowContainingColumnTitles = rowContainingColumnTitles;
  }
}
