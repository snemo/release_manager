(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .factory('InstallationSearch', InstallationSearch);

    InstallationSearch.$inject = ['$resource'];

    function InstallationSearch($resource) {
        var resourceUrl =  'api/_search/installations/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
