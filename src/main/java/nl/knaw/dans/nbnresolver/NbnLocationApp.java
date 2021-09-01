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
      if (!NbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName()) && !securityContext.isUserInRole("LTP"))
        return new Forbidden();
      if (Dao.isRegistrantIdentifier(identifier, registrantId))
        return new Conflict(identifier);
      else {
        Dao.createNbn(body, registrantId);
        return new Created(identifier);
      }
    }
    catch (SQLException e) {
      logger.error("A Sql error occurred in doCreateNbnLocations: " + e);
      return new InternalServerError();
    }
  }

  public OperationResult doGetNbnRecord(String identifier, SecurityContext securityContext) {
    Map<String, Object> nbnRecord = new HashMap<>();

    if (!NbnValidator.validate(identifier))
      return new BadRequest(identifier);

    if (!NbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName()) && !securityContext.isUserInRole("LTP")) //TODO: Undocumented feature: LTP is able to update ALL nbn's.
      return new Forbidden();

    List<String> locations = Dao.getLocations(identifier, securityContext.isUserInRole("LTP"));
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
    if (!NbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName()) && !securityContext.isUserInRole("LTP")) //TODO: Undocumented feature: LTP is able to update ALL nbn's.
      return new Forbidden();

    try {
      int registrantId = Dao.getRegistrantIdByOrgPrefix(securityContext.getUserPrincipal().getName());
      if (Dao.isExistingIdentifier(identifier)) { //Check if nbn exists (update), or not (create new one).
        Dao.deleteNbnLocationsByRegId(registrantId, identifier); //  Dao.deleteRegistrantNbnLocations(identifier, securityContext.isUserInRole("LTP"));
        Dao.createNbn(nbnLocationsObject, registrantId);
        return new Ok(new ResponseMessage(OK.getStatusCode(), "OK (updated existing)"));
      }
      else { //NBN does not exist (create new one).
        Dao.createNbn(nbnLocationsObject, registrantId);
        return new Created(identifier);
      }
    }
    catch (SQLException e) {
      logger.error("A Sql error occurred: " + e);
      return new InternalServerError();
    }
  }

  public OperationResult doGetLocationsByNbn(String identifier, SecurityContext securityContext) {
    if (!NbnValidator.validate(identifier))
      return new BadRequest(identifier);
    if (!NbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName()) && !securityContext.isUserInRole("LTP"))
      return new Forbidden(); //TODO: New undocumented response.
    List<String> locations = Dao.getLocations(identifier, securityContext.isUserInRole("LTP"));
    if (locations.isEmpty())
      return new NotFound(identifier);
    else {
      return new Ok(locations);
    }
  }

  public OperationResult doGetNbnByLocation(String location, SecurityContext securityContext) {

    //TODO: Do we need to check locationValidator for BAD REQUEST?
    List<String> nbn = Dao.getNbnByLocation(location);
    boolean isMatch = false;
    if (securityContext.isUserInRole("LTP")) { // No restrictions for LTP.
      isMatch = true;
    }
    else {
      for (String identifier : nbn) {
        if (NbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName())) {
          isMatch = true;
          break;
        }
      }
    }
    if (nbn.size() > 0 && isMatch) {
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
