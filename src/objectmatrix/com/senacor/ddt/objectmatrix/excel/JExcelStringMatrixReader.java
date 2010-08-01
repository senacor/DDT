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

package com.senacor.ddt.objectmatrix.excel;

import com.senacor.ddt.objectmatrix.StringMatrix;
import com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader;
import com.senacor.ddt.util.ParamChecker;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.Locale;
import java.util.TimeZone;

/**
 * StringMatrixReader implementation that represents a view on a single sheet of a Microsoft Excel workbook file using
 * Andy Khan's JExcel library.
 * <p>
 * This class doesn't read files directly. Instead the constructor expects a Workbook instance that must be prepared by
 * the caller. This way Workbook instances can be reused for reading more than one sheet from a single file without
 * having to load the entire file for each sheet (JExcelStringMatrixReader instance).
 * <p>
 * Due to Excel's limitation to 255 columns, this implementation allows transposed sheets: By transposing the table we
 * can have up to 65k "columns". Experience shows that 255 "rows" are usually more than enough for most test cases.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 * @version $Id$
 */
public class JExcelStringMatrixReader implements StringMatrixReader {
  private static final Log log = LogFactory.getLog(JExcelStringMatrixReader.class);
  
  private final Sheet sheet;
  
  private final boolean transposed;
  
  private final String identifier;
  
  private final Range[] mergedCells;
  
  /**
   * Construct a new instance, representing a view on a single sheet of the given Excel Workbook.
   * 
   * @param workbook
   *          The Workbook to read a sheet from. Must not be null.
   * @param sheetName
   *          The name of the sheet to read. Must not be null.
   * @param transposed
   *          Set to true if the data in the given sheet is transposed (swapped columns and rows).
   */
  public JExcelStringMatrixReader(final Workbook workbook, final String sheetName, final boolean transposed) {
    ParamChecker.notNull("workbook", workbook);
    ParamChecker.notNull("sheetName", sheetName);
    this.identifier = "[" + sheetName + "]";
    log.info("Getting sheet '" + sheetName + "'...");
    this.sheet = workbook.getSheet(sheetName);
    if (this.sheet == null) {
      throw new IllegalArgumentException("Could not find sheet named '" + sheetName + "'.");
    }
    this.mergedCells = this.sheet.getMergedCells();
    log.info("Getting sheet '" + sheetName + "'... done.");
    this.transposed = transposed;
  }
  
  /**
   * Read the string at the given coordinates, formatting it as required by the StringMatrix interface contract. If the
   * {@link #isTransposed()} flag is true, the indices are automatically swapped when going down into the Excel sheet.
   * Thus, the transposed nature of the data is completely transparent to any callers.
   * 
   * @see com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader#getString(int, int)
   */
  public String getString(final int colIndex, final int rowIndex) {
    int actualColIndex, actualRowIndex;
    if (isTransposed()) {
      actualColIndex = rowIndex;
      actualRowIndex = colIndex;
    } else {
      actualColIndex = colIndex;
      actualRowIndex = rowIndex;
    }
    if (this.mergedCells.length > 0) {
      for (int i = 0; i < this.mergedCells.length; i++) {
        final Range mergedRange = this.mergedCells[i];
        final Cell topLeft = mergedRange.getTopLeft();
        final Cell bottomRight = mergedRange.getBottomRight();
        final int top = topLeft.getRow();
        final int left = topLeft.getColumn();
        final int bottom = bottomRight.getRow();
        final int right = bottomRight.getColumn();
        if ((left <= actualColIndex) && (actualColIndex <= right) && (top <= actualRowIndex)
            && (actualRowIndex <= bottom)) {
          actualColIndex = left;
          actualRowIndex = top;
          break;
        }
      }
    }
    return readCell(actualColIndex, actualRowIndex);
  }
  
  private String readCell(final int colIndex, final int rowIndex) {
    log.debug("Getting string at col " + colIndex + ", row " + rowIndex);
    
    Cell cell;
    String result;
    
    cell = this.sheet.getCell(colIndex, rowIndex);
    
    if (cell.getType().equals(CellType.DATE) || cell.getType().equals(CellType.DATE_FORMULA)) {
      log.debug("Found date cell.");
      
      final DateCell dcell = (DateCell) cell;
      log.debug("Raw value is " + dcell.getDate());
      result = getDateFormat().format(dcell.getDate());
    } else if (cell.getType().equals(CellType.NUMBER) || cell.getType().equals(CellType.NUMBER_FORMULA)) {
      log.debug("Found number cell.");
      
      final NumberCell ncell = (NumberCell) cell;
      final double rawValue = ncell.getValue();
      log.debug("Raw value is " + rawValue);
      
      if (Double.isNaN(rawValue)) {
        log.debug("Converting NaN to \"\".");
        result = "";
      } else {
        result = getNumberFormat().format(rawValue);
      }
    } else {
      log.debug("Found cell without special handling.");
      result = cell.getContents();
    }
    
    if (result == null) {
      log.debug("Cell content is null, using empty string instead.");
      result = "";
    }
    
    log.debug("Returning string '" + result + "'");
    
    return result;
  }
  
  private NumberFormat getNumberFormat() {
    final DecimalFormat numberFormat = new DecimalFormat(StringMatrix.FORMAT_NUMBER);
    // even if i specify a pattern, DecimalFormat is STILL locale sensitive. ARGH! therefore, i am
    // setting it to US to get the
    // desired normalized output with one decimal point "." and no other special symbols
    numberFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    numberFormat.setDecimalSeparatorAlwaysShown(false);
    
    return numberFormat;
  }
  
  private DateFormat getDateFormat() {
    final SimpleDateFormat dateFormat = new SimpleDateFormat(StringMatrix.FORMAT_DATE);
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // dates are adjusted from GMT to local
    // timezone by java.util.Date,
    // which can lead to off-by-one errors when only looking at dates
    // without daytime
    
    return dateFormat;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader#getNumberOfRows()
   */
  public int getNumberOfRows() {
    if (isTransposed()) {
      return this.sheet.getColumns();
    } else {
      return this.sheet.getRows();
    }
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader#getNumberOfColumns()
   */
  public int getNumberOfColumns() {
    if (isTransposed()) {
      return this.sheet.getRows();
    } else {
      return this.sheet.getColumns();
    }
  }
  
  /**
   * @return true if this reader transposes the underlying matrix.
   * @see #JExcelStringMatrixReader(Workbook, String, boolean)
   */
  public boolean isTransposed() {
    return this.transposed;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader#getIdentifier()
   */
  public String getIdentifier() {
    return this.identifier;
  }
}
