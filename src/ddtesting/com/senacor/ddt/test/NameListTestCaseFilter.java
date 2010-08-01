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

package com.senacor.ddt.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Test case filter that only allows test cases in its name list.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class NameListTestCaseFilter implements TestCaseFilter {
  private final List allowedNames = new ArrayList(); // List<String>
  
  /**
   * Constructor
   */
  public NameListTestCaseFilter() {
    // nothing to be done
  }
  
  /**
   * @param names
   *          A collection of test case names that are to be allowed. The filter will copy the names into its internal
   *          list and not keep a reference to this collection.
   */
  public NameListTestCaseFilter(final Collection names) {
    if (names != null) {
      this.allowedNames.addAll(names);
    }
  }
  
  /**
   * @param names
   *          An array of test case names that are to be allowed. The filter will copy the names into its internal list
   *          and not keep a reference to this array.
   */
  public NameListTestCaseFilter(final String[] names) {
    this((names != null) ? Arrays.asList(names) : null);
  }
  
  /**
   * @return An unmodifiable view of the list of test case names.
   */
  public List getAllowedNames() {
    return Collections.unmodifiableList(this.allowedNames);
  }
  
  /**
   * @return true if <code>getAllowedNames().contains(tcd.getTestCaseName())</code>.
   * @see com.senacor.ddt.test.TestCaseFilter#isTestCaseAllowedToRun(com.senacor.ddt.test.TestCaseData)
   */
  public boolean isTestCaseAllowedToRun(final TestCaseData tcd) {
    return this.allowedNames.contains(tcd.getTestCaseName());
  }
  
  /**
   * Add a name to the list of allowed test case names.
   * 
   * @param name
   *          the name to add
   * @return <code>this</code>, for method chaining.
   */
  public NameListTestCaseFilter addName(final String name) {
    if (name != null) {
      this.allowedNames.add(name);
    }
    
    return this;
  }
}
