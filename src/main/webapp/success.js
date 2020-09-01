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

/**
 * Retrieves the name of the destination created from the url
 * and alerts the user this destination was created
 */
function confirmCreation() { // eslint-disable-line
  const params = new URLSearchParams(window.location.search);
  const name = params.get('name');
  const p = document.createElement('p');
  p.innerText = name + ' has been successfully created!';
  document.getElementById('confirmation-block').appendChild(p);
}
