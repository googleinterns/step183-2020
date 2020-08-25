package com.google.sps;

import static org.mockito.Mockito.doReturn;

import com.google.sps.servlets.TestGuessServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public final class GuessServletTest {
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  private static final String GUESS_PARAMETER = "guess-input";
  private static final String ANSWER_PARAMETER = "answer";

  private TestGuessServlet servlet;
  private StringWriter stringWriter;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    servlet = new TestGuessServlet();

    stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(writer);
  }

  private void setMockRequestParameters(String guess, String answer) {
    doReturn(guess).when(request).getParameter(GUESS_PARAMETER);
    doReturn(answer).when(request).getParameter(ANSWER_PARAMETER);
  }

  @Test
  public void BasicPositive() throws IOException {
    setMockRequestParameters("Golden Gate Bridge", "Golden Gate Bridge");

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "true\n");
  }

  @Test
  public void BasicNegative() throws IOException {
    setMockRequestParameters("Golden Gate Bridge", "Eiffel Tower");

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "false\n");
  }

  @Test
  public void ExtraWordInGuess() throws IOException {
    String userGuess = "The Golden Gate Bridge";
    String answer = "Golden Gate Bridge";
    setMockRequestParameters(userGuess, answer);

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "true\n");
  }

  @Test
  public void MissingWordInGuess() throws IOException {
    String userGuess = "Golden Bridge";
    String answer = "Golden Gate Bridge";
    setMockRequestParameters(userGuess, answer);

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "false\n");
  }

  @Test
  public void UserGuessInLowerCase() throws IOException {
    String userGuess = "golden gate bridge";
    String answer = "Golden Gate Bridge";
    setMockRequestParameters(userGuess, answer);

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "true\n");
  }

  @Test
  public void UserGuessInUpperCase() throws IOException {
    String userGuess = "GOLDEN GATE BRIDGE";
    String answer = "Golden Gate Bridge";
    setMockRequestParameters(userGuess, answer);

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "true\n");
  }
}
