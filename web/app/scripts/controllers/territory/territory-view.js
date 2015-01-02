'use strict';

angular.module('bonobo.webapp')
    .controller('TerritoryViewCtrl', function ($scope, $routeParams, $location, $modal) {
        $scope.territories = [{
            id: 100,
            code: 'W',
            isCountry: false,
            canChange: false,
            name: 'World',
            population: 53242383,
            recipients: ['greg@somecompany.com'],
            latLng: [21.90, 51.45],
            message: 'Hey, we should get together for lunch sometime and catch up.'
        }, {
            id: 8,
            code: 'SK',
            isCountry: true,
            canChange: true,
            name: 'SLOVAKIA',
            subject: 'Where did you leave my laptop?',
            date: 'Dec 7, 2013 8:15:12', recipients: ['greg@somecompany.com'],
            message: 'I thought you were going to put it in my desk drawer.'
        }, {
            id: 9,
            code: 'AU',
            isCountry: true,
            canChange: true,
            name: 'Australia',
            population: 53242383,
            container: {
                name: 'World',
                code: 'W'
            }
        }];

        var territoryRouter = function(arg) {
            for (var i = 0; i < $scope.territories.length; i++) {
                var t = $scope.territories[i];

                if (arg === t.code) {
                    return t;
                }
            }
        };

        $scope.territory = territoryRouter($routeParams.code);

        function View() {
            this.territoryFound = $scope.territory !== undefined;

            this.addTerritory = function() {
                $location.path('/territory/new');
            };

            if (this.territoryFound) {
                this.codeToFocus = function() {
                    if ($scope.territory.isCountry) {
                        return $scope.territory.code;
                    } else {
                        return "";
                    }
                };

                this.hideContainer = function() {
                    return $scope.territory.container === undefined;
                };

                this.editTerritory = function() {
                    $location.path('/territory/' + $scope.territory.code + '/edit');
                };

                this.deleteTerritory = function() {
                    var modalInstance = $modal.open({
                        templateUrl: "removeTerritoryModal.html",
                        controller: "ModalInstanceCtrl",
                        resolve: {
                            territoryName: function() {
                                return $scope.territory.name;
                            }
                        }
                    });

                    modalInstance.result.then(function() {
                        console.log("closed");
                    }, function() {
                        console.log("dismissed");
                    });
                }
            }
        }

        $scope.view = new View();
    })
    .controller('ModalInstanceCtrl', function ($scope, $location, $modalInstance, territoryName) {
        $scope.territoryName = territoryName;

        $scope.cancel = function() {
            $modalInstance.dismiss();
        };

        $scope.ok = function() {
            $modalInstance.close();
        };
    });