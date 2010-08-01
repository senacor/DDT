package com.senacor.ddt.tutorial;

import junit.framework.TestCase;
import junit.framework.Test;
import com.senacor.ddt.test.DataDrivenTestCase;
import com.senacor.ddt.test.TestCaseData;
import com.senacor.ddt.test.junit.JUnitTestSuiteBuilder;
import com.senacor.ddt.objectmatrix.excel.ExcelObjectMatrixFactory;

import java.math.BigDecimal;

/**
 * The simplest DDT-test we could come up with..
 */
public class AccountTest extends TestCase
    implements DataDrivenTestCase {
  TestCaseData testCaseData;

  public void testTransfer() {
    BigDecimal momsAccountBalance =
        testCaseData.getBigDecimal("momsAccountBalance");
    Boolean momsAccountHasCreditLine =
        testCaseData.getBoolean("momsAccountHasCreditline");
    BigDecimal myAccountBalance =
        testCaseData.getBigDecimal("myAccountBalance");
    BigDecimal transferAmount =
        testCaseData.getBigDecimal("transfer");

    Account myAccount = new Account();
    myAccount.setBalance(myAccountBalance);

    Account momsAccount = new Account();
    momsAccount.setBalance(momsAccountBalance);
    momsAccount.setCredit(momsAccountHasCreditLine.booleanValue());

    myAccount.deposit(momsAccount.withdraw(transferAmount));

    BigDecimal momsNewBalance =
        testCaseData.getBigDecimal("momsNewBalance");
    BigDecimal myNewBalance =
        testCaseData.getBigDecimal("myNewBalance");

    assertEquals(momsNewBalance, momsAccount.getBalance());
    assertEquals(myNewBalance, myAccount.getBalance());
    assertEquals(myAccountBalance.add(momsAccountBalance),
        myNewBalance.add(momsNewBalance));
  }

  public static Test suite() {
    ExcelObjectMatrixFactory objectMatrixFactory =
        new ExcelObjectMatrixFactory(
            "tutorial1.xls",
            new String[]{"TransferMoneyTests"});

    JUnitTestSuiteBuilder jUnitTestSuiteBuilder =
        new JUnitTestSuiteBuilder(objectMatrixFactory, AccountTest.class);
    return jUnitTestSuiteBuilder.buildSuite();
  }


  public void setTestCaseData(TestCaseData tcd) {
    this.testCaseData = tcd;
  }

  public TestCaseData getTestCaseData() {
    return testCaseData;
  }
}
