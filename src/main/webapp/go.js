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

// Button IDs.
const HINT_BUTTON = 'hint-button';
const START_BUTTON = 'start-button';
const PROCEED_BUTTON = 'proceed-button';
const CLEAR_BUTTON = 'clear-hint-button';
const SUBMIT_BUTTON = 'submit-button';
const GENERATE_BUTTON = 'generate-button';

// URLs that data should be fetched from.
const DATA_URL = '/go-data';
const GUESS_URL = '/guess-data';
const HOME_URL = 'index.html';

// Div IDs that text or a map should be inserted into.
const HINT_DISPLAY = 'hint-area';
const RIDDLE_DISPLAY = 'riddle-area';
const RIDDLE_BOX = 'riddle-container';
const SUBMIT_DISPLAY = 'response-area';
const SUBMIT_BOX = 'submit-container';
const MAP_DISPLAY = 'map-area';
const MAP_MSSG_DISPLAY = 'map-message-area';
const TIMER_DISPLAY = 'timer-area';
const PROGRESS_DISPLAY = 'progress-bar';
const GENERATE_DISPLAY = 'generate-area';

// Input IDs or parameters.
const GUESS_INPUT = 'guess-input';
const INDEX_PARAM = 'new-index';
const HUNT_PARAM = 'hunt_id';

// Hard-coded messages to be displayed to the user.
const PROCEED_FINAL_MSSG = 'Finish the Hunt';
const CORRECT_MSSG = 'Correct!';
const WRONG_MSSG = 'Wrong. Try again!';
const EXIT_MSSG = 'Do you want to exit this scavenger hunt?' +
    ' If so, make sure to bookmark this URL so you can return' +
    ' to this page later.';
const NO_PLACEID_MSSG = 'No destination found.';
const NO_PLACE_INFO_MSSG = 'No information found for this location.';

// Interval durations.
const MAP_INTERVAL_MS = 10000; // ten seconds
const HIDE_INTERVAL_MS = 5000; // five seconds
// one second
// Disabled lint check because TIMER_INTERVAL_MS is used
// in scavengerHuntManager.js.
const TIMER_INTERVAL_MS = 1000; // eslint-disable-line

// Other constants.
const INVISIBLE_CLASS = 'invisible';

// Global variables.
let hunt;
let map;
let hintClock;
let huntID;
let service;

window.onload = function() {
  addScriptToHead();
  getHunt();
};

/**
 * Updates the timer with the total number of seconds, minutes,
 * and hours since the user started the scavenger hunt.
 * Disabled lint check because updateTimer() is called in
 * scavengerHuntManager.js.
 */
function updateTimer() { // eslint-disable-line
  const difference = hunt.getTimeElapsed();
  const seconds = difference % (1000 * 60) / 1000;
  const minutes = difference % (1000 * 60 * 60) / (1000 * 60);
  const hours = difference % (1000 * 60 * 60 * 24) /
      (1000 * 60 * 60);
  document.getElementById(TIMER_DISPLAY).innerText =
      'Timer: ' + standardizeTime(hours) + ':' +
      standardizeTime(minutes) + ':' + standardizeTime(seconds);
}

/**
 * Standardize timer display by ensuring that {@code num} is an
 * integer and is two digits long.
 * @param {int} num Number to be standardized.
 * @return {String} standardized number.
 */
function standardizeTime(num) {
  if (num < 10) {
    return '0' + Math.floor(num);
  } else {
    return Math.floor(num);
  }
}

/**
 * Add the Map API key to the head.
 */
function addScriptToHead() {
  const newScript = document.createElement('script');
  newScript.src = 'https://maps.googleapis.com/maps/api/js?key=' +
      config.MAP_KEY + '&libraries=places&callback=createMap';
  document.getElementsByTagName('head')[0].appendChild(newScript);
}

/**
 * Retrieves scavenger hunt data, and updates to the current destination
 * to reflect the current state of the hunt.
 */
function getHunt() {
  // ID of the scavenger hunt that the user is on,
  // in the form of: hunt_id=[ID number here]
  huntID = new URLSearchParams(window.location.search).get(HUNT_PARAM);
  const queryStr = DATA_URL + '?' + HUNT_PARAM + '=' + huntID;
  fetch(queryStr).then((response) => response.json()).then((mssg) => {
    const destIndex = mssg.index;
    const huntArr = [];
    for (let i = 0; i < mssg.items.length; i++) {
      const cur = mssg.items[i];
      huntArr.push(new Destination(cur.name, cur.description,
          cur.riddle.puzzle, cur.riddle.hints, cur.location.lat,
          cur.location.lng));
    }
    hunt = new ScavengerHuntManager(destIndex, 0, huntArr);
    updateToCurrentState();
    window.setInterval(updateGeolocation, MAP_INTERVAL_MS);
    updateGeolocation();
  });
}

/**
 * Add a marker to the map at the specified location.
 * @param {Double} destLat: latitude of location.
 * @param {Double} destLng: longitude of location.
 * @param {String} destName: Name of location.
 */
function addMarkerToMap(destLat, destLng, destName) {
  const coord = new google.maps.LatLng(destLat, destLng);
  const marker = new google.maps.Marker({
    position: coord,
    title: destName,
  });
  marker.setMap(map);
}

/**
 * Creates a map and adds it to the page.
 * Disabled lint check because createMap() is called once
 * addScriptToHead() executes.
 */
function createMap() { // eslint-disable-line
  map = new google.maps.Map(
      document.getElementById(MAP_DISPLAY),
      // Centered at GooglePlex (updated below).
      {center: {lat: 37.422, lng: -122.084}, zoom: 7},
  );
  service = new google.maps.places.PlacesService(map);
}

/**
 * Update the user's current location.
 */
function updateGeolocation() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition((position) => {
      hunt.setPos(position.coords.latitude, position.coords.longitude);
      addMarkerToMap(position.coords.latitude, position.coords.longitude,
          'Your current location');
      map.setCenter(hunt.getPos());
    },
    () => updateMessage(MAP_MSSG_DISPLAY,
        'Error: I can\'t find your location.'));
  } else {
    updateMessage(MAP_MSSG_DISPLAY, 'Error: Your browser doesn\'t' +
        'support geolocation.');
  }
}

/**
 * Show or hide the hint button (as well as the clear hint button).
 * @param {boolean} hide Whether the proceed button should be hidden or shown.
 */
function toggleHintButton(hide) {
  const hintButton = document.getElementById(HINT_BUTTON);
  const clearButton = document.getElementById(CLEAR_BUTTON);
  if (hide) {
    hintButton.classList.add(INVISIBLE_CLASS);
    clearButton.classList.add(INVISIBLE_CLASS);
  } else {
    hintButton.classList.remove(INVISIBLE_CLASS);
    clearButton.classList.remove(INVISIBLE_CLASS);
  }
}

/**
 * Allow the user to see a hint.
 * Disable lint check because getHint() is called from go.html.
 */
function getHint() { //eslint-disable-line
  const nextHint = hunt.getNextHint();
  if (nextHint != '') {
    updateMessage(HINT_DISPLAY, 'Hint #' + (hunt.getHintIndex()) +
        ': ' + nextHint);
  } else {
    getAutoHint();
  }
}

/**
 * Generates a place_id near the midpoint between the user's
 * position and the destination.
 * Disable lint check because displayNearbyPlace() is called from go.html.
 */
function displayNearbyPlace() { //eslint-disable-line
  // Midpoint coordinates between the user's current position and
  // the current destination.
  const midpoint = new google.maps.LatLng(
      (hunt.getCurDestLat() + hunt.getPos().lat) / 2,
      (hunt.getCurDestLng() + hunt.getPos().lng) / 2);

  const request = {
    location: midpoint,
    radius: '5000', // in meters
    types: ['tourist_attraction'],
  };

  service.nearbySearch(request, function(results, status) {
    if (status === google.maps.places.PlacesServiceStatus.OK &&
        results.length > 0) {
      displayPlaceInfo(results[0].place_id);
    } else {
      updateMessage(GENERATE_DISPLAY, NO_PLACEID_MSSG);
    }
  });
}

/**
 * Finds and displays a name and photo for the specified destination.
 * @param {int} id Place ID of the destination to be found.
 */
function displayPlaceInfo(id) {
  const request = {
    placeId: id,
    fields: ['name', 'photo'],
  };
  service.getDetails(request, (place, status) => {
    if (status == google.maps.places.PlacesServiceStatus.OK &&
        place.photos.length > 0) {
      updateMessage(GENERATE_DISPLAY, place.name);
      updatePhoto(GENERATE_DISPLAY, place.photos[0]);
    } else {
      updateMessage(GENERATE_DISPLAY, NO_PLACE_INFO_MSSG);
    }
  });
}

/**
 * Provides the user with the next auto-generated hint.
 */
function getAutoHint() {
  // First time getting an auto hint for current destination.
  if (hunt.getPlaceID() == -1) {
    generatePlaceID();
  } else {
    getNextAutoHint();
  }
}

/**
 * Get the first auto-generated hint for the current destination.
 * This involves first retrieving the place ID for the current
 * destination, before getting a new hint.
 */
function generatePlaceID() {
  const request = {
    query: hunt.getCurDestName(),
    fields: ['place_id'],
  };
  service.findPlaceFromQuery(request, function(results, status) {
    if (status === google.maps.places.PlacesServiceStatus.OK) {
      hunt.setPlaceID(results[0].place_id);
      getNextAutoHint();
    } else {
      updateMessage(GENERATE_DISPLAY, NO_PLACEID_MSSG);
    }
  });
}

/**
 * Gets a new auto-generated hint, assuming that the place
 * ID for the current destination has already been determined.
 */
function getNextAutoHint() {
  const random = Math.random();
  if (random < 0.5) { // Get a hint in the form of a photo
    if (hunt.getPhotos().length == 0) {
      generatePhotos();
    } else {
      displayNextPhoto();
    }
  } else { // Get a hint in the form of a user review
    if (hunt.getReviews().length == 0) {
      generateReviews();
    } else {
      displayNextReview();
    }
  }
}

/**
 * Generate array of photos for the current destination using
 * the Places API, which will later be used to retrieve and
 * display photos.
 */
function generatePhotos() {
  const photosRequest = {
    placeId: hunt.getPlaceID(),
    fields: ['photo'],
  };
  service.getDetails(photosRequest, (place, status) => {
    if (status == google.maps.places.PlacesServiceStatus.OK) {
      hunt.setPhotos(place.photos);
      displayNextPhoto();
    } else {
      updateMessage(GENERATE_DISPLAY, NO_PLACE_INFO_MSSG);
    }
  });
}

/**
 * Display the next photo hint that the user should see.
 */
function displayNextPhoto() {
  const curIndex = hunt.getPhotoIndex();
  if (curIndex < hunt.getPhotos().length) {
    updateMessage(HINT_DISPLAY, 'Photo of destination: ');
    updatePhoto(HINT_DISPLAY, hunt.getPhotos()[curIndex]);
    hunt.setPhotoIndex(curIndex + 1);
  } // If there are no more photos to display, do nothing
}

/**
 * Generate array of reviews for the current destination using
 * the Places API, which will later be used to retrieve and
 * display reviews.
 */
function generateReviews() {
  const reviewsRequest = {
    placeId: hunt.getPlaceID(),
    fields: ['review'],
  };
  service.getDetails(reviewsRequest, (place, status) => {
    if (status == google.maps.places.PlacesServiceStatus.OK) {
      hunt.setReviews(place.reviews);
      displayNextReview();
    } else {
      updateMessage(GENERATE_DISPLAY, NO_PLACE_INFO_MSSG);
    }
  });
}

/**
 * Display the next user review hint that the user should see.
 */
function displayNextReview() {
  const curIndex = hunt.getReviewIndex();
  if (curIndex < hunt.getReviews().length) {
    updateMessage(HINT_DISPLAY, 'Review of destination: ');
    const cleanedReview = redactReview(hunt.getReviews()[curIndex].text);
    updateMessage(HINT_DISPLAY, cleanedReview);
    hunt.setReviewIndex(curIndex + 1);
  } // If there are no more reviews to display, do nothing
}

/**
 * Redact all references in text to the destination name.
 * @param {String} text Text to be redacted
 * @return {String} Redacted text
 */
function redactReview(text) {
  const re = new RegExp(hunt.getCurDestName(), 'gi');
  return text.replace(re, '[REDACTED]');
}

/**
 * Update {code@ display} to specified {code@ photo}.
 * @param {String} display ID of element to be updated
 * @param {String} photo Photo that should be added to display.
 */
function updatePhoto(display, photo) {
  const message = document.getElementById(display);
  const newImage = document.createElement('img');
  newImage.src = photo.getUrl({maxWidth: 400, maxHeight: 400});
  message.appendChild(newImage);
}

/**
 * Determines whether the user entered the correct destination, and
 * adjusts the display accordingly.
 * Disable lint check because handleDestinationAnswer() is called
 * from go.html.
 */
function handleDestinationAnswer() { //eslint-disable-line
  if (hunt.hasNotStarted()) { // User has not yet started the hunt.
    return;
  }
  const userGuess = document.getElementById(GUESS_INPUT).value;
  const queryStr = GUESS_URL + '?' + GUESS_INPUT + '=' + userGuess +
      '&answer=' + hunt.getCurDestName();
  fetch(queryStr).then((response) => response.json()).then((correctGuess) => {
    if (correctGuess) {
      toggleProceedButton(/* hide = */ false);
      hideHuntElements();
      updateMessagesForCorrectGuess();
      addMarkerToMap(hunt.getCurDestLat(), hunt.getCurDestLng(),
          hunt.getCurDestName());
      clearTimeout(hintClock);
    } else {
      updateMessage(SUBMIT_DISPLAY, WRONG_MSSG);
    }
  });
}

/**
 * Update the text display when the user correctly guesses
 * the destination. Remove hints and photos from the page,
 * and update the riddle to display information about the
 * destination that was found.
 */
function updateMessagesForCorrectGuess() {
  updateMessage(SUBMIT_DISPLAY, CORRECT_MSSG);
  deleteMessage(GENERATE_DISPLAY);
  deleteMessage(HINT_DISPLAY);
  updateMessage(RIDDLE_DISPLAY, hunt.getCurDestName() + ': ' +
      hunt.getCurDestDescription());
}

/**
 * The user presses the start button.
 * Disable lint check because startHunt() is called from go.html.
 */
function startHunt() { //eslint-disable-line
  hunt.start();
  sendIndexToServlet(0);
  document.getElementById(RIDDLE_BOX).classList.remove(INVISIBLE_CLASS);
  updateToCurrentState();
}

/**
 * Updates the hunt to the current destination that the user is on.
 */
function updateToCurrentState() {
  if (hunt.hasNotStarted()) {
    hideHuntElements();
    document.getElementById(RIDDLE_BOX).classList.add(INVISIBLE_CLASS);
  } else { // The user has started the hunt, and needs to solve the riddle.
    showElementsDuringHunt();
    hideStartButton();
    updateMessage(RIDDLE_DISPLAY, 'Riddle: ' + hunt.getCurDestPuzzle());
  }
  toggleProceedButton(/* hide = */ true);
  // Add all found destinations to the map as markers.
  for (let i = 0; i < hunt.getDestIndex(); i++) {
    addMarkerToMap(hunt.getDest(i).lat, hunt.getDest(i).lng,
        hunt.getDest(i).name);
  }
}

/**
 * While the user is not going on the hunt (i.e. before they click
 * the start button, after they finish the hunt, after they correctly
 * guess a destination), hide the hint button, text input bar, and the
 * 'Nearby Destinations' button.
 */
function hideHuntElements() {
  toggleHintButton(/* hide = */ true);
  toggleSubmitForm(/* hide = */ true);
  toggleGenerateButton(/* hide = */ true);
  document.getElementById(HINT_DISPLAY).classList.add(INVISIBLE_CLASS);
  document.getElementById(SUBMIT_BOX).classList.add(INVISIBLE_CLASS);
}

/**
 * While the user is going on the hunt (i.e. after they click the start
 * button and before they finish the hunt), show the hint button, text input
 * bar, and 'Nearby Destinations' button.
 */
function showElementsDuringHunt() {
  delayHintButton();
  toggleSubmitForm(/* hide = */ false);
  toggleGenerateButton(/* hide = */ false);
  document.getElementById(HINT_DISPLAY).classList.remove(INVISIBLE_CLASS);
  document.getElementById(SUBMIT_BOX).classList.remove(INVISIBLE_CLASS);
}

/**
 * Show or hide the submit form where the user inputs their guess
 * for the destination.
 * @param {boolean} hide Whether the form should be hidden or shown.
 */
function toggleSubmitForm(hide) {
  const guessBar = document.getElementById(GUESS_INPUT);
  const guessButton = document.getElementById(SUBMIT_BUTTON);
  if (hide) {
    guessBar.classList.add(INVISIBLE_CLASS);
    guessButton.classList.add(INVISIBLE_CLASS);
  } else {
    guessBar.classList.remove(INVISIBLE_CLASS);
    guessButton.classList.remove(INVISIBLE_CLASS);
  }
}

/**
 * Show or hide the generate button that allows users to see
 * a nearby destination (that isn't the one they're trying to find).
 * @param {boolean} hide Whether the button should be hidden or shown.
 */
function toggleGenerateButton(hide) {
  const generateButton = document.getElementById(GENERATE_BUTTON);
  if (hide) {
    generateButton.classList.add(INVISIBLE_CLASS);
  } else {
    generateButton.classList.remove(INVISIBLE_CLASS);
  }
}

/**
 * Creates a new paragraph element from text.
 *
 * @param {String} text to be displayed on main page.
 * @return {String} newLine New element to be appended to main page.
 */
function createLine(text) {
  const newLine = document.createElement('p');
  newLine.innerText = text;
  return newLine;
}

/**
 * Show or hide the proceed button. If the user has found the final
 * destination, the text of the button will change.
 * @param {boolean} hide Whether the proceed button should be hidden or shown.
 */
function toggleProceedButton(hide) {
  const proceedButton = document.getElementById(PROCEED_BUTTON);
  if (hide) {
    proceedButton.classList.add(INVISIBLE_CLASS);
  } else {
    if (hunt.isAtLastStop()) {
      proceedButton.innerText = PROCEED_FINAL_MSSG;
    }
    proceedButton.classList.remove(INVISIBLE_CLASS);
  }
}

/**
 * Hide the start button.
 */
function hideStartButton() {
  const startButton = document.getElementById(START_BUTTON);
  startButton.classList.add(INVISIBLE_CLASS);
}

/**
 * Update {code@ display} to specified {code@ text}.
 * @param {String} display ID of element to be updated
 * @param {String} text Text that display should be updated to.
 */
function updateMessage(display, text) {
  const message = document.getElementById(display);
  if (display == HINT_DISPLAY) {
  // Special case for HINT_DISPLAY to avoid clearing out its existing contents
  // because it may already contain previous hints.
    message.appendChild(createLine(text));
  } else {
    message.innerHTML = '';
    message.appendChild(createLine(text));
  }
}

/**
 * After the user has found all destinations, replace the riddle
 * with a final message to the user.
 */
function updateRiddleToFinalMessage() {
  const newLine = document.createElement('div');
  let message = '<p></p>'; // Extra line for readability.
  message += '<p> Congrats! You\'ve visited the following locations:</p>';
  for (let i = 0; i < hunt.getNumItems(); i++) {
    message += '<p>' + sanitize(hunt.getDest(i).name) + ': ' +
        sanitize(hunt.getDest(i).description) + '</p>';
  }
  newLine.innerHTML = message;
  const riddle = document.getElementById(RIDDLE_DISPLAY);
  riddle.innerHTML = '';
  riddle.appendChild(newLine);
}

/**
 * Update the scavenger hunt data with the current destination that
 * the user is on. Also sends back the ID of the scavenger hunt
 * that the user is on.
 * @param {int} index Index of the current destination the user needs
 * to find.
 */
function sendIndexToServlet(index) {
  const params = new URLSearchParams();
  params.append(INDEX_PARAM, index);
  params.append(HUNT_PARAM, huntID);
  fetch(DATA_URL, {method: 'POST', body: params});
}

/**
 * After the user correctly names the destination, proceed to
 * the next destination in the hunt.
 * Disable lint check because proceed() is called from go.html.
 */
function proceed() { //eslint-disable-line
  hunt.incrementDestIndex();
  sendIndexToServlet(hunt.getDestIndex());
  if (hunt.getDestIndex() < hunt.getNumItems()) {
    updateMessage(RIDDLE_DISPLAY, 'Riddle: ' + hunt.getCurDestPuzzle());
    showElementsDuringHunt();
  } else {
    updateRiddleToFinalMessage();
    hideHuntElements();
  }
  resetForNextDestination();
}

/**
 * Clear all text from display as the user proceeds to the
 * next destination.
 */
function resetForNextDestination() {
  updateProgressBar();
  toggleProceedButton(/* hide = */ true);
  deleteMessage(SUBMIT_DISPLAY);
  deleteMessage(HINT_DISPLAY);
  deleteMessage(GENERATE_DISPLAY);
  hunt.resetForNextDest();
}

/**
 * Update the progress bar to reflect how far the user is
 * on the scavenger hunt.
 */
function updateProgressBar() {
  const bar = document.getElementById(PROGRESS_DISPLAY);
  bar.style.width = hunt.getProgress() + '%';
}

/**
 * When the user first begins to find a destination, hide the hint
 * button. Only show the hint button after a certain amount of time
 * has passed.
 */
function delayHintButton() {
  toggleHintButton(/* hide = */ true);
  hintClock = window.setTimeout(toggleHintButton, HIDE_INTERVAL_MS, false);
}

/**
 * Remove text from display.
 * @param {String} display ID of element to be cleared.
 */
function deleteMessage(display) {
  const mssg = document.getElementById(display);
  mssg.innerHTML = '';
}

/**
 * Sanitize text to be added to the display.
 * @param {String} unsafeContent Text to be sanitized
 * @return {String} Sanitized text
 */
function sanitize(unsafeContent) {
  const element = document.createElement('span');
  element.innerText = unsafeContent;
  return element.innerHTML;
}

/**
 * Exit from hunt. A window will pop up confirming the user's exit.
 * Disable lint check because exit() is called from go.html.
 */
function exit() { //eslint-disable-line
  if (confirm(EXIT_MSSG)) {
    window.location.replace(HOME_URL);
  }
}
