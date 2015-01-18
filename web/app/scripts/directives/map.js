'use strict';

angular.module('utils.world-map', [])
  .directive('bonoboWorldMap', ['$location',
    function($location) {
      return {
        scope: {
          code: '@',
          markers: '@'
        },
        link: function(scope, element) {
          scope.$on('$routeChangeStart', function() {
            $('.jvectormap-label').remove();
          });

          scope.$watch('code', function() {
            $('.jvectormap-container').remove();

            element.vectorMap({
              map: 'world_mill_en',
              scaleColors: ['#C8EEFF', '#0071A4'],
              normalizeFunction: 'polynomial',
              hoverOpacity: 0.7,
              zoomMax: 100,
              focusOn: scope.code,
              markers: scope.markers,
              regionsSelectable: true,
              hoverColor: false,
              markerStyle: {
                initial: {
                  fill: '#F8E23B',
                  stroke: '#383f47'
                }
              },
              backgroundColor: '#ffffff',
              regionStyle: {
                initial: {
                  fill: '#B8E186'
                },
                selected: {
                  fill: '#B8E186'
                }
              },
              onRegionSelected: function(e, code, isSelected) {
                if (isSelected) {
                  scope.$apply(function() {
                    $location.path('/territory/' + code);
                  });
                }
              }
            });
          });
        }
      };
    }]);
