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
import com.google.sps.data.Riddle;
import com.google.sps.data.HuntItem;
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

  private ScavengerHunt hunt;

  @Override
  public void init() {
    // Constructing the first HuntItem.
    ArrayList<String> firstHints = new ArrayList<String>();
    firstHints.add("I am at the periphery of SF");
    firstHints.add("I am golden in color");
    Riddle firstRiddle = new Riddle("I was constructed in 1933", firstHints);
    HuntItem firstHunt = 
        new HuntItem(
            "Golden Gate Bridge", "San Francisco", "A famous bridge in San Francisco", firstRiddle);

    // Constructing the second HuntItem.
    ArrayList<String> secondHints = new ArrayList<String>();
    secondHints.add("I am very tall");
    secondHints.add("I am a popular tourist destination");
    Riddle secondRiddle = new Riddle("A famous tower in Paris", secondHints);
    HuntItem secondHunt = 
        new HuntItem("Eiffel Tower", "Paris", "A famous tower in Paris", secondRiddle);

    // Constructing the third HuntItem.
    ArrayList<String> thirdHints = new ArrayList<String>();
    thirdHints.add("I am a statue");
    thirdHints.add("I am very tall");
    Riddle thirdRiddle = new Riddle("I am an item often associated with America", thirdHints);
    HuntItem thirdHunt = 
        new HuntItem("Statue of Liberty", "NYC", "A statue in New York City", thirdRiddle);
    
    // Constructing the scavenger hunt.
    ArrayList<HuntItem> items = new ArrayList<HuntItem>();
    items.add(firstHunt);
    items.add(secondHunt);
    items.add(thirdHunt);
    hunt = new ScavengerHunt(items, 0, "San Francisco");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType(JSON_TYPE);
    Gson gson = new Gson();
    String json = gson.toJson(hunt);
    response.getWriter().println(json);
  }
}
