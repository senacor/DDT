package com.senacor.ddt.tutorial;

import com.senacor.ddt.objectmatrix.excel.ExcelObjectMatrixFactory;
import com.senacor.ddt.test.DataDrivenTestCase;
import com.senacor.ddt.test.TestCaseData;
import com.senacor.ddt.test.junit.JUnitTestSuiteBuilder;
import junit.framework.Test;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Date;

/**
 * a fully featured DDT test which executes a list of tranfers between two account
 * and tests for the correct balance at certain points of time. All test data objects
 * are created by DDT instead of using constructors here.
 */
public class BalanceTest extends TestCase implements DataDrivenTestCase {
  TestCaseData testCaseData;

  /**
   * execute all transfers before a certain date,
   * then check for expected balances.
   * Loops over all expected balances.
   */
  public void testBalances() throws Exception {
    AmountAtDate[] myBalances =
        (AmountAtDate[]) testCaseData.createAndFillBean("myBalances");
    AmountAtDate[] transfers =
        (AmountAtDate[]) testCaseData.createAndFillBean("transfers");

    for (int i = 0; i < myBalances.length; i++) {
      Account myAccount = (Account) testCaseData.createAndFillBean("myAccount");
      Account momsAccount = (Account) testCaseData.createAndFillBean("momsAccount");
      AmountAtDate myBalance = myBalances[i];

      executeAllBefore(transfers, momsAccount, myAccount, myBalance.getDate());
      assertEquals("balance of my account wrong on " + myBalance.getDate(),
          myBalance.getAmount(), myAccount.getBalance());
    }
    assertTrue(myBalances.length > 0);
  }

  private void executeAllBefore(AmountAtDate[] transfers,
                                Account source, Account target,
                                Date beforeDate) {
    Arrays.sort(transfers); // by date

    for (int i = 0; i < transfers.length; i++) {
      AmountAtDate transfer = transfers[i];
      if (transfer.getDate().before(beforeDate)) {
        if (transfer.getAmount().signum() >= 0) {
          target.deposit(source.withdraw(transfer.getAmount()));
        } else {
          source.deposit(target.withdraw(transfer.getAmount().negate()));
        }
      } else {
        break;
      }
    }
  }

  public static Test suite() {
    ExcelObjectMatrixFactory objectMatrixFactory = new ExcelObjectMatrixFactory(
        "tutorial2.xls",
        new String[]{"balanceTests"}
    );

    JUnitTestSuiteBuilder jUnitTestSuiteBuilder =
        new JUnitTestSuiteBuilder(objectMatrixFactory, BalanceTest.class);
    return jUnitTestSuiteBuilder.buildSuite();
  }

  public String getName() {
    return testCaseData.getTestCaseName() + "--" + super.getName();
  }

  public TestCaseData getTestCaseData() {
    return testCaseData;
  }

  public void setTestCaseData(TestCaseData testCaseData) {
    this.testCaseData = testCaseData;
  }
}
