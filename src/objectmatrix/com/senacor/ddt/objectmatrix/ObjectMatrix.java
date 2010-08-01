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

import java.math.BigDecimal;
import java.util.Date;

import com.senacor.ddt.typetransformer.SpecificTransformer;
import com.senacor.ddt.typetransformer.Transformer;

/**
 * <p>
 * This is an extension of {@link StringMatrix} that offers methods to retrieve Objects of any type from the
 * StringMatrix, provided there is an appropriate String representation.
 * </p>
 * <p>
 * Convenience accessors are provided to quickly retrieve Booleans, Integers, BigDecimals, Longs, Doubles as well as
 * arbitrary Objects of any given class. These methods most likely will all use {@link Transformer} internally, so
 * appropriate {@link SpecificTransformer} implementations are needed for custom Objects.
 * </p>
 * <p>
 * The following annotations are recognized in all data fields:
 * <dl>
 * <di>
 * <dt><code>ref=<i>column</i></code></dt>
 * <dd>To achieve minimal redundancy, values in field may refer to other fields in the same row by using the
 * <code>ref</code> annotation and specifying the name of the referenced column as value.</dd>
 * </di> <di>
 * <dt><code>null</code></dt>
 * <dd>Empty cells are returned as <code>null</code> for all <code>get<i>Type</i></code> methods. To make the null
 * reference more obvious you can use this token to explicitly set a cell to null. The only exception
 * <code>{@link #getString(String, String)}</code>: An empty cell is returned as an empty "" string, so you need to use
 * the explicit null token if you want a null string.</dd>
 * </di>
 * </dl>
 * </p>
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public interface ObjectMatrix extends AnnotatedStringMatrix {
  /**
   * Returns the Boolean represented by the String at the given position in the underlying StringMatrix.
   * 
   * @param column
   *          The name of the column.
   * @param row
   *          The name of the row.
   * @return The Boolean, or null if the cell is empty or contains a null-token.
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  Boolean getBoolean(String column, String row) throws MatrixReadFailedException;
  
  /**
   * Returns the Integer represented by the String at the given position in the underlying StringMatrix.
   * 
   * @param column
   *          The name of the column.
   * @param row
   *          The name of the row.
   * @return The Integer, or null if the cell is empty or contains a null-token.
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  Integer getInteger(String column, String row) throws MatrixReadFailedException;
  
  /**
   * Returns the BigDecimal represented by the String at the given position in the underlying StringMatrix.
   * 
   * @param column
   *          The name of the column.
   * @param row
   *          The name of the row.
   * @return The BigDecimal, or null if the cell is empty or contains a null-token.
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  BigDecimal getBigDecimal(String column, String row) throws MatrixReadFailedException;
  
  /**
   * Returns the Long represented by the String at the given position in the underlying StringMatrix.
   * 
   * @param column
   *          The name of the column.
   * @param row
   *          The name of the row.
   * @return The Long, or null if the cell is empty or contains a null-token.
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  Long getLong(String column, String row) throws MatrixReadFailedException;
  
  /**
   * Returns the Double represented by the String at the given position in the underlying StringMatrix.
   * 
   * @param column
   *          The name of the column.
   * @param row
   *          The name of the row.
   * @return The Double, or null if the cell is empty or contains a null-token.
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  Double getDouble(String column, String row) throws MatrixReadFailedException;
  
  /**
   * Returns the Date represented by the String at the given position in the underlying StringMatrix.
   * 
   * @param column
   *          The name of the column.
   * @param row
   *          The name of the row.
   * @return The Date, or null if the cell is empty or contains a null-token.
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  Date getDate(String column, String row) throws MatrixReadFailedException;
  
  /**
   * Returns the Object of the given class represented by the String at the given position in the underlying
   * StringMatrix.
   * 
   * @param column
   *          The name of the column.
   * @param row
   *          The name of the row.
   * @param type
   *          The class of the desired object.
   * @return The Object, or null if the cell is empty or contains a null-token.
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  Object getObject(String column, String row, Class type) throws MatrixReadFailedException;
  
  /**
   * Gets the String at the given index. This interface adds the restriction to
   * {@link StringMatrix#getString(String, String)} that null may only be returned if the {@link AnnotationKeys#NULL}
   * token is found. An empty cell must be returned as "". The same restriction applies to the other getString-Methods.
   * 
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getString(String, String)
   * @throws MatrixReadFailedException
   *           if anything goes wrong
   */
  String getString(String column, String row) throws MatrixReadFailedException;
  
  /**
   * This subinterface contains the various tokens that must be understood and respected by all ObjectMatrix
   * implementations.
   */
  interface AnnotationKeys extends StringMatrix.Tokens {
    /**
     * The "reference" annotation key.
     * 
     * @see ObjectMatrix
     */
    public static final String REFERENCE = "ref";
    
    /**
     * The annotation key to set an explicit null value.
     * 
     * @see ObjectMatrix
     */
    public static final String NULL = "null";
    
    /**
     * Reserved, not yet used.
     */
    public static final String AUTO_REFERENCE = "auto-ref";
    
    /**
     * Reserved, not yet used.
     */
    public static final String AUTO_REFERENCE_LEFT = "auto-reference-left";
    
    /**
     * Reserved, not yet used.
     */
    public static final String REFERENCE_LEFT = "ref-left";
    
    /**
     * Single-Value annotation that provides a default value for cells in the row/column that is used when the cell
     * itself would be null.
     */
    public static final String DEFAULT_VALUE = "default-value";
  }
  
  /**
   * Return an {@link ObjectMap} instance as a view on the named column.
   * 
   * @param columnName
   *          The name of the column to be represented by the ObjectMap.
   * @return A view on the named column.
   */
  ObjectMap getObjectMapForColumn(String columnName);
  
  /**
   * Return an {@link ObjectMap} instance as a view on the named row.
   * 
   * @param rowName
   *          The name of the row to be represented by the ObjectMap.
   * @return A view on the named row.
   */
  ObjectMap getObjectMapForRow(String rowName);
  
  /**
   * Return the {@link Transformer} instance used by this ObjectMatrix. If this matrix does not have a Transformer, it
   * must return the result of {@link Transformer#get()}.
   * 
   * @return A Transformer instance. Not null.
   */
  Transformer getTransformer();
}
