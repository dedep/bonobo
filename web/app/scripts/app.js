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
    'underscore',
    'Orbicular',
    'toastr'
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
      .when('/territory/:tCode/city/new', {
        templateUrl: 'views/city/city-edit.html',
        controller: 'CityNewCtrl'
      })
      .when('/territory/:tCode/city/:cityId', {
        templateUrl: 'views/city/city-view.html',
        controller: 'CityViewCtrl'
      })
      .when('/territory/:tCode/city', {
        templateUrl: 'views/city/city-all.html',
        controller: 'CityAllCtrl'
      })
      .when('/territory/:tCode/city/:cityId/edit', {
        templateUrl: 'views/city/city-edit.html',
        controller: 'CityEditCtrl'
      })
      .otherwise({
        redirectTo: '/'
      });
  })
  .run(function($rootScope, $window){
    $rootScope.back = function() {
      $window.history.back();
    }
  })
  .controller('RootCtrl', function ($scope) {
    $scope.$on('$routeChangeSuccess', function() {
      $scope.alertMsg = null;
    });
  })
  .config(function(toastrConfig) {
    angular.extend(toastrConfig, {
      allowHtml: false,
      closeButton: false,
      closeHtml: '<button>&times;</button>',
      containerId: 'toast-container',
      extendedTimeOut: 1000,
      iconClasses: {
        error: 'toast-error',
        info: 'toast-info',
        success: 'toast-success',
        warning: 'toast-warning'
      },
      maxOpened: 0,
      messageClass: 'toast-message',
      newestOnTop: true,
      onHidden: null,
      onShown: null,
      positionClass: 'toast-top-right',
      preventDuplicates: false,
      progressBar: false,
      tapToDismiss: true,
      target: 'body',
      templates: {
        toast: 'directives/toast/toast.html',
        progressbar: 'directives/progressbar/progressbar.html'
      },
      timeOut: 5000,
      titleClass: 'toast-title',
      toastClass: 'toast'
    });
  });
