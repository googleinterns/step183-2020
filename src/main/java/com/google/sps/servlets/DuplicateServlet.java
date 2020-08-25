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
import com.google.gson.Gson;
import com.google.sps.data.Destination;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Checks for duplicate destinations using their placeId */
@WebServlet("/duplicates")
public class DuplicateServlet extends HttpServlet {
  private static final String DUPLICATE = "duplicate";
  private static final String ORIGINAL = "original";
  private static final String PLACE_ID_URL_PARAMETER = "place-id";

  private static final DatastoreService DATASTORE = DatastoreServiceFactory.getDatastoreService();
  private static final Query QUERY = new Query(Constants.DESTINATION_ENTITY);
  private static final PreparedQuery RESULTS = DATASTORE.prepare(QUERY);

  private static final Gson GSON = new Gson();

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String placeId = request.getParameter(PLACE_ID_URL_PARAMETER);
    ArrayList<Destination> destinations = getDestinationsFromDatastore();
    response.setContentType("text/html");
    if (searchForDuplicate(destinations, placeId)) {
      response.getWriter().println(DUPLICATE);
    } else {
      response.getWriter().println(ORIGINAL);
    }
  }

  /* Query datastore for Destination objects, convert then to Destination class, store in ArrayList. */
  private ArrayList<Destination> getDestinationsFromDatastore() {
    ArrayList<Destination> allDestinations = new ArrayList();
    for (Entity dest : RESULTS.asIterable()) {
      Destination destination =
          GSON.fromJson((String) dest.getProperty(Constants.DESTINATION_JSON), Destination.class);
      allDestinations.add(destination);
    }
    return allDestinations;
  }

  /* Search for duplicate destinations based off of placeId*/
  private boolean searchForDuplicate(ArrayList<Destination> destinations, String placeId) {
    return destinations.stream().anyMatch(dest -> placeId.equals(dest.getPlaceId()));
    /*
    for (Destination destination : destinations) {
      if (placeId.equals(destination.getPlaceId())) {
        return true;
      }
    }
    return false;
    */
  }
}
