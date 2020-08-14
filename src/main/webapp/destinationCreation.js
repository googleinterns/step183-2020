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

/*
 * Adds a Script for the places library to the head of the destinationCreation.html
 */
function addScriptToHead() { // eslint-disable-line
  const newScript = document.createElement('script');
  newScript.src = 'https://maps.googleapis.com/maps/api/js?key=' + config.PLACES_KEY + '&libraries=places';
  newScript.type = 'text/javascript';
  document.getElementsByTagName('head')[0].appendChild(newScript);
}

function searchForPlace() { // eslint-disable-line
  const mapCenter = new google.maps.LatLng(37.421949,-122.083972);

  const map = new google.maps.Map(document.getElementById('map'), {
    center: mapCenter,
    zoom: 18
  });

  const placeService = new google.maps.places.PlacesService(map);

  const text = document.getElementById('search').value;

  const request = {
    query: text,
    fields: ['name', 'geometry'],
  }

  placeService.findPlaceFromQuery(request, (results, status) => {
    if (status === google.maps.places.PlacesServiceStatus.OK) {

      results.forEach((item) => {
        // TODO: Create clickable divs for each item that is returned from the query and fill in input fields with appropriate information
        // provided by the places object
        console.log(item)
      });
    }
  })
}
