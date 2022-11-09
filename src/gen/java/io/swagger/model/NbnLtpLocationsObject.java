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
/*
 * NBN:Resolver
 * For easy retrieval of a publication or data, assigned with a Dutch NBN in the Netherlands, a service called <a href ='https://persistent-identifier.nl'>National Resolver</a> is available. This service is managed by <a href ='https://dans.knaw.nl'>Data Archiving and Networked Services</a> (DANS) and <a href=\"https://www.kb.nl\">KB, National Library of the Netherlands</a> (KB). Find out more about the <a href=\"https://www.kb.nl/organisatie/onderzoek-expertise/informatie-infrastructuur-diensten-voor-bibliotheken/registration-agency-nbn\">'Registration Agency NBN'</a>.
 *
 * OpenAPI spec version: 0.1.1
 * Contact: harvester@dans.knaw.nl
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.LtpLocation;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.*;
import javax.validation.Valid;

/**
 * NbnLtpLocationsObject
 */
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaJerseyServerCodegen", date = "2022-10-27T13:45:58.722388+02:00[Europe/Amsterdam]")public class NbnLtpLocationsObject   {
  @JsonProperty("identifier")
  private String identifier = null;

  @JsonProperty("locations")
  private java.util.List<LtpLocation> locations = null;

  public NbnLtpLocationsObject identifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  /**
   * Get identifier
   * @return identifier
   **/
  @JsonProperty("identifier")
  @Schema(description = "")
  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public NbnLtpLocationsObject locations(java.util.List<LtpLocation> locations) {
    this.locations = locations;
    return this;
  }

  public NbnLtpLocationsObject addLocationsItem(LtpLocation locationsItem) {
    if (this.locations == null) {
      this.locations = new java.util.ArrayList<>();
    }
    this.locations.add(locationsItem);
    return this;
  }

  /**
   * Get locations
   * @return locations
   **/
  @JsonProperty("locations")
  @Schema(description = "")
  @Valid
  public java.util.List<LtpLocation> getLocations() {
    return locations;
  }

  public void setLocations(java.util.List<LtpLocation> locations) {
    this.locations = locations;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NbnLtpLocationsObject nbnLtpLocationsObject = (NbnLtpLocationsObject) o;
    return Objects.equals(this.identifier, nbnLtpLocationsObject.identifier) &&
        Objects.equals(this.locations, nbnLtpLocationsObject.locations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier, locations);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NbnLtpLocationsObject {\n");
    
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
    sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}