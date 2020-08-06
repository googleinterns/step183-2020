const FILTER = '.filter';
const UNCLICKED = 'unclicked-filter';
const CLICKED = 'clicked-filter';
const CLICKED_ARRAY_URL_PARAM = 'clicked-array';

/**
* Swap between "clicked" and "unclicked" classes
* (which change the color of the button) when the button is pressed.
* Note: function declaration disabled because the function is called
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
* Note: function declaration disabled because the function is called from an
* onclick attribute in generateHunt.html.
**/
function sendClickedFiltersToServer() { //eslint-disable-line
  const clickedArray = [];
  const clickedFilters = document.querySelectorAll('.' + CLICKED);
  for (let i = 0; i < clickedFilters.length; i++) {
    clickedArray[i] = clickedFilters[i].innerText;
  }

  const jsonArray = JSON.stringify(clickedArray);
  fetch('/generate-hunt?' + CLICKED_ARRAY_URL_PARAM + '=' + jsonArray,
      {method: 'POST'}).then((response) => response.text())
      .then((message) => {
        // TODO: take out, replace with success or error message
        console.log(message);
      });
}


