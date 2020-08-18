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

import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.EncodingType;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.LanguageServiceClient;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** GuessServlet handles the user's guess for the destination they are trying to find. */
@WebServlet("/guess-data")
public class GuessServlet extends HttpServlet {
  private static final String GUESS_PARAMETER = "guess-input";
  private static final String ANSWER_PARAMETER = "answer";
  private static final String TEXT_TYPE = "text/html";
  
  private LanguageServiceClient getLanguageServiceClientInstance() {
    returns LanguageServiceClient.create();
  }
  // In test, create a mock LanguageServiceClient, and replace this implementation with one
  // that returns that mock object.
  
  // Or (recommended) ......
  
  private final LanguageServiceClient lang;
  public GuessServlet() {
    this.lang = LanguageServiceClient.create();
  }
  
  public GuessServlet(LanguageServiceClient lang) {
    this.lang = lang;
  }
  // In test, create a mock LanguageServiceClient, and construct the object-under-test via
  // this constructor with the mock as the parameter.

  // Or...
  private List<String> getEntities(String word) {
    Document doc =
        Document.newBuilder().setContent(word.toLowerCase()).setType(Type.PLAIN_TEXT).build();
    AnalyzeEntitiesRequest request =
        AnalyzeEntitiesRequest.newBuilder()
            .setDocument(doc)
            .setEncodingType(EncodingType.UTF16)
            .build();
    AnalyzeEntitiesResponse response = lang.analyzeEntities(request);
  }
  
  /** Determines if the user's guess matches the destination location. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userGuess = request.getParameter(GUESS_PARAMETER);
    String answer = request.getParameter(ANSWER_PARAMETER);

    ArrayList<String> userEntities = findEntities(userGuess);
    ArrayList<String> answerEntities = findEntities(answer);

    // User's guess is counted as correct if every entity in the answer
    // is also present in the user's guess.
    userEntities.retainAll(answerEntities);
    boolean result = (userEntities.size() == answerEntities.size());

    response.setContentType(TEXT_TYPE);
    response.getWriter().println(result);
  }
  
  /*
    private static class TestGuessServlet extends GuessServlet {
      @Override ArrayList<String> findEntities(String word) throws IOException {
        return new ArrayList(word.split(" "));
      }
    }
  */

  /** Extracts and returns entities from {@code word} using the Natural Language API. */
  private ArrayList<String> findEntities(String word) throws IOException {
    ArrayList<String> entities = new ArrayList<String>();

    // LanguageServiceClient service = getLanguageServiceClientInstance();
    Document doc =
        Document.newBuilder().setContent(word.toLowerCase()).setType(Type.PLAIN_TEXT).build();
    AnalyzeEntitiesRequest request =
        AnalyzeEntitiesRequest.newBuilder()
            .setDocument(doc)
            .setEncodingType(EncodingType.UTF16)
            .build();
    AnalyzeEntitiesResponse response = lang.analyzeEntities(request);
    // Mock version of LanguageServiceClient would be like:
    // LanguageServiceClient mockService = Mockito.mock();
    // List<Entity> mockEntitiesList =
    // when(mockService.analyzeEntities(eq("Golden Gate")).thenReturn(mockGoldenGateEntitiesList);
    // when(mockService.analyzeEntities("Eiffel Tower").thenReturn(mockEiffelEntitiesList);

    for (Entity entity : response.getEntitiesList()) {
      entities.add(entity.getName());
    }
    return entities;
  }
}
