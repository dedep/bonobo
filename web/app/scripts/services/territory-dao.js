'use strict';

angular.module('bonobo.webapp')
  .factory('TerritoryDao', ['$resource', 'configuration',
    function($resource, configuration) {
      return $resource(configuration.SERVER_URL + '/territory/:code');
    }
  ]);
