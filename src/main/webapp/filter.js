const FILTER = '.filter';
const UNCLICKED = 'unclicked-filter';
const CLICKED = 'clicked-filter';
const PLACE_ARRAY_URL_PARAM = 'user-places';
const DIFF_ARRAY_URL_PARAM = 'user-diff';
const NUM_STOPS_URL_PARAM = 'user-num-stops';

/**
* Swap between "clicked" and "unclicked" classes
* (which change the color of the button) when the button is pressed.
* Note: function declaration lint disabled because the function is called
* in an onload attribute in generateHunt.html.
**/
function turnBlueWhenClicked() { //eslint-disable-line
  const allFilters = document.querySelectorAll(FILTER);
  for (let i = 0; i < allFilters.length; i++) {
    allFilters[i].onclick = function() {
      const currFilter = allFilters[i];
      if (currFilter.classList.contains(UNCLICKED)) {
        currFilter.classList.remove(UNCLICKED);
        currFilter.classList.add(CLICKED);
      } else {
        currFilter.classList.remove(CLICKED);
        currFilter.classList.add(UNCLICKED);
      }
    };
  }
}

/**
* Get all filters that have been clicked when user presses submit button,
* and pass array to servlet, and TODO: return success or error message.
* Note: function declaration lint disabled because the function is called from
* an onclick attribute in generateHunt.html.
**/
function sendClickedFiltersToServer() { //eslint-disable-line
  // Get clicked places
  let clickedPlaces = [];
  if (document.getElementsByClassName('city').length > 0) {
    const cityContainer = document.getElementsByClassName('city')[0];
    clickedPlaces = Array.from(cityContainer.getElementsByClassName(CLICKED))
        .map((element) => element.innerText);
  }
  const jsonPlaceArray = JSON.stringify(clickedPlaces);

  // Get clicked difficulty
  let clickedDifficulties = [];
  if (document.getElementsByClassName('difficulty').length > 0) {
    const diffContainer = document.getElementsByClassName('difficulty')[0];
    clickedDifficulties =
      Array.from(diffContainer.getElementsByClassName(CLICKED))
          .map((element) => element.innerText);
    if (clickedDifficulties.length === 0) {
      clickedDifficulties = ['easy', 'medium', 'hard'];
    }
  }
  const jsonDiffArray = JSON.stringify(clickedDifficulties);

  // Get clicked num stops
  let clickedNumStops = [];
  let numStops = '';
  if (document.getElementsByClassName('num-stops').length > 0) {
    const numStopsContainer = document.getElementsByClassName('num-stops')[0];
    clickedNumStops = 
        Array.from(numStopsContainer.getElementsByClassName(CLICKED))
        .map((element) => element.innerText);
    if (clickedNumStops.length > 1) {
       window.alert('Please choose only ONE number of stops.');
    } else if (clickedNumStops.length === 0) {
      numStops = 'Three';
    } else {
      numStops = clickedNumStops[0];
    }
  }
  const jsonNumPlaces = JSON.stringify(numStops);

  // TODO: Get clicked tags

  fetch('/generate-hunt?' + PLACE_ARRAY_URL_PARAM + '=' + jsonPlaceArray + '&' +
    DIFF_ARRAY_URL_PARAM + '=' + jsonDiffArray + '&' + 
    NUM_STOPS_URL_PARAM + '=' + jsonNumPlaces,
  {method: 'POST'}).then((response) => response.text())
      .then((message) => {
        // If success, redirect to scavenger hunt with ID
        // If failure, display on screen
        if (message.trim() != 'Error') {
          window.location = '/go.html?hunt_id=' + message;
        } else {
          if (document.getElementsByClassName('error-message').length > 0) {
            const errorDiv =
              document.getElementsByClassName('error-message')[0];
            errorDiv.classList.remove('hide');
          }
        }
      });
}


