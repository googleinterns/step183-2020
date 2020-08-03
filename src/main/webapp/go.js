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
const START_BUTTON = 'start-button';
const PROCEED_BUTTON = 'proceed-button';

// URLs that data should be fetched from.
const DATA_URL = '/go-data';

// Div IDs that text or a map should be inserted into.
const HINT_DISPLAY = 'hint-area';
const RIDDLE_DISPLAY = 'riddle-area';
const SUBMIT_DISPLAY = 'submit-area';

// Hard-coded messages to be displayed to the user.
const PROCEED_FINAL_MSSG = 'Finish the Hunt';
const CORRECT_MSSG = 'Correct!';
const WRONG_MSSG = 'Wrong. Try again!';

// Other constants.
const INDEX_PARAM = 'new-index';
const INVISIBLE_CLASS = 'invisible';

// Global variables.
let destIndex; // Marks the destination that the user currently needs to find.
let hintIndex = 0; // Marks the hint that the user will see next.
const huntArr = []; // Stores scavenger hunt data retrieved from the server.

/**
 * Represents a destination on the scavenger hunt.
 */
class Destination {
  /**
   * Constructor for Destination class.
   * @param {String} name Name of destination
   * @param {String} description Description of destination
   * @param {String} puzzle Primary riddle used to find destination
   * @param {array} hints Array of hints to supplement puzzle
   * @param {Double} lat Latitude of destination
   * @param {Double} lng Longitude of destination
   */
  constructor(name, description, puzzle, hints, lat, lng) {
    this.name = name;
    this.description = description;
    this.puzzle = puzzle;
    this.hints = hints;
    this.lat = lat;
    this.lng = lng;
  }
}
 
window.onload = getHunt();
 
/**
 * Retrieves scavenger hunt data, and updates to the current destination
 * to reflect the current state of the hunt.
 */
function getHunt() {
  fetch(DATA_URL).then((response) => response.json()).then((mssg) => {
    destIndex = mssg.index;
    if (huntArr.length == 0) {
      for (let i = 0; i < mssg.items.length; i++) {
        const cur = mssg.items[i];
        huntArr.push(new Destination(cur.name, cur.description,
            cur.riddle.puzzle, cur.riddle.hints, cur.location.lat,
            cur.location.lng));
      }
    }
    createMap();
    updateToCurrentState(destIndex);
    if (destIndex >= 0) {
      handleDestinationAnswer(mssg.guess);
    }
  });
}

/**
 * Determines whether the user entered the correct destination, and 
 * adjusts the display accordingly.
 * @param {String} guess The destination name entered by the user.
 */
function handleDestinationAnswer(guess) {
  if (guess === huntArr[destIndex].name) {
    toggleProceedButton(/* hide = */ false);
    toggleHintButton(/* hide = */ true);
    updateMessage(SUBMIT_DISPLAY, CORRECT_MSSG);
    updateMessage(RIDDLE_DISPLAY, huntArr[destIndex].name + ': '
        + huntArr[destIndex].description);
    addMarkerToMap(huntArr[destIndex].lat, huntArr[destIndex].lng,
        huntArr[destIndex].name);
  } else if (guess.length != 0) {
    updateMessage(SUBMIT_DISPLAY, WRONG_MSSG);
  }
}

/**
 * Add a marker to the map at the specified location.
 * @param {Double} destLat: latitude of location.
 * @param {Double} destLng: longitude of location.
 * @param {String} destName: Name of location.
 * This function is implemented in another PR.
 */
function addMarkerToMap(destLat, destLng, destName) {}

/**
 * Creates a map and adds it to the page.
 * This function is implemented in another PR.
 */
function createMap() {}

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
 * Show or hide the proceed button.
 * @param {boolean} hide Whether the proceed button should be hidden or shown.
 */
function toggleProceedButton(hide) {
  const proceedButton = document.getElementById(PROCEED_BUTTON);
  if (hide) {
    proceedButton.classList.add(INVISIBLE_CLASS);
  } else {
    if (destIndex == huntArr.length - 1) {
      document.querySelector('#' + PROCEED_BUTTON).innerText = PROCEED_FINAL_MSSG;
    }
    proceedButton.classList.remove(INVISIBLE_CLASS);
  }
}

/**
 * Show or hide the hint button.
 * @param {boolean} hide Whether the proceed button should be hidden or shown.
 * This function is implemented in the adjacent PR.
 */
function toggleHintButton(hide) {}

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
    message.appendChild(createLine('Hint #' + (hintIndex + 1)
        + ': ' + text));
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
    message += '<p>' + sanitize(huntArr[i].name) + ': '
        + sanitize(huntArr[i].description) + '</p>';
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
