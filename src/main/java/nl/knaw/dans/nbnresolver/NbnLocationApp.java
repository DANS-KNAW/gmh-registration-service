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

import io.swagger.model.NbnLocationsObject;
import nl.knaw.dans.nbnresolver.jdbc.Dao;
import nl.knaw.dans.nbnresolver.response.BadRequest;
import nl.knaw.dans.nbnresolver.response.Conflict;
import nl.knaw.dans.nbnresolver.response.Created;
import nl.knaw.dans.nbnresolver.response.Forbidden;
import nl.knaw.dans.nbnresolver.response.InternalServerError;
import nl.knaw.dans.nbnresolver.response.NotFound;
import nl.knaw.dans.nbnresolver.response.Ok;
import nl.knaw.dans.nbnresolver.response.OperationResult;
import nl.knaw.dans.nbnresolver.response.ResponseMessage;
import nl.knaw.dans.nbnresolver.validation.LocationValidator;
import nl.knaw.dans.nbnresolver.validation.NbnValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.SecurityContext;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.ws.rs.core.Response.Status.OK;

public class NbnLocationApp {

  private static final Logger logger = LoggerFactory.getLogger(NbnLocationApp.class);

  public NbnLocationApp() {
  }

  public OperationResult doCreateNbnLocations(NbnLocationsObject body, SecurityContext securityContext) {
    String identifier = body.getIdentifier();
    boolean nbnIsValid = NbnValidator.validate(body.getIdentifier());
    boolean locationsValid = LocationValidator.validateAllLocations(body.getLocations());

    try {
      int registrantId = Dao.getRegistrantIdByOrgPrefix(securityContext.getUserPrincipal().getName());

      if (!nbnIsValid || !locationsValid)
        return new BadRequest(identifier);
      if (!NbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName()))
        return new Forbidden();
      if (Dao.getIdentifier(identifier))
        return new Conflict(identifier);
      else {
        Dao.createNbn(body, registrantId);
        return new Created(identifier);
      }
    }
    catch (SQLException e) {
      logger.error("A Sql error occurred: " + e);
      return new InternalServerError();
    }
  }

  public OperationResult doGetNbnRecord(String identifier) {
    Map<String, Object> nbnRecord = new HashMap<>();

    if (!NbnValidator.validate(identifier))
      return new BadRequest(identifier);

    List<String> locations = Dao.getLocations(identifier);
    if (locations.isEmpty()) {
      return new NotFound(identifier);
    }
    else {
      nbnRecord.put("identifier", identifier);
      nbnRecord.put("locations", locations);
      return new Ok(nbnRecord);
    }
  }

  public OperationResult doUpdateNbnRecord(List<String> body, String identifier, SecurityContext securityContext) {
    NbnLocationsObject nbnLocationsObject = getNbnLocationsObject(body, identifier);
    boolean nbnIsValid = NbnValidator.validate(identifier);
    boolean locationsValid = LocationValidator.validateAllLocations(body);

    if (!nbnIsValid || !locationsValid)
      return new BadRequest(identifier);
    if (!NbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName()))
      return new Forbidden();

    try {
      int registrantId = Dao.getRegistrantIdByOrgPrefix(securityContext.getUserPrincipal().getName());
      if (Dao.getIdentifier(identifier)) {
        Dao.deleteNbn(identifier);
        Dao.createNbn(nbnLocationsObject, registrantId);
        return new Ok(new ResponseMessage(OK.getStatusCode(), "OK (updated existing)"));
      }
      else {
        Dao.createNbn(nbnLocationsObject, registrantId);
        return new Created(identifier);
      }
    }
    catch (SQLException e) {
      logger.error("A Sql error occurred: " + e);
      return new InternalServerError();
    }
  }

  public OperationResult doGetLocationsByNbn(String identifier) {
    if (!NbnValidator.validate(identifier))
      return new BadRequest(identifier);
    List<String> locations = Dao.getLocations(identifier);
    if (locations.isEmpty())
      return new NotFound(identifier);
    else {
      return new Ok(locations);
    }
  }

  public OperationResult doGetNbnByLocation(String location) {
    List<String> nbn = Dao.getNbnByLocation(location);

    if (nbn.size() > 0) {
      return new Ok(nbn);
    }
    else {
      return new NotFound(location);
    }
  }

  private NbnLocationsObject getNbnLocationsObject(List<String> body, String identifier) {
    NbnLocationsObject nbnLocationsObject = new NbnLocationsObject();
    nbnLocationsObject.setIdentifier(identifier);
    nbnLocationsObject.setLocations(body);
    return nbnLocationsObject;
  }

}
