'use strict';

angular.module('bonobo.webapp')
  .controller('CityViewCtrl', function ($scope, $routeParams, $location, CityDao) {
    $scope.city = CityDao.get({
      tCode: $routeParams.tCode,
      cityId: $routeParams.cityId
    }, function() {
      $scope.markers = [{
        latLng: [$scope.city.latitude, $scope.city.longitude],
        name: $scope.city.name
      }];
    });
  });
