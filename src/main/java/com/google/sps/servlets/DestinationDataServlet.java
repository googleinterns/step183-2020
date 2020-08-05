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

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;

/* Returns a destination created from user submitted information */
@WebServlet("/destination-data")
public class DestinationDataServlet extends HttpServlet {
  
  // Temporarily stores the destination created by the user
  public List<com.google.sps.data.Destination> destinations = new ArrayList<>();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");

    com.google.sps.data.LatLng location = new com.google.sps.data.LatLng.Builder()
            .withLat(Double.parseDouble(request.getParameter("latitude")))
            .withLng(Double.parseDouble(request.getParameter("longitude")))
            .build();

    String city = request.getParameter("city");
    String description = request.getParameter("description");

    com.google.sps.data.Riddle riddle = new com.google.sps.data.Riddle.Builder()
            .withPuzzle(request.getParameter("riddle"))
            .withHint(request.getParameter("hint1"))
            .withHint(request.getParameter("hint2"))
            .withHint(request.getParameter("hint3"))
            .build();
            
    /* Retrieves the obscurity level chosen by the user as a List of Strings and converts the level to an Enum Obscurity value */
    List<String> levels = Arrays.stream(request.getParameterValues("obscurity")).filter(level -> level != null).collect(Collectors.toList());
    com.google.sps.data.Destination.Obscurity level = convertLevelToEnum(levels);

    /* Retrieves the tags selected by the user as a List of Strings and converts them into a Set of Enum Tags */
    List<String> tags = Arrays.stream(request.getParameterValues("tag")).filter(tag -> tag != null).collect(Collectors.toList());
    Set<com.google.sps.data.Destination.Tag> checkedTags = convertTagsToEnum(tags);

    com.google.sps.data.Destination d1 = new com.google.sps.data.Destination.Builder()
            .withName(name)
            .withLocation(location)
            .withCity(city)
            .withDescription(description)
            .withRiddle(riddle)
            .withTags(checkedTags)
            .withObscurity(level)
            .build();
    
    destinations.add(d1);
    response.sendRedirect("/destination-data");
  }

  /* Retrieves the destination created by the user, turns it into a JSON formatted string, 
   *and displays the JSON-ified String on /destination-data
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    String json = gson.toJson(destinations.get(destinations.size()-1));
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  public Set<com.google.sps.data.Destination.Tag> convertTagsToEnum(List<String> tags){
    Set<com.google.sps.data.Destination.Tag> tagEnums = new HashSet<com.google.sps.data.Destination.Tag>();
    for(String tag: tags){
      switch(tag){
        case "art":
          tagEnums.add(com.google.sps.data.Destination.Tag.ART);
          break;
        case "sports":
          tagEnums.add(com.google.sps.data.Destination.Tag.SPORT);
          break;
        case "historical":
          tagEnums.add(com.google.sps.data.Destination.Tag.HISTORICAL);
          break;
        case "food":
          tagEnums.add(com.google.sps.data.Destination.Tag.FOOD);
          break;
        case "family":
          tagEnums.add(com.google.sps.data.Destination.Tag.FAMILY);
          break;
        case "tourist":
          tagEnums.add(com.google.sps.data.Destination.Tag.TOURIST);
          break;
        default:
          tagEnums.add(com.google.sps.data.Destination.Tag.UNDEFINED);
      }
    }
    return tagEnums;
  }

  public com.google.sps.data.Destination.Obscurity convertLevelToEnum(List<String> levels){
    for(String level: levels){
      switch(level){
        case "easy":
          return com.google.sps.data.Destination.Obscurity.EASY;
        case "medium":
          return com.google.sps.data.Destination.Obscurity.MEDIUM;
        case "hard":
          return com.google.sps.data.Destination.Obscurity.HARD;
        default:
          return com.google.sps.data.Destination.Obscurity.UNDEFINED;
      }
    }
    return com.google.sps.data.Destination.Obscurity.UNDEFINED;
  }
}
