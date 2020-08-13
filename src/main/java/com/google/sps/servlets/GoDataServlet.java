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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;
import com.google.sps.data.ScavengerHunt;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns a scavenger hunt and updates the current index of the hunt. */
@WebServlet("/go-data")
public class GoDataServlet extends HttpServlet {
  private static final String INDEX_PARAMETER = "new-index";

  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Updates the index of the scavenger hunt (aka the destination that the user currently needs to
   * find).
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Gson gson = new Gson();
    String indexStr = request.getParameter(INDEX_PARAMETER);
    String huntIDStr = request.getParameter(Constants.HUNTID_PARAMETER);
    try {
      int index = Integer.parseInt(indexStr);
      long huntID = Long.parseLong(huntIDStr);
      Entity huntEntity = findScavengerHunt(huntID);
      if (huntEntity == null) {
        return;
      }

      // Update index of scavenger hunt.
      ScavengerHunt hunt =
          gson.fromJson((String) huntEntity.getProperty(Constants.HUNT_VAL), ScavengerHunt.class);
      hunt.updateIndex(index);

      // Convert hunt to JSON string and put into Datastore
      String huntStr = gson.toJson(hunt);
      huntEntity.setProperty(Constants.HUNT_VAL, huntStr);
      datastore.put(huntEntity);
    } catch (Exception e) {
    }

    // Redirect back to main page.
    response.sendRedirect(Constants.GO_URL);
  }

  /** Retrieves scavenger hunt data from Datastore, and sends to /go-data. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String huntIDStr = request.getParameter(Constants.HUNTID_PARAMETER);
    Entity huntEntity = null;
    try {
      long huntID = Long.parseLong(huntIDStr);
      huntEntity = findScavengerHunt(huntID);
    } catch (Exception e) {
    }

    response.setContentType(Constants.JSON_TYPE);
    if (huntEntity == null) {
      String errorMessage = "An error has occurred that prevents a scavenger hunt from being displayed.";
      response.getWriter().println(errorMessage);
    } else {
      String json = (String) huntEntity.getProperty(Constants.HUNT_VAL);
      response.getWriter().println(json);
    }
  }

  /**
   * Retrieves the scavenger hunt from Datastore using {@code huntID}, the ID corresponding to the
   * scavenger hunt that should be retrieved.
   */
  private Entity findScavengerHunt(long huntID) {
    Key key = KeyFactory.createKey(Constants.SCAVENGER_HUNT_ENTITY, huntID);
    try {
      return datastore.get(key);
    } catch (Exception e) {
      return null;
    }
  }
}

