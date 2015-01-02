'use strict';

angular.module('bonobo.webapp')
    .controller('TerritoryAllCtrl', function ($scope, $location) {
        $scope.territories = {
            name: "World",
            code: "W",
            children: [{
                name: "Europe",
                code: "EU",
                children: [{
                    name: "Lithuania",
                    code: "LT"
                }, {
                    name: "Poland",
                    code: "PL",
                    children: [{
                        name: "Lubelskie",
                        code: "PLLU",
                        children: [{
                            name: "Powiat lubelski",
                            code: "PLLUB"
                        }]
                    }, {
                        name: "Podkarpackie",
                        code: "PLPK",
                        children: [{
                            name: "Powiat bieszczadzki",
                            code: "PLPKB"
                        }]
                    }, {
                        name: "Mazowieckie",
                        code: "PLMZ"
                    }]
                }]}, {
                name: "Oceania",
                code: "O",
                children: [{
                    name: "Australia",
                    code: "AU"
                }]
            }]
        };

        $scope.addTerritory = function() {
            $location.path('/territory/new');
        }
    });