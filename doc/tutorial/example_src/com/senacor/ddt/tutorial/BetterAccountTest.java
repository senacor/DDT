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
 * ... but this time we are reading objects instead of single values
 */
public class BetterAccountTest extends TestCase implements DataDrivenTestCase {
  TestCaseData testCaseData;

  public void testTransfer() {
    Account momsAccount = new Account();
    testCaseData.fillBean(momsAccount, "momsAccount");

    Account myAccount = new Account();
    testCaseData.fillBean(myAccount, "myAccount");

    BigDecimal transferAmount = testCaseData.getBigDecimal("transfer");

    myAccount.deposit(momsAccount.withdraw(transferAmount));

    BigDecimal momsNewBalance = testCaseData.getBigDecimal("momsNewBalance");
    BigDecimal myNewBalance = testCaseData.getBigDecimal("myNewBalance");

    assertEquals(momsNewBalance, momsAccount.getBalance());
    assertEquals(myNewBalance, myAccount.getBalance());
    assertEquals(myAccount.getBalance().add(momsAccount.getBalance()),
        myNewBalance.add(momsNewBalance));
  }

  public static Test suite() {
    ExcelObjectMatrixFactory objectMatrixFactory = new ExcelObjectMatrixFactory(
        "tutorial2.xls",
        new String[]{"balanceTests"}
    );

    JUnitTestSuiteBuilder jUnitTestSuiteBuilder =
        new JUnitTestSuiteBuilder(objectMatrixFactory, BetterAccountTest.class);
    return jUnitTestSuiteBuilder.buildSuite();
  }


  public void setTestCaseData(TestCaseData tcd) {
    this.testCaseData = tcd;
  }

  public TestCaseData getTestCaseData() {
    return testCaseData;
  }
}
