package io.swagger.api.impl;

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

  public static final String NBN_PATTERN = "^[uU][rR][nN]:[nN][bB][nN]:[nN][lL](:([a-zA-Z]{2}))?:\\d{2}-.+";
  private Pattern pattern;
  private Matcher matcher;

  public NbnValidator() {
    pattern = Pattern.compile(NBN_PATTERN);
  }

  public boolean validate(String nbn) {
    matcher = pattern.matcher(nbn);
    return matcher.matches();

  }
}