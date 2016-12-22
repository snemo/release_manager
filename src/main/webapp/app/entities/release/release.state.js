(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('release', {
            parent: 'entity',
            url: '/release?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'releasemanagerApp.release.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/release/releases.html',
                    controller: 'ReleaseController',
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
                    $translatePartialLoader.addPart('release');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('release-detail', {
            parent: 'entity',
            url: '/release/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'releasemanagerApp.release.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/release/release-detail.html',
                    controller: 'ReleaseDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('release');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Release', function($stateParams, Release) {
                    return Release.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'release',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('release-detail.edit', {
            parent: 'release-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/release/release-dialog.html',
                    controller: 'ReleaseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Release', function(Release) {
                            return Release.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('release.new', {
            parent: 'release',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/release/release-dialog.html',
                    controller: 'ReleaseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                date: null,
                                commitId: null,
                                config: null,
                                adapters: null,
                                otherSteps: null,
                                file: null,
                                fileContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('release', null, { reload: 'release' });
                }, function() {
                    $state.go('release');
                });
            }]
        })
        .state('release.edit', {
            parent: 'release',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/release/release-dialog.html',
                    controller: 'ReleaseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Release', function(Release) {
                            return Release.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('release', null, { reload: 'release' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('release.delete', {
            parent: 'release',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/release/release-delete-dialog.html',
                    controller: 'ReleaseDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Release', function(Release) {
                            return Release.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('release', null, { reload: 'release' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
