package io.swagger.api;

import io.swagger.api.impl.jdbc.Dao;
import io.swagger.api.impl.validation.LocationValidator;
import io.swagger.api.impl.validation.NbnValidator;
import io.swagger.api.impl.response.BadRequest;
import io.swagger.api.impl.response.Forbidden;
import io.swagger.api.impl.response.NotFound;
import io.swagger.api.impl.response.Ok;
import io.swagger.api.impl.response.OperationResult;
import io.swagger.model.NbnLocationsObject;

import javax.ws.rs.core.SecurityContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NbnLocationApp {

  NbnValidator nbnValidator = new NbnValidator();
  LocationValidator locationValidator = new LocationValidator();

  public NbnLocationApp() {
  }

  public OperationResult doCreateNbnLocations(NbnLocationsObject body, SecurityContext securityContext) {
    OperationResult result;

    boolean nbnIsValid = nbnValidator.validate(body.getIdentifier());
    boolean locationsValid = locationValidator.validateAllLocations(body.getLocations());

    if (nbnIsValid && locationsValid) {
      String identifier = body.getIdentifier();
      if (nbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName())) {
        result = Dao.createOrUpdateNbn(body);
      }
      else {
        result = new Forbidden();
      }
    }
    else {
      result = new BadRequest(body.getIdentifier());
    }
    return result;

  }

  public OperationResult dogetNbnRecord(String identifier, SecurityContext securityContext) throws NotFoundException {
    OperationResult result;

    Map<String, Object> nbnRecord = new HashMap<>();

    if (nbnValidator.validate(identifier)) {
      List<String> locations = Dao.getLocations(identifier);
      if (locations.isEmpty()) {
        result = new NotFound(identifier);
      }
      else {
        nbnRecord.put("identifier", identifier);
        nbnRecord.put("locations", locations);
        result = new Ok(nbnRecord);
      }
    }
    else {
      result = new BadRequest(identifier);
    }

    return result;
  }

  public OperationResult doUpdateNbnRecord(List<String> body, String identifier, SecurityContext securityContext) throws NotFoundException {
    OperationResult result;

    NbnLocationsObject nbnLocationsObject = new NbnLocationsObject();
    nbnLocationsObject.setIdentifier(identifier);
    nbnLocationsObject.setLocations(body);

    if (nbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName())) {
      result = Dao.createOrUpdateNbn(nbnLocationsObject);
    }
    else {
      result = new Forbidden();
    }
    return result;
  }

  public OperationResult doGetLocationsByNbn(String identifier, SecurityContext securityContext) throws NotFoundException {
    OperationResult result;

    if (nbnValidator.validate(identifier)) {
      List<String> locations = Dao.getLocations(identifier);
      if (locations.isEmpty()) {
        result = new NotFound(identifier);
      }
      else {
        result = new Ok(locations);
      }
    }
    else {
      result = new BadRequest(identifier);
    }
    return result;
  }

  public OperationResult doGetNbnByLocation(String location, SecurityContext securityContext) {
    OperationResult result;

    List<String> nbn = Dao.getNbnByLocation(location);

    if (nbn.size() > 0) {
      result = new Ok(nbn);
    }
    else {
      result = new NotFound(location);
    }
    return result;
  }

}
