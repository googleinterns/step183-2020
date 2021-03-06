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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.google.sps.data.Destination;
import com.google.sps.data.HuntItem;
import com.google.sps.data.LatLng;
import com.google.sps.data.ScavengerHunt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/generate-hunt")
public class GenerateServlet extends HttpServlet {

  private static final String PLACE_FILTERS = "user-places";
  private static final String DIFF_FILTERS = "user-diff";
  private static final String NUM_PLACES = "user-num-stops";
  private static final String TAG_FILTERS = "user-tags";
  private static final String ERROR = "Error";
  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    HashSet<String> userPlaces =
        new Gson().fromJson(request.getParameter(PLACE_FILTERS), HashSet.class);
    HashSet<String> userDifficultyStrings =
        new Gson().fromJson(request.getParameter(DIFF_FILTERS), HashSet.class);
    String numPlacesString = request.getParameter(NUM_PLACES);
    int numPlaces = Integer.parseInt(numPlacesString);
    HashSet<String> userTagStrings =
        new Gson().fromJson(request.getParameter(TAG_FILTERS), HashSet.class);

    // Get destinations from datastore
    ArrayList<Destination> allDestinations = getDestinationsFromDatastore();

    // Convert difficulty level strings to Destination.Obscurity
    HashSet<Destination.Obscurity> userDifficultyLevels = new HashSet();
    for (String level : userDifficultyStrings) {
      Destination.Obscurity obscurity = Destination.stringToObscurity(level);
      if (obscurity != Destination.Obscurity.UNDEFINED) {
        userDifficultyLevels.add(obscurity);
      }
    }

    // Convert tag strings to Destination.Tag
    HashSet<Destination.Tag> userTags = new HashSet();
    for (String stringTag : userTagStrings) {
      Destination.Tag tag = Destination.stringToTag(stringTag);
      if (tag != Destination.Tag.UNDEFINED) {
        userTags.add(tag);
      }
    }

    // Filter
    Set<Destination> filteredDestinations =
        filter(allDestinations, userPlaces, userDifficultyLevels, userTags);

    // If there are enough destinations to return, pick random ones to put in Hunt
    if (filteredDestinations.size() >= numPlaces) {
      // Get numPlaces number of Destinations
      filteredDestinations = correctNumDests(filteredDestinations, numPlaces);

      // Convert Destinations to Hunt Items, create Scavenger Hunt, store in Datastore
      ArrayList<HuntItem> huntItems = convertToHuntItems(filteredDestinations);
      ScavengerHunt scavHunt = new ScavengerHunt(huntItems);
      long huntId = writeToDataStore(scavHunt);

      // Set response: scavenger hunt id
      response.setContentType("text/html;");
      response.getWriter().println(huntId);
    } else {
      // If there are not enough destinations, return an error
      response.setContentType("text/html");
      response.getWriter().println(ERROR);
    }
  }

  /* Query datastore for Destination objects, convert then to Destination class, store in ArrayList. */
  public ArrayList<Destination> getDestinationsFromDatastore() {
    Query query = new Query(Constants.DESTINATION_ENTITY);
    PreparedQuery results = datastore.prepare(query);

    ArrayList<Destination> allDestinations = new ArrayList();
    for (Entity dest : results.asIterable()) {
      Destination destination =
          new Gson()
              .fromJson((String) dest.getProperty(Constants.DESTINATION_JSON), Destination.class);
      allDestinations.add(destination);
    }
    return allDestinations;
  }

  /* Return all Destination objects that match the filters. */
  public Set<Destination> filter(
      List<Destination> allDestinations,
      Set<String> userPlaces,
      Set<Destination.Obscurity> userDifficultyLevels,
      Set<Destination.Tag> userTags) {
    Set<Destination> filteredDestinations =
        allDestinations.stream()
            .filter(
                destination ->
                    userPlaces.contains(destination.getCity())
                        && userDifficultyLevels.contains(destination.getDifficulty())
                        && (userTags.isEmpty() || destination.hasAtLeastOneCommonTag(userTags)))
            .collect(Collectors.toSet());
    return filteredDestinations;
  }

  /* Chooses a random subset of the filtered Destinations with size numPlaces. */
  public Set<Destination> correctNumDests(Set<Destination> filteredDestinations, int numPlaces) {
    if (filteredDestinations.size() == numPlaces) {
      return filteredDestinations;
    }
    Set<Destination> newFilteredDests = new HashSet<Destination>();
    List<Destination> filteredList = new ArrayList<Destination>(filteredDestinations);
    Integer[] indexArr = new Integer[filteredDestinations.size()];
    for (int i = 0; i < indexArr.length; i++) {
      indexArr[i] = i;
    }
    Collections.shuffle(Arrays.asList(indexArr));
    for (int i = 0; i < numPlaces; i++) {
      newFilteredDests.add(filteredList.get(indexArr[i]));
    }
    return newFilteredDests;
  }

  /* Convert all Destinations in set to Hunt Items, and organize by Longitude. */
  public ArrayList<HuntItem> convertToHuntItems(Set<Destination> filteredDestinations) {
    TreeMap<Double, Destination> sortDests = new TreeMap<Double, Destination>();
    for (Destination destination : filteredDestinations) {
      LatLng location = destination.getLocation();
      Double lng = location.getLng();
      sortDests.put(lng, destination);
    }

    ArrayList<HuntItem> filteredHuntItems = new ArrayList();
    for (Destination destination : sortDests.values()) {
      HuntItem item = destination.convertToHuntItem();
      filteredHuntItems.add(item);
    }
    return filteredHuntItems;
  }

  /* Store Scavenger Hunt object in Datastore, return Id of created Hunt. */
  public long writeToDataStore(ScavengerHunt scavHunt) {
    String jsonScavHunt = new Gson().toJson(scavHunt);
    Text scavHuntText = new Text(jsonScavHunt);
    Entity scavHuntEntity = new Entity(Constants.SCAVENGER_HUNT_ENTITY);
    scavHuntEntity.setProperty(Constants.SCAVENGER_HUNT_ENTITY, scavHuntText);
    datastore.put(scavHuntEntity);

    return scavHuntEntity.getKey().getId();
  }
}
