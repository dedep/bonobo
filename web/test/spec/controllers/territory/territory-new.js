'use strict';

describe('Controller: TerritoryNewCtrl', function () {
  beforeEach(module('bonobo.webapp'));

  var controller, scope, mockBackend;

  beforeEach(inject(function($controller, $httpBackend){
    scope = {};
    controller = $controller('TerritoryNewCtrl', { $scope: scope });
    mockBackend = $httpBackend;
  }));

  it('should create new territory', function () {
    mockBackend.expectGET('http://localhost:9000/json/territory')
      .respond([
        {"id":4,"code":"W","name":"World","population":7243000000,"isCountry":false,"modifiable":false},
        {"id":9,"code":"PLMZ","name":"Mazowieckie","population":5164612,"parent":{"name":"Poland","code":"PL","id":3},
         "isCountry":false,"modifiable":true}]);

    expect(scope.territory.name).toBe("");
    expect(scope.territory.code).toBe("");
    expect(scope.territory.population).toBe("");
    expect(scope.territory.parent).toBeUndefined();
    expect(scope.territory.isCountry).toBeFalsy();

    mockBackend.flush();

    expect(scope.territory.parent.name).toBe("World");
    expect(scope.territory.parent.code).toBe("W");
  });
});
