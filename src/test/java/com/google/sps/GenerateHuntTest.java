package com.google.sps.data;

import com.google.sps.servlets.GenerateServlet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class GenerateHuntTest {
  Destination goldenGate =
      new Destination.Builder()
          .withName("Golden Gate")
          .withCity("San Francisco")
          .withObscurity(Destination.Obscurity.EASY)
          .build();

  Destination teaGarden =
      new Destination.Builder()
          .withName("Tea Garden")
          .withCity("San Francisco")
          .withObscurity(Destination.Obscurity.MEDIUM)
          .build();

  Destination orpheumTheater =
      new Destination.Builder()
          .withName("Orpheum Theater")
          .withCity("San Francisco")
          .withObscurity(Destination.Obscurity.HARD)
          .build();

  Destination louvre =
      new Destination.Builder()
          .withName("Louvre")
          .withCity("Paris")
          .withObscurity(Destination.Obscurity.EASY)
          .build();

  Destination eiffelTower =
      new Destination.Builder()
          .withName("Eiffel Tower")
          .withCity("Paris")
          .withObscurity(Destination.Obscurity.MEDIUM)
          .build();

  Destination arcDeTriomphe =
      new Destination.Builder()
          .withName("Arc de Triomphe")
          .withCity("Paris")
          .withObscurity(Destination.Obscurity.HARD)
          .build();

  List<Destination> arrayDest =
      Arrays.asList(goldenGate, teaGarden, orpheumTheater, louvre, eiffelTower, arcDeTriomphe);
  List<Destination> allDestinations = Collections.unmodifiableList(arrayDest);

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
    expected.add(goldenGate);
    expected.add(teaGarden);
    expected.add(orpheumTheater);
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
    expected.add(teaGarden);
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
    expected.add(goldenGate);
    expected.add(louvre);
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
    expected.add(goldenGate);
    expected.add(teaGarden);
    expected.add(orpheumTheater);
    expected.add(louvre);
    expected.add(eiffelTower);
    expected.add(arcDeTriomphe);
    Assert.assertEquals(expected, actual);
  }
}
