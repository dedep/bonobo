'use strict';

angular
    .module('bonobo.webapp', [
        'ngResource',
        'ngRoute',
        'ui.bootstrap',
        'cfp.hotkeys',
        'utils.ajaxloader',
        'utils.autofocus',
        'utils.world-map',
        'bonobo.services'
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
            .when('/territory/new', {
                templateUrl: 'views/territory/territory-edit.html',
                controller: 'TerritoryEditCtrl'
            })
            .when('/territory/:code', {
                templateUrl: 'views/territory/territory-view.html',
                controller: 'TerritoryViewCtrl'
            })
            .when('/territory/:code/edit', {
                templateUrl: 'views/territory/territory-edit.html',
                controller: 'TerritoryEditCtrl'
            })
            .when('/territory', {
                templateUrl: 'views/territory/territory-all.html',
                controller: 'TerritoryAllCtrl'
            })
            .otherwise({
                redirectTo: '/'
            });
    });
