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
const NAME_URL = '/name-data';

// Div IDs that text or a map should be inserted into.
const HINT_DISPLAY = 'hint-area';
const RIDDLE_DISPLAY = 'riddle-area';
const MAP_DISPLAY = 'map-area';
const MAP_MSSG_DISPLAY = 'map-message-area';

// Hard-coded messages to be displayed to the user.
const FINAL_MSSG = 'Congrats, you\'ve finished the hunt!';

// Other constants.
const INDEX_PARAM = 'new-index';
const INVISIBLE_CLASS = 'invisible';

// Global variables.
const puzzleArr = [];
let destIndex;
const destArr = [];
let map;

window.onload = function() {
  addScriptToHead();
  getHunt();
};

/**
 * Add the Map API key to the head.
 */
function addScriptToHead() {
  const newScript = document.createElement('script');
  newScript.src = 'https://maps.googleapis.com/maps/api/js?key=' + config.MAP_KEY;
  document.getElementsByTagName('head')[0].appendChild(newScript);
}

/**
 * Retrieves scavenger hunt data, and updates to the current destination
 * to reflect the current state of the hunt.
 */
function getHunt() {
  fetch(DATA_URL).then((response) => response.json()).then((mssg) => {
    destIndex = mssg.index;
    for (let i = 0; i < mssg.items.length; i++) {
      puzzleArr.push(mssg.items[i].riddle.puzzle);
      destArr.push(mssg.items[i].name);
    }
    createMap();
    updateToCurrentState(destIndex);
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
 */
function createMap() {
  map = new google.maps.Map(
      document.getElementById(MAP_DISPLAY),
      // Centered at GooglePlex (updated below).
      {center: {lat: 37.422, lng: -122.084}, zoom: 7},
  );
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition((position) => {
      const pos = {lat: position.coords.latitude,
        lng: position.coords.longitude};
      addMarkerToMap(position.coords.latitude, position.coords.longitude,
          'Your current location');
      map.setCenter(pos);
    }, 
    () => updateMessage(MAP_MSSG_DISPLAY, 'Error: I can\'t find your location.'));
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
 * Update {code@ display} to specified {code@ text}.
 * @param {String} display ID of element to be updated
 * @param {String} text Text that display should be updated to.
 * This function is implemented in an adjacent PR.
 */
function updateMessage(display, text) {}

/**
 * Updates the hunt to the current destination that the user is on.
 *
 * @param {int} index The index of the destination that the user needs
 * to find.
 */
async function updateToCurrentState(index) {
  if (index == 0) { // User presses the start button.
    sendIndexToServlet(index);
  }
  if (index >= 0) { // The user has already begun the hunt.
    hideStartButton();
    changeRiddleMessage(puzzleArr[index]);
    const isCorrect = await checkCorrectDestination();
    if (isCorrect) {
      toggleProceedButton(/* hide = */ false);
    }
  } else { // The user has not yet pressed the start button.
    toggleProceedButton(/* hide = */ true);
  }
}

/**
 * Retrieves the string the user submitted as the destination name.
 * This data is fetched from the server because entity extraction
 * will be used in the MVP.
 * @return {boolean} Whether or not the user entered the correct name.
 */
async function checkCorrectDestination() {
  fetch(NAME_URL).then((response) => response.json()).then((mssg) => {
    if (mssg === destArr[destIndex]) { // The user entered the correct name.
      return true;
    }
    return false;
  });
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
 * Show or hide the proceed button.
 *
 * @param {boolean} hide Whether the proceed button should be hidden or shown.
 */
function toggleProceedButton(hide) {
  const proceedButton = document.getElementById(PROCEED_BUTTON);
  if (hide) {
    proceedButton.classList.add(INVISIBLE_CLASS);
  } else {
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
 * Change the riddle text displayed on the mmain page.
 * @param {String} text Text that the riddle should be changed to.
 */
function changeRiddleMessage(text) {
  const riddle = document.getElementById(RIDDLE_DISPLAY);
  riddle.innerHTML = '';
  riddle.appendChild(createLine(text));
}

/**
 * Update the scavenger hunt data with the current destination that
 * the user is on.
 * @param {int} index Index of the current destination the user needs to find.
 */
function sendIndexToServlet(index) {
  const params = new URLSearchParams();
  params.append(INDEX_PARAM, index);
  fetch(DATA_URL, {method: 'POST', body: params});
}

/**
 * After the user correctly names the destination, proceed to
 * the next destination in the hunt.
 * This function is used in an onclick attribute in go.html,
 * so "eslint-disable-line" is used to disable errors that arise
 * from proceed() not being called in this file.
 */
function proceed() { //eslint-disable-line
  destIndex++;
  sendIndexToServlet(destIndex); // Update index to next destination.
  if (destIndex < puzzleArr.length) {
    changeRiddleMessage(puzzleArr[destIndex]);
  } else {
    changeRiddleMessage(FINAL_MSSG);
  }
  toggleProceedButton(/* hide = */ true);
}
