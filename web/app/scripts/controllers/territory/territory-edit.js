'use strict';

angular.module('bonobo.webapp')
    .controller('TerritoryEditCtrl', function ($scope, $routeParams, $location) {
        $scope.idToEdit = $routeParams.code;
        $scope.isInEditMode = ($scope.idToEdit != undefined);

        if ($scope.isInEditMode) {
            $scope.territory = {
                name: "Australia",
                population: 9028345,
                code: "AU",
                parent: {
                    name: "World",
                    code: "W"
                }
            };
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

        $scope.view = {
            title: function() {
                if ($scope.isInEditMode) {
                    return "Edit " + $scope.territory.name;
                } else {
                    return "New territory";
                }
            },

            territories: [{
                name: "World",
                code: "W"
            }, {
                name: "Poland",
                code: "PL"
            }],

            back: function() {
                if ($scope.isInEditMode) {
                    $location.path("/territory/" + $scope.territory.code);
                } else {
                    $location.path("/territory")
                }
            }
        };

    });