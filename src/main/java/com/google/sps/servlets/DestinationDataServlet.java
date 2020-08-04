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

@WebServlet("/destination-data")
public class DestinationDataServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String name = request.getParameter("name");

    LatLng location = new LatLng.Builder()
            .withLat(request.getParameter("latitude"))
            .withLng(request.getParameter("longitude"))
            .build();

    String city = request.getParameter("city");
    String Description = request.getParameter("description");

    Riddle riddle = new Riddle.Builder()
            .withPuzzle(request.getParameter("riddle"))
            .withHint(request.getParameter("hint1"))
            .withHint(request.getParameter("hint2"))
            .withHint(request.getParameter("hint3"))
            .build();

    List<String> levels = request.getParameterValues("obscurity").stream().filter(level -> level != null);

    List<String> tags = request.getParameterValues("tag").stream().filter(tag -> tag != null);

    Destination d1 = new Destination().Builder()
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

  public List<Tag> convertTagsToEnum(List<String> tags){
    List<Tag> tagEnums;
    for(String tag: tags){
      switch(tag){
        case "art":
          tagEnums.add(Tag.ART);
        case "sport":
          tagEnums.add(Tag.SPORT);
        case "historical":
          tagEnums.add(Tag.HISTORICAL);
        case "food":
          tagEnums.add(Tag.FOOD);
        case "family":
          tagEnums.add(Tag.FAMILY);
        case "tourist":
          tagEnums.add(Tag.TOURIST);
        default:
          tagEnums.add(Tag.UNDEFINED);
      }
    }

    return tagEnums;
  }

  public obscurity convertLevelToEnum(List<String> levels){
    Obscurity obscureLevel;
    for(String level: levels){
      switch(level){
        case "easy":
          obscureLevel = Obscurity.EASY;
        case "medium":
          obscureLevel = Obscurity.MEDIUM;
        case "hard":
          obscureLevel = Obscurity.HARD;
        default:
          obscureLevel = Obscurity.UNDEFINED;
      }
    }
  }
}