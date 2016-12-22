(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .controller('ReleaseDetailController', ReleaseDetailController);

    ReleaseDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Release', 'Installation', 'Project'];

    function ReleaseDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Release, Installation, Project) {
        var vm = this;

        vm.release = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('releasemanagerApp:releaseUpdate', function(event, result) {
            vm.release = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
