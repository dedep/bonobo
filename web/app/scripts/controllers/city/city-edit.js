'use strict';

angular.module('bonobo.webapp')
  .controller('CityEditCtrl', function ($scope, CityDao, $routeParams, TerritoryDao, _, $location, toastr) {
    toastr.success('Hello world!', 'Toastr fun!');
    CityDao.get({tCode: $routeParams.tCode, cityId: $routeParams.cityId}).$promise
      .then(function(result) {
        if (!result.modifiable) {
          throw "Cannot edit unmodifiable city";
        }

        $scope.city = result;
        $scope.view = {
          title: "Edit " + $scope.city.name,
          submit: function() {
            CityDao.save({tCode: $routeParams.tCode, cityId: $routeParams.cityId}, $scope.city, function() {
              $location.path("territory/" + $scope.city.territory.id + "/city/" + $scope.city.id);
            });
          }
        };

        return TerritoryDao.query().$promise;
      })
      .then(function(result) {
        $scope.territories = _.reject(result, function(t) { return t.code === 'W' });
      })
      .catch(function(err) {
        $scope.$parent.alertMsg = err;
      });
  });
