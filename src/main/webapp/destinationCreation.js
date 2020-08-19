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
 * Adds a Script for the places api to the head of the destinationCreation.html
 */
function addScriptToHead() { // eslint-disable-line
  const newScript = document.createElement('script');
  newScript.src = 'https://maps.googleapis.com/maps/api/js?key=' + config.PLACES_KEY + '&libraries=places';
  document.getElementsByTagName('head')[0].appendChild(newScript);
}

function searchForPlace() { // eslint-disable-line
  document.getElementsByClassName('search-results').innerHtml = '';
  // Coresponds to the location of the Googleplex building
  const mapCenter = new google.maps.LatLng(37.421949, -122.083972);

  const map = new google.maps.Map(document.getElementById('map'), {
    center: mapCenter,
    zoom: 18,
  });

  const placeService = new google.maps.places.PlacesService(map);

  const text = document.getElementById('search').value;

  const request = {
    query: text,
    fields: ['name', 'geometry', 'place_id', 'icon', 'photos'],
  };

  placeService.findPlaceFromQuery(request, (results, status) => {
    let div;
    if (status === google.maps.places.PlacesServiceStatus.OK) {
      results.forEach((place) => {
        div = document.createElement('div');
        div.setAttribute('data-place-id', place.place_id);
        div.innerText = place.name;
        div.id = 'place';
        div.onclick = fillInValues;
        document.getElementsByClassName('search-results')[0].appendChild(div);
      });
    }
  });
}

function fillInValues() { // eslint-disable-line
  const place = this.dataset.placeId;
  const nameField = document.getElementById('name-input');
  const latField = document.getElementById('lat-input');
  const lngField = document.getElementById('lng-input');
  const placeField = document.getElementById('place-Id-input');
  const mapCenter = new google.maps.LatLng(37.421949, -122.083972);
  const map = new google.maps.Map(document.getElementById('map'), {
    center: mapCenter,
    zoom: 15,
  });

  const placeService = new google.maps.places.PlacesService(map);
  const request = {
    placeId: place,
    fields: ['name', 'geometry'],
  };
  let marker;
  placeService.getDetails(request, (result, status) => {
    if (status === google.maps.places.PlacesServiceStatus.OK) {
      nameField.value = result.name;
      latField.value = result.geometry.location.lat();
      lngField.value = result.geometry.location.lng();
      placeField.value = place;
      marker = new google.maps.Marker({
        map,
        position: result.geometry.location,
      });
    }
    if (!map.getBounds().contains(marker.getPosition())) {
      map.setCenter(marker.getPosition());
    }
  });
}
