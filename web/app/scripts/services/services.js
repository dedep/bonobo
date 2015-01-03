'use strict';

var serverUrl = 'http://localhost:9000/json';

angular.module('bonobo.services', ['ngResource'])
    .factory('TerritoryDao', ['$resource',
        function($resource) {
            return $resource(serverUrl + '/territory/:code');
        }
    ]);