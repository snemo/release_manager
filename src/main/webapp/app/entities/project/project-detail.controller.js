(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Project', 'Release', 'Instance'];

    function ProjectDetailController($scope, $rootScope, $stateParams, previousState, entity, Project, Release, Instance) {
        var vm = this;

        vm.project = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('releasemanagerApp:projectUpdate', function(event, result) {
            vm.project = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
