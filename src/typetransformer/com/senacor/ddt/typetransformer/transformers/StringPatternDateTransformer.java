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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.senacor.ddt.typetransformer.SpecificTransformer;
import com.senacor.ddt.util.ParamChecker;

public class StringPatternDateTransformer extends AbstractTwoWayDateTransformer {
  private final String pattern;
  
  public static final String DATEPATTERN_ISO_8601_FULL_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS";
  
  public static final StringPatternDateTransformer DATETRANSFORMER_ISO_8601_FULL_UTC =
      new StringPatternDateTransformer(DATEPATTERN_ISO_8601_FULL_UTC);
  
  public static final String DATEPATTERN_ISO_8601_DATETIME_UTC = "yyyy-MM-dd'T'HH:mm";
  
  public static final StringPatternDateTransformer DATETRANSFORMER_ISO_8601_DATETIME_UTC =
      new StringPatternDateTransformer(DATEPATTERN_ISO_8601_DATETIME_UTC);
  
  public static final String DATEPATTERN_ISO_8601_DATE_ONLY = "yyyy-MM-dd";
  
  public static final StringPatternDateTransformer DATETRANSFORMER_ISO_8601_DATE_ONLY =
      new StringPatternDateTransformer(DATEPATTERN_ISO_8601_DATE_ONLY);
  
  public static final SpecificTransformer DATETRANSFORMER_YEAR_ONLY = new StringPatternDateTransformer("yyyy") {
    protected Object convertFromString(final Class targetType, final String string) {
      // guard against dumb strings like "0.4" that SimpleDateFormat would treat as "year 0"
      if (string.length() == 4) {
        try {
          Integer.parseInt(string);
          return super.convertFromString(targetType, string);
        } catch (final NumberFormatException e) {
          ; // eat it and let the exception below fly
        }
      }
      return TRY_NEXT;
    }
  };
  
  public StringPatternDateTransformer(final String pattern) {
    this.pattern = pattern;
    ParamChecker.notNull("pattern", pattern);
  }
  
  protected Object convertToString(final Date time) {
    return createFormat().format(time);
  }
  
  protected Object convertFromString(final Class targetType, final String string) {
    try {
      final Date date = createFormat().parse(string);
      if (targetType.equals(java.sql.Date.class)) {
        return new java.sql.Date(date.getTime());
      } else if (targetType.equals(Calendar.class)) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
      } else if (targetType.equals(GregorianCalendar.class)) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;
      } else if (targetType.equals(java.util.Date.class)) {
        return date;
      } else {
        // this must be a custom Calendar or Date class: we can't handle this.
        return null;
      }
    } catch (final ParseException e) {
      // we can't parse this, so we return null. doTransform will then call the next transformer.
      return null;
    }
  }
  
  private DateFormat createFormat() {
    return new SimpleDateFormat(this.pattern);
  }
}
