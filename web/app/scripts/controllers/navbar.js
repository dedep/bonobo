'use strict';

angular.module('bonobo.webapp')
    .controller('NavBarCtrl', function ($scope, $location, hotkeys) {
        hotkeys.bindTo($scope)
            .add({
                combo: 'alt+a',
                description: 'About page',
                callback: function() {
                    $location.path('/about');
                }
            })
            .add({
                combo: 'alt+w',
                description: 'World page',
                callback: function() {
                    $location.path('/territory/world');
                }
            })
            .add({
                combo: 'alt+h',
                description: 'Home page',
                callback: function() {
                    $location.path('/');
                }
            });

        $scope.toggleHelp = function() {
            hotkeys.toggleCheatSheet();
        };
    });