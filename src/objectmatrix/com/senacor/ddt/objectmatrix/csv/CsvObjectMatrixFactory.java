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

import com.senacor.ddt.objectmatrix.AbstractDefaultStringMatrixBasedObjectMatrixFactory;
import com.senacor.ddt.objectmatrix.ObjectMatrixCreationFailedException;
import com.senacor.ddt.objectmatrix.ObjectMatrixFactory;
import com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader;
import com.senacor.ddt.util.ParamChecker;

import java.io.IOException;
import java.io.Reader;

/**
 * /** An {@link ObjectMatrixFactory} implementation that creates ObjectMatrices based on {@link CsvStringMatrixReader},
 * i.e. using character-separated-value files as datasource.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class CsvObjectMatrixFactory extends AbstractDefaultStringMatrixBasedObjectMatrixFactory {
  private final Reader[] csvInput;
  
  private final String[] identifier;
  
  private final char delimiter;
  
  /**
   * Construct a CsvObjectMatrixFactory from one or more CSV input files.
   * 
   * @param csvInput
   *          One or more Reader instances pointing to valid CSV files. Must not be null or empty.
   * @param delimRegex
   *          The regex used to split the lines into fields. See
   *          {@link CsvStringMatrixReader#CsvStringMatrixReader(Reader, String, String)}. Must not be null or blank.
   * @param identifier
   *          The identifiers of the given csv files. Must be of the exact same length as csvInput.
   * 
   * @see CsvStringMatrixReader#CsvStringMatrixReader(Reader, String, String)
   * 
   * @throws IllegalArgumentException
   *           if
   *           <ul>
   *           <li>Any of the parameters is null or empty</li>
   *           <li>csvInput and identifier are of different lengths</li>
   *           </ul>
   */
  public CsvObjectMatrixFactory(final Reader[] csvInput, final char delimiter, final String[] identifier) {
    ParamChecker.notNullAnywhere("csvInput", csvInput);
    ParamChecker.notBlank("delimRegex", delimiter);
    ParamChecker.notNullAnywhere("identifier", identifier);
    ParamChecker.require("csvInput[] and identifier[] must have the same length", csvInput.length == identifier.length);
    this.delimiter = delimiter;
    this.csvInput = csvInput;
    this.identifier = identifier;
  }
  
  /**
   * Construct a CsvObjectMatrixFactory from a single CSV input file. Delegates to the constructor taking array
   * parameters.
   * 
   * @see #CsvObjectMatrixFactory(Reader[], String, String[])
   */
  public CsvObjectMatrixFactory(final Reader csvInput, final char delimiter, final String identifier) {
    this(new Reader[] { csvInput }, delimiter, new String[] { identifier });
  }
  
  public StringMatrixReader[] createReaders() {
    try {
      final StringMatrixReader[] readers = new StringMatrixReader[this.csvInput.length];
      for (int i = 0; i < this.csvInput.length; i++) {
        readers[i] = new CsvStringMatrixReader(this.csvInput[i], this.delimiter, this.identifier[i]);
      }
      
      return readers;
    } catch (final IOException e) {
      throw new ObjectMatrixCreationFailedException("Error creating CSV ObjectMatrix", e);
    }
  }
}
