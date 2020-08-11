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
  const clickedPlacesArray = [];
  let cityContainer = document.getElementsByClassName('city')[0];
  let clickedPlaces = cityContainer.getElementsByClassName(CLICKED);
  for (let i = 0; i < clickedPlaces.length; i++) {
    clickedPlacesArray[i] = clickedPlaces[i].innerText;
  }
  const jsonPlaceArray = JSON.stringify(clickedPlacesArray);

  // Get clicked difficulty 
  const clickedDiffArray = [];
  let diffContainer = document.getElementsByClassName('difficulty')[0];
  let clickedDifficulties = diffContainer.getElementsByClassName(CLICKED);
  if (clickedDifficulties.length === 0) {
    clickedDiffArray[0] = "Easy";
    clickedDiffArray[1] = "Medium";
    clickedDiffArray[2] = "Hard";
  } else {
    for (let i = 0; i < clickedPlaces.length; i++) {
      clickedDiffArray[i] = clickedDifficulties[i].innerText;
    }
  }
  const jsonDiffArray = JSON.stringify(clickedDiffArray);

  // TODO: Get clicked num stops 
  // TODO: Get clicked tags

  fetch('/generate-hunt?' + PLACE_ARRAY_URL_PARAM + '=' + jsonPlaceArray + "&" + DIFF_ARRAY_URL_PARAM + "=" + jsonDiffArray,
      {method: 'POST'}).then((response) => response.text())
      .then((message) => {
        // TODO: take out, replace with success or error message
        console.log(message);
      });
}


