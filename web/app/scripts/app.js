'use strict';

/**
 * @ngdoc overview
 * @name webApp
 * @description
 * # webApp
 *
 * Main module of the application.
 */
angular
    .module('bonobo.webapp', [
        'ngResource',
        'ngRoute',
        'ui.bootstrap',
        'cfp.hotkeys',
        'bonobo.webapp.directives'
    ])
    .config(function ($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'views/main.html',
                controller: 'MainCtrl'
            })
            .when('/about', {
                templateUrl: 'views/about.html',
                controller: 'AboutCtrl'
            })
            .when('/territory/:id', {
                templateUrl: 'views/territory.html',
                controller: 'TerritoryCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    });
