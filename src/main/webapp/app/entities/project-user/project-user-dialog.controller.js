(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .controller('ProjectUserDialogController', ProjectUserDialogController);

    ProjectUserDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProjectUser', 'Project', 'User'];

    function ProjectUserDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProjectUser, Project, User) {
        var vm = this;

        vm.projectUser = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.projects = Project.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.projectUser.id !== null) {
                ProjectUser.update(vm.projectUser, onSaveSuccess, onSaveError);
            } else {
                ProjectUser.save(vm.projectUser, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('releasemanagerApp:projectUserUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.created = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
