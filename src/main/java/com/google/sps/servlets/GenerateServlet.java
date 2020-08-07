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
    HashSet<String> userPlaces = new HashSet();
    HashSet<String> userDifficulties = new HashSet();

    ArrayList<Destination> allDestinations = new ArrayList();
    // ArrayList<Name of Destination>
    ArrayList<String> filteredDestinations = new ArrayList();

    // Get array of clicked filters and convert to ArrayList<String>
    HashSet<String> clickedFilters =
        new Gson().fromJson(request.getParameter(FILTER_ARRAY), HashSet.class);

    allDestinations = createFakeDestinations();
    userPlaces = separatePlaceFilters(clickedFilters);
    userDifficulties = separateDifficultyFilters(clickedFilters);
    filteredDestinations = filterPlaces(userPlaces, allDestinations);
    filteredDestinations =
        filterDifficulty(filteredDestinations, userDifficulties, allDestinations);
    writeToDataStore(filteredDestinations);

    response.setContentType("text/html;");
    response.getWriter().println(clickedFilters);
  }

  /* Separate place filters from rest of user filters. */
  public HashSet<String> separatePlaceFilters(HashSet<String> clickedFilters) {
    HashSet<String> userPlaces = new HashSet();
    for (int i = 0; i < allPlaces.length; i++) {
      if (clickedFilters.contains(allPlaces[i])) {
        userPlaces.add(allPlaces[i]);
      }
    }
    return userPlaces;
  }

  /* Return HashSet of only difficulty filters. */
  public HashSet<String> separateDifficultyFilters(HashSet<String> clickedFilters) {
    HashSet<String> userDifficulties = new HashSet();
    for (int i = 0; i < allDifficulties.length; i++) {
      if (clickedFilters.contains(allDifficulties[i])) {
        userDifficulties.add(allDifficulties[i]);
      }
    }
    return userDifficulties;
  }

  /* Return Destination objects within specified place. */
  public ArrayList<String> filterPlaces(
      HashSet<String> userPlaces, ArrayList<Destination> allDestinations) {
    ArrayList<String> filteredDestinations = new ArrayList();
    // Iterate through destinations
    // If Destination place is in userPlaces array
    // Add Destination to filteredDestinations array
    for (int i = 0; i < allDestinations.size(); i++) {
      Destination currDestination = allDestinations.get(i);
      if (userPlaces.contains(currDestination.getCity())) {
        filteredDestinations.add(currDestination.getName());
      }
    }
    return filteredDestinations;
  }

  /* Return Destination objects with specified difficulty. */
  public ArrayList<String> filterDifficulty(
      ArrayList<String> filteredDestinations,
      HashSet<String> userDifficulties,
      ArrayList<Destination> allDestinations) {
    // Iterate through difficulty levels
    // Create an array of difficulty levels NOT chosen by user
    // If any Destination object in list has any of those levels, delete from filteredDestinations
    HashSet<Destination.Obscurity> diffNotPicked = 
          filterDifficultyHelper(allDifficulties, userDifficulties);
    for (int i = 0; i < allDestinations.size(); i++) {
      Destination currDestination = allDestinations.get(i);
      if (diffNotPicked.contains(currDestination.getDifficulty())) {
        filteredDestinations.remove(currDestination.getName());
      }
    }
    return filteredDestinations;
  }

  /* Create an HashSet of difficulty levels NOT chosen by the user, and conver those to Destination.Obscurity objects. */
  public HashSet<Destination.Obscurity> filterDifficultyHelper(
      String[] allDifficulties, HashSet<String> userDifficulties) {
    HashSet<Destination.Obscurity> diffNotPicked = new HashSet();
    for (int i = 0; i < allDifficulties.length; i++) {
      String currDiff = allDifficulties[i];
      // If difficulty is one selected by user,
      if (!userDifficulties.contains(currDiff)) {
        switch (currDiff) {
          case "Easy":
            diffNotPicked.add(Destination.Obscurity.EASY);
            break;
          case "Medium":
            diffNotPicked.add(Destination.Obscurity.MEDIUM);
            break;
          case "Hard":
            diffNotPicked.add(Destination.Obscurity.HARD);
            break;
        }
      }
    }
    return diffNotPicked;
  }

  /* TODO: Create hunt item / scavenger hunt objects and store in DataStore. */
  public void writeToDataStore(ArrayList<String> filteredDestinations) {
    System.out.println(filteredDestinations);
  }

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