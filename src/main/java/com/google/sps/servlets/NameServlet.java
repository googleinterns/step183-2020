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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** NameServlet handles the user's guess for the destination they are trying to find. */
@WebServlet("/name-data")
public class NameServlet extends HttpServlet {
  private static final String NAME_PARAMETER = "name-input";
  private static final String TEXT_TYPE = "text/html";
  private static final String MAIN_URL = "/go.html";

  private String name = "";

  /** Receives the user's guess from the form. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    name = request.getParameter(NAME_PARAMETER);

    response.setContentType(TEXT_TYPE);
    response.getWriter().println(name);

    // Redirect back to main page.
    response.sendRedirect(MAIN_URL);
  }

  /** Allows the user's guess to be fetched from /name-data. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String json = new Gson().toJson(name);
    response.setContentType(Constants.JSON_TYPE);
    response.getWriter().println(json);
  }
}
