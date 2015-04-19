'use strict';

angular.module('bonobo.webapp')
  .controller('TerritoryViewCtrl', function ($scope, $routeParams, $location, $modal, TerritoryDao, configuration, hotkeys) {

    $scope.territoryFound = false;

    $scope.territory = TerritoryDao.get({code: $routeParams.code}, function() {
        $scope.territoryFound = true;
        $scope.appendHotKeys();
      }, function() {
        $scope.$parent.alertMsg = 'Territory not found';
        $scope.territoryFound = false;
      }
    );

    $scope.codeToFocus = function() {
      if ($scope.territoryFound) {
        if ($scope.territory.isCountry) {
          return $scope.territory.code;
        } else {
          return "";
        }
      } else {
        return "none";
      }
    };

    $scope.addTerritory = function() {
      $location.path('/territory/new');
    };

    $scope.editTerritory = function() {
      $location.path('/territory/' + $scope.territory.id + '/edit');
    };

    $scope.deleteTerritory = function() {
      var modalInstance = $modal.open({
        templateUrl: "removeTerritoryModal.html",
        controller: "ModalInstanceCtrl",
        resolve: {
          territoryName: function() {
            return $scope.territory.name;
          }
        }
      });

      modalInstance.result.then(function() {
        TerritoryDao.delete({code: $routeParams.code});
        $location.path('/territory/' + configuration.WORLD_CODE);
      });
    };

    $scope.hideContainer = function() {
      return $scope.territory.parent === undefined;
    };

    $scope.appendHotKeys = function() {
      if ($scope.territory.modifiable) {
        hotkeys.bindTo($scope)
          .add({
            combo: 'shift+del',
            description: 'Delete territory',
            callback: function () {
              $scope.deleteTerritory();
            }
          })
          .add({
            combo: 'shift+e',
            description: 'Edit territory',
            callback: function () {
              $scope.editTerritory();
            }
          });
      }
    }
  })
  .controller('ModalInstanceCtrl', function ($scope, $location, $modalInstance, territoryName) {
    $scope.territoryName = territoryName;

    $scope.cancel = function() {
      $modalInstance.dismiss();
    };

    $scope.ok = function() {
      $modalInstance.close();
    };
  })
  .directive('fallbackSrc', function () {
    var fallbackSrc = {
      link: function postLink(scope, iElement, iAttrs) {
        iElement.bind('error', function() {
          angular.element(this).attr("src", iAttrs.fallbackSrc);
        });
      }
    };
    return fallbackSrc;
  });
