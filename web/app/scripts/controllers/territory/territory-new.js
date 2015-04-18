'use strict';

angular.module('bonobo.webapp')
  .controller('TerritoryNewCtrl', function ($scope, TerritoryDao, $window, $location) {

    $scope.territory = {
      name: "",
      population: "",
      code: "",
      isCountry: false,
      modifiable: true
    };

    $scope.territories = TerritoryDao.query(function() {
      $scope.territory.parent = $scope.territories[0];
    });

    $scope.view = {
      title: function() {
        return "New territory";
      },

      submit: function() {
        TerritoryDao.save($scope.territory, function() {
          $location.path("/territory/");
        });
      },

      back: function() {
        $window.history.back();
      }
    };
  });
