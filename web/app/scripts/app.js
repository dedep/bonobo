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
    'underscore'
  ])
  .constant('configuration', {
    SERVER_URL: 'http://localhost:9000/json',
    WORLD_CODE: 'W'
  })
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
        controller: 'TerritoryNewCtrl'
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
      .when('/territory/:tCode/city/:cityId', {
        templateUrl: 'views/city/city-view.html',
        controller: 'CityViewCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  })
  .controller('RootCtrl', function ($scope) {
    $scope.$on('$routeChangeSuccess', function() {
      $scope.alertMsg = null;
    });
  });
