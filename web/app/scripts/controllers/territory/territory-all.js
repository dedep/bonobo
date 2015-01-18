'use strict';

angular.module('bonobo.webapp')
  .controller('TerritoryAllCtrl', function ($scope, $location, TerritoryDao) {
    $scope.territories = TerritoryDao.query();

    $scope.addTerritory = function() {
      $location.path('/territory/new');
    };
  });
