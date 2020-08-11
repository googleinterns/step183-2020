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

// URLs that data should be fetched from.
const DATA_URL = '/go-data';
const GUESS_URL = '/guess-data';

// Div IDs that text or a map should be inserted into.
const HINT_DISPLAY = 'hint-area';
const RIDDLE_DISPLAY = 'riddle-area';

const SUBMIT_DISPLAY = 'response-area';
const MAP_DISPLAY = 'map-area';
const MAP_MSSG_DISPLAY = 'map-message-area';

// Hard-coded messages to be displayed to the user.
const PROCEED_FINAL_MSSG = 'Finish the Hunt';
const CORRECT_MSSG = 'Correct!';
const WRONG_MSSG = 'Wrong. Try again!';

// Other constants.
const INDEX_PARAM = 'new-index';
const INVISIBLE_CLASS = 'invisible';
const HUNTID_PARAM = 'hunt_id';
const REFRESH_TIME = 10000; // ten seconds

// Global variables.
let destIndex; // Marks the destination that the user currently needs to find.
let hintIndex = 0; // Marks the hint that the user will see next.
const huntArr = []; // Stores scavenger hunt data retrieved from the server.
let map;
let huntID;

window.onload = function() {
  addScriptToHead();
  getHunt();
};

/**
 * Add the Map API key to the head.
 */
function addScriptToHead() {
  const newScript = document.createElement('script');
  newScript.src = 'https://maps.googleapis.com/maps/api/js?key=' + config.MAP_KEY + '&callback=createMap';
  document.getElementsByTagName('head')[0].appendChild(newScript);
}

/**
 * Retrieves scavenger hunt data, and updates to the current destination
 * to reflect the current state of the hunt.
 */
function getHunt() {
  huntID = window.location.params.get(HUNTID_PARAM);
  fetch(DATA_URL + '?hunt_id=' + huntID).then((response) => response.json()).then((mssg) => {
    destIndex = mssg.index;
    for (let i = 0; i < mssg.items.length; i++) {
      const cur = mssg.items[i];
      huntArr.push(new Destination(cur.name, cur.description,
          cur.riddle.puzzle, cur.riddle.hints, cur.location.lat,
          cur.location.lng));
    }
    updateToCurrentState(destIndex);
    if (destIndex >= 0) {
      handleDestinationAnswer();
    }
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
  window.setInterval(updateGeolocation, REFRESH_TIME);
  updateGeolocation();
}

/**
 * Update the user's current location.
 */
function updateGeolocation() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition((position) => {
      const pos = {lat: position.coords.latitude,
        lng: position.coords.longitude};
      addMarkerToMap(position.coords.latitude, position.coords.longitude,
          'Your current location');
      map.setCenter(pos);
    },
    () => updateMessage(MAP_MSSG_DISPLAY,
        'Error: I can\'t find your location.'));
  } else {
    updateMessage(MAP_MSSG_DISPLAY, 'Error: Your browser doesn\'t' +
        'support geolocation.');
  }
}

/**
 * Show or hide the hint button.
 * @param {boolean} hide Whether the proceed button should be hidden or shown.
 * Disable lint check because this will later be called by
 * handleDestinationAnswer(), updateToCurrentState(), and proceed().
 *
 */
function toggleHintButton(hide) { //eslint-disable-line
  const hintButton = document.getElementById(HINT_BUTTON);
  if (hide) {
    hintButton.classList.add(INVISIBLE_CLASS);
  } else {
    hintButton.classList.remove(INVISIBLE_CLASS);
  }
}

/**
 * Allow the user to see a hint.
 * Disable lint check because getHint() is called from go.html.
 */
function getHint() { //eslint-disable-line
  if (destIndex >= 0) {
    const hintArr = huntArr[destIndex].hints;
    if (hintIndex < hintArr.length) {
      updateMessage(HINT_DISPLAY, hintArr[hintIndex]);
      hintIndex++;
    }
  }
}

/**
 * Determines whether the user entered the correct destination, and
 * adjusts the display accordingly.
 */
function handleDestinationAnswer() {
  fetch(GUESS_URL).then((response) => response.json()).then((guess) => {
    if (guess === huntArr[destIndex].name) {
      toggleProceedButton(/* hide = */ false);
      toggleHintButton(/* hide = */ true);
      updateMessage(SUBMIT_DISPLAY, CORRECT_MSSG);
      updateMessage(RIDDLE_DISPLAY, huntArr[destIndex].name + ': ' +
          huntArr[destIndex].description);
      addMarkerToMap(huntArr[destIndex].lat, huntArr[destIndex].lng,
          huntArr[destIndex].name);
    } else if (guess.length != 0) {
      updateMessage(SUBMIT_DISPLAY, WRONG_MSSG);
    }
  });
}

/**
 * The user presses the start button.
 * Disable lint check because startHunt() is called from go.html.
 */
function startHunt() { //eslint-disable-line
  destIndex = 0;
  sendIndexToServlet(0);
  updateToCurrentState(0);
}

/**
 * Updates the hunt to the current destination that the user is on.
 * @param {int} index The index of the destination that the user needs
 * to find.
 */
function updateToCurrentState(index) {
  if (index <= -1) { // The user has not yet pressed the start button.
    toggleHintButton(/* hide = */ true);
  } else { // The user has started the hunt, and needs to solve the riddle.
    toggleHintButton(/* hide = */ false);
    hideStartButton();
    updateMessage(RIDDLE_DISPLAY, 'Riddle: ' + huntArr[index].puzzle);
  }
  toggleProceedButton(/* hide = */ true);
  // Add all found destinations to the map as markers.
  for (let i = 0; i < index; i++) {
    addMarkerToMap(huntArr[i].lat, huntArr[i].lng, huntArr[i].name);
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
    if (destIndex === huntArr.length - 1) {
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
    message.appendChild(createLine('Hint #' + (hintIndex + 1) +
        ': ' + text));
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
  let message = '<p> Congrats! You\'ve visited the following locations:</p>';
  for (let i = 0; i < huntArr.length; i++) {
    message += '<p>' + sanitize(huntArr[i].name) + ': ' +
        sanitize(huntArr[i].description) + '</p>';
  }
  newLine.innerHTML = message;
  const riddle = document.getElementById(RIDDLE_DISPLAY);
  riddle.innerHTML = '';
  riddle.appendChild(newLine);
}

/**
 * Update the scavenger hunt data with the current destination that
 * the user is on.
 * @param {int} index Index of the current destination the user needs
 * to find.
 */
function sendIndexToServlet(index) {
  const params = new URLSearchParams();
  params.append(INDEX_PARAM, index);
  params.append(HUNTID_PARAM, huntID);
  fetch(DATA_URL, {method: 'POST', body: params});
}

/**
 * After the user correctly names the destination, proceed to
 * the next destination in the hunt.
 * Disable lint check because proceed() is called from go.html.
 */
function proceed() { //eslint-disable-line
  destIndex++;
  sendIndexToServlet(destIndex);
  if (destIndex < huntArr.length) {
    updateMessage(RIDDLE_DISPLAY, 'Riddle: ' + huntArr[destIndex].puzzle);
    toggleHintButton(/* hide = */ false);
  } else {
    updateRiddleToFinalMessage();
    toggleHintButton(/* hide = */ true);
  }
  toggleProceedButton(/* hide = */ true);
  deleteMessage(SUBMIT_DISPLAY);
  deleteMessage(HINT_DISPLAY);
  hintIndex = 0;
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
  if (confirm('Exit hunt?')) {
    window.location.replace('index.html');
  }
}
