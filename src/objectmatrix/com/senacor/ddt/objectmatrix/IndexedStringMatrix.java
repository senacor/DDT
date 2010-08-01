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

/**
 * Legacy interface that was split off {@link StringMatrix}. This is currently not used anywhere except
 * {@link DefaultStringMatrix}.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public interface IndexedStringMatrix extends StringMatrix {
  /**
   * Retrieve the String at the given position.
   * 
   * @param colIndex
   *          0-based index of the column
   * @param rowIndex
   *          0-based index of the row
   * @return The String at the given position. This may be null, depending on the implementation.
   * @throws MatrixReadFailedException
   *           On read errors
   */
  String getString(int colIndex, int rowIndex) throws MatrixReadFailedException;
  
  /**
   * Retrieve the String at the given position.
   * 
   * @param colName
   *          Name of the desired column.
   * @param rowIndex
   *          0-based index of the row
   * @return The String at the given position. This may be null, depending on the implementation.
   * @throws MatrixReadFailedException
   *           On read errors
   */
  String getString(String colName, int rowIndex) throws MatrixReadFailedException;
  
  /**
   * Retrieve the String at the given position.
   * 
   * @param colIndex
   *          0-based index of the column
   * @param rowName
   *          Name of the desired row.
   * @return The String at the given position. This may be null, depending on the implementation.
   * @throws MatrixReadFailedException
   *           On read errors
   */
  String getString(int colIndex, String rowName) throws MatrixReadFailedException;
}
