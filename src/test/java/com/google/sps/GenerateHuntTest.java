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

// TODO: Write tests for the # destinations input
@RunWith(JUnit4.class)
public final class GenerateHuntTest {
  Set<Destination.Tag> goldenGateSet = new HashSet<>(Arrays.asList(Destination.Tag.HISTORICAL));
  Set<Destination.Tag> teaGardenSet = new HashSet<>(Arrays.asList(Destination.Tag.FOOD));
  Set<Destination.Tag> orpheumTheaterSet =
      new HashSet<>(Arrays.asList(Destination.Tag.HISTORICAL, Destination.Tag.ART));
  Set<Destination.Tag> fishermansWharfSet =
      new HashSet<>(Arrays.asList(Destination.Tag.FOOD, Destination.Tag.TOURIST));
  Set<Destination.Tag> coitTowerSet = new HashSet<>(Arrays.asList(Destination.Tag.TOURIST));
  Set<Destination.Tag> louvreSet =
      new HashSet<>(
          Arrays.asList(Destination.Tag.HISTORICAL, Destination.Tag.ART, Destination.Tag.TOURIST));
  Set<Destination.Tag> eiffelTowerSet =
      new HashSet<>(
          Arrays.asList(
              Destination.Tag.HISTORICAL, Destination.Tag.FAMILY, Destination.Tag.TOURIST));
  Set<Destination.Tag> arcDeTriompheSet =
      new HashSet<>(Arrays.asList(Destination.Tag.HISTORICAL, Destination.Tag.TOURIST));
  Set<Destination.Tag> cathedralSet = new HashSet<>(Arrays.asList(Destination.Tag.HISTORICAL));

  Destination goldenGate =
      new Destination.Builder()
          .withName("Golden Gate")
          .withCity("San Francisco")
          .withObscurity(Destination.Obscurity.EASY)
          .withTags(goldenGateSet)
          .build();

  Destination teaGarden =
      new Destination.Builder()
          .withName("Tea Garden")
          .withCity("San Francisco")
          .withObscurity(Destination.Obscurity.MEDIUM)
          .withTags(teaGardenSet)
          .build();

  Destination orpheumTheater =
      new Destination.Builder()
          .withName("Orpheum Theater")
          .withCity("San Francisco")
          .withObscurity(Destination.Obscurity.HARD)
          .withTags(orpheumTheaterSet)
          .build();

  Destination coitTower =
      new Destination.Builder()
          .withName("COIT Tower")
          .withCity("San Francisco")
          .withObscurity(Destination.Obscurity.EASY)
          .withTags(coitTowerSet)
          .build();

  Destination fishermansWharf =
      new Destination.Builder()
          .withName("Fisherman's Wharf")
          .withCity("San Francisco")
          .withObscurity(Destination.Obscurity.MEDIUM)
          .withTags(fishermansWharfSet)
          .build();

  Destination louvre =
      new Destination.Builder()
          .withName("Louvre")
          .withCity("Paris")
          .withObscurity(Destination.Obscurity.EASY)
          .withTags(louvreSet)
          .build();

  Destination eiffelTower =
      new Destination.Builder()
          .withName("Eiffel Tower")
          .withCity("Paris")
          .withObscurity(Destination.Obscurity.MEDIUM)
          .withTags(eiffelTowerSet)
          .build();

  Destination arcDeTriomphe =
      new Destination.Builder()
          .withName("Arc de Triomphe")
          .withCity("Paris")
          .withObscurity(Destination.Obscurity.HARD)
          .withTags(arcDeTriompheSet)
          .build();

  Destination cathedral =
      new Destination.Builder()
          .withName("Cathedral")
          .withCity("Paris")
          .withObscurity(Destination.Obscurity.EASY)
          .withTags(cathedralSet)
          .build();

  List<Destination> arrayDest =
      Arrays.asList(
          goldenGate,
          teaGarden,
          orpheumTheater,
          coitTower,
          fishermansWharf,
          louvre,
          eiffelTower,
          arcDeTriomphe,
          cathedral);
  List<Destination> allDestinations = Collections.unmodifiableList(arrayDest);

  @Test
  /* SF, all difficulties. */
  public void SanFrancisco() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);
    userDiff.add(Destination.Obscurity.MEDIUM);
    userDiff.add(Destination.Obscurity.HARD);
    // No tags
    HashSet<Destination.Tag> userTags = new HashSet();
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff, userTags);

    // Should return golden gate, orpheum theater, tea garden, coit tower, fishermans wharf
    Set<Destination> expected = new HashSet();
    expected.add(goldenGate);
    expected.add(teaGarden);
    expected.add(orpheumTheater);
    expected.add(coitTower);
    expected.add(fishermansWharf);
    Assert.assertEquals(expected, actual);
  }

  @Test
  /* SF, medium difficulty. */
  public void SanFranciscoMedium() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.MEDIUM);
    // No tags
    HashSet<Destination.Tag> userTags = new HashSet();
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff, userTags);

    // Should return tea garden, fishermans wharf
    Set<Destination> expected = new HashSet();
    expected.add(teaGarden);
    expected.add(fishermansWharf);
    Assert.assertEquals(expected, actual);
  }

  @Test
  /* SF and Paris, easy difficulty. */
  public void SanFranciscoParisEasy() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    userPlaces.add("Paris");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);
    // No tags
    HashSet<Destination.Tag> userTags = new HashSet();
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff, userTags);

    // Should return golden gate, coit tower, louvre, cathedral
    Set<Destination> expected = new HashSet();
    expected.add(goldenGate);
    expected.add(coitTower);
    expected.add(louvre);
    expected.add(cathedral);
    Assert.assertEquals(expected, actual);
  }

  @Test
  /* All places and difficulties. */
  public void allFiltersAllowed() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    userPlaces.add("Paris");
    userPlaces.add("New York City");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);
    userDiff.add(Destination.Obscurity.MEDIUM);
    userDiff.add(Destination.Obscurity.HARD);
    // No tags
    HashSet<Destination.Tag> userTags = new HashSet();
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff, userTags);

    // Should return all Destinations
    Set<Destination> expected = new HashSet();
    expected.add(goldenGate);
    expected.add(teaGarden);
    expected.add(orpheumTheater);
    expected.add(coitTower);
    expected.add(fishermansWharf);
    expected.add(louvre);
    expected.add(eiffelTower);
    expected.add(arcDeTriomphe);
    expected.add(cathedral);
    Assert.assertEquals(expected, actual);
  }

  @Test
  /* SF, easy difficulty, and Tag Tourist. */
  public void sanFranciscoEasyTourist() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);
    HashSet<Destination.Tag> userTags = new HashSet();
    userTags.add(Destination.Tag.TOURIST);
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff, userTags);

    // Should return coit tower
    Set<Destination> expected = new HashSet();
    expected.add(coitTower);
    Assert.assertEquals(expected, actual);
  }

  @Test
  /* SF, no difficulty, Tag Historical. */
  public void sanFranciscoHistorical() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);
    userDiff.add(Destination.Obscurity.MEDIUM);
    userDiff.add(Destination.Obscurity.HARD);
    HashSet<Destination.Tag> userTags = new HashSet();
    userTags.add(Destination.Tag.HISTORICAL);
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff, userTags);

    // Should return golden gate, orpheum theater
    Set<Destination> expected = new HashSet();
    expected.add(goldenGate);
    expected.add(orpheumTheater);
    Assert.assertEquals(expected, actual);
  }

  @Test 
  /* Paris, easy difficulty, Tag Family. */
  public void parisEasyFamily() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("Paris");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);
    HashSet<Destination.Tag> userTags = new HashSet();
    userTags.add(Destination.Tag.FAMILY);
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff, userTags);

    // Should return nothing
    Set<Destination> expected = new HashSet();
    Assert.assertEquals(expected, actual);
  }

  /* Sf, no difficulty, Tag Historical and Food. */
  public void multipleTags() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);
    userDiff.add(Destination.Obscurity.MEDIUM);
    userDiff.add(Destination.Obscurity.HARD);
    HashSet<Destination.Tag> userTags = new HashSet();
    userTags.add(Destination.Tag.HISTORICAL);
    userTags.add(Destination.Tag.FOOD);
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff, userTags);

    // Should return golden gate, tea garden, orpheum theater, fishermans wharf
    Set<Destination> expected = new HashSet();
    expected.add(goldenGate);
    expected.add(teaGarden);
    expected.add(orpheumTheater);
    expected.add(fishermansWharf);
    Assert.assertEquals(expected, actual);
  }

  @Test
  /* All places, tags, and difficulties. */
  public void sanFranciscoAllTags() {
    HashSet<String> userPlaces = new HashSet();
    userPlaces.add("San Francisco");
    HashSet<Destination.Obscurity> userDiff = new HashSet();
    userDiff.add(Destination.Obscurity.EASY);
    userDiff.add(Destination.Obscurity.MEDIUM);
    userDiff.add(Destination.Obscurity.HARD);
    HashSet<Destination.Tag> userTags = new HashSet();
    userTags.add(Destination.Tag.FOOD);
    userTags.add(Destination.Tag.HISTORICAL);
    userTags.add(Destination.Tag.ART);
    userTags.add(Destination.Tag.FAMILY);
    userTags.add(Destination.Tag.TOURIST);
    userTags.add(Destination.Tag.SPORT);
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> actual = generate.filter(allDestinations, userPlaces, userDiff, userTags);

    // Should return golden gate, tea garden, orpheum, coit tower, fishermans wharf
    Set<Destination> expected = new HashSet();
    expected.add(goldenGate);
    expected.add(teaGarden);
    expected.add(orpheumTheater);
    expected.add(coitTower);
    expected.add(fishermansWharf);
    Assert.assertEquals(expected, actual);
  }

  @Test 
  /* Get 3 random destinations. */
  public void threeDests() {
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> allDestinationsSet = new HashSet<Destination>(allDestinations);
    Set<Destination> actualSet = generate.correctNumDests(allDestinationsSet, 3);

    int actual = actualSet.size();
    int expected = 3;
    Assert.assertEquals(expected, actual);
  }

  @Test 
  /* 5 random destinations. */
  public void fiveDests() {
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> allDestinationsSet = new HashSet<Destination>(allDestinations);
    Set<Destination> actualSet = generate.correctNumDests(allDestinationsSet, 5);

    int actual = actualSet.size();
    int expected = 5;
    Assert.assertEquals(expected, actual);
  }

  @Test
  /* 0 random destinations. */
  public void noDests() {
    GenerateServlet generate = new GenerateServlet();

    Set<Destination> allDestinationsSet = new HashSet<Destination>(allDestinations);
    Set<Destination> actualSet = generate.correctNumDests(allDestinationsSet, 0);

    int actual = actualSet.size();
    int expected = 0;
    Assert.assertEquals(expected, actual);
  }
}
