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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.sps.data.HuntItem;
import com.google.sps.data.LatLng;
import com.google.sps.data.Riddle;
import com.google.sps.data.ScavengerHunt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Number;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns a fake scavenger hunt and updates the current index of the hunt. */
@WebServlet("/go-data")
public class GoDataServlet extends HttpServlet {
  private static final String INDEX_PARAMETER = "new-index";
  private static final String HUNTID_PARAMETER = "hunt_id";
  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  private static final String DEST_VAL = "Value";
  private static final String DEST_TYPE = "Destination";
  private static final String DEST_LOCATION = "location";
  private static final String DEST_DESCR = "description";
  private static final String DEST_NAME = "name";
  private static final String DEST_LAT = "lat";
  private static final String DEST_LNG = "lng";

  private static final String HUNT_VAL = "Value";
  private static final String HUNT_TYPE = "Hunt Item";
  private static final String HUNT_DESTKEY = "DestKey";

  private static final String SCAVENGER_TYPE = "Scavenger Hunt";
  private static final String SCAVENGER_INDEX = "Index";
  private static final String SCAVENGER_NUMITEMS = "NumItems";

  // Placing fake data into Datastore for testing purposes -- OUTDATED.
  @Override
  public void init() {
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
    Gson gson = new Gson();
    String jsonHunt = gson.toJson(firstHunt);
    Entity destination = new Entity(DEST_TYPE);
    destination.setProperty(DEST_VAL, jsonHunt);
    //datastore.put(destination);
 
    Entity huntItem = new Entity(HUNT_TYPE);
    huntItem.setProperty(HUNT_DESTKEY, "Destination(5629499534213120)");
    String jsonRiddle = gson.toJson(firstRiddle);
    huntItem.setProperty(HUNT_VAL, jsonRiddle);
    //datastore.put(huntItem);
 
    Entity hunt = new Entity(SCAVENGER_TYPE);
    hunt.setProperty("Item1", "Hunt Item(6473924464345088)");
    hunt.setProperty(SCAVENGER_INDEX, -1);
    hunt.setProperty(SCAVENGER_NUMITEMS, 1);
    //datastore.put(hunt);
  }

  /**
   * Updates the index of the scavenger hunt (aka the destination that the user currently needs to
   * find.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    String indexStr = request.getParameter(INDEX_PARAMETER);
    String huntID = request.getParameter(HUNTID_PARAMETER);
    try {
      int index = Integer.parseInt(indexStr);
      Entity huntEntity = findScavengerHunt(huntID);

      // Update index of scavenger hunt.
      ScavengerHunt hunt = gson.fromJson((String) huntEntity.getProperty("Value"), ScavengerHunt.class);
      hunt.updateIndex(index);

      // Convert hunt to JSON string and put into Datastore
      String huntStr = gson.toJson(hunt);
      huntEntity.setProperty("Value", huntStr);
      datastore.put(huntEntity);
    } catch (Exception e) {
    }

    // Redirect back to main page.
    response.sendRedirect(Constants.GO_URL);
  }

  /**
   * Retrieves scavenger hunt data from Datastore, and sends to /go-data.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String huntID = request.getParameter(HUNTID_PARAMETER);
    Entity entityHunt = findScavengerHunt(huntID);

    Gson gson = new Gson();
    ScavengerHunt hunt = gson.fromJson((String) entityHunt.getProperty("Value"), ScavengerHunt.class);

    response.setContentType(Constants.JSON_TYPE);
    String json = gson.toJson(hunt);
    response.getWriter().println(json);
  }

  /**
   * Retrieves the scavenger hunt from Datastore.
   * Currently assumes that there is only one scavenger hunt in Datastore at all times.
   */
  private Entity findScavengerHunt(String huntID) {
    Query huntQuery = new Query(SCAVENGER_TYPE);
    PreparedQuery huntResults = datastore.prepare(huntQuery);
    for (Entity entity: huntResults.asIterable()) {
      if (huntID.equals(entity.getKey().toString())) { // Found the correct scavenger hunt.
        return entity;
      }
    }
    return null;
  }
}
