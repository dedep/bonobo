'use strict';

angular.module('bonobo.webapp')
    .controller('TerritoryEditCtrl', function ($scope, $routeParams, $location, TerritoryDao) {
        $scope.codeToEdit = $routeParams.code;

        $scope.isInEditMode = ($scope.codeToEdit != undefined);

        $scope.territoryFound = function() {
            return $scope.territory !== undefined && $scope.territory.name !== undefined;
        };

        if ($scope.isInEditMode) {
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
        } else {
            $scope.territory = {
                name: "",
                population: "",
                code: "",
                parent: {
                    name: "World",
                    code: "W"
                }
            };
        }

        $scope.territories = [{
            name: "World",
            code: "W"
        }, {
            name: "Poland",
            code: "PL"
        }];

        $scope.view = {
            title: function() {
                if ($scope.isInEditMode) {
                    if ($scope.territoryFound()) {
                        return "Edit " + $scope.territory.name;
                    } else {
                        return "Edit ...";
                    }
                } else {
                    return "New territory";
                }
            },

            back: function() {
                if ($scope.isInEditMode) {
                    $location.path("/territory/" + $scope.territory.code);
                } else {
                    $location.path("/territory")
                }
            }
        };
    });