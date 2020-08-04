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

@WebServlet("/destination-data")
public class DestinationDataServlet extends HttpServlet {

  List<com.google.sps.data.Destination.Tag> tagEnums;
  com.google.sps.data.Destination.Obscurity obscureLevel;

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

    List<String> levels = Arrays.stream(request.getParameterValues("obscurity")).filter(level -> level != null).collect(Collectors.toList());
    com.google.sps.data.Destination.Obscurity level = convertLevelToEnum(levels);

    List<String> tags = Arrays.stream(request.getParameterValues("tag")).filter(tag -> tag != null).collect(Collectors.toList());
    List<com.google.sps.data.Destination.Tag> checkedTags = convertTagsToEnum(tags);

    com.google.sps.data.Destination d1 = new com.google.sps.data.Destination.Builder()
            .withName(name)
            .withLocation(location)
            .withCity(city)
            .withDescription(description)
            .withRiddle(riddle)
            .withTags(checkedTags)
            .withObscurity(level)
            .build();
    
    Gson gson = new Gson();
    String json = gson.toJson(d1);
    response.setContentType("application/json;");
    response.getWriter().println(json);
    response.sendRedirect("/destination-data");
  }

  public List<com.google.sps.data.Destination.Tag> convertTagsToEnum(List<String> tags){
    for(String tag: tags){
      switch(tag){
        case "art":
          tagEnums.add(com.google.sps.data.Destination.Tag.ART);
        case "sport":
          tagEnums.add(com.google.sps.data.Destination.Tag.SPORT);
        case "historical":
          tagEnums.add(com.google.sps.data.Destination.Tag.HISTORICAL);
        case "food":
          tagEnums.add(com.google.sps.data.Destination.Tag.FOOD);
        case "family":
          tagEnums.add(com.google.sps.data.Destination.Tag.FAMILY);
        case "tourist":
          tagEnums.add(com.google.sps.data.Destination.Tag.TOURIST);
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
          obscureLevel = com.google.sps.data.Destination.Obscurity.EASY;
        case "medium":
          obscureLevel = com.google.sps.data.Destination.Obscurity.MEDIUM;
        case "hard":
          obscureLevel = com.google.sps.data.Destination.Obscurity.HARD;
        default:
          obscureLevel = com.google.sps.data.Destination.Obscurity.UNDEFINED;
      }
    }
    return obscureLevel;
  }
}