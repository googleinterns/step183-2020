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

const GO_URL = '/go-data';
const HUNT_ID = 'hunt-area';

const riddleArr = [];
const destArr = [];

/*
 * Retrieves scavenger hunt data.
 */
export function getHunt() {
  fetch(GO_URL).then((response) => response.json()).then((mssg) => {
    const mssgElem = document.getElementById(HUNT_ID);
    mssgElem.appendChild(createLine('index: ' + mssg.index + ', city: ' +
        mssg.city));
    for (let i = 0; i < mssg.items.length; i++) {
      riddleArr.push(mssg.items[i].riddle.puzzle);
      destArr.push(mssg.items[i].name);
    }
  });
}

/*
 * Creates a new paragraph element from text.
 */
export function createLine(text) {
  const newLine = document.createElement('p');
  newLine.innerText = text;
  return newLine;
}
