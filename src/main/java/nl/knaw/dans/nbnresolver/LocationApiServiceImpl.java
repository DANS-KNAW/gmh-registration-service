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
package nl.knaw.dans.nbnresolver;

import io.swagger.api.LocationApiService;
import nl.knaw.dans.nbnresolver.response.OperationResult;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2021-01-08T12:34:19.815Z[GMT]")
public class LocationApiServiceImpl extends LocationApiService {

  NbnLocationApp app = new NbnLocationApp();

  @Override
  public Response getNbnByLocation(String location, SecurityContext securityContext) {
    OperationResult result = app.doGetNbnByLocation(location);
    return Response.status(result.getStatus()).entity(result.getResponseBody()).build();
  }
}
