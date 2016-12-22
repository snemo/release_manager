(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .controller('InstallationDialogController', InstallationDialogController);

    InstallationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Installation', 'Release', 'Instance'];

    function InstallationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Installation, Release, Instance) {
        var vm = this;

        vm.installation = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.releases = Release.query();
        vm.instances = Instance.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.installation.id !== null) {
                Installation.update(vm.installation, onSaveSuccess, onSaveError);
            } else {
                Installation.save(vm.installation, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('releasemanagerApp:installationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
