'use strict';

angular.module('bonobo.webapp')
    .controller('TerritoryViewCtrl', function ($scope, $routeParams, $location, $modal, TerritoryDao) {
        $scope.territoryFound = false;

        $scope.territory = TerritoryDao.get({code: $routeParams.code},
            function() {
                $scope.territoryFound = true;

            }, function() {
                $scope.$parent.alertMsg = 'Territory not found';
                $scope.territoryFound = false;
            }
        );

//      todo: UNIT - TEST
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
            $location.path('/territory/' + $scope.territory.code + '/edit');
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
                console.log("closed");
            }, function() {
                console.log("dismissed");
            });
        };

        $scope.hideContainer = function() {
            return $scope.territory.parent === undefined;
        };
    })
    .controller('ModalInstanceCtrl', function ($scope, $location, $modalInstance, territoryName) {
        $scope.territoryName = territoryName;

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };

        $scope.ok = function() {
            $modalInstance.close();
        };
    });