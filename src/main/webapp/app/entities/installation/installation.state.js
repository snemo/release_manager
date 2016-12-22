(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('installation', {
            parent: 'entity',
            url: '/installation?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'releasemanagerApp.installation.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/installation/installations.html',
                    controller: 'InstallationController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('installation');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('installation-detail', {
            parent: 'entity',
            url: '/installation/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'releasemanagerApp.installation.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/installation/installation-detail.html',
                    controller: 'InstallationDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('installation');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Installation', function($stateParams, Installation) {
                    return Installation.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'installation',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('installation-detail.edit', {
            parent: 'installation-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/installation/installation-dialog.html',
                    controller: 'InstallationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Installation', function(Installation) {
                            return Installation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('installation.new', {
            parent: 'installation',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/installation/installation-dialog.html',
                    controller: 'InstallationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                date: null,
                                notes: null,
                                success: null,
                                adapterErrors: null,
                                configErrors: null,
                                otherErrors: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('installation', null, { reload: 'installation' });
                }, function() {
                    $state.go('installation');
                });
            }]
        })
        .state('installation.edit', {
            parent: 'installation',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/installation/installation-dialog.html',
                    controller: 'InstallationDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Installation', function(Installation) {
                            return Installation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('installation', null, { reload: 'installation' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('installation.delete', {
            parent: 'installation',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/installation/installation-delete-dialog.html',
                    controller: 'InstallationDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Installation', function(Installation) {
                            return Installation.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('installation', null, { reload: 'installation' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
