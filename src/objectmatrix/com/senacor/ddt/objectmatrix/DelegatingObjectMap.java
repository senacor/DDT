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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;
import com.senacor.ddt.util.StringUtils;

/**
 * Default implementation of {@link ObjectMap} that delegates to an {@link ObjectMatrix} instance.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class DelegatingObjectMap implements ObjectMap {
  
  private final ObjectMatrix matrix;
  
  private final String rowOrColumnName;
  
  private final Mode accessMode;
  
  private List keys;
  
  private final String identifier;
  
  /**
   * Create an ObjectMap over a row or column of a given ObjectMatrix.
   * 
   * @param matrix
   *          The matrix. Must not be null.
   * @param rowOrColumnName
   *          The name of the row or column. Must not be blank.
   * @param accessMode
   *          {@link Mode#COLUMN} if <code>rowOrColumnName</code> refers to a column, {@link Mode#ROW} otherwise. Must
   *          not be null.
   */
  public DelegatingObjectMap(final ObjectMatrix matrix, final String rowOrColumnName, final Mode accessMode) {
    ParamChecker.notNull("matrix", matrix);
    ParamChecker.notBlank("rowOrColumnName", rowOrColumnName);
    ParamChecker.notNull("accessMode", accessMode);
    this.matrix = matrix;
    this.rowOrColumnName = rowOrColumnName;
    this.accessMode = accessMode;
    if (accessMode == Mode.COLUMN) {
      ParamChecker.require("Given matrix must contain column with given name '" + rowOrColumnName + "'", matrix
          .getColNames().contains(rowOrColumnName));
      this.identifier = "[Column '" + rowOrColumnName + "' on Matrix '" + matrix.getMatrixIdentifier() + "']";
    } else if (accessMode == Mode.ROW) {
      ParamChecker.require("Given matrix must contain row with given name '" + rowOrColumnName + "'", matrix
          .getRowNames().contains(rowOrColumnName));
      this.identifier = "[Row '" + rowOrColumnName + "' on Matrix '" + matrix.getMatrixIdentifier() + "']";
    } else {
      throw new AssertionError("impossible case");
    }
    setupKeyList();
  }
  
  /**
   * Read the row or column names - depending on this.accessMode - from the underlying matrix and create our key list
   * from it. Skip empty and reserved keys.
   */
  private void setupKeyList() {
    final List tempKeys;
    if (this.accessMode == Mode.COLUMN) {
      tempKeys = new ArrayList(this.matrix.getRowNames());
    } else if (this.accessMode == Mode.ROW) {
      tempKeys = new ArrayList(this.matrix.getColNames());
    } else {
      throw new AssertionError("impossible case");
    }
    final ListIterator iter = tempKeys.listIterator();
    while (iter.hasNext()) {
      final String key = (String) iter.next();
      if ("".equals(key) || StringMatrix.Tokens.RESERVED.equals(key)) {
        iter.remove();
      }
    }
    this.keys = Collections.unmodifiableList(tempKeys);
  }
  
  /**
   * enum-like construct to indicate the access mode.
   * 
   * @see DelegatingObjectMap#DelegatingObjectMap(ObjectMatrix, String,
   *      com.senacor.ddt.objectmatrix.DelegatingObjectMap.Mode)
   */
  public static class Mode {
    private Mode() {
      // just to make this private
    }
    
    /**
     * Construct the map over a column.
     */
    public static final Mode COLUMN = new Mode();
    
    /**
     * Construct the map over a row.
     */
    public static final Mode ROW = new Mode();
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getKeys()
   */
  public List getKeys() {
    return this.keys;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getObject(java.lang.String, java.lang.Class)
   */
  public Object getObject(final String key, final Class type) {
    return this.matrix.getObject(getColName(key), getRowName(key), type);
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getBigDecimal(java.lang.String)
   */
  public BigDecimal getBigDecimal(final String key) {
    return this.matrix.getBigDecimal(getColName(key), getRowName(key));
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getBoolean(java.lang.String)
   */
  public Boolean getBoolean(final String key) {
    return this.matrix.getBoolean(getColName(key), getRowName(key));
  }
  
  /**
   * Get correct column name for the underlying matrix.
   */
  private String getColName(final String key) {
    if (this.accessMode == Mode.COLUMN) {
      return this.rowOrColumnName;
    } else if (this.accessMode == Mode.ROW) {
      return key;
    } else {
      throw new AssertionError("impossible case");
    }
  }
  
  /**
   * Get correct row name for the underlying matrix.
   */
  private String getRowName(final String key) {
    if (this.accessMode == Mode.COLUMN) {
      return key;
    } else if (this.accessMode == Mode.ROW) {
      return this.rowOrColumnName;
    } else {
      throw new AssertionError("impossible case");
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getDate(java.lang.String)
   */
  public Date getDate(final String key) {
    return this.matrix.getDate(getColName(key), getRowName(key));
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getDouble(java.lang.String)
   */
  public Double getDouble(final String key) {
    return this.matrix.getDouble(getColName(key), getRowName(key));
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getInteger(java.lang.String)
   */
  public Integer getInteger(final String key) {
    return this.matrix.getInteger(getColName(key), getRowName(key));
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getLong(java.lang.String)
   */
  public Long getLong(final String key) {
    return this.matrix.getLong(getColName(key), getRowName(key));
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getString(java.lang.String)
   */
  public String getString(final String key) {
    return this.matrix.getString(getColName(key), getRowName(key));
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#filterKeys(java.lang.String, java.lang.String, java.lang.String)
   */
  public List filterKeys(final String prefix, final String infix, final String postfix) {
    return StringUtils.filterStringList(prefix, infix, postfix, getKeys());
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getAnnotation(java.lang.String)
   */
  public Properties getAnnotation(final String key) {
    return this.matrix.getAnnotation(getColName(key), getRowName(key));
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getIdentifier()
   */
  public String getIdentifier() {
    return this.identifier;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMap#getTransformer()
   */
  public Transformer getTransformer() {
    return this.matrix.getTransformer();
  }
  
  public boolean isDefinedAt(final String key) {
    return this.matrix.isDefinedAt(getColName(key), getRowName(key));
  }
}
