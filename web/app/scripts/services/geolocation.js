'use strict';

angular.module('bonobo.webapp')
  .factory('GeoLocation', [function() {
      return {
        getLongitudeDesc: function(longitude) {
          longitude = longitude.toFixed(1);

          if (longitude < 0) {
            return (longitude * -1) + 'W'
          } else {
            return longitude + 'E'
          }
        },
        getLatitudeDesc: function(latitude) {
          latitude = latitude.toFixed(1);

          if (latitude < 0) {
            return (latitude * -1) + 'S'
          } else {
            return latitude + 'N'
          }
        }
      }
    }
  ]);
