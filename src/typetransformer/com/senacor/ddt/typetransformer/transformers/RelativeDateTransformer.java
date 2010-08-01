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

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.senacor.ddt.typetransformer.AbstractGuardedTransformer;

/**
 * Converter, der aus einem String ein Date erzeugen kann. Der String enthält entweder einen absoluten Wert, z.B.
 * "1.2.2004", oder einen relativen Wert folgender Form:<br>
 * <var>( now | (adder*) | (adder* setter adder*) ) </var>,<br>
 * wobei <br>
 * <var>now := 'NOW' </var><br>
 * <var>adder := ('+'|'-') val field (('+'|'-')? val field)* </var><br>
 * <var>val := ([0-9])+ </var><br>
 * <var>field := 'Second'|'Seconds'|'Minute'|'Minutes'|'Hour'|'Hours'|
 * 'Day'|'Days'|'Week'|'Weeks'|'Month'|'Months'|'Year'|'Years' </var><br>
 * <var>setter:= ('FIRST'|'LAST') ('DAY_OF_WEEK'|'DAY_OF_MONTH'|'DAY_OF_QUARTER'|'DAY_OF_YEAR') </var><br>
 * <br>
 * Im Klartext: Der relative Wert ist entweder <code>'Now'<code> oder besteht aus maximal einem SETTER und beliebig vielen ADDERn.<br>
 * Beispiele fuer ADDER: "+3 Days", "-1 Year 2 Months", "- 4 Months + 1 Day", ... <br>
 * Beispiele fuer SETTER: "First Day_of_Week", "Last Day_of_Month", "First Day_of_Quarter", "Last Day_of_Year", ... <br>
 * Außerdem gilt:
 * <ul>
 * <li>Leerzeichen bei '+' und '-' sind nicht nötig.</li>
 * <li>Groß- oder Kleinschreibung ist egal.</li>
 * <li><code>FIRST DAY_OF_WEEK</code> = letzter Montag.</li> <li><code>LAST DAY_OF_WEEK</code> = nächster Sonntag.</li> </ul>
 * 
 * @author Martin Trapp
 * @author Carl-Eric Menzel
 */
public final class RelativeDateTransformer extends AbstractGuardedTransformer {
  private static final String NOW = "NOW";
  
  private static final String FIRST = "FIRST";
  
  private static final String LAST = "LAST";
  
  private static final String DAY_OF_WEEK = "DAY_OF_WEEK";
  
  private static final String DAY_OF_MONTH = "DAY_OF_MONTH";
  
  private static final String DAY_OF_QUARTER = "DAY_OF_QUARTER";
  
  private static final String DAY_OF_YEAR = "DAY_OF_YEAR";
  
  private static final String SECOND = "SECOND";
  
  private static final String SECONDS = "SECONDS";
  
  private static final String MINUTE = "MINUTE";
  
  private static final String MINUTES = "MINUTES";
  
  private static final String HOUR = "HOUR";
  
  private static final String HOURS = "HOURS";
  
  private static final String DAY = "DAY";
  
  private static final String DAYS = "DAYS";
  
  private static final String WEEK = "WEEK";
  
  private static final String WEEKS = "WEEKS";
  
  private static final String MONTH = "MONTH";
  
  private static final String MONTHS = "MONTHS";
  
  private static final String YEAR = "YEAR";
  
  private static final String YEARS = "YEARS";
  
  public static final RelativeDateTransformer INSTANCE = new RelativeDateTransformer();
  
  private final Log log = LogFactory.getLog(getClass());
  
  /**
   * Convert-Methode
   */
  protected Object doTransform(final Object object, final Class targetType) {
    final String str = ((String) object).trim();
    // Testen, ob der String einen relativen Wert enthält:
    if (str.equalsIgnoreCase(NOW) || str.startsWith("+") || str.startsWith("-") || str.toUpperCase().startsWith(FIRST)
        || str.toUpperCase().startsWith(LAST)) {
      if (this.log.isDebugEnabled()) {
        this.log.debug("Converting " + str + " to Date.");
      }
      
      Calendar cal;
      try {
        cal = computeDate(str);
      } catch (final ParseException e) {
        this.log.debug("parsing error", e);
        return TRY_NEXT;
      }
      if (this.log.isDebugEnabled()) {
        this.log.debug("Converted  " + str + " to " + cal);
      }
      
      if (java.util.Date.class.equals(targetType)) {
        return new java.util.Date(cal.getTimeInMillis());
      } else if (java.sql.Date.class.equals(targetType)) {
        return new java.sql.Date(cal.getTimeInMillis());
      } else if (Calendar.class.equals(targetType)) {
        final Calendar result = Calendar.getInstance();
        result.setTimeInMillis(cal.getTimeInMillis());
        return result;
      } else if (GregorianCalendar.class.equals(targetType)) {
        return cal;
      } else {
        throw new AssertionError("unexpected targetType: " + targetType);
      }
    } else {
      return TRY_NEXT;
    }
  }
  
  /**
   * Parst einen String der Form ( now | (adder*) | (adder* setter adder*) ) , wobei now ::= 'NOW' adder ::= (('+'|'-')
   * val field (('+'|'-')? val field)*) val ::= ([0-9])+ field ::=
   * ('Second'|'Seconds'|'Minute'|'Minutes'|'Hour'|'Hours'| 'Day'|'Days'|'Week'|'Weeks'|'Month'|'Months'|'Year'|'Years')
   * setter::= (('FIRST'|'LAST') ('DAY_OF_WEEK'|'DAY_OF_MONTH'|'DAY_OF_QUARTER'|'DAY_OF_YEAR')) Hinweise: Leerzeichen
   * bei '+' und '-' sind nicht nötig. Groß- oder Kleinschreibung ist egal. FIRST DAY_OF_WEEK = letzter Montag. LAST
   * DAY_OF_WEEK = nächster Sonntag.
   */
  private GregorianCalendar computeDate(final String string) throws ParseException {
    // Calender mit aktuellem Datum erzeugen:
    final GregorianCalendar gc = new GregorianCalendar();
    
    // NOW => aktuelles Datum:
    if (string.equalsIgnoreCase(NOW)) {
      return gc;
    } else {
      
      // StringTokenizer erzeugen mit den Delimitern ' ', '+' und '-', die auch als Token betrachtet
      // werden:
      final StringTokenizer tokenizer = new StringTokenizer(string, " +-", true);
      
      // Es darf nur ein setter vorkommen:
      boolean setterParsed = false;
      
      // Booleans fuer adder:
      boolean add = false;
      boolean subtract = false;
      boolean defaultIsSet = false;
      boolean defaultValue = false;
      
      // Durchgang durch die Token:
      while (tokenizer.hasMoreTokens()) {
        try {
          final String token = getNextToken(tokenizer);
          
          // SETTER:
          if (token.equalsIgnoreCase(FIRST) || token.equalsIgnoreCase(LAST)) {
            if (!setterParsed) {
              evaluateSetter(gc, token, tokenizer, string);
              setterParsed = true;
              defaultIsSet = false;
            } else {
              throw new ParseException("Fehlerhafte Eingabe: '" + string
                  + "': 'FIRST' und 'LAST' duerfen nur insgesamt einmal auftreten.", getPosition(string, token,
                  tokenizer));
            }
          }
          // ADDER:
          else {
            // fuehrendes '+' bzw. '-' auslesen, falls vorhanden:
            add = token.equalsIgnoreCase("+");
            subtract = token.equalsIgnoreCase("-");
            
            if (add || subtract) {
              // Nächstes nichtleeres Token auslesen:
              final String numberString = getNextToken(tokenizer);
              
              // Berechnen:
              evaluateAdder(gc, add, numberString, tokenizer, string);
              
              // Defaultwert setzen:
              defaultValue = add;
              defaultIsSet = true;
            }
            // Kein +/- angegeben => Default verwenden, falls vorhanden:
            else if (defaultIsSet) {
              evaluateAdder(gc, defaultValue, token, tokenizer, string);
            } else {
              throw new ParseException("Erwartet: '+', '-', '" + FIRST + "' oder '" + LAST + "'. " + "Erhalten: '"
                  + token + "'.", getPosition(string, token, tokenizer));
            }
          }
        } catch (final NoSuchElementException nsee) { // Fehler bei tokenizer.nextToken()
          // aufgetreten
          throw new ParseException("Eingabe unvollständig: " + string, string.length() - 1);
        }
      }
      
      return gc;
    }
  }
  
  /**
   * Setzt den Calendar <code>gc</code> auf einen bestimmten Zeitpunkt, abhaengig von <code>firstLast</code> und dem
   * nächsten nicht-leeren Token in <code>remainingToken</code>.
   */
  private void evaluateSetter(final GregorianCalendar gc, final String firstLast, final StringTokenizer remainingToken,
      final String completeString) throws ParseException {
    final String dayOf_String = getNextToken(remainingToken);
    
    // Spezialfälle zuerst:
    if (dayOf_String.equalsIgnoreCase(DAY_OF_WEEK)) {
      evaluateDayOfWeekSetter(gc, firstLast);
    } else if (dayOf_String.equalsIgnoreCase(DAY_OF_QUARTER)) {
      evaluateDayOfQuarterSetter(gc, firstLast);
    } else {
      // Restliche Fälle sind gleichzeitig behandelbar:
      int dayOf;
      
      if (dayOf_String.equalsIgnoreCase(DAY_OF_MONTH)) {
        dayOf = Calendar.DAY_OF_MONTH;
      } else if (dayOf_String.equalsIgnoreCase(DAY_OF_YEAR)) {
        dayOf = Calendar.DAY_OF_YEAR;
      } else {
        throw new ParseException("Erwartet: '" + DAY_OF_WEEK + "', '" + DAY_OF_MONTH + "', '" + DAY_OF_QUARTER + "', '"
            + DAY_OF_YEAR + "'. " + "Erhalten: " + dayOf_String + ".", getPosition(completeString, dayOf_String,
            remainingToken));
      }
      
      // Berechung des ersten bzw. letzten Tages:
      if (firstLast.equalsIgnoreCase(FIRST)) {
        gc.set(dayOf, 1);
        gc.get(Calendar.DATE); // um neue Berechung aller Felder zu erzwingen.
      } else { // firstLast.equalsIgnoreCase(LAST)
        gc.set(dayOf, gc.getActualMaximum(dayOf));
        gc.get(Calendar.DATE); // um neue Berechung aller Felder zu erzwingen.
      }
    }
  }
  
  /**
   * @param gc
   *          Calendar, in dem ein Zeitpunkt gesetzt werden soll.
   * @param firstLast
   *          "FIRST" oder "LAST"
   */
  private void evaluateDayOfWeekSetter(final GregorianCalendar gc, final String firstLast) {
    if (firstLast.equalsIgnoreCase(FIRST)) {
      // Datum des letzten Montags berechnen:
      while (gc.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        gc.add(Calendar.DATE, -1);
      }
    } else { // firstLast.equalsIgnoreCase(LAST)
    
      // Datum des nächsten Sonntags berechnen:
      while (gc.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
        gc.add(Calendar.DATE, 1);
      }
    }
    
    return;
  }
  
  /**
   * @param gc
   *          Calendar, in dem ein Zeitpunkt gesetzt werden soll.
   * @param firstLast
   *          "FIRST" oder "LAST"
   */
  private void evaluateDayOfQuarterSetter(final GregorianCalendar gc, final String firstLast) {
    if (firstLast.equalsIgnoreCase(FIRST)) {
      // Tag auf 1 und Monat auf JANUARY, APRIL, JULY oder OCTOBER setzen:
      gc.set(Calendar.DAY_OF_MONTH, 1);
      
      int month = gc.get(Calendar.MONTH);
      while ((month != Calendar.JANUARY) && (month != Calendar.APRIL) && (month != Calendar.JULY)
          && (month != Calendar.OCTOBER)) {
        gc.add(Calendar.MONTH, -1);
        month = gc.get(Calendar.MONTH);
      }
      
      return;
    } else { // firstLast.equalsIgnoreCase(LAST)
      // Monat auf MARCH, JUNE, SEPTEMBER oder DECEMBER setzen:
      gc.set(Calendar.DAY_OF_MONTH, 1); // um overflow zu verhindern
      
      int month = gc.get(Calendar.MONTH);
      while ((month != Calendar.MARCH) && (month != Calendar.JUNE) && (month != Calendar.SEPTEMBER)
          && (month != Calendar.DECEMBER)) {
        gc.add(Calendar.MONTH, 1);
        month = gc.get(Calendar.MONTH);
      }
      
      // Tag auf letzten möglichen Tag des Monats setzen:
      gc.set(Calendar.DAY_OF_MONTH, gc.getActualMaximum(Calendar.DAY_OF_MONTH));
      gc.get(Calendar.DATE); // um neue Berechung aller Felder zu erzwingen.
      
      return;
    }
  }
  
  /**
   * Addiert einen bestimmten Zeitraum zum aktuellen Zeitpunkt im Calendar.
   */
  private void evaluateAdder(final GregorianCalendar gc, final boolean add, final String numberString,
      final StringTokenizer remainingToken, final String completeString) throws ParseException {
    // Zahl parsen:
    int number;
    try {
      number = Integer.parseInt(numberString);
    } catch (final NumberFormatException nfe) {
      throw new ParseException("Erwartet: Zahl. Erhalten: '" + numberString + "'.", getPosition(completeString,
          numberString, remainingToken));
    }
    
    // Nächstes nichtleeres Token auslesen:
    final String field = getNextToken(remainingToken);
    
    int fieldAsInt;
    
    if (field.equalsIgnoreCase(SECOND) || field.equalsIgnoreCase(SECONDS)) {
      fieldAsInt = Calendar.SECOND;
    } else if (field.equalsIgnoreCase(MINUTE) || field.equalsIgnoreCase(MINUTES)) {
      fieldAsInt = Calendar.MINUTE;
    } else if (field.equalsIgnoreCase(HOUR) || field.equalsIgnoreCase(HOURS)) {
      fieldAsInt = Calendar.HOUR;
    } else if (field.equalsIgnoreCase(DAY) || field.equalsIgnoreCase(DAYS)) {
      fieldAsInt = Calendar.DATE;
    } else if (field.equalsIgnoreCase(WEEK) || field.equalsIgnoreCase(WEEKS)) {
      fieldAsInt = Calendar.WEEK_OF_YEAR;
    } else if (field.equalsIgnoreCase(MONTH) || field.equalsIgnoreCase(MONTHS)) {
      fieldAsInt = Calendar.MONTH;
    } else if (field.equalsIgnoreCase(YEAR) || field.equalsIgnoreCase(YEARS)) {
      fieldAsInt = Calendar.YEAR;
    } else {
      throw new ParseException("Erwartet: '" + SECOND + "(S)', '" + MINUTE + "(S)', '" + HOUR + "(S)', '" + DAY
          + "(S)', '" + WEEK + "(S)', '" + MONTH + "(S)', '" + YEAR + "(S)'. " + "Erhalten: '" + field + "'.",
          getPosition(completeString, field, remainingToken));
    }
    
    // Berechnung:
    if (add) {
      gc.add(fieldAsInt, number);
    } else {
      gc.add(fieldAsInt, -number);
    }
  }
  
  /**
   * @param tokenizer
   * @return Naechstes Token, das kein whitespace ist.
   */
  private String getNextToken(final StringTokenizer tokenizer) {
    String s = tokenizer.nextToken();
    while (s.trim().equalsIgnoreCase("")) {
      s = tokenizer.nextToken();
    }
    
    return s;
  }
  
  /**
   * Zur Berechnung der Position des <code>token</code> im String <code>string</code>.
   */
  private int getPosition(final String completeString, final String currentToken, final StringTokenizer restTokens) {
    // Noch nicht geparsten Teil des string zusammenbauen:
    final StringBuffer restString = new StringBuffer(currentToken);
    while (restTokens.hasMoreTokens()) {
      restString.append(restTokens.nextToken()); // hier wird natürlich NICHT getNextToken
      // verwendet!
    }
    
    // Position auslesen:
    return completeString.lastIndexOf(restString.toString());
  }
  
  protected boolean canTransform(final Class sourceType, final Class targetType) {
    if (String.class.equals(sourceType)) {
      if (java.util.Date.class.equals(targetType) || java.sql.Date.class.equals(targetType)
          || java.util.Calendar.class.equals(targetType) || java.util.GregorianCalendar.class.equals(targetType)) {
        return true;
      }
    }
    return false;
  }
}
