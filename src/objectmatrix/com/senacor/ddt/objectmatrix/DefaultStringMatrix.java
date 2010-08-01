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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.util.ParamChecker;
import com.senacor.ddt.util.StringUtils;

/**
 * The default {@link StringMatrix} implementation with embedded column and row titles. Instances of this class
 * represent a 2-dimensional array of Strings. The column and row titles required by StringMatrix are taken from a row
 * and column contained in this matrix itself. StringMatrix implementations that get their column or row titles from a
 * different source (e.g. column names in an RDBMS) should use a different implementation.
 * <p>
 * This implementation provides all necessary behavior for an embedded-title StringMatrix. All it requires is an
 * implementation of {@link StringMatrixReader} to actually read data from an underlying source.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class DefaultStringMatrix implements IndexedStringMatrix {
  private static final Log log = LogFactory.getLog(DefaultStringMatrix.class);
  
  private final Map rowTitleToIndicesMap = new HashMap();
  
  private final List rowTitles = new ArrayList();
  
  private final List colTitles = new ArrayList();
  
  private final Map colTitleToIndicesMap = new HashMap();
  
  private final int rowContainingColumnTitles;
  
  private final int columnContainingRowTitles;
  
  private final StringMatrixReader reader;
  
  /**
   * Construct a DefaultStringMatrix based on the given reader, placing the title column/row at the default indices
   * (0,0). Delegates to {@link #DefaultStringMatrix(StringMatrixReader, int, int)}. See there.
   * 
   * @param reader
   *          the reader
   * @throws DuplicateColumnTitleException
   *           if a column title other than "" or "Reserved" is encountered more than once
   * @throws DuplicateRowTitleException
   *           if a row title other than "" or "Reserved" is encountered more than once
   */
  public DefaultStringMatrix(final StringMatrixReader reader) throws DuplicateColumnTitleException,
      DuplicateRowTitleException {
    this(reader, 0, 0);
  }
  
  /**
   * Construct a DefaultStringMatrix based on the given reader, placing the title column/row at the given indices.
   * 
   * @param reader
   *          the reader
   * @param columnContainingRowTitles
   *          index of the column that contains the row titles
   * @param rowContainingColumnTitles
   *          index of the row that contains the column titles
   * @throws DuplicateColumnTitleException
   *           if a column title other than "" or "Reserved" is encountered more than once
   * @throws DuplicateRowTitleException
   *           if a row title other than "" or "Reserved" is encountered more than once
   */
  public DefaultStringMatrix(final StringMatrixReader reader, final int columnContainingRowTitles,
      final int rowContainingColumnTitles) throws DuplicateColumnTitleException, DuplicateRowTitleException {
    ParamChecker.notNull("reader", reader);
    this.reader = reader;
    this.columnContainingRowTitles = columnContainingRowTitles;
    this.rowContainingColumnTitles = rowContainingColumnTitles;
    populateTitleMaps();
  }
  
  /**
   * Collect the column and row titles and map them to the corresponding indices.
   * 
   * @throws DuplicateColumnTitleException
   *           if a column title other than "" or "Reserved" is encountered more than once
   * @throws DuplicateRowTitleException
   *           if a row title other than "" or "Reserved" is encountered more than once
   */
  private void populateTitleMaps() throws DuplicateColumnTitleException, DuplicateRowTitleException {
    log.debug("Populating title maps...");
    
    final int columns = this.reader.getNumberOfColumns();
    final int rows = this.reader.getNumberOfRows();
    
    populateTitleMap(columns, true);
    populateTitleMap(rows, false);
    
    assert columns == this.colTitles.size() : "not all columns made it into the list!";
    assert rows == this.rowTitles.size() : "not all rows made it into the list!";
    assert this.colTitleToIndicesMap.keySet().containsAll(this.colTitles) : "not all columns made it into the map!";
    assert this.rowTitleToIndicesMap.keySet().containsAll(this.rowTitles) : "not all rows made it into the map!";
    log.debug("Done: Title maps populated.");
  }
  
  private void populateTitleMap(final int titlecount, final boolean columnMode) {
    for (int i = 0; i < titlecount; i++) {
      String titleString;
      
      final int titleIndex = columnMode ? this.columnContainingRowTitles : this.rowContainingColumnTitles;
      final int oppositeTitleIndex = columnMode ? this.rowContainingColumnTitles : this.columnContainingRowTitles;
      final Map titleMap = columnMode ? this.colTitleToIndicesMap : this.rowTitleToIndicesMap;
      final List titleList = columnMode ? this.colTitles : this.rowTitles;
      if (i == titleIndex) {
        titleString = Tokens.RESERVED; // mask the column containing row titles
      } else {
        if (columnMode) {
          titleString = getString(i, oppositeTitleIndex).trim();
        } else {
          titleString = getString(oppositeTitleIndex, i).trim();
        }
      }
      
      // empty column titles and reserved columns are allowed to appear multiple times
      if (!titleList.contains(titleString) || (titleString.length() == 0)
          || Tokens.RESERVED.equalsIgnoreCase(titleString)) {
        titleMap.put(titleString, new Integer(i));
        titleList.add(titleString);
      } else {
        if (columnMode) {
          // but no other titles may appear twice
          final String msg = "Found duplicate column title '" + titleString + "'";
          log.error(msg);
          throw new DuplicateColumnTitleException(msg);
        } else {
          final String msg = "Found duplicate row title '" + titleString + "''";
          log.error(msg);
          throw new DuplicateRowTitleException(msg);
        }
      }
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.IndexedStringMatrix#getString(int, int)
   */
  public String getString(final int colIndex, final int rowIndex) {
    return this.reader.getString(colIndex, rowIndex);
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getString(java.lang.String, java.lang.String)
   */
  public String getString(final String colName, final String rowName) {
    log.debug("Getting string at col name '" + colName + "', row name '" + rowName + "'.");
    
    return getString(getColumnIndex(colName), getRowIndex(rowName));
  }
  
  /**
   * Get the index of the column with the given name.
   * 
   * @param columnName
   *          the column name
   * @return the 0-based index of the corresponding column
   */
  private int getColumnIndex(final String columnName) {
    final Integer col = (Integer) this.colTitleToIndicesMap.get(columnName);
    
    if (col == null) {
      throw new ColumnNotFoundException("Column '" + columnName + "' doesn't exist");
    } else {
      return col.intValue();
    }
  }
  
  /**
   * Get the index of the row with the given name.
   * 
   * @param rowName
   *          the row name
   * @return the 0-based index of the corresponding row
   */
  private int getRowIndex(final String rowName) {
    final Integer row = (Integer) this.rowTitleToIndicesMap.get(rowName);
    
    if (row == null) {
      throw new RowNotFoundException("Row '" + rowName + "' doesn't exist");
    } else {
      return row.intValue();
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getRowNames()
   */
  public List getRowNames() {
    return this.rowTitles;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getColNames()
   */
  public List getColNames() {
    return this.colTitles;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.IndexedStringMatrix#getString(java.lang.String, int)
   */
  public String getString(final String colName, final int rowIndex) {
    return getString(getColumnIndex(colName), rowIndex);
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.IndexedStringMatrix#getString(int, java.lang.String)
   */
  public String getString(final int colIndex, final String rowName) {
    return getString(colIndex, getRowIndex(rowName));
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#filterRowNames(java.lang.String, java.lang.String, java.lang.String)
   */
  public List filterRowNames(final String rowPrefix, final String rowInfix, final String rowSuffix) {
    return StringUtils.filterStringList(rowPrefix, rowInfix, rowSuffix, getRowNames());
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.StringMatrix#filterColumnNames(java.lang.String, java.lang.String,
   *      java.lang.String)
   */
  public List filterColumnNames(final String colPrefix, final String colInfix, final String colSuffix)
      throws MatrixReadFailedException {
    return StringUtils.filterStringList(colPrefix, colInfix, colSuffix, getColNames());
  }
  
  /**
   * This implementation asks the contained StringMatrixReader for an identifier, because only it knows anything about
   * the underlying datasource.
   * 
   * @see com.senacor.ddt.objectmatrix.StringMatrix#getMatrixIdentifier()
   */
  public String getMatrixIdentifier() {
    return this.reader.getIdentifier();
  }
  
  /**
   * Interface used by {@link DefaultStringMatrix} to actually read the data from an underlying datasource. To utilize a
   * datasource that contains embedded row and column titles, implement this interface and give an instance of it to
   * DefaultStringMatrix.
   * 
   * @author Carl-Eric Menzel
   */
  public static interface StringMatrixReader {
    /**
     * Read the String at the given position in the Matrix. Must conform to the formatting rules specified in
     * {@link StringMatrix}.
     * 
     * @param colIndex
     *          column index, 0-based
     * @param rowIndex
     *          row index, 0-based
     * @return the string at the given position
     * @throws IndexOutOfBoundsException
     *           if colIndex or rowIndex fall outside the bounds of the matrix
     * @throws MatrixReadFailedException
     *           if reading the matrix fails for any reason
     * 
     */
    String getString(int colIndex, int rowIndex) throws IndexOutOfBoundsException, MatrixReadFailedException;
    
    /**
     * Get the number of rows available in the matrix.
     * 
     * @return number of rows
     * @throws MatrixReadFailedException
     *           if reading the matrix fails for any reason
     */
    int getNumberOfRows() throws MatrixReadFailedException;
    
    /**
     * Get the number of columns available in the matrix.
     * 
     * @return number of columns
     * @throws MatrixReadFailedException
     *           if reading the matrix fails for any reason
     */
    int getNumberOfColumns() throws MatrixReadFailedException;
    
    /**
     * Return a unique identifier for the matrix.
     * 
     * @return the identifier.
     * @throws MatrixReadFailedException
     *           if reading the matrix fails for any reason
     */
    String getIdentifier() throws MatrixReadFailedException;
  }
  
  public boolean isDefinedAt(final String colName, final String rowName) {
    return !StringUtils.isNullOrBlank(getString(colName, rowName));
  }
}
