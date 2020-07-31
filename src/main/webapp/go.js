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
const NAME_URL = '/name-data';

// Div IDs that text or a map should be inserted into.
const RIDDLE_DISPLAY = 'riddle-area';

// Hard-coded messages to be displayed to the user.
const FINAL_MSSG = 'Congrats, you\'ve finished the hunt!';

// Other constants.
const INDEX_PARAM = 'new-index';
const INVISIBLE_CLASS = 'invisible';

// Global variables.
const puzzleArr = [];
let destIndex;
const destArr = [];

window.onload = getHunt();

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
    updateToCurrentState(destIndex);
  });
}

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
