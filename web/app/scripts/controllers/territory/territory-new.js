'use strict';

angular.module('bonobo.webapp')
    .controller('TerritoryNewCtrl', function ($scope, $location) {
        $scope.territory = {
            name: "",
            population: "",
            code: "",
            parent: {
                name: "World",
                code: "W"
            }
        };

        $scope.territories = [{
            name: "World",
            code: "W"
        }, {
            name: "Poland",
            code: "PL"
        }];

        $scope.view = {
            title: function() {
                return "New territory";
            },

            submit: function() {

            },

            back: function() {
                $location.path("/territory")
            }
        };
    });