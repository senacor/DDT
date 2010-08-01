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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.senacor.ddt.util.ParamChecker;
import com.senacor.ddt.util.StringUtils;

/**
 * Decorator that provides annotation capability to any StringMatrix. The annotations are parsed from cell content, row
 * titles and column titles and must have the forms:
 * <ul>
 * <li><code>~<i>annoKey</i></code> for key-only annotations.</li>
 * <li><code>~<i>annoKey</i>=<i>annoValue</i></code> for single-valued annotations.</li>
 * <li><code>~<i>annoKey</i>=</code> for multi-valued annotations. In this case, the annotation value is taken from the
 * cell content.</li>
 * </ul>
 * Annotations prefixed with another <code>~</code> (i.e. "<code>~~foo=bar</code>") are global annotations.
 * <p>
 * This decorator virtualizes all rows and columns. For example: Given the row titles
 * <ul>
 * <li>foo~someAnnotation</li>
 * <li>foo~someOtherAnnotation=</li>
 * <li>foo</li>
 * </ul>
 * this decorator will only show the virtual row "foo", which will have two annotations. Cell content is taken from the
 * underlying row "foo". The annotations' values will be taken from their own underlying rows. If there is no underlying
 * row named simply "foo", the last annotated row will be used.
 * 
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class EmbeddedAnnotationMatrixDecorator implements AnnotatedStringMatrix {
  /**
   * Composite key for the annotation cache map.
   */
  private static class Key {
    
    private final String column;
    
    private final String row;
    
    Key(final String column, final String row) {
      this.column = column;
      this.row = row;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj) {
      if ((obj == null) || (!(obj instanceof Key))) {
        return false;
      }
      final Key that = (Key) obj;
      return this.column.equals(that.column) && this.row.equals(that.row);
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
      return this.column.hashCode() + this.row.hashCode();
    }
    
  }
  
  /**
   * Although Annotations are simply String key/value pairs, it is useful to represent them as actual types within this
   * parser. This way different subclasses can handle reading of values from the cell itself or other rows/columns.
   */
  private static abstract class Annotation {
    /**
     * Getter for the value of the annotation. This may be different for different cells, thus the column and row are
     * given as parameters.
     * 
     * @param column
     *          The column name. Not blank.
     * @param row
     *          The row name. Not blank.
     * @return The value.
     */
    abstract String getValue(String column, String row);
    
    private final String key;
    
    String getKey() {
      return this.key;
    }
    
    Annotation(final String key) {
      this.key = key;
    }
  }
  
  /**
   * Annotation that consists of a key and a single constant value.
   */
  private static class SingleValueAnnotation extends Annotation {
    
    private final String value;
    
    SingleValueAnnotation(final String key, final String value) {
      super(key);
      this.value = value;
    }
    
    /**
     * @see com.senacor.ddt.objectmatrix.EmbeddedAnnotationMatrixDecorator.Annotation#getValue(java.lang.String,
     *      java.lang.String)
     * @param column
     *          ignored.
     * @param row
     *          ignored.
     */
    String getValue(final String column, final String row) {
      return this.value;
    }
    
  }
  
  /**
   * Annotation that consists only of a value and an empty key.
   */
  private static class FlagAnnotation extends SingleValueAnnotation {
    FlagAnnotation(final String key) {
      super(key, "");
    }
  }
  
  /**
   * Superclass for all multi-valued annotations, i.e. those that have different values depending on the column and row
   * that's asked for.
   */
  private static abstract class MultiValueAnnotation extends Annotation {
    protected final StringMatrix delegate;
    
    /**
     * @param key
     *          The annotation's key
     * @param delegate
     *          The StringMatrix from which the values should be read.
     */
    MultiValueAnnotation(final String key, final StringMatrix delegate) {
      super(key);
      this.delegate = delegate;
    }
  }
  
  /**
   * Multi-value annotation that is attached to a row and looks into the columns for its value.
   */
  private static class MultiColumnAnnotation extends MultiValueAnnotation {
    
    private final String rowName;
    
    /**
     * @param key
     *          The annotation's key.
     * @param rowName
     *          The row this annotation is attached to.
     * @param delegate
     *          The StringMatrix that's used to look up the values.
     */
    MultiColumnAnnotation(final String key, final String rowName, final StringMatrix delegate) {
      super(key, delegate);
      this.rowName = rowName;
    }
    
    /**
     * @see com.senacor.ddt.objectmatrix.EmbeddedAnnotationMatrixDecorator.Annotation#getValue(java.lang.String,
     *      java.lang.String)
     * @param column
     *          The column to look into.
     * @param row
     *          ignored.
     */
    String getValue(final String column, final String row) {
      return this.delegate.getString(column, this.rowName);
    }
  }
  
  /**
   * Multi-value annotation that is attached to a column and looks into the rows for its value.
   */
  private static class MultiRowAnnotation extends MultiValueAnnotation {
    private final String columnName;
    
    /**
     * @param key
     *          The annotation's key.
     * @param columnName
     *          The column this annotation is attached to.
     * @param delegate
     *          The StringMatrix that's used to look up the values.
     */
    MultiRowAnnotation(final String key, final String columnName, final StringMatrix delegate) {
      super(key, delegate);
      this.columnName = columnName;
    }
    
    /**
     * @see com.senacor.ddt.objectmatrix.EmbeddedAnnotationMatrixDecorator.Annotation#getValue(java.lang.String,
     *      java.lang.String)
     * @param column
     *          ignored.
     * @param row
     *          The row to look into.
     */
    String getValue(final String column, final String row) {
      return this.delegate.getString(this.columnName, row);
    }
  }
  
  /**
   * The character that separates a cell's content from an annotation: '~'
   */
  public static final String ANNOTATION_MARK = "~";
  
  /**
   * The character that separates an annotation's key from its value: '='
   */
  public static final String ANNOTATION_KEYVALUE_SEPARATOR = "=";
  
  /**
   * Internal: Array index of the cell's content as returned by {@link #splitContentAndAnnotation(String)}.
   */
  private static final int CONTENT = 0;
  
  /**
   * Internal: Array index of the cell's annotation key as returned by {@link #splitContentAndAnnotation(String)}.
   */
  private static final int ANNOT_KEY = 1;
  
  /**
   * Internal: Array index of the cell's annotation value as returned by {@link #splitContentAndAnnotation(String)}.
   */
  private static final int ANNOT_VALUE = 2;
  
  /**
   * Internal: Hack: When {@link #splitContentAndAnnotation(String)} discovers an annotation without value but a
   * trailing =, this token is returned as ANNOT_VALUE to signal that the value must be loaded separately.
   */
  private static final String TOKEN_LOAD_VALUE_FROM_FIELD =
      EmbeddedAnnotationMatrixDecorator.class.getName() + ".TOKEN_LOAD_VALUE_FROM_FIELD";
  
  /**
   * The marker for a global annotation, i.e. one that is valid for all cells: "~~"
   */
  public static final String GLOBAL_ANNOTATION_MARKER = "~~";
  
  /**
   * Internal: ROWS mode for annotation parsing.
   */
  private static final int ROWS = 0;
  
  /**
   * Internal: COLUMNS mode for annotation parsing.
   */
  private static final int COLUMNS = 1;
  
  /**
   * The delegate StringMatrix we're decorating and reading from.
   */
  private final StringMatrix delegate;
  
  /**
   * Mappings from virtual row names to actual row names in the delegate.
   */
  private final Map rowAliases = new HashMap();
  
  /**
   * Mappings from virtual column names to actual column names in the delegate.
   */
  private final Map colAliases = new HashMap();
  
  /**
   * The virtual row names we're showing to the outside world.
   */
  private final List rowNames = new ArrayList();
  
  /**
   * The virtual column names we're showing to the outside world.
   */
  private final List colNames = new ArrayList();
  
  /**
   * Annotation cache.
   */
  private final Map cache = new HashMap();
  
  /**
   * Global annotations. Initialized at construction time.
   */
  private final Set globalAnnotations = new HashSet();
  
  /**
   * @param delegate
   *          The StringMatrix to decorate and read from.
   */
  public EmbeddedAnnotationMatrixDecorator(final StringMatrix delegate) {
    ParamChecker.notNull("delegate", delegate);
    this.delegate = delegate;
    createAnnotationsAndNecessaryAliases(delegate.getRowNames(), this.rowNames, this.rowAliases, ROWS);
    createAnnotationsAndNecessaryAliases(delegate.getColNames(), this.colNames, this.colAliases, COLUMNS);
  }
  
  /**
   * Walk through row or column names, create annotations and aliases.
   * 
   * @param originalNames
   *          List containing original names
   * @param filteredNames
   *          List to store filtered and virtual names in
   * @param aliases
   *          Map to store alias mappings
   * @param mode
   *          go through rows or columns
   */
  private void createAnnotationsAndNecessaryAliases(final List originalNames, final List filteredNames,
      final Map aliases, final int mode) {
    final Iterator iter = originalNames.iterator();
    while (iter.hasNext()) {
      final String rowOrColumnName = (String) iter.next();
      final String[] splitString = splitContentAndAnnotation(rowOrColumnName);
      final String rowOrColumnNameWithoutAnnotation = splitString[CONTENT];
      if (rowOrColumnName.startsWith(GLOBAL_ANNOTATION_MARKER)) {
        splitString[ANNOT_KEY] = splitString[ANNOT_KEY].substring(1);
        this.globalAnnotations.add(createAnnotation(rowOrColumnName, splitString, mode));
      } else {
        /*
         * create an alias if no row/column with the un-annotated base name exists in the delegate. this way, the last
         * row/column wins, which is the specified behavior.
         */
        if (!originalNames.contains(rowOrColumnNameWithoutAnnotation)) {
          aliases.put(rowOrColumnNameWithoutAnnotation, rowOrColumnName);
        }
        
        /*
         * remove and re-add the row/column name. this ensures that if there are several rows/columns getting
         * virtualized into one, the virtual one will be at the position of the last real one.
         */
        if (!(Tokens.RESERVED.equals(rowOrColumnNameWithoutAnnotation))
            && (rowOrColumnNameWithoutAnnotation.length() > 0)) {
          filteredNames.remove(rowOrColumnNameWithoutAnnotation);
        }
        filteredNames.add(rowOrColumnNameWithoutAnnotation);
        
        /*
         * create an annotation if there is one in the delegate and store it.
         */
        final Annotation anno = createAnnotation(rowOrColumnName, splitString, mode);
        if (anno != null) {
          if (mode == ROWS) {
            addRowAnnotation(rowOrColumnNameWithoutAnnotation, anno);
          } else {
            addColumnAnnotation(rowOrColumnNameWithoutAnnotation, anno);
          }
        }
      }
    }
  }
  
  /**
   * Create an annotation from the result of {@link #splitContentAndAnnotation(String)}, if necessary.
   * 
   * @param rowOrColumnName
   *          row or column name
   * @param splitString
   *          result of {@link #splitContentAndAnnotation(String)}
   * @param mode
   *          ROWS or COLUMNS
   * @return an Annotation if one was found in <code>splitString</code>, otherwise null.
   */
  private Annotation createAnnotation(final String rowOrColumnName, final String[] splitString, final int mode) {
    final String annotKey = splitString[ANNOT_KEY];
    if (annotKey.length() > 0) {
      final Annotation anno;
      final String annotValue = splitString[ANNOT_VALUE];
      /*
       * hack: #splitContentAndAnnotation signals with TOKEN_LOAD_VALUE_FROM_FIELD that we need a multi-value
       * annotation. this isn't pretty, but it works perfectly and is simple enough, so i decided to keep it.
       */
      if (annotValue == TOKEN_LOAD_VALUE_FROM_FIELD) {
        if (mode == ROWS) {
          anno = new MultiColumnAnnotation(annotKey, rowOrColumnName, this.delegate);
        } else {
          anno = new MultiRowAnnotation(annotKey, rowOrColumnName, this.delegate);
        }
      } else if (annotValue.length() == 0) {
        anno = new FlagAnnotation(annotKey);
      } else {
        anno = new SingleValueAnnotation(annotKey, annotValue);
      }
      return anno;
    } else {
      return null;
    }
  }
  
  /**
   * Map that stores annotations for all rows, mapped from rowName to set of annotations
   */
  private final Map/* <String,Set> */rowAnnotations = new HashMap();
  
  /**
   * Map that stores annotations for all columns, mapped from columnName to set of annotations
   */
  private final Map/* <String,Set> */columnAnnotations = new HashMap();
  
  /**
   * Store annotation for a column.
   * 
   * @param colName
   *          column name
   * @param annotation
   *          annotation
   */
  private void addColumnAnnotation(final String colName, final Annotation annotation) {
    getAnnotationsForColumn(colName).add(annotation);
  }
  
  /**
   * Store annotation for a row.
   * 
   * @param rowName
   *          row name
   * @param annotation
   *          annotation
   */
  private void addRowAnnotation(final String rowName, final Annotation annotation) {
    getAnnotationsForRow(rowName).add(annotation);
  }
  
  /**
   * Return all currently known annotations for the given row.
   * 
   * @param rowName
   *          row name
   * @return The set of annotations. This is mutable, so do not pass it out of this instance!
   */
  private Set getAnnotationsForRow(final String rowName) {
    Set annotationsForThisRow = (Set) this.rowAnnotations.get(rowName);
    if (annotationsForThisRow == null) {
      annotationsForThisRow = new HashSet();
      this.rowAnnotations.put(rowName, annotationsForThisRow);
    }
    return annotationsForThisRow;
  }
  
  /**
   * Return all currently known annotations for the given column.
   * 
   * @param colName
   *          column name
   * @return The set of annotations. This is mutable, so do not pass it out of this instance!
   */
  private Set getAnnotationsForColumn(final String colName) {
    Set annotationsForThisColumn = (Set) this.columnAnnotations.get(colName);
    if (annotationsForThisColumn == null) {
      annotationsForThisColumn = new HashSet();
      this.columnAnnotations.put(colName, annotationsForThisColumn);
    }
    return annotationsForThisColumn;
  }
  
  /**
   * Caching implementation of {@link AnnotatedStringMatrix#getAnnotation(String, String)}. If no annotation is found in
   * the cache, this method delegates to {@link #doGetAnnotation(String, String)}.
   * 
   * @see com.senacor.ddt.objectmatrix.AnnotatedStringMatrix#getAnnotation(java.lang.String, java.lang.String)
   */
  public Properties getAnnotation(final String column, final String row) throws MatrixReadFailedException {
    final Key key = new Key(column, row);
    Properties ann = (Properties) this.cache.get(key);
    if (ann == null) {
      ann = doGetAnnotation(column, row);
      this.cache.put(key, ann);
    }
    return ann;
  }
  
  /**
   * Find the annotations for a given cell.
   * 
   * @param virtualColumnName
   *          column name
   * @param virtualRowName
   *          row name
   * @return Global, row, column and local annotations for the cell.
   */
  private Properties doGetAnnotation(final String virtualColumnName, final String virtualRowName) {
    final Properties p = new Properties();
    final String underlyingColumnName = checkForColAlias(virtualColumnName);
    final String underlyingRowName = checkForRowAlias(virtualRowName);
    addAnnotationsToResult(this.globalAnnotations, p, underlyingColumnName, underlyingRowName);
    addAnnotationsToResult(getAnnotationsForRow(virtualRowName), p, underlyingColumnName, underlyingRowName);
    addAnnotationsToResult(getAnnotationsForColumn(virtualColumnName), p, underlyingColumnName, underlyingRowName);
    final String[] cellAnnotation = splitContentAndAnnotation(getStringFromDelegate(virtualColumnName, virtualRowName));
    if (cellAnnotation[ANNOT_KEY].length() > 0) {
      p.setProperty(cellAnnotation[ANNOT_KEY], cellAnnotation[ANNOT_VALUE]);
    }
    return p;
  }
  
  private void addAnnotationsToResult(final Set annotationsFromRow, final Properties p, final String column,
      final String row) {
    final Iterator iterator = annotationsFromRow.iterator();
    while (iterator.hasNext()) {
      final Annotation annot = (Annotation) iterator.next();
      addAnnotationToResult(annot, p, column, row);
    }
  }
  
  private void addAnnotationToResult(final Annotation annot, final Properties p, final String column, final String row) {
    p.setProperty(annot.getKey(), annot.getValue(column, row));
  }
  
  private String[] splitContentAndAnnotation(final String string) {
    final String[] result = new String[3];
    final String content;
    final String annotationKey;
    final String annotationValue;
    final int annotationIndex = string.indexOf(ANNOTATION_MARK);
    if (annotationIndex == -1) {
      content = string;
      annotationKey = "";
      annotationValue = "";
    } else {
      content = string.substring(0, annotationIndex);
      final String annotation = string.substring(annotationIndex + 1);
      final int keyValueSeparatorIndex = annotation.indexOf(ANNOTATION_KEYVALUE_SEPARATOR);
      if (keyValueSeparatorIndex > 0) {
        annotationKey = annotation.substring(0, keyValueSeparatorIndex);
        if (keyValueSeparatorIndex + 1 == annotation.length()) {
          annotationValue = TOKEN_LOAD_VALUE_FROM_FIELD;
        } else {
          annotationValue = annotation.substring(keyValueSeparatorIndex + 1);
        }
      } else {
        annotationKey = annotation;
        annotationValue = "";
      }
    }
    result[CONTENT] = content;
    result[ANNOT_KEY] = annotationKey;
    result[ANNOT_VALUE] = annotationValue;
    return result;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#filterRowNames(java.lang.String, java.lang.String, java.lang.String)
   */
  public List filterRowNames(final String rowPrefix, final String rowInfix, final String rowSuffix)
      throws MatrixReadFailedException {
    return StringUtils.filterStringList(rowPrefix, rowInfix, rowSuffix, getRowNames());
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getColNames()
   */
  public List getColNames() throws MatrixReadFailedException {
    return Collections.unmodifiableList(this.colNames);
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getMatrixIdentifier()
   */
  public String getMatrixIdentifier() throws MatrixReadFailedException {
    return this.delegate.getMatrixIdentifier();
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getRowNames()
   */
  public List getRowNames() throws MatrixReadFailedException {
    return Collections.unmodifiableList(this.rowNames);
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getString(java.lang.String, java.lang.String)
   */
  public String getString(final String colName, final String rowName) throws MatrixReadFailedException {
    if (isMaskedCell(colName, rowName)) {
      return "";
    } else {
      return splitContentAndAnnotation(getStringFromDelegate(colName, rowName))[CONTENT];
    }
  }
  
  private boolean isMaskedCell(String colName, String rowName) {
    colName = checkForColAlias(colName);
    rowName = checkForRowAlias(rowName);
    return (colName.endsWith(ANNOTATION_KEYVALUE_SEPARATOR) || rowName.endsWith(ANNOTATION_KEYVALUE_SEPARATOR));
  }
  
  private String getStringFromDelegate(final String colName, final String rowName) {
    return this.delegate.getString(checkForColAlias(colName), checkForRowAlias(rowName));
  }
  
  private String checkForRowAlias(final String rowName) {
    return checkForAlias(rowName, this.rowAliases);
  }
  
  private String checkForColAlias(final String colName) {
    return checkForAlias(colName, this.colAliases);
  }
  
  private String checkForAlias(final String name, final Map aliases) {
    final String alias = (String) aliases.get(name);
    if (alias != null) {
      return alias;
    } else {
      return name;
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#filterColumnNames(java.lang.String, java.lang.String,
   *      java.lang.String)
   */
  public List filterColumnNames(final String colPrefix, final String colInfix, final String colSuffix)
      throws MatrixReadFailedException {
    return StringUtils.filterStringList(colPrefix, colInfix, colSuffix, getColNames());
  }
  
  public boolean isDefinedAt(final String colName, final String rowName) {
    return this.delegate.isDefinedAt(checkForColAlias(colName), checkForRowAlias(rowName));
  }
}
