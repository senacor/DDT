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

import com.senacor.ddt.objectmatrix.AbstractDefaultStringMatrixBasedObjectMatrixFactory;
import com.senacor.ddt.objectmatrix.ObjectMatrixCreationFailedException;
import com.senacor.ddt.objectmatrix.ObjectMatrixFactory;
import com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader;
import com.senacor.ddt.util.ParamChecker;

import jxl.Workbook;
import jxl.WorkbookSettings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

/**
 * An {@link ObjectMatrixFactory} implementation that creates ObjectMatrices based on {@link JExcelStringMatrixReader},
 * i.e. using Microsoft Excel workbooks as datasource. It can open Excel files via a URL, an InputStream or a
 * Classpath-relative path. All parameters supported by {@link JExcelStringMatrixReader} are supported as well.
 * Instances of this class are <em>not</em> reusable. Always construct fresh instances if you need to create another set
 * of ObjectMatrices.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class ExcelObjectMatrixFactory extends AbstractDefaultStringMatrixBasedObjectMatrixFactory {
  private static final Log log = LogFactory.getLog(ExcelObjectMatrixFactory.class);
  
  private final InputStream excelInput;
  
  private final String[] sheets;
  
  private boolean transposed;
  
  private final WorkbookSettings workbookSettings = new WorkbookSettings();
  
  /**
   * Construct an ExcelObjectMatrixFactory from the Excel workbook file available at the given URL using the specified
   * sheets.
   * 
   * @param excelUrl
   *          URL of a readable .xls file. Must not be null.
   * @param sheetNames
   *          Names of the contained Excel sheets to be used. Must not be null or empty.
   * @throws IOException
   *           If the call to excelUrl.openStream() fails.
   */
  public ExcelObjectMatrixFactory(final URL excelUrl, final String[] sheetNames) throws IOException {
    this(nullsafeUrlToStream("excelUrl", excelUrl), sheetNames);
  }
  
  /**
   * Construct an ExcelObjectMatrixFactory from the Excel workbook file available via the given InputStream using the
   * specified sheets.
   * 
   * @param excelInput
   *          An InputStream that delivers a valid .xls file. Must not be null.
   * @param sheetNames
   *          Names of the contained Excel sheets to be used. Must not be null or empty.
   */
  public ExcelObjectMatrixFactory(final InputStream excelInput, final String[] sheetNames) {
    ParamChecker.notNull("excelInput", excelInput);
    ParamChecker.notNull("sheetNames", sheetNames);
    ParamChecker.require("need at least one sheet name", sheetNames.length > 0);
    this.excelInput = excelInput;
    this.sheets = sheetNames;
  }
  
  /**
   * Construct an ExcelObjectMatrixFactory from the Excel workbook file available at the given path in the classpath
   * using the specified sheets. The classloader returned by
   * <code>ExcelObjectMatrixFactory.class.getClassLoader()</code> is used to open the required InputStream.
   * 
   * @param xlsInClassPath
   *          Classpath-relative path to a valid .xls file. Must not be null.
   * @param sheetNames
   *          Names of the contained Excel sheets to be used. Must not be null or empty.
   */
  public ExcelObjectMatrixFactory(final String xlsInClassPath, final String[] sheetNames) {
    this(ExcelObjectMatrixFactory.class.getClassLoader().getResourceAsStream(xlsInClassPath), sheetNames);
  }
  
  /**
   * Convenience constructor to be used when you only need a single sheet from the Excel workbook file. Delegates to
   * {@link ExcelObjectMatrixFactory#ExcelObjectMatrixFactory(String, String[])}, see there.
   * 
   * @param xlsInClassPath
   *          -
   * @param sheetName
   *          -
   */
  public ExcelObjectMatrixFactory(final String xlsInClassPath, final String sheetName) {
    this(xlsInClassPath, new String[] { sheetName });
  }
  
  /**
   * @see com.senacor.ddt.objectmatrix.AbstractDefaultStringMatrixBasedObjectMatrixFactory#createReaders()
   */
  public StringMatrixReader[] createReaders() {
    assert this.sheets != null : "sheets are null";
    assert this.sheets.length > 0 : "sheets are empty";
    assert this.excelInput != null : "excelInput is null";
    
    Workbook workbook;
    try {
      log.info("Loading Excel file...");
      workbook = Workbook.getWorkbook(this.excelInput, this.workbookSettings);
      log.info("Loading Excel file... done.");
    } catch (final Exception e) {
      throw new ObjectMatrixCreationFailedException("Failed to load Excel workbook", e);
    }
    
    final StringMatrixReader[] readers = new StringMatrixReader[this.sheets.length];
    for (int i = 0; i < readers.length; i++) {
      final JExcelStringMatrixReader reader = new JExcelStringMatrixReader(workbook, this.sheets[i], this.transposed);
      readers[i] = reader;
    }
    
    return readers;
  }
  
  /**
   * Set to true if the Excel file contains the data in transposed format.
   * 
   * @param transposed
   *          flag
   * @return <code>this</code>, for method chaining
   * 
   * @see JExcelStringMatrixReader#isTransposed()
   */
  public ExcelObjectMatrixFactory setTransposed(final boolean transposed) {
    this.transposed = transposed;
    return this;
  }
  
  /**
   * Set the encoding of the Excel file that will be read. This setting is passed directly into {@link WorkbookSettings}
   * .
   * 
   * @param encoding
   *          The encoding. See {@link WorkbookSettings#setEncoding(String)}
   * @return <code>this</code>, for method chaining
   */
  public ExcelObjectMatrixFactory setEncoding(final String encoding) {
    this.workbookSettings.setEncoding(encoding);
    return this;
  }
  
}
