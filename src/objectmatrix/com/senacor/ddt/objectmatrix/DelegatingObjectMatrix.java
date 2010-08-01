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
import java.util.List;
import java.util.Properties;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.typetransformer.NoSuccessfulTransformerException;
import com.senacor.ddt.typetransformer.TransformationException;
import com.senacor.ddt.typetransformer.TransformationFailedException;
import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;

/**
 * Default ObjectMatrix implementation that uses an arbitrary underlying StringMatrix as a data source.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class DelegatingObjectMatrix implements ObjectMatrix {
  private static final Log log = LogFactory.getLog(DelegatingObjectMatrix.class);
  
  private final AnnotatedStringMatrix stringMatrix;
  
  private final Transformer localTransformer;
  
  /**
   * Create an ObjectMatrix delegating to the given string matrix and using the default Transformer (
   * {@link Transformer#get()}).
   * 
   * @param stringMatrix
   *          The string matrix to delegate to.
   */
  public DelegatingObjectMatrix(final AnnotatedStringMatrix stringMatrix) {
    this(stringMatrix, Transformer.get());
  }
  
  /**
   * Create an ObjectMatrix delegating to the given string matrix and using the given Transformer.
   * 
   * @param stringMatrix
   *          The string matrix to delegate to.
   * @param localTransformer
   *          The transformer to use.
   */
  public DelegatingObjectMatrix(final AnnotatedStringMatrix stringMatrix, final Transformer localTransformer) {
    ParamChecker.notNull("stringMatrix", stringMatrix);
    ParamChecker.notNull("localTransformer", localTransformer);
    this.localTransformer = localTransformer;
    this.stringMatrix = stringMatrix;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getString(java.lang.String, java.lang.String)
   */
  public String getString(final String colName, final String rowName) {
    if (log.isTraceEnabled()) {
      log.trace("getString(" + colName + ", " + rowName + ")");
    }
    final Properties annotation = this.stringMatrix.getAnnotation(colName, rowName);
    // Follow references, if necessary.
    final String redirect = (annotation).getProperty(AnnotationKeys.REFERENCE);
    if (redirect != null) {
      if (log.isDebugEnabled()) {
        log.debug("following column reference to: " + redirect);
      }
      return getString(redirect, rowName);
    } else {
      if (annotation.containsKey(AnnotationKeys.NULL)) {
        return null;
      }
      final String string =
          (String) getTransformer().transform(this.stringMatrix.getString(colName, rowName), String.class);
      if ((string == null) || string.equals("")) {
        if (annotation.containsKey(AnnotationKeys.DEFAULT_VALUE)) {
          return annotation.getProperty(AnnotationKeys.DEFAULT_VALUE);
        } else {
          return "";
        }
      } else {
        return string;
      }
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getRowNames()
   */
  public List getRowNames() {
    return this.stringMatrix.getRowNames();
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getColNames()
   */
  public List getColNames() {
    return this.stringMatrix.getColNames();
  }
  
  /**
   * This implementation recognizes the strings "true", "ja", "yes", "wahr", "1", "j" and "y" as representing
   * <code>true</code>, ignoring character case. All other strings are taken to represent <code>false</code>.
   * 
   * @see com.senacor.ddt.objectmatrix.ObjectMatrix#getBoolean(java.lang.String, java.lang.String)
   */
  public Boolean getBoolean(final String column, final String row) {
    if (log.isTraceEnabled()) {
      log.trace("getBoolean(" + column + ", " + row + ")");
    }
    final String value = getString(column, row);
    
    if (nullOrBlank(value)) {
      return null;
    } else {
      return (Boolean) getLocalTransformer().transform(value, Boolean.class);
    }
  }
  
  /**
   * Returns true if the given string is null, empty or contains only whitespace.
   * 
   * @param value
   * @return
   */
  private boolean nullOrBlank(final String value) {
    return nullOrEmpty(value) || (value.trim().length() == 0);
  }
  
  /**
   * Returns true if the given string is null or has length zero.
   * 
   * @param value
   * @return
   */
  private boolean nullOrEmpty(final String value) {
    if (value == null) {
      return true;
    } else {
      return value.length() == 0;
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMatrix#getInteger(java.lang.String, java.lang.String)
   */
  public Integer getInteger(final String column, final String row) {
    final String string = getString(column, row);
    
    if (nullOrBlank(string)) {
      return null;
    } else {
      return (Integer) parseNumber(column, row, string, Integer.class);
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMatrix#getBigDecimal(java.lang.String, java.lang.String)
   */
  public BigDecimal getBigDecimal(final String column, final String row) {
    String string = getString(column, row);
    
    if (nullOrBlank(string)) {
      return null;
    } else {
      string = string.replace(',', '.');
      return (BigDecimal) parseNumber(column, row, string, BigDecimal.class);
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMatrix#getLong(java.lang.String, java.lang.String)
   */
  public Long getLong(final String column, final String row) {
    final String string = getString(column, row);
    
    if (nullOrBlank(string)) {
      return null;
    } else {
      return (Long) parseNumber(column, row, string, Long.class);
    }
  }
  
  /**
   * This method is called by the get<Number> methods upon catching a NumberFormatExceptions. This is done to let the
   * Transformer try and give us a valid Number in case the String in the matrix is not a parseable number but rather
   * some kind of symbolic name that is recognized by a user-supplied Transformer.
   * 
   * @param column
   *          The column of the looked-at cell. Used only for exception information.
   * @param row
   *          The row of the looked-at cell. Used only for exception information.
   * @param string
   *          The string contained in the looked-at cell. Used as parameter for performance reasons, since it was
   *          already fetched in the calling method.
   * @param numberType
   *          The desired number type (i.e. Double, Long, Integer).
   * @param formatEx
   *          The previously caught Exception. This will be rethrown if the Transformer can't find our Number either.
   * @return The Number the Transformer found for us. This will be of type numberType.
   */
  private Number parseNumber(final String column, final String row, final String string, final Class numberType) {
    try {
      return (Number) getLocalTransformer().transform(string, numberType);
    } catch (final NoSuccessfulTransformerException e) {
      throw addFieldInfoToException(column, row, e);
    } catch (final TransformationFailedException e) {
      throw addFieldInfoToException(column, row, e);
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMatrix#getDouble(java.lang.String, java.lang.String)
   */
  public Double getDouble(final String column, final String row) {
    final String string = getString(column, row);
    
    if (nullOrBlank(string)) {
      return null;
    } else {
      return (Double) parseNumber(column, row, string, Double.class);
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMatrix#getDate(java.lang.String, java.lang.String)
   */
  public Date getDate(final String column, final String row) {
    final String string = getString(column, row);
    
    if (nullOrBlank(string)) {
      return null;
    } else {
      try {
        return (Date) getLocalTransformer().transform(string, Date.class);
      } catch (final RuntimeException e) {
        throw addFieldInfoToException(column, row, e);
      }
    }
  }
  
  /**
   * Take the given exception and add information about the matrix field in which the error occurred.
   * NumberFormatException, ConversionException and IllegalArgumentException instances are recreated with new messages
   * and given the original stack trace. Other RuntimeExceptions are wrapped in a generic RuntimeException containing
   * the enhanced message.
   * 
   * @param column
   *          column in which the error occurred
   * @param row
   *          row in which the error occurred
   * @param exception
   *          the exception
   * @return exception with enhanced message
   */
  private RuntimeException addFieldInfoToException(final String column, final String row,
      final RuntimeException exception) {
    final RuntimeException newEx;
    boolean overrideStackTrace = true;
    
    // well known exceptions: replace them with new instance containing extra info
    if (exception instanceof NumberFormatException) {
      newEx = new NumberFormatException(fieldErrorMessage(column, row) + exception.getMessage());
    } else if (exception instanceof ConversionException) {
      newEx = new ConversionException(fieldErrorMessage(column, row) + exception.getMessage(), exception.getCause());
    } else if (exception instanceof IllegalArgumentException) {
      newEx = new IllegalArgumentException(fieldErrorMessage(column, row) + exception.getMessage());
    } else if (exception instanceof NoSuccessfulTransformerException) {
      final NoSuccessfulTransformerException cte = (NoSuccessfulTransformerException) exception;
      newEx =
          new NoSuccessfulTransformerException(cte.getObject(), cte.getTargetType(), fieldErrorMessage(column, row)
              + cte.getMessage(), cte.getCause());
    } else if (exception instanceof TransformationFailedException) {
      final TransformationFailedException tfe = (TransformationFailedException) exception;
      newEx =
          new TransformationFailedException(tfe.getObject(), tfe.getTargetType(), fieldErrorMessage(column, row)
              + tfe.getMessage(), tfe.getCause());
    } else if (exception instanceof TransformationException) {
      newEx =
          new TransformationException(fieldErrorMessage(column, row) + exception.getMessage(), exception.getCause());
    } else {
      // generic runtimeexception: do not replace original exception, just wrap it
      // @PMD:REVIEWED:AvoidThrowingRawExceptionTypes: by cmenzel on 06.04.06 14:44
      newEx = new RuntimeException(fieldErrorMessage(column, row) + exception.getMessage(), exception);
      overrideStackTrace = false;
    }
    
    // only override stack trace if replacing the exception - leave it alone when wrapping
    if (overrideStackTrace) {
      newEx.setStackTrace(exception.getStackTrace());
    }
    
    return newEx;
  }
  
  /**
   * Generate the more informative error message used by
   * {@link #addFieldInfoToException(String, String, RuntimeException)}.
   */
  private String fieldErrorMessage(final String column, final String row) {
    return "Error at column '" + column + "', row '" + row + "' in ObjectMatrix '" + getMatrixIdentifier() + "': ";
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMatrix#getObject(java.lang.String, java.lang.String, Class)
   */
  public Object getObject(final String column, final String row, Class type) {
    ParamChecker.notBlank("column", column);
    ParamChecker.notBlank("row", row);
    ParamChecker.notNull("type", type);
    
    if (type.isPrimitive()) {
      type = (Class) Transformer.BOXED_TYPES.get(type);
    }
    
    final String string = getString(column, row);
    
    if (nullOrEmpty(string)) {
      return null;
    } else {
      Object value;
      try {
        value = getLocalTransformer().transform(string, type);
      } catch (final RuntimeException e) {
        throw addFieldInfoToException(column, row, e);
      }
      if ((value != null) && !type.isAssignableFrom(value.getClass())) {
        throw new MatrixReadFailedException("Tried to convert string '" + string + "' to type '" + type
            + "', but got object of type '" + value.getClass()
            + "' instead! Did you register an appropriate Transformer for the desired type? (column " + column
            + ", row " + row + ")");
      }
      
      return value;
    }
  }
  
  private Transformer getLocalTransformer() {
    return this.localTransformer;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getMatrixIdentifier()
   */
  public String getMatrixIdentifier() {
    return this.stringMatrix.getMatrixIdentifier();
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#filterRowNames(java.lang.String, java.lang.String, java.lang.String)
   */
  public List filterRowNames(final String rowPrefix, final String rowInfix, final String rowSuffix) {
    return this.stringMatrix.filterRowNames(rowPrefix, rowInfix, rowSuffix);
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.AnnotatedStringMatrix#getAnnotation(java.lang.String, java.lang.String)
   */
  public Properties getAnnotation(final String column, final String row) throws MatrixReadFailedException {
    return this.stringMatrix.getAnnotation(column, row);
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#filterColumnNames(java.lang.String, java.lang.String,
   *      java.lang.String)
   */
  public List filterColumnNames(final String colPrefix, final String colInfix, final String colSuffix)
      throws MatrixReadFailedException {
    return this.stringMatrix.filterColumnNames(colPrefix, colInfix, colSuffix);
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMatrix#getObjectMapForColumn(java.lang.String)
   */
  public ObjectMap getObjectMapForColumn(final String columnName) {
    return new DelegatingObjectMap(this, columnName, DelegatingObjectMap.Mode.COLUMN);
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMatrix#getObjectMapForRow(java.lang.String)
   */
  public ObjectMap getObjectMapForRow(final String rowName) {
    return new DelegatingObjectMap(this, rowName, DelegatingObjectMap.Mode.ROW);
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.ObjectMatrix#getTransformer()
   */
  public Transformer getTransformer() {
    return getLocalTransformer();
  }
  
  public boolean isDefinedAt(final String colName, final String rowName) {
    final Properties annotation = getAnnotation(colName, rowName);
    return !annotation.containsKey(AnnotationKeys.NULL)
        && (annotation.containsKey(AnnotationKeys.DEFAULT_VALUE) || this.stringMatrix.isDefinedAt(colName, rowName));
  }
}
