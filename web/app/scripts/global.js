'use strict';

$(document).keyup(function(e) {
  if (e.keyCode === 27) {
    e.target.blur();
  }
});

