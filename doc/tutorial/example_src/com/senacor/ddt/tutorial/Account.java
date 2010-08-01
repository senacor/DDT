package com.senacor.ddt.tutorial;

import java.math.BigDecimal;

/**
 * A beginner's bank account class. It doesn't do much.
 * There is a tiny amount of business logic within the
 * withdraw-method we consider worth testing.
 */
public class Account {
  private BigDecimal balance = new BigDecimal("0");

  /**
   * does this account have an unlimited credit line?
   */
  private boolean isCredit;

  /**
   * wihtdraw some money.
   * Only if this account has a credit line,
   * the withdrawn amount may be greater than the balance.
   *
   * @param amount amount to be withdrawn (if possible)
   * @return the actually withdrawn amount
   */
  public BigDecimal withdraw(BigDecimal amount) {
    BigDecimal actualAmount = amount;
    if (!isCredit && balance.compareTo(amount) == -1) {
      actualAmount = balance;
    }
    balance = balance.subtract(actualAmount);
    return actualAmount;
  }

  /**
   * guess what
   */
  public void deposit(BigDecimal amount) {
    balance = balance.add(amount);
  }

  // Getter and setter methods
  // -- what would life be without them?

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public boolean isCredit() {
    return isCredit;
  }

  public void setCredit(boolean credit) {
    isCredit = credit;
  }
}
