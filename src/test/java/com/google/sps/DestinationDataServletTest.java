package com.google.sps;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.sps.servlets.Constants;
import com.google.sps.servlets.DestinationDataServlet;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public final class DestinationDataServletTest {
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;

  private DestinationDataServlet servlet;

  private static final String NAME_PARAMETER = "name";
  private static final String LAT_PARAMETER = "latitude";
  private static final String LNG_PARAMETER = "longitude";
  private static final String CITY_PARAMETER = "city";
  private static final String DESCRIPTION_PARAMETER = "description";
  private static final String RIDDLE_PARAMETER = "riddle";
  private static final String HINT1_PARAMETER = "hint1";
  private static final String HINT2_PARAMETER = "hint2";
  private static final String HINT3_PARAMETER = "hint3";
  private static final String OBSCURITY_PARAMETER = "obscurity";
  private static final String TAG_PARAMETER = "tag";
  private static final String PLACEID_PARAMETER = "placeId";
  private static final String HOME_URL = "/index.html";

  private static final Gson GSON = new Gson();

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void storeInDatastore() throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    servlet = new DestinationDataServlet(datastore);
    doReturn("Golden Gate Bridge").when(request).getParameter(NAME_PARAMETER);
    doReturn("123.456").when(request).getParameter(LAT_PARAMETER);
    doReturn("234.567").when(request).getParameter(LNG_PARAMETER);
    doReturn("San Francisco").when(request).getParameter(CITY_PARAMETER);
    doReturn("Famous Bridge in SF").when(request).getParameter(DESCRIPTION_PARAMETER);
    doReturn("Stay away from me if you're afraid of heights")
        .when(request)
        .getParameter(RIDDLE_PARAMETER);
    doReturn("Overlooks the water").when(request).getParameter(HINT1_PARAMETER);
    doReturn("Golden-red in color").when(request).getParameter(HINT2_PARAMETER);
    doReturn("You have to pay to use me").when(request).getParameter(HINT3_PARAMETER);
    String[] returnedTags = {"historical", "tourist"};
    doReturn(returnedTags).when(request).getParameterValues(TAG_PARAMETER);
    doReturn("easy").when(request).getParameter(OBSCURITY_PARAMETER);
    doReturn("123").when(request).getParameter(PLACEID_PARAMETER);
    servlet.doPost(request, response);

    Assert.assertEquals(
        1, datastore.prepare(new Query(Constants.DESTINATION_ENTITY)).countEntities(withLimit(10)));

    verify(response).sendRedirect(HOME_URL);
  }
}
