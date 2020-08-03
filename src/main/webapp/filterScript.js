/**
* Turn a filter a different color when pressed, and change class accordingly.
**/
function turnBlueWhenClicked() {
  const allFilters = document.querySelectorAll('.unclicked-filter');
  for (let i = 0; i < allFilters.length; i++) {
    allFilters[i].onclick = function() {
      const currFilter = allFilters[i];
      if (currFilter.classList.contains('unclicked-filter')) {
        currFilter.classList.remove('unclicked-filter');
        currFilter.classList.add('clicked-filter');
      } else {
        currFilter.classList.remove('clicked-filter');
        currFilter.classList.add('unclicked-filter');
      }
    };
  }
}

/**
* Get all filters that have been clicked when user presses submit button,
* and pass array to servlet, and TODO: return success or error message.
**/
function getClickedFilters() {
  const clickedArray = [];
  const clickedFilters = document.querySelectorAll('.clicked-filter');
  for (let i = 0; i < clickedFilters.length; i++) {
    clickedArray[i] = clickedFilters[i].innerHTML;
  }

  const JSONArray = JSON.stringify(clickedArray);
  console.log(JSONArray);
  fetch('/generate?clicked-array=' + JSONArray, {method: 'POST'}).then(
    response => response.json()).then((message) => {
    console.log(message);
  });
}


