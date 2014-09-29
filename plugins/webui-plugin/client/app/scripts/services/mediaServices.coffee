'use strict';

# var request = { filter  : {
#   order: 'alpha', # 'last', 'random'
#   seen: true,
#   unseen: true,
#   title:'',
#   crew:'',
#   genres: ['action']},
#   size: 10, # number of expected elements
# pagination: {
#   index: 2, # page to display
#   pageSize: 10 # element by page
# };

# var result = {
#   page: 1,   # 0 if no pagination possible
#   pageSize: 10,  # elements by page
#   number: 5  # Number of pages
#   size: 48,  # Number of available elements
#   elements: [...] # Elements !
# }

angular.module 'mediaServices', [ 'ngResource' ]
.factory 'Genre', ($http) ->
  query: (callback) ->
    $http.get('/api/medias/genres').success callback

.factory 'Movie', ($resource) ->
  $resource '/api/:destination/:order:id', {destination: "movies"}, {
    find: {method: 'GET', params: {destination: "movie"}}
    query: {method: 'GET', isArray: false}

    last: { method: 'GET', params: { order: 'LAST', size: '@size' } },
    random: { method: 'GET', params: { order: 'RANDOM', size: '@size' } },
    list: { method: 'GET', params: { order: 'LIST', size: '@size' } },
  }

.factory 'Player', ($resource) ->
  $resource '/api/players/:cmd/:type:playerId/:mediaId:action/:path', {}, {
    play: { method: 'GET', params: { cmd: "play"}}
    resume: { method: 'GET', params: { cmd: "resume"}}
    playing: { method: 'GET', params: { cmd: "playing"}, isArray: true}
    ctrl: { method: 'GET', params: { cmd: "ctrl"}}
  }

.factory 'Media', ($resource) ->
  $resource '/api/medias/:request', {}, {
    inProgress: { method: 'GET', params: { request: "inProgress"}, isArray: true}
  }

.factory 'Admin', ($resource) ->
  $resource '/api/admin/:type/:id/:action', {}, {
    ctrl: { method: 'GET'}
  }
