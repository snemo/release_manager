(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .controller('InstanceDetailController', InstanceDetailController);

    InstanceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Instance', 'Installation', 'Project'];

    function InstanceDetailController($scope, $rootScope, $stateParams, previousState, entity, Instance, Installation, Project) {
        var vm = this;

        vm.instance = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('releasemanagerApp:instanceUpdate', function(event, result) {
            vm.instance = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
