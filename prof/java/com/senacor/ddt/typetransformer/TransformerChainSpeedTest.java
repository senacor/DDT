/*
 * Copyright (c) 2009 Senacor Technologies AG.
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
package com.senacor.ddt.typetransformer;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TransformerChainSpeedTest extends TestCase {
  private static final Log log = LogFactory.getLog(TransformerChainSpeedTest.class);
  private static final int RUNS = 1000000;
  private long startTime;
  private long stopTime;
  
  public void testInteger() throws Exception {
    final Transformer t = Transformer.get();
    log.info("Starting " + RUNS + " successful Integer transformations");
    start();
    for (int i = 0; i < RUNS; i++) {
      t.transform("123", Integer.class);
    }
    stop();
  }
  
  public void testUnsuccessful() throws Exception {
    final Transformer t = Transformer.get();
    log.info("Starting " + RUNS + " unsuccessful transformations");
    start();
    for (int i = 0; i < RUNS; i++) {
      try {
        t.transform("qwe", TestCase.class);
        fail("should have thrown exception");
      } catch (final NoSuccessfulTransformerException e) {
        // expected
      }
    }
    stop();
  }
  
  private void stop() {
    this.stopTime = System.currentTimeMillis();
    log.info("Took " + (this.stopTime - this.startTime) + " ms");
  }
  
  private void start() {
    this.startTime = System.currentTimeMillis();
  }
}
