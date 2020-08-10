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
import java.util.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors; 
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns bucket list content */
@WebServlet("/generate-hunt")
public class GenerateServlet extends HttpServlet {

  private static final String FILTER_ARRAY = "clicked-array";

  String[] allPlaces = {"Paris", "New York City", "San Francisco", "London", "Sydney", "Venice"};
  String[] allDifficulties = {"Easy", "Medium", "Hard"};

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get array of clicked filters and convert to ArrayList<String>
    HashSet<String> clickedFilters =
        new Gson().fromJson(request.getParameter(FILTER_ARRAY), HashSet.class);

    // Create fake destinations TODO: get destinations from datastore
    ArrayList<Destination> allDestinations = createFakeDestinations();

    // Create arrays for each category of filter
    HashSet<String> userPlaces = new HashSet(clickedFilters);
    HashSet<String> userDifficultyStrings = new HashSet(clickedFilters);

    // Retain only the filters that user chose (for each category)
    userPlaces.retainAll(Arrays.asList(allPlaces));
    userDifficultyStrings.retainAll(Arrays.asList(allDifficulties));

    // Convert difficulty level strings to Destination.Obscurity
    HashSet<Destination.Obscurity> userDifficultyLevels = 
        convertStringsToEnum(userDifficultyStrings);

    // Filter
    Set<Destination> filteredDestinations = 
        filter(allDestinations, userPlaces, userDifficultyLevels);

    writeToDataStore(filteredDestinations);

    response.setContentType("text/html;");
    response.getWriter().println(clickedFilters);
  }

  /* Takes strings of difficulty level and converts into Destination.Obscurity objects. */
  public HashSet<Destination.Obscurity> convertStringsToEnum(
      HashSet<String> userDifficultyStrings) {
    HashSet<Destination.Obscurity> userDifficultyLevels = new HashSet();
    if (userDifficultyStrings.isEmpty()) {
      userDifficultyLevels.add(Destination.Obscurity.EASY);
      userDifficultyLevels.add(Destination.Obscurity.MEDIUM);
      userDifficultyLevels.add(Destination.Obscurity.HARD);
    } else {
      for (String userLevel : userDifficultyStrings) {
        switch (userLevel) {
          case "Easy":
            userDifficultyLevels.add(Destination.Obscurity.EASY);
            break;
          case "Medium":
            userDifficultyLevels.add(Destination.Obscurity.MEDIUM);
            break;
          case "Hard":
            userDifficultyLevels.add(Destination.Obscurity.HARD);
            break;
        }
      }
    }
    return userDifficultyLevels;
  }

  public Set<Destination> filter(
      ArrayList<Destination> allDestinations, 
      HashSet<String> userPlaces, 
      HashSet<Destination.Obscurity> userDifficultyLevels) {
    // Filter by place
    Set<Destination> filteredDestinations =
        allDestinations.stream()
      .filter(destination->userPlaces
      .contains(destination.getCity()))
      .map(destination->destination)
      .collect(Collectors.toSet());
    
    // Filter by difficulty
    filteredDestinations = 
        filteredDestinations.stream()
      .filter(destination->userDifficultyLevels
      .contains(destination.getDifficulty()))
      .map(destination->destination)
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
