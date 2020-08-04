const UNCLICKED = 'unclicked-filter';
const CLICKED = 'clicked-filter';
const CLICKED_ARRAY_URL = 'clicked-array=';

/**
* Turn a filter a different color when pressed, and change class accordingly.
**/
function turnBlueWhenClicked() { //eslint-disable-line
  const allFilters = document.querySelectorAll('.unclicked-filter');
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
**/
function getClickedFilters() { //eslint-disable-line
  const clickedArray = [];
  const clickedFilters = document.querySelectorAll('.clicked-filter');
  for (let i = 0; i < clickedFilters.length; i++) {
    clickedArray[i] = clickedFilters[i].innerHTML;
  }

  const JSONArray = JSON.stringify(clickedArray);
  fetch('/generate?' + CLICKED_ARRAY_URL + JSONArray, {method: 'POST'}).then(
      (response) => response.json()).then((message) => {
    // TODO: take out, replace with success or error message
    console.log(message); 
  });
}


