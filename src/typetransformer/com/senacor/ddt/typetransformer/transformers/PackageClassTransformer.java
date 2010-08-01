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
package com.senacor.ddt.typetransformer.transformers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.senacor.ddt.typetransformer.SpecificTransformer;
import com.senacor.ddt.typetransformer.Transformer;
import com.senacor.ddt.util.ParamChecker;
import com.senacor.ddt.util.StateChecker;

/**
 * Class transformer that takes class names and tries to instantiate the corresponding classes by prefixing them with
 * given package names. Packages are tried in reverse order, i.e. the package added last is tried first.
 * <p>
 * Example: Given the String "Date", this transformer would return <code>java.util.Date.class</code>. If you have a
 * class <code>com.foo.Bar</code> and add "com.foo" to the package list, you would get <code>com.foo.Bar</code> for the
 * String "Bar".
 * 
 * @author Carl-Eric Menzel
 */
public class PackageClassTransformer extends ClassTransformer {
  /**
   * Default {@link #freeze() frozen} instance included in every {@link Transformer} that contains the packages
   * java.lang, java.util and java.math.
   */
  public static final SpecificTransformer DEFAULT_JAVA_PACKAGES_INSTANCE =
      new PackageClassTransformer().addPackage("java.lang").addPackage("java.util").addPackage("java.math").freeze();
  
  private final List packages = new LinkedList();
  
  private boolean frozen;
  
  /**
   * Default constructor.
   */
  public PackageClassTransformer() {
    // nothing to do
  }
  
  /**
   * Freeze this instance. A frozen instance does {@link #addPackage(String) not accept} any new packages.
   * 
   * @return <code>this</code>
   */
  public PackageClassTransformer freeze() {
    this.frozen = true;
    return this;
  }
  
  protected boolean canTransform(final Class sourceType, final Class targetType) {
    return String.class.equals(sourceType) && Class.class.equals(targetType) && !this.packages.isEmpty();
  }
  
  protected Object doTransform(final Object object, final Class targetType) {
    final String className = (String) object;
    for (final Iterator iterator = this.packages.iterator(); iterator.hasNext();) {
      final String packageName = (String) iterator.next();
      final Object attempt = super.doTransform(packageName + className, Class.class);
      if (attempt == TRY_NEXT) {
        continue;
      } else {
        return attempt;
      }
    }
    
    // give up:
    return TRY_NEXT;
  }
  
  /**
   * Add a package prefix. Throws an IllegalStateInformation if this instance was previously {@link #freeze() frozen}.
   * 
   * @param packageName
   *          Prefix. Not blank.
   * @return <code>this</code>, for method chaining.
   */
  public PackageClassTransformer addPackage(final String packageName) {
    StateChecker.require("Packages must not be added to an already frozen transformer!", !this.frozen);
    ParamChecker.notBlank("packageName", packageName);
    if (packageName.endsWith(".")) {
      this.packages.add(0, packageName); // insert at first slot so custom packages are tried first
    } else {
      // add dot now so we don't have to do it all over again next time
      addPackage(packageName + ".");
    }
    return this;
  }
}
