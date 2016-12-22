(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .controller('ProjectUserDetailController', ProjectUserDetailController);

    ProjectUserDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectUser', 'Project', 'User'];

    function ProjectUserDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectUser, Project, User) {
        var vm = this;

        vm.projectUser = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('releasemanagerApp:projectUserUpdate', function(event, result) {
            vm.projectUser = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
