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
import com.google.sps.data.Destination;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns bucket list content */
@WebServlet("/generate-hunt")
public class GenerateServlet extends HttpServlet {

  private static final String PLACE_FILTERS = "user-places";
  private static final String DIFF_FILTERS = "user-diff";

  String[] allPlaces = {"Paris", "New York City", "San Francisco", "London", "Sydney", "Venice"};
  String[] allDifficulties = {"Easy", "Medium", "Hard"};

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    HashSet<String> userPlaces =
        new Gson().fromJson(request.getParameter(PLACE_FILTERS), HashSet.class);
    HashSet<String> userDifficultyStrings =
        new Gson().fromJson(request.getParameter(DIFF_FILTERS), HashSet.class);

    // Create fake destinations TODO: get destinations from datastore
    ArrayList<Destination> allDestinations = createFakeDestinations();

    // Convert difficulty level strings to Destination.Obscurity
    HashSet<Destination.Obscurity> userDifficultyLevels = new HashSet();
    for (String level : userDifficultyStrings) {
      userDifficultyLevels.add(Destination.stringToEnum(level));
    }
    if (userDifficultyStrings.size() == 0) {
      userDifficultyLevels.add(Destination.stringToEnum("Easy"));
      userDifficultyLevels.add(Destination.stringToEnum("Medium"));
      userDifficultyLevels.add(Destination.stringToEnum("Hard"));
    }

    // Filter
    Set<Destination> filteredDestinations =
        filter(allDestinations, userPlaces, userDifficultyLevels);

    writeToDataStore(filteredDestinations);

    response.setContentType("text/html;");
    response.getWriter().println(filteredDestinations);
  }

  /* Return ArrayList<Destination> of filtered Destination objects. */
  public Set<Destination> filter(
      ArrayList<Destination> allDestinations,
      HashSet<String> userPlaces,
      HashSet<Destination.Obscurity> userDifficultyLevels) {
    Set<Destination> filteredDestinations =
        allDestinations.stream()
            .filter(
                destination ->
                    userPlaces.contains(destination.getCity())
                        && userDifficultyLevels.contains(destination.getDifficulty()))
            .collect(Collectors.toSet());
    return filteredDestinations;
  }

  /* TODO: Create hunt item / scavenger hunt objects and store in DataStore. */
  public void writeToDataStore(Set<Destination> filteredDestinations) {
    System.out.println(filteredDestinations);
  }

  /* Temporary function to create fake Destination objects. */
  public ArrayList<Destination> createFakeDestinations() {
    ArrayList<Destination> allDestinations = new ArrayList();
    Destination dest1 =
        new Destination.Builder()
            .withName("Golden Gate")
            .withCity("San Francisco")
            .withObscurity(Destination.Obscurity.EASY)
            .build();

    Destination dest2 =
        new Destination.Builder()
            .withName("Tea Garden")
            .withCity("San Francisco")
            .withObscurity(Destination.Obscurity.MEDIUM)
            .build();

    Destination dest3 =
        new Destination.Builder()
            .withName("Orpheum Theater")
            .withCity("San Francisco")
            .withObscurity(Destination.Obscurity.HARD)
            .build();

    Destination dest4 =
        new Destination.Builder()
            .withName("Louvre")
            .withCity("Paris")
            .withObscurity(Destination.Obscurity.EASY)
            .build();

    Destination dest5 =
        new Destination.Builder()
            .withName("Eiffel Tower")
            .withCity("Paris")
            .withObscurity(Destination.Obscurity.MEDIUM)
            .build();

    Destination dest6 =
        new Destination.Builder()
            .withName("Arc de Triomphe")
            .withCity("Paris")
            .withObscurity(Destination.Obscurity.HARD)
            .build();

    allDestinations.add(dest1);
    allDestinations.add(dest2);
    allDestinations.add(dest3);
    allDestinations.add(dest4);
    allDestinations.add(dest5);
    allDestinations.add(dest6);

    return allDestinations;
  }
}
