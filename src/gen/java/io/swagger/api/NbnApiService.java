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
package io.swagger.api;

import io.swagger.api.*;
import io.swagger.model.*;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import io.swagger.model.LtpLocation;
import io.swagger.model.NbnLocationsObject;
import io.swagger.model.NbnLtpLocationsObject;

import java.util.Map;
import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-10-27T13:45:58.722388+02:00[Europe/Amsterdam]")public abstract class NbnApiService {
    public abstract Response createNbnLocations(NbnLocationsObject body,SecurityContext securityContext) throws NotFoundException;
    public abstract Response getLocationsByNbn(String identifier,SecurityContext securityContext) throws NotFoundException;
    public abstract Response getNbnRecord(String identifier,SecurityContext securityContext) throws NotFoundException;
    public abstract Response updateNbnRecord(java.util.List<String> body,String identifier,SecurityContext securityContext) throws NotFoundException;
}
