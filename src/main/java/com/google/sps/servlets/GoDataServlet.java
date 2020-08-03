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
import com.google.sps.data.HuntItem;
import com.google.sps.data.LatLng;
import com.google.sps.data.Riddle;
import com.google.sps.data.ScavengerHunt;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns a fake scavenger hunt and updates the current index of the hunt. */
@WebServlet("/go-data")
public class GoDataServlet extends HttpServlet {
  private static final String NAME_PARAMETER = "name-input";
  private static final String INDEX_PARAMETER = "new-index";
  private static final String MAIN_URL = "/go.html";

  // Keeps track of the current stage in the hunt that the user is on.
  // Before the user begins the hunt, the index should be -1.
  private int index = -1;
  private String name = "";

  /**
   * Updates the index of the scavenger hunt (aka the destination that the user currently needs to
   * find.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String indexStr = request.getParameter(INDEX_PARAMETER);
    try {
      index = Integer.parseInt(indexStr);
    } catch (Exception e) {
    }

    name = request.getParameter(NAME_PARAMETER);
 
    // Redirect back to main page.
    response.sendRedirect(MAIN_URL);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ScavengerHunt hunt = buildScavengerHunt();
 
    response.setContentType(Constants.JSON_TYPE);
    Gson gson = new Gson();
    String json = gson.toJson(hunt);
    response.getWriter().println(json);
  }

  private ScavengerHunt buildScavengerHunt() {
    // Constructing the first HuntItem.
    Riddle firstRiddle =
        new Riddle.Builder()
            .withPuzzle("I was constructed in 1933")
            .withHint("I am at the periphery of SF")
            .withHint("I am golden in color")
            .build();
    LatLng firstCoord = new LatLng.Builder().withLat(37.819).withLng(-122.479).build();
    HuntItem firstHunt =
        new HuntItem.Builder()
            .withName("Golden Gate Bridge")
            .atLocation(firstCoord)
            .withDescription("A famous bridge in San Francisco")
            .withRiddle(firstRiddle)
            .build();

    // Constructing the second HuntItem.
    Riddle secondRiddle =
        new Riddle.Builder()
            .withPuzzle("A famous tower in Paris")
            .withHint("I am very tall")
            .withHint("I am a popular tourist destination")
            .build();
    LatLng secondCoord = new LatLng.Builder().withLat(48.858).withLng(2.295).build();
    HuntItem secondHunt =
        new HuntItem.Builder()
            .withName("Eiffel Tower")
            .atLocation(secondCoord)
            .withDescription("A famous tower in Paris")
            .withRiddle(secondRiddle)
            .build();

    // Constructing the third HuntItem.
    Riddle thirdRiddle =
        new Riddle.Builder()
            .withPuzzle("I am often associated with America")
            .withHint("I am a statue")
            .withHint("I am very tall")
            .build();
    LatLng thirdCoord = new LatLng.Builder().withLat(40.689).withLng(-74.045).build();
    HuntItem thirdHunt =
        new HuntItem.Builder()
            .withName("Statue of Liberty")
            .atLocation(thirdCoord)
            .withDescription("A statue in New York City")
            .withRiddle(thirdRiddle)
            .build();

    // Constructing the scavenger hunt.
    ArrayList<HuntItem> items = new ArrayList<HuntItem>();
    items.add(firstHunt);
    items.add(secondHunt);
    items.add(thirdHunt);
    ScavengerHunt hunt = new ScavengerHunt(items, index, "San Francisco", name);
    return hunt;
  }
}
