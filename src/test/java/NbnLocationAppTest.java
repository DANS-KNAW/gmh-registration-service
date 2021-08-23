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
import io.swagger.model.NbnLocationsObject;
import nl.knaw.dans.nbnresolver.NbnLocationApp;
import nl.knaw.dans.nbnresolver.jdbc.Dao;
import nl.knaw.dans.nbnresolver.response.BadRequest;
import nl.knaw.dans.nbnresolver.response.Conflict;
import nl.knaw.dans.nbnresolver.response.Created;
import nl.knaw.dans.nbnresolver.response.Forbidden;
import nl.knaw.dans.nbnresolver.response.NotFound;
import nl.knaw.dans.nbnresolver.response.Ok;
import nl.knaw.dans.nbnresolver.response.OperationResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@PowerMockIgnore("javax.security.auth.Subject")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Dao.class })
public class NbnLocationAppTest {

  private SecurityContext securityContextMock;
  private Principal principalMock;
  private NbnLocationApp app;
  private NbnLocationsObject nbnLocationsObject;
  private final String VALID_URN_NBN = "urn:nbn:nl:ui:17-example";
  private final String INVALID_URN_NBN = "urn:nbn:nl:ui-example";
  private final String EXISTING_URN_NBN = "urn:nbn:nl:ui:17-existing";
  private final String NON_EXISTING_URN_NBN = "urn:nbn:nl:ui:17-non-existing";
  private final String URN_NBN_INVALID_PREFIX = "urn:nbn:nl:ui:11-example";
  private final List<String> TEST_LOCATIONS = Arrays.asList("http://test_location1.nl", "http://test_location2.nl");
  private final String NON_EXISTING_LOCATION = "http://test_location_non_existing.nl";

  @Before
  public void setup() throws SQLException {
    app = new NbnLocationApp();
    nbnLocationsObject = new NbnLocationsObject();
    nbnLocationsObject.setLocations(TEST_LOCATIONS);
    prepareMocks();
  }

  private void prepareMocks() throws SQLException {
    PowerMockito.mockStatic(Dao.class);
    securityContextMock = mock(SecurityContext.class);
    principalMock = mock(Principal.class);
    when(securityContextMock.getUserPrincipal()).thenReturn(principalMock);
    when(principalMock.getName()).thenReturn("ui:17");
    when(Dao.getLocations(VALID_URN_NBN)).thenReturn(TEST_LOCATIONS);
    when(Dao.getRegistrantIdByOrgPrefix(any())).thenReturn(1);
    when(Dao.getIdentifier(EXISTING_URN_NBN)).thenReturn(true);
    when(Dao.getIdentifier(VALID_URN_NBN)).thenReturn(false);
    when(Dao.getLocations(NON_EXISTING_URN_NBN)).thenReturn(emptyList());
    when(Dao.getNbnByLocation(TEST_LOCATIONS.get(0))).thenReturn(singletonList(VALID_URN_NBN));
    when(Dao.getNbnByLocation(NON_EXISTING_LOCATION)).thenReturn(emptyList());
  }

  @Test
  public void testdoCreateNbnLocations() {
    nbnLocationsObject.setIdentifier(VALID_URN_NBN);
    OperationResult result = app.doCreateNbnLocations(nbnLocationsObject, securityContextMock);
    assertTrue(result instanceof Created);
    assertEquals(result.getStatus().getStatusCode(), 201);
  }

  @Test
  public void testdoCreateNbnLocationsInvalidNbn() {
    nbnLocationsObject.setIdentifier(INVALID_URN_NBN);
    OperationResult result = app.doCreateNbnLocations(nbnLocationsObject, securityContextMock);
    assertTrue(result instanceof BadRequest);
    assertEquals(result.getStatus().getStatusCode(), 400);
  }

  @Test
  public void testdoCreateNbnLocationsWrongOrganizationPrefix() {
    nbnLocationsObject.setIdentifier(URN_NBN_INVALID_PREFIX);
    OperationResult result = app.doCreateNbnLocations(nbnLocationsObject, securityContextMock);
    assertTrue(result instanceof Forbidden);
    assertEquals(result.getStatus().getStatusCode(), 403);
  }

  @Test
  public void testdoCreateNbnLocationsWithExistingNbn() {
    nbnLocationsObject.setIdentifier(EXISTING_URN_NBN);
    OperationResult result = app.doCreateNbnLocations(nbnLocationsObject, securityContextMock);
    assertTrue(result instanceof Conflict);
    assertEquals(result.getStatus().getStatusCode(), 409);
  }

  @Test
  public void testDoGetNbnRecord() {
    Map<String, Object> nbnRecordInput = new HashMap<>();
    nbnRecordInput.put("identifier", VALID_URN_NBN);
    nbnRecordInput.put("locations", TEST_LOCATIONS);

    OperationResult result = app.doGetNbnRecord(VALID_URN_NBN);
    assertTrue(result instanceof Ok);
    assertEquals(result.getStatus().getStatusCode(), 200);
    assertEquals(result.getResponseBody(), nbnRecordInput);
  }

  @Test
  public void testDoGetNbnRecordNonExistingNbn() {
    OperationResult result = app.doGetNbnRecord(NON_EXISTING_URN_NBN);
    assertTrue(result instanceof NotFound);
    assertEquals(result.getStatus().getStatusCode(), 404);
  }

  @Test
  public void testDoGetNbnRecordInvalidNbn() {
    OperationResult result = app.doGetNbnRecord(INVALID_URN_NBN);
    assertTrue(result instanceof BadRequest);
    assertEquals(result.getStatus().getStatusCode(), 400);
  }

  @Test
  public void testDoUpdateNbnRecordNewNbn() {
    OperationResult result = app.doUpdateNbnRecord(TEST_LOCATIONS, VALID_URN_NBN, securityContextMock);
    assertTrue(result instanceof Created);
    assertEquals(result.getStatus().getStatusCode(), 201);
  }

  @Test
  public void testDoUpdateNbnRecordExistingNbn() {
    OperationResult result = app.doUpdateNbnRecord(TEST_LOCATIONS, EXISTING_URN_NBN, securityContextMock);
    assertTrue(result instanceof Ok);
    assertEquals(result.getStatus().getStatusCode(), 200);
  }

  @Test
  public void testDoUpdateNbnRecordInvalidNbn() {
    OperationResult result = app.doUpdateNbnRecord(TEST_LOCATIONS, INVALID_URN_NBN, securityContextMock);
    assertTrue(result instanceof BadRequest);
    assertEquals(result.getStatus().getStatusCode(), 400);
  }

  @Test
  public void testDoUpdateNbnRecordInvalidOrgPrefix() {
    OperationResult result = app.doUpdateNbnRecord(TEST_LOCATIONS, URN_NBN_INVALID_PREFIX, securityContextMock);
    assertTrue(result instanceof Forbidden);
    assertEquals(result.getStatus().getStatusCode(), 403);
  }

  @Test
  public void testdoGetLocationsByNbn() {
    OperationResult result = app.doGetLocationsByNbn(VALID_URN_NBN);
    assertTrue(result instanceof Ok);
    assertEquals(result.getStatus().getStatusCode(), 200);
  }

  @Test
  public void testdoGetLocationsByNbnInvalidNbn() {
    OperationResult result = app.doGetLocationsByNbn(INVALID_URN_NBN);
    assertTrue(result instanceof BadRequest);
    assertEquals(result.getStatus().getStatusCode(), 400);
  }

  @Test
  public void testdoGetLocationsByNbnNonExistingNbn() {
    OperationResult result = app.doGetLocationsByNbn(NON_EXISTING_URN_NBN);
    assertTrue(result instanceof NotFound);
    assertEquals(result.getStatus().getStatusCode(), 404);
  }

  @Test
  public void testDoGetNbnByLocation() {
    OperationResult result = app.doGetNbnByLocation(TEST_LOCATIONS.get(0), securityContextMock);
    assertTrue(result instanceof Ok);
    assertEquals(result.getStatus().getStatusCode(), 200);
  }

  @Test
  public void testDoGetNbnByLocationWithNonExistingLocation() {
    OperationResult result = app.doGetNbnByLocation(NON_EXISTING_LOCATION, securityContextMock);
    assertTrue(result instanceof NotFound);
    assertEquals(result.getStatus().getStatusCode(), 404);
  }

}
