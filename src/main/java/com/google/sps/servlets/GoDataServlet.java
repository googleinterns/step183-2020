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

  // Placing fake data into Datastore for testing purposes.
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
    String indexStr = request.getParameter(INDEX_PARAMETER);
    try {
      Entity scavengerHunt = findScavengerHunt();
      scavengerHunt.setProperty(SCAVENGER_INDEX, Integer.parseInt(indexStr));
      datastore.put(scavengerHunt);
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
    Entity entityHunt = findScavengerHunt();

    // retrieve destinations on the scavenger hunt
    ArrayList<HuntItem> items = new ArrayList<HuntItem>();
    for (int i = 1; i <= ((Long) entityHunt.getProperty(SCAVENGER_NUMITEMS)).intValue(); i++) {
      findHuntItem((String) entityHunt.getProperty("Item" + i), items);
    }

    //create ScavengerHunt object
    ScavengerHunt hunt = new ScavengerHunt(items, ((Long) entityHunt.getProperty(SCAVENGER_INDEX)).intValue(),"San Francisco");

    response.setContentType(Constants.JSON_TYPE);
    Gson gson = new Gson();
    String json = gson.toJson(hunt);
    response.getWriter().println(json);
  }

  /**
   * Retrieves the scavenger hunt from Datastore.
   * Currently assumes that there is only one scavenger hunt in Datastore at all times.
   */
  private Entity findScavengerHunt() {
    Query huntQuery = new Query(SCAVENGER_TYPE);
    PreparedQuery huntResults = datastore.prepare(huntQuery);
    return huntResults.asList(FetchOptions.Builder.withDefaults()).get(0);
  }

  /**
   * Given the key to a hunt item, retrieves the given HuntItem entity from Datastore.
   * Currently assumes that HuntItem contains three fields: (1) key (2) destKey (linking
   * to a Destination object) (3) value (containing riddle as a JSON string).
   */
  private void findHuntItem(String key, ArrayList<HuntItem> items) {
    Query itemQuery = new Query(HUNT_TYPE);
    PreparedQuery itemResults = datastore.prepare(itemQuery);
    for (Entity entity: itemResults.asIterable()) {
      if (key.equals(entity.getKey().toString())) { // Found the correct HuntItem.
        Gson gson = new Gson();
        JsonObject huntObj = new JsonParser().parse((String) entity.getProperty(HUNT_VAL)).getAsJsonObject();
        Riddle riddle = gson.fromJson(gson.toJson(huntObj), Riddle.class);
        HuntItem item = findDestItem((String) entity.getProperty(HUNT_DESTKEY), riddle);
        items.add(item);
        return;
      }
    }
  }

  /**
   * Given the key to a dstination, retrieves the given Destination entity from Datastore.
   * Currently assumes that Destination contains two fields: (1) key (2) value (containing
   * the name, location, and description of the destination).
   */
  private HuntItem findDestItem(String key, Riddle riddle) {
    Query destQuery = new Query(DEST_TYPE);
    PreparedQuery destResults = datastore.prepare(destQuery);
    for (Entity entity: destResults.asIterable()) {
      if (key.equals(entity.getKey().toString())) { // Found the correct Destination.
        JsonObject obj = new JsonParser().parse((String) entity.getProperty(DEST_VAL)).getAsJsonObject();

        // Construct LatLng object.
        JsonObject coordObj = obj.get(DEST_LOCATION).getAsJsonObject();
        LatLng coord = new LatLng.Builder()
            .withLat(coordObj.get(DEST_LAT).getAsDouble())
            .withLng(coordObj.get(DEST_LNG).getAsDouble())
            .build();
        
        // Construct HuntItem object.
        HuntItem huntItem = new HuntItem.Builder()
            .withName(obj.get(DEST_NAME).getAsString())
            .atLocation(coord)
            .withDescription(obj.get(DEST_DESCR).getAsString())
            .withRiddle(riddle)
            .build();
        return huntItem;
      }
    }
    return null;
  }
}
