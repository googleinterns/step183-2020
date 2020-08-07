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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** GuessServlet handles the user's guess for the destination they are trying to find. */
@WebServlet("/guess-data")
public class GuessServlet extends HttpServlet {
  private static final String USER_PARAMETER = "user-input";
  private static final String ANSWER_PARAMETER = "answer";
  private static final String TEXT_TYPE = "text/html";

  /** Allows the user's guess to be fetched from /guess-data. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userGuess = request.getParameter(USER_PARAMETER);
    String answer = request.getParameter(ANSWER_PARAMETER);

    boolean result = (userGuess.equals(answer)) ? true : false;

    response.setContentType(TEXT_TYPE);
    response.getWriter().println(result);
  }
}
