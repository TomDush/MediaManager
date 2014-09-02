'use strict';

angular.module 'mediamanager.services.settings', [ 'ngResource' ]
.factory 'RootDirectory', ($resource) ->
  $resource '/api/directories/list.json', {},
    list: { method: 'GET', isArray: true }
