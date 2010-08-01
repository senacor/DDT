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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.senacor.ddt.typetransformer.TransformationFailedException;
import com.senacor.ddt.util.ParamChecker;

public class DateFormatTransformer extends AbstractTwoWayDateTransformer {
  private final boolean dateOnly;
  
  private final int dateStyle;
  
  private final Locale locale;
  
  private final int timeStyle;
  
  private boolean lenient;
  
  public DateFormatTransformer(final int dateStyle, final Locale locale) {
    ParamChecker.notNull("locale", locale);
    this.dateStyle = dateStyle;
    this.locale = locale;
    this.timeStyle = -1;
    this.dateOnly = true;
  }
  
  public DateFormatTransformer(final int dateStyle, final int timeStyle, final Locale locale) {
    ParamChecker.notNull("locale", locale);
    this.dateStyle = dateStyle;
    this.timeStyle = timeStyle;
    this.locale = locale;
    this.dateOnly = false;
  }
  
  private DateFormat createFormat() {
    final DateFormat format;
    if (this.dateOnly) {
      format = DateFormat.getDateInstance(this.dateStyle, this.locale);
    } else {
      format = DateFormat.getDateTimeInstance(this.dateStyle, this.timeStyle, this.locale);
    }
    format.setLenient(isLenient());
    return format;
  }
  
  protected Object convertFromString(final Class targetType, final String string) {
    try {
      final Date date = createFormat().parse(string);
      if (Date.class.equals(targetType)) {
        return date;
      } else if (java.sql.Date.class.equals(targetType)) {
        return new java.sql.Date(date.getTime());
      } else if (Calendar.class.equals(targetType) || GregorianCalendar.class.equals(targetType)) {
        final Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return cal;
      } else {
        // we can't convert this and it shouldn't even have come here in a guarded transformer
        throw new AssertionError("targetType should be Date, sql.Date or Calendar but is: " + targetType.getName());
      }
    } catch (final ParseException e) {
      if (isDebugMode()) {
        throw new TransformationFailedException(string, targetType, "debug mode: cannot convert", e);
      } else {
        // we can't parse this, so we return null. doTransform will then call the next transformer.
        return null;
      }
    }
  }
  
  protected Object convertToString(final Date time) {
    return createFormat().format(time);
  }
  
  public boolean isLenient() {
    return this.lenient;
  }
  
  public void setLenient(final boolean lenient) {
    this.lenient = lenient;
  }
  
}
