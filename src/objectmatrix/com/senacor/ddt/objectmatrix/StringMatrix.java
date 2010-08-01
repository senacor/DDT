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

import java.util.List;

/**
 * StringMatrix represents a two-dimensional array of Strings. Each field can be accessed by column and row indexes,
 * column and row names, or a combination thereof.
 * <p>
 * For some types of source data, implementations must adhere to the following formatting rules:
 * <ul>
 * <li>Dates must be string-formatted as specified in {@link #FORMAT_DATE}</li>
 * <li>Numbers must be string-formatted as specified in {@link #FORMAT_NUMBER}</li>
 * </ul>
 * 
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public interface StringMatrix {
  /**
   * If the underlying datasource is capable of typed date fields, they must be string-formatted using this pattern.
   */
  static final String FORMAT_DATE = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  
  /**
   * If the underlying datasource is capable of typed number fields, they must be string-formatted using this pattern.
   */
  static final String FORMAT_NUMBER = "#.########";
  
  /**
   * Retrieve the String at the given position.
   * 
   * @param colName
   *          Name of the desired column.
   * @param rowName
   *          Name of the desired row.
   * @return The String at the given position. This may be null, depending on the implementation.
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  String getString(String colName, String rowName) throws MatrixReadFailedException;
  
  /**
   * Returns the list of row names, i.e. all valid fields in column 0.
   * 
   * @return The list
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  List getRowNames() throws MatrixReadFailedException;
  
  /**
   * Returns the list of column names, i.e. all valid fields in row 0.
   * 
   * @return The list
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  List getColNames() throws MatrixReadFailedException;
  
  /**
   * Returns a list of row names that start with the given prefix, contain the given infix and end with the given
   * suffix. Both parameters are optional.
   * 
   * @param rowPrefix
   *          Prefix to look for. If null, this method will not filter by prefixes.
   * @param rowInfix
   *          Infix to look for. If null, this method will not filter by infixes.
   * @param rowSuffix
   *          Suffix to look for. If null, this method will not filter by suffixes.
   * @return filtered List<String> of row names
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  List filterRowNames(String rowPrefix, String rowInfix, String rowSuffix) throws MatrixReadFailedException;
  
  /**
   * Returns a list of column names that start with the given prefix, contain the given infix and end with the given
   * suffix. Both parameters are optional.
   * 
   * @param colPrefix
   *          Prefix to look for. If null, this method will not filter by prefixes.
   * @param colInfix
   *          Infix to look for. If null, this method will not filter by infixes.
   * @param colSuffix
   *          Suffix to look for. If null, this method will not filter by suffixes.
   * @return filtered List<String> of column names
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  List filterColumnNames(String colPrefix, String colInfix, String colSuffix) throws MatrixReadFailedException;
  
  /**
   * Returns an implementation-dependent unique identifier for this StringMatrix instance.
   * 
   * @return the Identifier
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  String getMatrixIdentifier() throws MatrixReadFailedException;
  
  /**
   * Check whether a value is defined at this position. This method checks only untransformed content and (if
   * applicable) the default-value annotation, not the results of any transformations. Usually, this would mean that an
   * empty cell in the underlying table, without a default-value annotation, makes this method return false.
   * 
   * @param colName
   *          The name of the column.
   * @param rowName
   *          The name of the row.
   * @return <ul>
   *         <li><code>true</code> if there is a value available for this key</li>
   *         <li><code>false</code> otherwise.</li>
   *         </ul>
   */
  boolean isDefinedAt(String colName, String rowName);
  
  /**
   * Contains tokens that can appear in fields of the StringMatrix to trigger behavior.
   */
  interface Tokens {
    /**
     * A column or row can have the title "Reserved" to mark it as reserved for special purposes. It is then only
     * accessible via its indices. Actual uses of this are implementation-dependent.
     */
    static final String RESERVED = "Reserved";
  }
}
