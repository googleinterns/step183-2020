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
public final class DestinationDataServletTest {
  @Mock private DatastoreService datastore;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  
  private DestinationDataServlet servlet;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    servlet = new DestinationDataServlet(datastore);
    // Mockito.when(datastore.put(Mockito.any())).thenReturn(true);
    
    
    doReturn("Golden gate").when(request).getParameter(NAME_PARAMETER);
    doReturn("123.45").when(request).getParameter(LATITUDE_PARAM);
    doReturn("456.45").when(request).getParameter(LONGITUDE_PARAM);
  }

  // TODO
  private void setMockRequestParameters(String guess, String answer) {
    doReturn(guess).when(request).getParameter(GUESS_PARAMETER);
    doReturn(answer).when(request).getParameter(ANSWER_PARAMETER);
  }

  @Test
  public void fillAllAvailableFields() throws IOException {    
    servlet.doPut(request, response);
    ArgumentCaptor<Entity> = ArgumentCaptor.forClass(Entity.class);
    verify(datastore).put(entityCaptor.capture());
    
    Destination expectedDestination = Destination.builder().setBlahblah...
    String expected = gson.stringify(expectedDestination);
    
    Assert.assertEquals(entityCaptor.getValue().getProperty(PROPERTY_NAME), expected);
    
    verify(response).sendRedirect(HOME_URL);
  }

  @Test
  public void missingName() throws IOException {
    doReturn("").when(request).getParameter(NAME_PARAMETER);
    
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
