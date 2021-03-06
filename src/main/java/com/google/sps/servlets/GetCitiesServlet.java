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
import java.util.HashSet;
import java.util.Set;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/get-cities")
public class GetCitiesServlet extends HttpServlet {

  private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Set<String> cities = getCities();

    Gson gson = new Gson();
    String json = gson.toJson(cities);

    response.setContentType("text/html;");
    response.getWriter().println(json);
  }

  public Set<String> getCities() {
    Query query = new Query(Constants.DESTINATION_ENTITY);
    PreparedQuery results = datastore.prepare(query);

    Set<String> cities = new HashSet();
    for (Entity dest : results.asIterable()) {
      Destination destination =
          new Gson()
              .fromJson((String) dest.getProperty(Constants.DESTINATION_JSON), Destination.class);
      cities.add(destination.getCity());
    }
    return cities;
  }
}
