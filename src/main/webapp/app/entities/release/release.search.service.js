(function() {
    'use strict';

    angular
        .module('releasemanagerApp')
        .factory('ReleaseSearch', ReleaseSearch);

    ReleaseSearch.$inject = ['$resource'];

    function ReleaseSearch($resource) {
        var resourceUrl =  'api/_search/releases/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
