'use strict';

angular.module('bonobo.webapp')
  .factory('CityDao', ['$resource', 'configuration',
    function($resource, configuration) {
      return $resource(configuration.SERVER_URL + '/territory/:tCode/city/:cityId', {},
        {
          findAll: {method: 'GET', isArray: true},
          create: {method: 'POST'}
        }
      );
    }
  ]);
