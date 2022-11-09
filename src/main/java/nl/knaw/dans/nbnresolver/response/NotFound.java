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
package nl.knaw.dans.nbnresolver.response;

import static javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

public class NotFound implements OperationResult {

  private String identifier;

  public NotFound(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public Status getStatus() {
    return NOT_FOUND;
  }

  @Override
  public Object getResponseBody() {
    return new ResponseMessage(NOT_FOUND.getStatusCode(), "Object not found: " + identifier);
  }
}

