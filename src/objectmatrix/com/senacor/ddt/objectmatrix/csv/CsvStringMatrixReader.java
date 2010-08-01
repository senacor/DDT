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

package com.senacor.ddt.objectmatrix.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.senacor.ddt.objectmatrix.DefaultStringMatrix;
import com.senacor.ddt.objectmatrix.StringMatrix;
import com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader;
import com.senacor.ddt.util.ParamChecker;

/**
 * A StringMatrixReader implementation that loads a CSV (Character Separated Value) file into memory and then provides
 * indexed-based access to the contained values. Whitespace surrounding field values is trimmed.
 * <p>
 * The delimiting character separating the values can be specified, even as a Regular Expression (internally
 * {@link String#split(String, int)} is used).
 * <p>
 * All lines in the CSV file must have the same number of delimiters, i.e. the same number of fields.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class CsvStringMatrixReader implements StringMatrixReader {
  private static final Log log = LogFactory.getLog(CsvStringMatrixReader.class);
  
  /**
   * This array contains the full data of the CSV file that was read in. The first index selects the column, the second
   * index selects the row.
   * <p>
   * This makes filling the array a little awkward when reading the file line by line, but it correctly represents the
   * natural way to access fields in a matrix and also matches the parameter order of all applicable methods in
   * StringMatrix and ObjectMatrix.
   */
  private final String[][] data;
  
  private final int rows;
  
  private final int columns;
  
  private final String identifier;
  
  private final char delim;
  
  /**
   * Construct a CsvStringMatrixReader instance, reading the CSV file from the given {@link Reader} and using the given
   * delimiter to split the lines into fields. The entire file will be read into memory by this constructor.
   * 
   * @param input
   *          A Reader providing the CSV file. Must not be null.
   * @param delimiter
   *          The delimiter used to separate the values in each line. Must not be empty.
   * @param identifier
   *          An arbitrary identifier for the StringMatrix ({@link StringMatrix#getMatrixIdentifier()}). Must not be
   *          null.
   * @throws IOException
   *           If the file cannot be read.
   * @throws IllegalArgumentException
   *           If
   *           <ul>
   *           <li>any of the parameters is null</li> <li>not all lines in the CSV file have the same number of fields
   *           </li> <li>the file is empty</li>
   *           </ul>
   */
  public CsvStringMatrixReader(final Reader input, final char delimiter, final String identifier) throws IOException,
      IllegalArgumentException {
    ParamChecker.notNull("input", input);
    ParamChecker.notBlank("delimiter", delimiter);
    ParamChecker.notNull("identifier", identifier);
    this.identifier = identifier;
    this.delim = delimiter;
    
    final List lines = readFile(input);
    this.data = buildArray(lines);
    this.rows = this.data[0].length;
    this.columns = this.data.length;
    if (log.isDebugEnabled()) {
      log.debug("Loaded CSV file with " + this.rows + " rows and " + this.columns + " columns.");
    }
  }
  
  /**
   * Read the entire file line-by-line and return the result as a list.
   * 
   * @param input
   *          the file to be read
   * @return a list of strings representing each line in the file
   * @throws IOException
   *           if something goes wrong
   */
  private List readFile(final Reader input) throws IOException {
    return new CSVReader(input, this.delim).readAll();
  }
  
  /**
   * Convert the list of lines to a String[][] array by splitting each line into fields using the given delimiter regex.
   * 
   * @param lines
   *          List of lines
   * @return the 2-dimensional string array
   */
  private String[][] buildArray(final List lines) {
    assert !lines.isEmpty() : "lines is unexpectedly empty";
    
    final int numberOfLines = lines.size();
    
    final String[] firstLine = (String[]) lines.get(0);
    final int numberOfFields = firstLine.length;
    
    final String[][] result = new String[numberOfFields][numberOfLines];
    for (int currentRow = 0; currentRow < lines.size(); currentRow++) {
      final String[] currentLine = (String[]) lines.get(currentRow);
      final boolean lineIsEmpty;
      if ((currentLine.length == 0) || ((currentLine.length == 1) && (currentLine[0].trim().length() == 0))) {
        // OpenCSV's CSVReader gives us a 1-element string array containing an empty string, if it
        // encounters
        // an empty line. we just want to skip over empty lines:
        lineIsEmpty = true;
      } else {
        if (currentLine.length != numberOfFields) {
          throw new IllegalArgumentException("Field count in line " + currentRow
              + " differs from field count in first line");
        }
        lineIsEmpty = false;
      }
      for (int currentColumn = 0; currentColumn < numberOfFields; currentColumn++) {
        // since we read line by line the outer loop walks over the rows and the inner loop over the
        // columns.
        // thus the filling of the array is a little awkward (faster-counting index is the first
        // index),
        // but as explained at {@link #data} this makes for more convenient reading access.
        result[currentColumn][currentRow] = lineIsEmpty ? "" : currentLine[currentColumn].trim();
      }
    }
    
    return result;
  }
  
  /**
   * Returns the String at the given position in the matrix/array.
   * 
   * @see DefaultStringMatrix.StringMatrixReader#getString(int, int)
   */
  public String getString(final int colIndex, final int rowIndex) throws IndexOutOfBoundsException {
    return this.data[colIndex][rowIndex];
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader#getNumberOfRows()
   */
  public int getNumberOfRows() {
    return this.rows;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader#getNumberOfColumns()
   */
  public int getNumberOfColumns() {
    return this.columns;
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader#getIdentifier()
   */
  public String getIdentifier() {
    return this.identifier;
  }
}
