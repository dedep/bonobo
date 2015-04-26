'use strict';

angular.module('bonobo.webapp')
  .controller('CityNewCtrl', function ($scope, $routeParams, TerritoryDao, $window, CityDao, $location, _) {

    $scope.city = {
      name: "",
      population: "",
      latitude: "",
      longitude: "",
      territory: TerritoryDao.get({code: $routeParams.tCode}),
      points: 0,
      modifiable: false
    };

    TerritoryDao.query(function(result) {
      $scope.territories = _.reject(result, function(t) { return t.code === 'W' })
    });

    $scope.view = {
      title: "New city",
      submit: function() {
        CityDao
          .create({tCode: $routeParams.tCode}, $scope.city).$promise
          .then(function(res) {
            $location.path("territory/" + $scope.city.territory.id + "/city/" + res.id)
          })
          .catch(function(err) { console.error("Unable to create city. " + err) })
      }
    }
  });
