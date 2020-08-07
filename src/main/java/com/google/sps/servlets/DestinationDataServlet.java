// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.sps.data.Destination;
import com.google.sps.data.LatLng;
import com.google.sps.data.Riddle;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Returns a destination created from user submitted information */
@WebServlet("/destination-data")
public class DestinationDataServlet extends HttpServlet {
  private static final String NAME_PARAMETER = "name";
  private static final String LAT_PARAMETER = "latitude";
  private static final String LNG_PARAMETER = "longitude";
  private static final String CITY_PARAMETER = "city";
  private static final String DESCRIPTION_PARAMETER = "description";
  private static final String RIDDLE_PARAMETER = "riddle";
  private static final String HINT1_PARAMETER = "hint1";
  private static final String HINT2_PARAMETER = "hint2";
  private static final String HINT3_PARAMETER = "hint3";
  private static final String OBSCURITY_PARAMETER = "obscurity";
  private static final String TAG_PARAMETER = "tag";
  private static final String REDIRECT_URL = "/index.html";

  private static final DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();

  private static final Gson GSON = new Gson();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter(NAME_PARAMETER);

    LatLng location =
        new LatLng.Builder()
            .withLat(Double.parseDouble(request.getParameter(LAT_PARAMETER)))
            .withLng(Double.parseDouble(request.getParameter(LNG_PARAMETER)))
            .build();

    String city = request.getParameter(CITY_PARAMETER);
    String description = request.getParameter(DESCRIPTION_PARAMETER);

    Riddle riddle =
        new Riddle.Builder()
            .withPuzzle(request.getParameter(RIDDLE_PARAMETER))
            .withHint(request.getParameter(HINT1_PARAMETER))
            .withHint(request.getParameter(HINT2_PARAMETER))
            .withHint(request.getParameter(HINT3_PARAMETER))
            .build();

    /* Retrieves the obscurity level chosen by the user as a List of Strings and converts the level to an Enum Obscurity value */
    String obscureLevel = request.getParameter(OBSCURITY_PARAMETER);
    Destination.Obscurity level = convertLevelToEnum(obscureLevel);

    /* Retrieves the tags selected by the user as a List of Strings and converts them into a Set of Enum Tags */
    List<String> tags =
        Arrays.stream(request.getParameterValues(TAG_PARAMETER))
            .filter(tag -> tag != null)
            .collect(Collectors.toList());
    Set<Destination.Tag> checkedTags = convertTagsToEnum(tags);

    Destination destination =
        new Destination.Builder()
            .withName(name)
            .withLocation(location)
            .withCity(city)
            .withDescription(description)
            .withRiddle(riddle)
            .withTags(checkedTags)
            .withObscurity(level)
            .build();

    /* Takes the destination object and turns it into a JSON String to be stored in Datastore */
    String jsonDestination = GSON.toJson(destination);

    Entity destinationEntity = new Entity("Destination");
    destinationEntity.setProperty("value", jsonDestination);
    DATASTORE.put(destinationEntity);

    response.sendRedirect(REDIRECT_URL);
  }

  private Set<Destination.Tag> convertTagsToEnum(List<String> tags) {
    Set<Destination.Tag> tagEnums = new HashSet<Destination.Tag>();
    for (String tag : tags) {
      switch (tag) {
        case "art":
          tagEnums.add(Destination.Tag.ART);
          break;
        case "sports":
          tagEnums.add(Destination.Tag.SPORT);
          break;
        case "historical":
          tagEnums.add(Destination.Tag.HISTORICAL);
          break;
        case "food":
          tagEnums.add(Destination.Tag.FOOD);
          break;
        case "family":
          tagEnums.add(Destination.Tag.FAMILY);
          break;
        case "tourist":
          tagEnums.add(Destination.Tag.TOURIST);
          break;
      }
    }

    return tagEnums;
  }

  private Destination.Obscurity convertLevelToEnum(String obscureLevel) {
    switch (obscureLevel) {
      case "easy":
        return Destination.Obscurity.EASY;
      case "medium":
        return Destination.Obscurity.MEDIUM;
      case "hard":
        return Destination.Obscurity.HARD;
      default:
        return Destination.Obscurity.UNDEFINED;
    }
  }
}