package com.senacor.ddt.tutorial;

import java.util.Date;
import java.math.BigDecimal;

/**
 * Container object for a timed money transfer.
 */
public class AmountAtDate implements Comparable {

  /**
   * date of tranfer
   */
  private Date date;

  /**
   * amount of transfer
   */
  private BigDecimal amount;

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String toString() {
    return "transfer $" + amount + " on " + date;
  }

  public int compareTo(Object o) {
    return date.compareTo(((AmountAtDate) o).date);
  }
}
