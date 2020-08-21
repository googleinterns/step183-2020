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

// DIV IDs that should have text or a place div inserted into them
const SEARCH_RESULTS = 'search-results';
const NAME_INPUT = 'name-input';
const LAT_INPUT = 'lat-input';
const LNG_INPUT = 'lng-input';
const PLACE_INPUT = 'place-input';

// DIV IDs that retrieve information from the DOM
const SEARCH = 'search';
const MAP = 'map';

// Class name constants
const PLACE_CLASS = '.place';

/*
 * Adds a Script for the places api to the head of the destinationCreation.html
 */
function addScriptToHead() { // eslint-disable-line
  const newScript = document.createElement('script');
  newScript.src = 'https://maps.googleapis.com/maps/api/js?key=' + config.PLACES_KEY + '&libraries=places';
  document.getElementsByTagName('head')[0].appendChild(newScript);
}

function searchForPlace() { // eslint-disable-line
  const element = document.querySelectorAll(PLACE_CLASS);
  if (element.length > 0) {
    removeElements(PLACE_CLASS);
  }
  // Coresponds to the location of the Googleplex building
  const mapCenter = new google.maps.LatLng(37.421949, -122.083972);

  const map = new google.maps.Map(document.getElementById(MAP), {
    center: mapCenter,
    zoom: 15,
  });

  const placeService = new google.maps.places.PlacesService(map);

  const text = document.getElementById(SEARCH).value;

  const request = {
    query: text,
    fields: ['name', 'geometry', 'place_id'],
  };

  placeService.findPlaceFromQuery(request, (results, status) => {
    let div;
    if (status === google.maps.places.PlacesServiceStatus.OK) {
      results.forEach((place) => {
        div = document.createElement('div');
        div.setAttribute('data-place-id', place.place_id);
        div.setAttribute('data-lat', place.geometry.location.lat());
        div.setAttribute('data-lng', place.geometry.location.lng());
        div.innerText = place.name;
        div.classList.add('place');
        div.onclick = fillInValues;
        document.getElementById(SEARCH_RESULTS).appendChild(div);
      });
    } else {
      div = document.createElement('div');
      div.innerText = "Sorry no results were found";
      div.classList.add('place');
      document.getElementById(SEARCH_RESULTS).appendChild(div);
    }
  });
}

function fillInValues() { // eslint-disable-line
  const place = this.dataset.placeId;
  const lat = this.dataset.lat;
  const lng = this.dataset.lng;
  const nameField = document.getElementById(NAME_INPUT);
  const latField = document.getElementById(LAT_INPUT);
  const lngField = document.getElementById(LNG_INPUT);
  const placeField = document.getElementById(PLACE_INPUT);
  nameField.value = this.innerText;
  latField.value = lat;
  lngField.value = lng;
  placeField.value = place;
  const mapCenter = new google.maps.LatLng(lat, lng);
  const map = new google.maps.Map(document.getElementById(MAP), {
    center: mapCenter,
    zoom: 15,
  });
  removeElements(PLACE_CLASS);
  const marker = new google.maps.Marker({ //eslint-disable-line
    map,
    position: mapCenter,
  });
}

// Removes a place div
function removeElements(elemcls) { 
  const element = document.querySelectorAll(elemcls);
  for (let i = 0; i < element.length; i++) {
    element[i].remove();
  }
}
