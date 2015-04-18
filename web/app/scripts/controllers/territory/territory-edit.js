'use strict';

angular.module('bonobo.webapp')
  .controller('TerritoryEditCtrl', function ($scope, $routeParams, $location, TerritoryDao, _) {
    $scope.codeToEdit = $routeParams.code;

    $scope.territoryFound = function() {
      return $scope.territory !== undefined && $scope.territory.name !== undefined;
    };

    var checkIfTerritoryCouldNotBeEdited = function() {
      return !$scope.territoryFound || !$scope.territory.modifiable;
    };

    $scope.territory = TerritoryDao.get({code: $scope.codeToEdit}, function() {
      if (checkIfTerritoryCouldNotBeEdited()) {
        $scope.$parent.alertMsg = 'Cannot edit specified territory';
      }

      $scope.allTerritories = TerritoryDao.query(function() {
        $scope.territories = _.reject($scope.allTerritories, function(t) {
          return t.code === $scope.territory.code;
        });
      });

    }, function() {
      $scope.$parent.alertMsg = 'Territory not found';
    });


    $scope.view = {
      title: function() {
        if ($scope.territoryFound()) {
          return "Edit " + $scope.territory.name;
        } else {
          return "Edit ...";
        }
      },

      submit: function() {
        TerritoryDao.save({code: $scope.codeToEdit}, $scope.territory, function() {
          $location.path("/territory/" + $scope.territory.id);
        });
      },

      back: function() {
        $location.path("/territory/" + $scope.territory.id);
      }
    };
  })
  .directive('codeValidator', function (_) {
    return {
      require: 'ngModel',
      link: function (scope, elm, attrs, ctrl) {
        scope.$watch('territory.code', function() {
          var sameCodeTerritories = _.where(scope.territories, {code: ctrl.$viewValue});
          ctrl.$setValidity('occupiedCode', sameCodeTerritories.length === 0);
        });
      }
    };
  });
