package com.google.sps;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.sps.data.Destination;
import com.google.sps.data.LatLng;
import com.google.sps.data.Riddle;
import com.google.sps.servlets.Constants;
import com.google.sps.servlets.DestinationDataServlet;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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

  private static final String NAME_INPUT = "Golden Gate Bridge";
  private static final String LAT_INPUT = "123.456";
  private static final String LNG_INPUT = "234.567";
  private static final String CITY_INPUT = "San Francisco";
  private static final String DESC_INPUT = "Famous Bridge in SF";
  private static final String RIDDLE_INPUT = "Stay away from me if you're afraid of heights";
  private static final String H1_INPUT = "Overlooks the water";
  private static final String H2_INPUT = "Golden-red in color";
  private static final String H3_INPUT = "You have to pay to use me";
  private static final String[] TAG_INPUT = {"historical", "tourist"};
  private static final String OBSCURITY_INPUT = "easy";
  private static final String PLACEID_INPUT = "123";

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
    doReturn(NAME_INPUT).when(request).getParameter(DestinationDataServlet.NAME_PARAMETER);
    doReturn(LAT_INPUT).when(request).getParameter(DestinationDataServlet.LAT_PARAMETER);
    doReturn(LNG_INPUT).when(request).getParameter(DestinationDataServlet.LNG_PARAMETER);
    doReturn(CITY_INPUT).when(request).getParameter(DestinationDataServlet.CITY_PARAMETER);
    doReturn(DESC_INPUT).when(request).getParameter(DestinationDataServlet.DESCRIPTION_PARAMETER);
    doReturn(RIDDLE_INPUT).when(request).getParameter(DestinationDataServlet.RIDDLE_PARAMETER);
    doReturn(H1_INPUT).when(request).getParameter(DestinationDataServlet.HINT1_PARAMETER);
    doReturn(H2_INPUT).when(request).getParameter(DestinationDataServlet.HINT2_PARAMETER);
    doReturn(H3_INPUT).when(request).getParameter(DestinationDataServlet.HINT3_PARAMETER);
    doReturn(TAG_INPUT).when(request).getParameterValues(DestinationDataServlet.TAG_PARAMETER);
    doReturn(OBSCURITY_INPUT)
        .when(request)
        .getParameter(DestinationDataServlet.OBSCURITY_PARAMETER);
    doReturn(PLACEID_INPUT).when(request).getParameter(DestinationDataServlet.PLACEID_PARAMETER);
    servlet.doPost(request, response);

    Query query = new Query(Constants.DESTINATION_ENTITY);
    PreparedQuery results = datastore.prepare(query);

    Assert.assertEquals(1, results.countEntities());

    Destination destination =
        GSON.fromJson(
            (String) results.asSingleEntity().getProperty(Constants.DESTINATION_JSON),
            Destination.class);

    String actual = GSON.toJson(destination);

    String expected = getExpectedDestination();

    Assert.assertEquals(actual, expected);

    verify(response).sendRedirect(DestinationDataServlet.HOME_URL);
  }

  private String getExpectedDestination() {
    LatLng location = new LatLng.Builder().withLat(123.456).withLng(234.567).build();

    Riddle riddle =
        new Riddle.Builder()
            .withPuzzle(RIDDLE_INPUT)
            .withHint(H1_INPUT)
            .withHint(H2_INPUT)
            .withHint(H3_INPUT)
            .build();
    Destination.Obscurity level = Destination.Obscurity.EASY;
    Set<Destination.Tag> tagEnums = new HashSet<Destination.Tag>();
    tagEnums.add(Destination.Tag.HISTORICAL);
    tagEnums.add(Destination.Tag.TOURIST);

    Destination expectedDestination =
        new Destination.Builder()
            .withName(NAME_INPUT)
            .withLocation(location)
            .withCity(CITY_INPUT)
            .withDescription(DESC_INPUT)
            .withRiddle(riddle)
            .withTags(tagEnums)
            .withObscurity(level)
            .withPlaceId(PLACEID_INPUT)
            .build();
    return GSON.toJson(expectedDestination);
  }
}
