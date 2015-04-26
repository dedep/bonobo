'use strict';

angular.module('utils.world-map', [])
  .directive('bonoboWorldMap', ['$location', '$timeout',
    function($location, $timeout) {
      return {
        scope: {
          code: '=',
          markers: '='
        },
        link: function(scope, element) {
          scope.$on('$routeChangeStart', function() {
            $('.jvectormap-label').remove();
          });

          element.vectorMap({
            map: 'world_mill_en',
            scaleColors: ['#C8EEFF', '#0071A4'],
            normalizeFunction: 'polynomial',
            hoverOpacity: 0.7,
            zoomMax: 100,
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

          var map = element.vectorMap('get', 'mapObject');

          scope.$watch('code', function() {
            if (scope.code) {
              $timeout(function() {
                map.setFocus([scope.code]);
              })
            }
          });

          scope.$watch('markers', function() {
            if (scope.markers) {
              $timeout(function() {
                map.addMarkers(scope.markers);
              })
            }
          });
        }
      };
    }]);
