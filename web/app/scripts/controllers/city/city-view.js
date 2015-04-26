'use strict';

angular.module('bonobo.webapp')
  .controller('CityViewCtrl', function ($scope, $routeParams, $location, CityDao, GeoLocation) {
    $scope.city = CityDao.get({
      tCode: $routeParams.tCode,
      cityId: $routeParams.cityId
    }, function() {
      $scope.markers = [{
        latLng: [$scope.city.latitude, $scope.city.longitude],
        name: $scope.city.name
      }];

      $scope.displayLongitude = GeoLocation.getLongitudeDesc($scope.city.longitude);
      $scope.displayLatitude = GeoLocation.getLatitudeDesc($scope.city.latitude);

      $scope.cityFound = true;
    }, function() {
      $scope.$parent.alertMsg = 'City not found';
    });

    $scope.newCity = function() {
      $location.path("/territory/" + $routeParams.tCode + "/city/new");
    }
  });
