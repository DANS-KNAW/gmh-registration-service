/**
 * Copyright (C) 2018 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.nbnresolver.validation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationValidator {

  public static final String NBN_PATTERN = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
  private static Pattern pattern;
  private static Matcher matcher;

  public LocationValidator() {
  }

  public static boolean validate(String location) {
    pattern = Pattern.compile(NBN_PATTERN);
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
