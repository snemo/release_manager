(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .controller('ReleaseDialogController', ReleaseDialogController);

    ReleaseDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Release', 'Installation', 'Project'];

    function ReleaseDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Release, Installation, Project) {
        var vm = this;

        vm.release = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.installations = Installation.query();
        vm.projects = Project.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.release.id !== null) {
                Release.update(vm.release, onSaveSuccess, onSaveError);
            } else {
                Release.save(vm.release, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('releasemanagerApp:releaseUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        vm.setFile = function ($file, release) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        release.file = base64Data;
                        release.fileContentType = $file.type;
                    });
                });
            }
        };

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
