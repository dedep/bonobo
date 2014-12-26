'use strict';

angular.module('bonobo.webapp')
    .controller('TerritoryCtrl', function ($scope, $routeParams) {

        $scope.territories = [{
            id: 100,
            code: '',
            name: 'World',
            population: 53242383,
            recipients: ['greg@somecompany.com'],
            latLng: [21.90, 51.45],
            message: 'Hey, we should get together for lunch sometime and catch up.'
        }, {
            id: 8,
            code: 'SK',
            name: 'maria@somecompany.com',
            subject: 'Where did you leave my laptop?',
            date: 'Dec 7, 2013 8:15:12', recipients: ['greg@somecompany.com'],
            message: 'I thought you were going to put it in my desk drawer.'
        }, {
            id: 9,
            code: 'AU',
            name: 'Australia',
            population: 53242383,
            container: {
                name: 'World',
                code: 'world'
            }
        }];

        var territoryRouter = function(arg) {
            if (arg === 'world') {
                return $scope.territories[0];
            } else { // getFromCode
                return $scope.territories[2];
            }
        };

        $scope.territory = territoryRouter($routeParams.id);

        $scope.hideContainer = function() {
            return $scope.territory.container === undefined;
        }
    });