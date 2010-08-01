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
 * ... in a cleaned up version: using setUp and naming test cases
 */
public class CleanAccountTest extends TestCase implements DataDrivenTestCase {
  TestCaseData testCaseData;
  Account momsAccount;
  Account myAccount;
  BigDecimal momsNewBalance;
  BigDecimal myNewBalance;

  protected void setUp() throws Exception {
    momsAccount = new Account();
    testCaseData.fillBean(momsAccount, "momsAccount");

    myAccount = new Account();
    testCaseData.fillBean(myAccount, "myAccount");

    momsNewBalance = testCaseData.getBigDecimal("momsNewBalance");
    myNewBalance = testCaseData.getBigDecimal("myNewBalance");
  }

  public String getName() {
    return testCaseData.getTestCaseName() + "--" + super.getName();
  }

  public void testTransfer() {
    BigDecimal transferAmount = testCaseData.getBigDecimal("transfer");

    myAccount.deposit(momsAccount.withdraw(transferAmount));

    assertEquals(momsNewBalance, momsAccount.getBalance());
    assertEquals(myNewBalance, myAccount.getBalance());
    assertEquals(myAccount.getBalance().add(momsAccount.getBalance()),
        myNewBalance.add(momsNewBalance));
  }

  public static Test suite() {
    ExcelObjectMatrixFactory objectMatrixFactory = new ExcelObjectMatrixFactory(
        "tutorial1.xls",
        new String[]{"TransferMoneyTests2"}
    );

    JUnitTestSuiteBuilder jUnitTestSuiteBuilder =
        new JUnitTestSuiteBuilder(objectMatrixFactory, CleanAccountTest.class);
    return jUnitTestSuiteBuilder.buildSuite();
  }


  public void setTestCaseData(TestCaseData tcd) {
    this.testCaseData = tcd;
  }

  public TestCaseData getTestCaseData() {
    return testCaseData;
  }
}
