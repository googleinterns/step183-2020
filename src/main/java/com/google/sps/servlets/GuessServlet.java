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

import java.io.IOException;
import java.util.ArrayList;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.AnalyzeEntitiesRequest;
import com.google.cloud.language.v1.AnalyzeEntitiesResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.EncodingType;
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

  /** Extracts and returns entities from {@code word} using the Natural Language API. */
  private ArrayList<String> findEntities(String word) throws IOException {
    ArrayList<String> entities = new ArrayList<String>();

    LanguageServiceClient service = LanguageServiceClient.create();
    Document doc = Document.newBuilder().setContent(word.toLowerCase()).setType(Type.PLAIN_TEXT).build();
    AnalyzeEntitiesRequest request = AnalyzeEntitiesRequest.newBuilder().setDocument(doc).setEncodingType(EncodingType.UTF16).build();
    AnalyzeEntitiesResponse response = service.analyzeEntities(request);

    for (Entity entity: response.getEntitiesList()) {
      entities.add(entity.getName());
    }
    return entities;
  }
}
