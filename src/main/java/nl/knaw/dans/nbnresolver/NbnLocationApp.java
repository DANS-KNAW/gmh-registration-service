package nl.knaw.dans.nbnresolver;

import io.swagger.api.ApiResponseMessage;
import io.swagger.model.NbnLocationsObject;
import nl.knaw.dans.nbnresolver.jdbc.Dao;
import nl.knaw.dans.nbnresolver.response.BadRequest;
import nl.knaw.dans.nbnresolver.response.Created;
import nl.knaw.dans.nbnresolver.response.Forbidden;
import nl.knaw.dans.nbnresolver.response.NotFound;
import nl.knaw.dans.nbnresolver.response.Ok;
import nl.knaw.dans.nbnresolver.response.OperationResult;
import nl.knaw.dans.nbnresolver.validation.LocationValidator;
import nl.knaw.dans.nbnresolver.validation.NbnValidator;

import javax.ws.rs.core.SecurityContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NbnLocationApp {

  public NbnLocationApp() {
  }

  public OperationResult doCreateNbnLocations(NbnLocationsObject body, SecurityContext securityContext) {
    OperationResult result;

    boolean nbnIsValid = NbnValidator.validate(body.getIdentifier());
    boolean locationsValid = LocationValidator.validateAllLocations(body.getLocations());
    int registrantId = Dao.getRegistrantIdByOrgPrefix(securityContext.getUserPrincipal().getName());

    if (nbnIsValid && locationsValid) {
      String identifier = body.getIdentifier();
      if (NbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName())) {
        result = Dao.createNbn(body, registrantId);
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

  public OperationResult doGetNbnRecord(String identifier) {
    OperationResult result;

    Map<String, Object> nbnRecord = new HashMap<>();

    if (NbnValidator.validate(identifier)) {
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

  public OperationResult doUpdateNbnRecord(List<String> body, String identifier, SecurityContext securityContext) {
    OperationResult result;

    if (NbnValidator.prefixMatches(identifier, securityContext.getUserPrincipal().getName())) {
      int registrantId = Dao.getRegistrantIdByOrgPrefix(securityContext.getUserPrincipal().getName());
      NbnLocationsObject nbnLocationsObject = getNbnLocationsObject(body, identifier);
      if (Dao.getIdentifier(identifier)) {
        Dao.deleteNbn(identifier);
        result = Dao.createNbn(nbnLocationsObject, registrantId);
        if (result instanceof Created) {
          result = new Ok(new ApiResponseMessage(4, "OK (updated existing)"));
        }
      }
      else {
        result = Dao.createNbn(nbnLocationsObject, registrantId);
      }
    }
    else {
      result = new Forbidden();
    }
    return result;
  }

  public OperationResult doGetLocationsByNbn(String identifier) {
    OperationResult result;

    if (NbnValidator.validate(identifier)) {
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

  public OperationResult doGetNbnByLocation(String location) {
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

  private NbnLocationsObject getNbnLocationsObject(List<String> body, String identifier) {
    NbnLocationsObject nbnLocationsObject = new NbnLocationsObject();
    nbnLocationsObject.setIdentifier(identifier);
    nbnLocationsObject.setLocations(body);
    return nbnLocationsObject;
  }

}
