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
const HOME_URL = 'index.html';

// Div IDs that text or a map should be inserted into.
const HINT_DISPLAY = 'hint-area';
const RIDDLE_DISPLAY = 'riddle-area';
const SUBMIT_DISPLAY = 'response-area';
const MAP_DISPLAY = 'map-area';
const MAP_MSSG_DISPLAY = 'map-message-area';
const TIMER_DISPLAY = 'timer-area';
const PROGRESS_DISPLAY = 'progress-bar';

// Input IDs or parameters.
const GUESS_INPUT = 'guess-input';
const INDEX_PARAM = 'new-index';

// Hard-coded messages to be displayed to the user.
const PROCEED_FINAL_MSSG = 'Finish the Hunt';
const CORRECT_MSSG = 'Correct!';
const WRONG_MSSG = 'Wrong. Try again!';

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
      config.MAP_KEY + '&callback=createMap';
  document.getElementsByTagName('head')[0].appendChild(newScript);
}

/**
 * Retrieves scavenger hunt data, and updates to the current destination
 * to reflect the current state of the hunt.
 */
function getHunt() {
  // ID of the scavenger hunt that the user is on,
  // in the form of: hunt_id=ScavengerHunt([ID number here])
  // Using index 1 because index 0 is the '?' character.
  huntID = window.location.search.substr(1);
  const queryStr = DATA_URL + '?' + huntID;
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
  window.setInterval(updateGeolocation, MAP_INTERVAL_MS);
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
  const nextHint = hunt.getNextHint();
  if (nextHint != '') {
    updateMessage(HINT_DISPLAY, nextHint);
  }
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
      toggleProceedButton(false);
      toggleHintButton(true);
      updateMessage(SUBMIT_DISPLAY, CORRECT_MSSG);
      updateMessage(RIDDLE_DISPLAY, hunt.getCurDestName() + ': ' +
          hunt.getCurDestDescription());
      addMarkerToMap(hunt.getCurDestLat(), hunt.getCurDestLng(),
          hunt.getCurDestName());
      clearTimeout(hintClock);
    } else {
      updateMessage(SUBMIT_DISPLAY, WRONG_MSSG);
    }
  });
}

/**
 * The user presses the start button.
 * Disable lint check because startHunt() is called from go.html.
 */
function startHunt() { //eslint-disable-line
  hunt.start();
  sendIndexToServlet(0);
  updateToCurrentState();
}

/**
 * Updates the hunt to the current destination that the user is on.
 */
function updateToCurrentState() {
  if (hunt.hasNotStarted()) {
    toggleHintButton(/* hide = */ true);
  } else { // The user has started the hunt, and needs to solve the riddle.
    delayHintButton();
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
    message.appendChild(createLine('Hint #' + (hunt.getHintIndex()) +
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
  const pair = huntID.split('=');
  if (pair.length >= 2) {
    params.append(pair[0], pair[1]);
  }
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
    delayHintButton();
  } else {
    updateRiddleToFinalMessage();
    toggleHintButton(/* hide = */ true);
  }
  updateProgressBar();
  toggleProceedButton(/* hide = */ true);
  deleteMessage(SUBMIT_DISPLAY);
  deleteMessage(HINT_DISPLAY);
  hunt.setHintIndex(0);
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
  if (confirm('Exit hunt?')) {
    window.location.replace(HOME_URL);
  }
}
