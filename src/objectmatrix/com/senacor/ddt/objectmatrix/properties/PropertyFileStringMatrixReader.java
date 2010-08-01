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

package com.senacor.ddt.objectmatrix.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.objectmatrix.MatrixReadFailedException;
import com.senacor.ddt.objectmatrix.DefaultStringMatrix.StringMatrixReader;
import com.senacor.ddt.util.ParamChecker;

/**
 * FIXME: Testen. Dokumentieren.
 * 
 * @author Ralph Winzinger
 * @author Carl-Eric Menzel
 */
public class PropertyFileStringMatrixReader implements StringMatrixReader {
  private static final String TESTCASE_NAME_ROWNAME = "__testCaseName__";
  
  private static final Log log = LogFactory.getLog(PropertyFileStringMatrixReader.class);
  
  private final List propertySets = new ArrayList();
  
  private final List propertyNames = new ArrayList();
  
  private final String identifier;
  
  public PropertyFileStringMatrixReader(final String[] fileNames, final String identifier) {
    ParamChecker.notBlank("identifier", identifier);
    ParamChecker.notBlankAnywhere("fileNames", fileNames);
    this.identifier = identifier;
    for (int i = 0; i < fileNames.length; i++) {
      final String fileName = fileNames[i];
      this.log.info("reading '" + fileName + "' ...");
      final Properties properties = new Properties();
      try {
        final InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is == null) {
          throw new RuntimeException("file '" + fileName + "' not found");
        }
        properties.load(is);
        // add filename as testcase-name
        properties.put(TESTCASE_NAME_ROWNAME, fileName);
        this.propertySets.add(properties);
        this.log.info("read ok");
        
        final Iterator keyIt = properties.keySet().iterator();
        while (keyIt.hasNext()) {
          final String key = (String) keyIt.next();
          if ((properties.get(key) == null) || (((String) properties.get(key)).length() == 0)) {
            this.log.info("skipping '" + key + "' because it is empty");
            continue;
          }
          if (!this.propertyNames.contains(key)) {
            this.propertyNames.add(key);
          }
          final String value = properties.getProperty(key);
          this.log.debug(key + " -> " + value);
        }
        
        final StringWriter sw = new StringWriter();
        properties.list(new PrintWriter(sw));
        this.log.debug("contents:");
        this.log.debug(sw.toString());
      } catch (final IOException e) {
        this.log.error("error reading file - skipping", e);
      }
    }
    
    Collections.sort(this.propertyNames);
    
    if (this.log.isDebugEnabled()) {
      this.log.debug("sorted propertyNames:");
      
      for (int i = 0; i < this.propertyNames.size(); i++) {
        final String name = (String) this.propertyNames.get(i);
        this.log.debug(name);
      }
    }
  }
  
  public String getString(final int i, final int j) throws IndexOutOfBoundsException, MatrixReadFailedException {
    String propertyName = null;
    Properties properties = null;
    
    if (j == 0) {
      // Zugriff auf erste Zeile (Name der TestCases bzw. 'reserved' in der ersten Spalte)
      if (i == 0) {
        // erste Spalte -> 'reserved'
        return "reserved";
      } else {
        // Spalte 2-n -> TestCase-Name (ist als 'virtueller' Eintrag realisiert)
        // Spaltenindex muss um eine Stelle zurückgezählt werden, da die erste Spalte (Feldnamen)
        // virtuell ist
        properties = (Properties) this.propertySets.get(i - 1);
        return (String) properties.get(TESTCASE_NAME_ROWNAME);
      }
    } else {
      // Zugriff auf Zeile 2-n
      // Zeilenindex muss um eine Stelle zurückgezählt werden, da die erste Zeile (Testcase-Namen)
      // virtuell ist
      propertyName = (String) this.propertyNames.get(j - 1);
      if (i == 0) {
        // erste Spalte -> Feldname
        return propertyName;
      } else {
        // Zeile 2-n, Spalte 2-n -> echter Inhalt
        // Spaltenindex muss um eine Stelle zurückgezählt werden, da die erste Spalte (Feldnamen)
        // virtuell ist
        properties = (Properties) this.propertySets.get(i - 1);
        if (properties.get(propertyName) == null) {
          this.log.warn("Property '" + propertyName + "' not existing in File '"
              + ((String) properties.get(TESTCASE_NAME_ROWNAME)) + "'");
          return "";
        }
        return (String) properties.get(propertyName);
      }
    }
  }
  
  public int getNumberOfRows() throws MatrixReadFailedException {
    return this.propertyNames.size() + 1;
  }
  
  public int getNumberOfColumns() throws MatrixReadFailedException {
    return this.propertySets.size() + 1;
  }
  
  public String getIdentifier() throws MatrixReadFailedException {
    return this.identifier;
  }
}
