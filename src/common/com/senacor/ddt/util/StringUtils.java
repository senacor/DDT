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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Small helper class for dealing with strings.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class StringUtils {
  /**
   * Filters the given list of strings, taking all elements that start with the given prefix, contain the given infix
   * and end with the given suffix and returning them as a new list. The given list will not be modified. All three
   * search arguments are optional.
   * 
   * @param prefix
   *          Prefix to look for. If null, this method will not filter by prefixes.
   * @param infix
   *          Infix to look for. If null, this method will not filter by infixes.
   * @param suffix
   *          Suffix to look for. If null, this method will not filter by suffixes.
   * @param list
   *          List that is to be filtered. If empty or null, an empty list will be returned.
   * @return filtered List<String>
   */
  public static List filterStringList(final String prefix, final String infix, final String suffix, final List list) {
    // straightforward implementation with simple string matching. nothing fancy.
    if ((list == null) || list.isEmpty()) {
      return new ArrayList();
    }
    final List result = new ArrayList(list);
    final ListIterator iter = result.listIterator();
    
    while (iter.hasNext()) {
      final String rowName = (String) iter.next();
      
      if (prefix != null) {
        if (!rowName.startsWith(prefix)) {
          iter.remove();
          
          continue;
        }
      }
      
      if (infix != null) {
        if (!(rowName.indexOf(infix) > -1)) {
          iter.remove();
          
          continue;
        }
      }
      
      if (suffix != null) {
        if (!rowName.endsWith(suffix)) {
          iter.remove();
          
          continue;
        }
      }
    }
    return result;
  }
  
  public static boolean isNullOrBlank(final String string) {
    return (string == null) || string.trim().isEmpty();
  }
}
