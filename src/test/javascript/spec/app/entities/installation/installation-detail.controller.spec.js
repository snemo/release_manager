'use strict';

describe('Controller Tests', function() {

    describe('Installation Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockInstallation, MockRelease, MockInstance;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockInstallation = jasmine.createSpy('MockInstallation');
            MockRelease = jasmine.createSpy('MockRelease');
            MockInstance = jasmine.createSpy('MockInstance');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Installation': MockInstallation,
                'Release': MockRelease,
                'Instance': MockInstance
            };
            createController = function() {
                $injector.get('$controller')("InstallationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'releasemanagerApp:installationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
