package com.google.sps.data;

import com.google.sps.servlets.GenerateServlet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class GenerateHuntTest {
  ArrayList<Destination> allDestinations = new ArrayList();
  Destination dest1 =
    new Destination.Builder()
        .withName("Golden Gate")
        .withCity("San Francisco")
        .withObscurity(Destination.Obscurity.EASY)
        .build();

  Destination dest2 =
    new Destination.Builder()
        .withName("Tea Garden")
        .withCity("San Francisco")
        .withObscurity(Destination.Obscurity.MEDIUM)
        .build();

  Destination dest3 =
    new Destination.Builder()
        .withName("Orpheum Theater")
        .withCity("San Francisco")
        .withObscurity(Destination.Obscurity.HARD)
        .build();

  Destination dest4 =
    new Destination.Builder()
        .withName("Louvre")
        .withCity("Paris")
        .withObscurity(Destination.Obscurity.EASY)
        .build();

  Destination dest5 =
    new Destination.Builder()
        .withName("Eiffel Tower")
        .withCity("Paris")
        .withObscurity(Destination.Obscurity.MEDIUM)
        .build();

  Destination dest6 =
    new Destination.Builder()
        .withName("Arc de Triomphe")
        .withCity("Paris")
        .withObscurity(Destination.Obscurity.HARD)
        .build();

  @Before
  public void setUp() {
    allDestinations.add(dest1);
    allDestinations.add(dest2);
    allDestinations.add(dest3);
    allDestinations.add(dest4);
    allDestinations.add(dest5);
    allDestinations.add(dest6);
  }

  @Test
  /* Get all objects with San Francisco as the place, and all difficulties. */
  public void SanFrancisco() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);
    userDiff.add(Destination.Obscurity.MEDIUM);
    userDiff.add(Destination.Obscurity.HARD);

    GenerateServlet generate = new GenerateServlet();
    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff);
    
    // Should return first 3 destinations
    Set<Destination> expected = new HashSet();
    expected.add(dest1);
    expected.add(dest2);
    expected.add(dest3);
    Assert.assertEquals(expected, actual);
  }

  @Test
  /* Get all objects with San Francisco as the place and medium difficulty. */
  public void SanFranciscoMedium() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.MEDIUM);

    GenerateServlet generate = new GenerateServlet();
    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff);
    
    // Should return only 2nd destination
    Set<Destination> expected = new HashSet();
    expected.add(dest2);

    Assert.assertEquals(expected, actual);
  }

  @Test
  /* Get all objects in SF and Paris, that are easy difficulty. */
  public void SanFranciscoParisEasy() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    userPlaces.add("Paris");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);

    GenerateServlet generate = new GenerateServlet();
    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff);

    // Should return objects 1 and 4
    Set<Destination> expected = new HashSet();
    expected.add(dest1);
    expected.add(dest4);

    Assert.assertEquals(expected, actual);
  }

  @Test
  /* Press all filters. */
  public void allFiltersAllowed() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    userPlaces.add("Paris");
    userPlaces.add("New York City");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);
    userDiff.add(Destination.Obscurity.MEDIUM);
    userDiff.add(Destination.Obscurity.HARD);

    GenerateServlet generate = new GenerateServlet();
    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff);
    
    // Should return all Destinations
    Set<Destination> expected = new HashSet();
    expected.add(dest1);
    expected.add(dest2);
    expected.add(dest3);
    expected.add(dest4);
    expected.add(dest5);
    expected.add(dest6);
    Assert.assertEquals(expected, actual);
  }
}
