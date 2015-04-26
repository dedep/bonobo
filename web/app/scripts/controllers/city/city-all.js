'use strict';

angular.module('bonobo.webapp')
  .controller('CityAllCtrl', function ($scope, $routeParams, CityDao, TerritoryDao, $location) {
    $scope.cities = CityDao.findAll({tCode: $routeParams.tCode});
    $scope.territory = TerritoryDao.get({code: $routeParams.tCode});

    $scope.addCity = function() {
      $location.path("/territory/" + $routeParams.tCode + "/city/new");
    }
  });
