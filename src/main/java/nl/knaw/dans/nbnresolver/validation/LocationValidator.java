package nl.knaw.dans.nbnresolver.validation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationValidator {

  public static final String NBN_PATTERN = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
  private static Pattern pattern;
  private static Matcher matcher;

  public LocationValidator() {
    pattern = Pattern.compile(NBN_PATTERN);
  }

  public static boolean validate(String location) {
    matcher = pattern.matcher(location);
    return matcher.matches();
  }

  public static boolean validateAllLocations(List<String> locations) {
    for (String location : locations) {
      boolean isValid = validate(location);
      if (!isValid) {
        return false;
      }
    }
    return true;
  }
}
