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
package io.swagger.api.impl;

import io.swagger.api.NbnApiService;
import io.swagger.model.NbnLocationsObject;
import nl.knaw.dans.nbnresolver.NbnLocationApp;
import nl.knaw.dans.nbnresolver.response.OperationResult;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-10-26T14:14:37.219784+02:00[Europe/Amsterdam]")
public class NbnApiServiceImpl extends NbnApiService {

  NbnLocationApp app = new NbnLocationApp();

  @Override
  public Response createNbnLocations(NbnLocationsObject body, SecurityContext securityContext) {
    OperationResult result = app.doCreateNbnLocations(body, securityContext);
    return Response.status(result.getStatus()).entity(result.getResponseBody()).build();
  }

  @Override
  public Response getNbnRecord(String identifier, SecurityContext securityContext) {
    OperationResult result = app.doGetNbnRecord(identifier, securityContext);
    return Response.status(result.getStatus()).entity(result.getResponseBody()).build();
  }

  @Override
  public Response updateNbnRecord(List<String> body, String identifier, SecurityContext securityContext) {
    OperationResult result = app.doUpdateNbnRecord(body, identifier, securityContext);
    return Response.status(result.getStatus()).entity(result.getResponseBody()).build();
  }

  @Override
  public Response getLocationsByNbn(String identifier, SecurityContext securityContext) {
    OperationResult result = app.doGetLocationsByNbn(identifier, securityContext);
    return Response.status(result.getStatus()).entity(result.getResponseBody()).build();
  }

}
