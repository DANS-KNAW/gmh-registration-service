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

/**
 * Copyright (C) 2020 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NbnValidator {

  private static final String NBN_PATTERN = "^[uU][rR][nN]:[nN][bB][nN]:[nN][lL](:([a-zA-Z]{2}))?:\\d{2}-.+";
  private static final String ORG_PREFIX_PATTERN = "([a-zA-Z]{2}):\\d{2}";

  public NbnValidator() {
  }

  public static boolean validate(String nbn) {
    Pattern pattern = Pattern.compile(NBN_PATTERN);
    Matcher matcher = pattern.matcher(nbn);
    return matcher.matches();
  }

  public static boolean prefixMatches(String nbn, String orgPrefix) {
    boolean matches = false;
    Pattern pattern = Pattern.compile(ORG_PREFIX_PATTERN);
    Matcher matcher = pattern.matcher(nbn);
    if (matcher.find()) {
      if (matcher.group().equalsIgnoreCase(orgPrefix)) {
        matches = true;
      }
    }
    else {
      matches = false;
    }
    return matches;
  }
}