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
package com.senacor.ddt.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Generic utility class to match strings against sets of including and excluding regular expressions.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class StringFilter {
  private final Set /* <Pattern> */includingFilters = new HashSet();
  
  private final Set /* <Pattern> */excludingFilters = new HashSet();
  
  /**
   * StringFilter without any active filters.
   */
  public StringFilter() {
    // nothing to be done
  }
  
  /**
   * Copy constructor.
   * 
   * @param original
   *          filter to copy from
   */
  public StringFilter(final StringFilter original) {
    this.includingFilters.addAll(original.includingFilters);
    this.excludingFilters.addAll(original.excludingFilters);
  }
  
  /**
   * Add an including filter regex. If any including filters are present, only those strings will be accepted that are
   * matched by at least one including filter <em>and not</em> by any excluding filters.
   * 
   * @param filter
   *          regular expression that will be matched against strings
   * @return <code>this</code> for method chaining
   * @throws PatternSyntaxException
   *           if the pattern is malformed
   */
  public StringFilter addIncludingFilter(final String filter) throws PatternSyntaxException {
    final Pattern pattern = Pattern.compile(filter);
    this.includingFilters.add(pattern);
    return this;
  }
  
  /**
   * @see #addIncludingFilter(String)
   * @param filters
   *          regular expressions that will be matched against strings
   * @return <code>this</code> for method chaining
   * @throws PatternSyntaxException
   *           if any of the patterns is malformed
   */
  public StringFilter addIncludingFilters(final String[] filters) throws PatternSyntaxException {
    for (int i = 0; i < filters.length; i++) {
      addIncludingFilter(filters[i]);
    }
    return this;
  }
  
  /**
   * Add an excluding filter regex. If any excluding filters are present, only those strings will be accepted that are
   * <em>not</em> matched by any excluding filters.
   * 
   * @param filter
   *          regular expression that will be matched against test case names
   * @return <code>this</code> for method chaining
   * @throws PatternSyntaxException
   *           if the pattern is malformed
   */
  public StringFilter addExcludingFilter(final String filter) throws PatternSyntaxException {
    final Pattern pattern = Pattern.compile(filter);
    this.excludingFilters.add(pattern);
    return this;
  }
  
  /**
   * @see #addExcludingFilter(String)
   * @param filters
   *          regular expressions that will be matched against test case names
   * @return <code>this</code> for method chaining
   * @throws PatternSyntaxException
   *           if any of the patterns is malformed
   */
  public StringFilter addExcludingFilters(final String[] filters) throws PatternSyntaxException {
    for (int i = 0; i < filters.length; i++) {
      addExcludingFilter(filters[i]);
    }
    return this;
  }
  
  /**
   * Clear all filters.
   * 
   * @return <code>this</code>, for method chaining
   */
  public StringFilter clear() {
    this.excludingFilters.clear();
    this.includingFilters.clear();
    return this;
  }
  
  /**
   * Check if the given string is accepted by the filters represented by this StringFilter.
   * 
   * @param stringToTest
   *          The string to test
   * @return <code>true</code> if accepted; <code>false</code> otherwise.
   */
  public boolean accepts(final String stringToTest) {
    if (this.includingFilters.isEmpty()) {
      return !matches(stringToTest, this.excludingFilters);
    } else {
      return matches(stringToTest, this.includingFilters) && !matches(stringToTest, this.excludingFilters);
    }
  }
  
  private boolean matches(final String stringToTest, final Set filters) {
    final Iterator iterator = filters.iterator();
    while (iterator.hasNext()) {
      final Pattern pattern = (Pattern) iterator.next();
      if (pattern.matcher(stringToTest).matches()) {
        return true;
      }
    }
    return false;
  }
}
