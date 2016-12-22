(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .controller('InstallationDetailController', InstallationDetailController);

    InstallationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Installation', 'Release', 'Instance'];

    function InstallationDetailController($scope, $rootScope, $stateParams, previousState, entity, Installation, Release, Instance) {
        var vm = this;

        vm.installation = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('releasemanagerApp:installationUpdate', function(event, result) {
            vm.installation = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
