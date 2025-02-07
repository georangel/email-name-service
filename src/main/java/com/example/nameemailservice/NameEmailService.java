package com.example.nameemailservice;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class NameEmailService {

  private static final Pattern SPECIAL_CHARS_PATTERN = Pattern.compile("[^A-Za-z]");

  /**
   * Extracts and processes the handle (username) from an email address.
   * Removes numbers and special characters, converting the handle to uppercase.
   *
   * @param email The email address to process.
   *
   * @return The cleaned handle in uppercase, or "UNKNOWN" if empty or invalid.
   */
  public String processHandle(String email) {
    if (email == null || !email.contains("@")) {
      return "UNKNOWN";  // Avoids IndexOutOfBoundsException
    }
    String handle = email.split("@")[0];
    handle = SPECIAL_CHARS_PATTERN.matcher(handle).replaceAll("").toUpperCase();

    return handle.isEmpty() ? "UNKNOWN" : handle;
  }

  /**
   * Removes letters that appear three or more times consecutively in the given handle.
   *
   * @param handle The processed handle to clean.
   *
   * @return The cleaned handle, ensuring it is not empty.
   */
  public String removeFrequentLetters(String handle) {
    if (handle == null || handle.isEmpty()) {
      return "UNKNOWN";  // Prevents IndexOutOfBoundsException
    }

    StringBuilder cleanedHandle = new StringBuilder();
    int count = 1;
    char prevChar = handle.charAt(0);

    for (int i = 1; i < handle.length(); i++) {
      if (handle.charAt(i) == prevChar) {
        count++;
      } else {
        if (count < 3) cleanedHandle.append(String.valueOf(prevChar).repeat(count));
        count = 1;
      }
      prevChar = handle.charAt(i);
    }
    if (count < 3) cleanedHandle.append(String.valueOf(prevChar).repeat(count));

    return cleanedHandle.length() > 0 ? cleanedHandle.toString() : "UNKNOWN";
  }

  /**
   * Processes a full name by splitting it into individual components and converting them to uppercase.
   *
   * @param name The full name to process.
   *
   * @return An array of processed name parts.
   */
  public String[] processName(String name) {
    if (name == null || name.trim().isEmpty()) {
      return new String[0];
    }
    return name.toUpperCase().split("\\s+");
  }

  /**
   * Generates all consecutive substrings of a given name, starting from the first character.
   *
   * @param name The name to process.
   *
   * @return A set of consecutive substrings.
   */
  public Set<String> getConsecutiveSubstrings(String name) {
    Set<String> substrings = new HashSet<>();
    for (int i = 0; i < name.length(); i++) {
      for (int j = i + 1; j <= name.length(); j++) {
        substrings.add(name.substring(i, j));
      }
    }
    return substrings;
  }

  /**
   * Checks if a substring is valid based on its length and presence in the handle.
   *
   * @param substring The substring to check.
   * @param handle    The email handle.
   *
   * @return True if the substring is valid, otherwise false.
   */
  public boolean isValid(String substring, String handle) {
    return handle.contains(substring) && (substring.length() > 3 || substring.length() >= 0.6 * handle.length());
  }

  /**
   * Checks if any combination of two name substrings exists in the given handle.
   *
   * @param firstName  The first name.
   * @param secondName The second name.
   * @param handle     The processed email handle.
   *
   * @return True if a valid combination exists, otherwise false.
   */
  public boolean checkIfCombinationInHandle(String firstName, String secondName, String handle) {
    Set<String> firstNameSubstrings = getConsecutiveSubstrings(firstName);
    Set<String> secondNameSubstrings = getConsecutiveSubstrings(secondName);

    for (String sub2 : secondNameSubstrings) {
      if (isValid(sub2, handle)) {
        return true;
      }
    }
    for (String sub1 : firstNameSubstrings) {
      if (isValid(sub1, handle)) {
        return true;
      }
    }
    for (String sub1 : firstNameSubstrings) {
      for (String sub2 : secondNameSubstrings) {
        if (isValid(sub1 + sub2, handle) || isValid(sub2 + sub1, handle)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Compares an email handle with a given name using substring matching rules.
   *
   * @param email     The email address.
   * @param firstName The first name of the person.
   * @param lastName  The last name of the person.
   *
   * @return True if the email handle matches the name, otherwise false.
   */
  public boolean compareEmailName(String email, String firstName, String lastName) {
    String handle = processHandle(email);
    handle = removeFrequentLetters(handle);

    if ("UNKNOWN".equals(handle)) {
      return false; // Prevents processing errors on invalid emails
    }

    String[] processedNames = processName(firstName + " " + lastName);
    for (int i = 0; i < processedNames.length - 1; i++) {
      for (int j = i + 1; j < processedNames.length; j++) {
        if (checkIfCombinationInHandle(processedNames[i], processedNames[j], handle)) {
          return true;
        }
      }
    }
    return false;
  }
}
