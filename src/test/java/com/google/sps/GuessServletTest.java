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

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    servlet = new TestGuessServlet();
  }

  private void setMockRequestParameters(HttpServletRequest request, String guess, String answer) {
    doReturn(guess).when(request).getParameter(GUESS_PARAMETER);
    doReturn(answer).when(request).getParameter(ANSWER_PARAMETER);
  }

  @Test
  public void BasicPositive() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(writer);
    setMockRequestParameters(request, "Golden Gate Bridge", "Golden Gate Bridge");

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "true\n");
  }

  @Test
  public void BasicNegative() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(writer);
    setMockRequestParameters(request, "Golden Gate Bridge", "Eiffel Tower");

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "false\n");
  }

  @Test
  public void ExtraWordInGuess() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(writer);
    String userGuess = "The Golden Gate Bridge";
    String answer = "Golden Gate Bridge";
    setMockRequestParameters(request, userGuess, answer);

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "true\n");
  }

  @Test
  public void MissingWordInGuess() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(writer);
    String userGuess = "Golden Bridge";
    String answer = "Golden Gate Bridge";
    setMockRequestParameters(request, userGuess, answer);

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "false\n");
  }

  @Test
  public void UserGuessInLowerCase() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(writer);
    String userGuess = "golden gate bridge";
    String answer = "Golden Gate Bridge";
    setMockRequestParameters(request, userGuess, answer);

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "true\n");
  }

  @Test
  public void UserGuessInUpperCase() throws IOException {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    Mockito.when(response.getWriter()).thenReturn(writer);
    String userGuess = "GOLDEN GATE BRIDGE";
    String answer = "Golden Gate Bridge";
    setMockRequestParameters(request, userGuess, answer);

    servlet.doGet(request, response);

    Assert.assertEquals(stringWriter.toString(), "true\n");
  }
}
