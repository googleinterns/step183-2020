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
import com.google.sps.data.Riddle;
import com.google.sps.data.ScavengerHunt;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/go-data")
public class DataServlet extends HttpServlet {
  private static final String JSON_TYPE = "application/json";

  private int index = -1;

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String indexStr = request.getParameter("new-index");
    index = Integer.parseInt(indexStr);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ScavengerHunt hunt = buildScavengerHunt();

    response.setContentType(JSON_TYPE);
    Gson gson = new Gson();
    String json = gson.toJson(hunt);
    response.getWriter().println(json);
  }

  public ScavengerHunt buildScavengerHunt() {
    // Constructing the first HuntItem.
    Riddle firstRiddle = new Riddle.Builder().withPuzzle("I was constructed in 1933")
        .withHint("I am at the periphery of SF").withHint("I am golden in color").build();
    HuntItem firstHunt = new HuntItem.Builder().withName("Golden Gate Bridge")
        .atLocation("San Francisco").withDescription("A famous bridge in San Francisco")
        .withRiddle(firstRiddle).build();

    // Constructing the second HuntItem.
    Riddle secondRiddle = new Riddle.Builder().withPuzzle("A famous tower in Paris")
        .withHint("I am very tall").withHint("I am a popular tourist destination").build();
    HuntItem secondHunt = new HuntItem.Builder().withName("Eiffel Tower").atLocation("Paris")
        .withDescription("A famous tower in Paris").withRiddle(secondRiddle).build();
    
    // Constructing the third HuntItem.
    Riddle thirdRiddle = new Riddle.Builder().withPuzzle("I am often associated with America")
        .withHint("I am a statue").withHint("I am very tall").build();
    HuntItem thirdHunt = new HuntItem.Builder().withName("Statue of Liberty")
        .atLocation("NYC").withDescription("A statue in New York City")
        .withRiddle(thirdRiddle).build();

    // Constructing the scavenger hunt.
    ArrayList<HuntItem> items = new ArrayList<HuntItem>();
    items.add(firstHunt);
    items.add(secondHunt);
    items.add(thirdHunt);
    ScavengerHunt hunt = new ScavengerHunt(items, index, "San Francisco");
    return hunt;
  }
}
