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

package com.senacor.ddt.test.util;

import java.net.URL;

import com.senacor.ddt.util.ParamChecker;

/**
 * Helper for getting URLs in the package of a given class.
 * 
 * @author Carl-Eric Menzel
 * @version $Id$
 */
public class UrlUtil {
  /**
   * Get the URL pointing to the given file in the package of the given class.
   * 
   * @param clazz
   *          The class whose package should be the path.
   * @param fileName
   *          The name of a file in that package.
   * @return A URL pointing to the file.
   */
  public static URL getUrlInPackage(final Class clazz, final String fileName) {
    ParamChecker.notNull("clazz", clazz);
    ParamChecker.notNull("fileName", fileName);
    
    String res = "/" + clazz.getName().replace('.', '/');
    res = res.substring(0, res.lastIndexOf('/'));
    res = res + "/" + fileName;
    
    return getResource(clazz, res);
  }
  
  private static URL getResource(final Class clazz, final String res) {
    final URL url = clazz.getResource(res);
    if (url == null) {
      throw new RuntimeException("Resource '" + res + "' not found!");
    } else {
      return url;
    }
  }
  
  /**
   * Get the URL pointing to a file named <i>classname</i>.<i>suffix</i> in the same package as the class. Example:
   * <code>getClassnameBasedUrlInPackage(com.foo.Bar.class, ".dat")</code> will return a URL pointing to
   * <code><i>classpath</i>/com/foo/Bar.dat</code>.
   * 
   * @param clazz
   *          The class to use as path and filename template
   * @param suffix
   *          The suffix to append to the classname
   * @return The URL
   */
  public static URL getClassnameBasedUrlInPackage(final Class clazz, final String suffix) {
    ParamChecker.notNull("clazz", clazz);
    ParamChecker.notNull("suffix", suffix);
    
    final String res = "/" + clazz.getName().replace('.', '/') + suffix;
    
    return getResource(clazz, res);
  }
}
