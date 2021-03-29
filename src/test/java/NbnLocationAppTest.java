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
import nl.knaw.dans.nbnresolver.response.Created;
import nl.knaw.dans.nbnresolver.response.Forbidden;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private final String TEST_URN_NBN = "urn:nbn:nl:ui:17-example";
  private final String INVALID_URN_NBN = "urn:nbn:nl-example";
  private final List<String> TEST_LOCATIONS = Arrays.asList("http://testLocationDANS-KNAW.nl1", "http://testLocationDANS-KNAW.nl2");

  @Before
  public void setup() {
    PowerMockito.mockStatic(Dao.class);
    securityContextMock = mock(SecurityContext.class);
    principalMock = mock(Principal.class);
    app = new NbnLocationApp();
    nbnLocationsObject = new NbnLocationsObject();
    nbnLocationsObject.setIdentifier(TEST_URN_NBN);
    nbnLocationsObject.setLocations(TEST_LOCATIONS);
    mockDaoMethods();
  }

  public void mockDaoMethods() {
    when(Dao.getLocations(TEST_URN_NBN)).thenReturn(TEST_LOCATIONS);
    when(Dao.getRegistrantIdByOrgPrefix(any())).thenReturn(1);
    when(Dao.createNbn(nbnLocationsObject, 1)).thenReturn(new Created(TEST_URN_NBN));
    when(securityContextMock.getUserPrincipal()).thenReturn(principalMock);
    when(principalMock.getName()).thenReturn("ui:17");
  }

  @Test
  public void testDoGetNbnRecord() {
    Map<String, Object> nbnRecordInput = new HashMap<>();
    nbnRecordInput.put("identifier", TEST_URN_NBN);
    nbnRecordInput.put("locations", TEST_LOCATIONS);

    OperationResult result = app.doGetNbnRecord(TEST_URN_NBN);
    assertTrue(result instanceof Ok);
    assertEquals(result.getStatus().getStatusCode(), 200);
    assertEquals(result.getResponseBody(), nbnRecordInput);
  }

  @Test
  public void testDoGetNbnRecordInvalidId() {
    OperationResult result = app.doGetNbnRecord(INVALID_URN_NBN);
    assertTrue(result instanceof BadRequest);
    assertEquals(result.getStatus().getStatusCode(), 400);
  }

  @Test
  public void testdoCreateNbnLocations() {
    OperationResult result = app.doCreateNbnLocations(nbnLocationsObject, securityContextMock);
    assertTrue(result instanceof Created);
    assertEquals(result.getStatus().getStatusCode(), 201);
  }

  @Test
  public void testdoCreateNbnLocationsInvalidUrnNbn() {
    nbnLocationsObject = nbnLocationsObject.identifier(INVALID_URN_NBN);
    OperationResult result = app.doCreateNbnLocations(nbnLocationsObject, securityContextMock);
    assertTrue(result instanceof BadRequest);
    assertEquals(result.getStatus().getStatusCode(), 400);
  }

  @Test
  public void testdoCreateNbnLocationsWrongOrganizationPrefix() {
    String urnNbnWithOtherPrefix = "urn:nbn:nl:ui:11-example";
    nbnLocationsObject = nbnLocationsObject.identifier(urnNbnWithOtherPrefix);
    OperationResult result = app.doCreateNbnLocations(nbnLocationsObject, securityContextMock);
    assertTrue(result instanceof Forbidden);
    assertEquals(result.getStatus().getStatusCode(), 403);
  }

  @Test
  public void testdoCreateNbnLocationsWithExistingUrnNbn() {
  }

}
