'use strict';

angular.module('bonobo.webapp')
    .controller('TerritoryEditCtrl', function ($scope, $routeParams, $location, TerritoryDao) {
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
        }, function() {
            $scope.$parent.alertMsg = 'Territory not found';
        });

        $scope.territories = [{
            name: "World",
            code: "W",
            id: 4
        }, {
            name: "Poland",
            code: "PL",
            id: 3
        }];

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
                    $location.path("/territory/" + $scope.territory.code);
                });
            },

            back: function() {
                $location.path("/territory/" + $scope.territory.code);
            }
        };
    });