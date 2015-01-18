'use strict';

angular.module('utils.ajaxloader', [])
  .directive('ajaxloader', ['$rootScope', function() {
    return {
      scope: {
        enabled: '@'
      },
      restrict: 'A',
      link : function($scope, $element) {
        $scope.$watch('enabled', function() {
          if ($scope.enabled === 'true') {
            $element.addClass('ajaxloader');
          } else {
            $element.removeClass('ajaxloader');
          }
        });
      }
    };
  }]);
