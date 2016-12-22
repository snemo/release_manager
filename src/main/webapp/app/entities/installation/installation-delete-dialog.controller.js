(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .controller('InstallationDeleteController',InstallationDeleteController);

    InstallationDeleteController.$inject = ['$uibModalInstance', 'entity', 'Installation'];

    function InstallationDeleteController($uibModalInstance, entity, Installation) {
        var vm = this;

        vm.installation = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Installation.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
