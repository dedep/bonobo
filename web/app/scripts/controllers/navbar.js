'use strict';

angular.module('bonobo.webapp')
  .controller('NavBarCtrl', function ($scope, $location, hotkeys, configuration) {
    hotkeys.bindTo($scope)
      .add({
        combo: 'shift+a',
        description: 'About page',
        callback: function() {
          $location.path('/about');
        }
      })
      .add({
        combo: 'shift+h',
        description: 'Home page',
        callback: function() {
          $location.path('/');
        }
      })
      .add({
        combo: 'shift+t',
        description: 'Territories page',
        callback: function() {
          $location.path('/territory');
        }
      })
      .add({
        combo: 'shift+ins',
        description: 'New territory',
        callback: function() {
          $location.path('/territory/new');
        }
      })
      .add({
        combo: 'shift+w',
        description: 'World page',
        callback: function() {
          $location.path('/territory/' + configuration.WORLD_CODE);
        }
      });

    $scope.toggleHelp = function() {
      hotkeys.toggleCheatSheet();
    };
  });
