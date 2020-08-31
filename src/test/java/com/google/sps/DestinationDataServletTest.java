package com.google.sps;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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

  private DestinationDataServlet servlet;
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

    LatLng location = new LatLng.Builder().withLat(123.456).withLng(234.567).build();

    Riddle riddle =
        new Riddle.Builder()
            .withPuzzle("Stay away from me if you're afraid of heights")
            .withHint("Overlooks the water")
            .withHint("Golden-red in color")
            .withHint("You have to pay to use me")
            .build();
    String[] returnedTags = {"historical", "tourist"};
    Destination.Obscurity level = Destination.Obscurity.EASY;
    Set<Destination.Tag> tagEnums = new HashSet<Destination.Tag>();
    tagEnums.add(Destination.Tag.HISTORICAL);
    tagEnums.add(Destination.Tag.TOURIST);

    Destination expectedDestination =
        new Destination.Builder()
            .withName("Golden Gate Bridge")
            .withLocation(location)
            .withCity("San Francisco")
            .withDescription("Famous Bridge in SF")
            .withRiddle(riddle)
            .withTags(tagEnums)
            .withObscurity(level)
            .withPlaceId("123")
            .build();
    String expected = GSON.toJson(expectedDestination);
    Entity destinationEntity = new Entity(Constants.DESTINATION_ENTITY);
    destinationEntity.setProperty(Constants.DESTINATION_JSON, expected);
    datastore.put(destinationEntity);

    Assert.assertEquals(
        1, datastore.prepare(new Query(Constants.DESTINATION_ENTITY)).countEntities(withLimit(10)));
  }
}
