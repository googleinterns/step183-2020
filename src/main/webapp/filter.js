const FILTER = '.filter';
const UNCLICKED = 'unclicked-filter';
const CLICKED = 'clicked-filter';
const PLACE_ARRAY_URL_PARAM = 'user-places';
const DIFF_ARRAY_URL_PARAM = 'user-diff';

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

  // TODO: Get clicked num stops
  // TODO: Get clicked tags

  fetch('/generate-hunt?' + PLACE_ARRAY_URL_PARAM + '=' + jsonPlaceArray + '&' +
    DIFF_ARRAY_URL_PARAM + '=' + jsonDiffArray,
  {method: 'POST'}).then((response) => response.text())
      .then((message) => {
        // If success, redirect to scavenger hunt with ID
        // If failure, display on screen 
        if (message != "Error\n") {
          window.location = '/go.html?hunt_id=' + message;
        } else {
          if (document.getElementsByClassName('error-message').length > 0) {
            const errorDiv = document.getElementsByClassName('error-message')[0];
            errorDiv.classList.remove('hide');
          }
        }
      });
}


