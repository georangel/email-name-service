package com.example.nameemailservice;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Set;

public class NameEmailServiceTest extends AbstractTestNGSpringContextTests {

  private NameEmailService nameEmailService;

  @BeforeClass
  public void setUp() {
    nameEmailService = new NameEmailService();
  }

  @DataProvider(name = "processHandleData")
  public Object[][] processHandleData() {
    return new Object[][]{
      {"johnsmith@email.com", "JOHNSMITH"},
      {"1234johns345mith@email.com", "JOHNSMITH"},
      {"mar1aka!!a$@email.com", "MARAKAA"},
      {" maria_kallas @email.com", "MARIAKALLAS"},
      {"invalidemail", "UNKNOWN"},
      {null, "UNKNOWN"}
    };
  }

  @Test(dataProvider = "processHandleData")
  public void testProcessHandle(String email, String expected) {
    String result = nameEmailService.processHandle(email);
    Assert.assertEquals(result, expected);
  }

  @DataProvider(name = "removeFrequentLettersData")
  public Object[][] removeFrequentLettersData() {
    return new Object[][]{
      {"johnsmith", "johnsmith"},
      {"nnnnnjohn", "john"},
      {"nnjohn", "nnjohn"},
      {null, "UNKNOWN"},
      {"", "UNKNOWN"}
    };
  }

  @Test(dataProvider = "removeFrequentLettersData")
  public void testRemoveFrequentLetters(String handle, String expected) {
    String result = nameEmailService.removeFrequentLetters(handle);
    Assert.assertEquals(result, expected);
  }

  @DataProvider(name = "processNameData")
  public Object[][] processNameData() {
    return new Object[][]{
      {"johnsmith", new String[]{"JOHNSMITH"}},
      {"mariakallas", new String[]{"MARIAKALLAS"}},
      {"ιωαννης", new String[]{"ΙΩΑΝΝΗΣ"}},
      {"konstadinos ioannis", new String[]{"KONSTADINOS", "IOANNIS"}},
      {"john", new String[]{"JOHN"}},
      {"", new String[]{}},
      {null, new String[]{}}
    };
  }

  @Test(dataProvider = "processNameData")
  public void testProcessName(String name, String[] expected) {
    String[] result = nameEmailService.processName(name);
    Assert.assertEquals(result, expected);
  }

  @Test
  public void testGetConsecutiveSubstrings() {
    Set<String> result = nameEmailService.getConsecutiveSubstrings("john");
    Set<String> expected = Set.of("j", "jo", "joh", "john", "o", "oh", "ohn", "h", "hn", "n");
    Assert.assertEquals(result, expected);
  }

  @DataProvider(name = "checkIfCombinationInHandleData")
  public Object[][] checkIfCombinationInHandleData() {
    return new Object[][]{
      {"johnsmith", true},
      {"jo_sm", false},
      {"1232324johnsmith4545", true},
      {"2342johns_54mith43", true},
      {"jo_smdfgfd", false},
      {"js", true},
      {"jsa", true},
      {"jsaa", false},
      {"jocoke", false},
      {"29579759792", false},
      {"john", true}
    };
  }

  @Test(dataProvider = "checkIfCombinationInHandleData")
  public void testCheckIfCombinationInHandle(String handle, boolean expected) {
    boolean result = nameEmailService.checkIfCombinationInHandle("john", "smith", handle);
    Assert.assertEquals(result, expected);
  }

  @DataProvider(name = "compareEmailNameData")
  public Object[][] compareEmailNameData() {
    return new Object[][]{
      {"johnsmith@email.com", true},
      {"jo_sm@email.com", true},
      {"1232324johnsmith4545@email.com", true},
      {"2342johns_54mith43@email.com", true},
      {"jo_smdfgfd@email.com", true},
      {"js@email.com", true},
      {"jsa@email.com", true},
      {"jocoke@email.com", false},
      {"29579759792@email.com", false},
      {"johnlucas@email.com", true},
      {"lucassmith@email.com", true},
      {"12nhojlucas@email.com", true},
      {"smith@email.com", true},
      {null, false},
      {"invalidemail", false}
    };
  }

  @Test(dataProvider = "compareEmailNameData")
  public void testCompareEmailName(String email, boolean expected) {
    boolean result = nameEmailService.compareEmailName(email, "john lucas", "smith");
    Assert.assertEquals(result, expected);
  }
}