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
.factory 'Movie', ($resource) ->
  $resource 'api/:destination/:ctrl:id.json', {destination: "movies"}, {
    last: { method: 'GET', params: { ctrl: 'last', size: '@size' } },
    random: { method: 'GET', params: { ctrl: 'random', size: '@size' } },
    list: { method: 'GET', params: { ctrl: 'list', size: '@size' } },
    find: {method: 'GET', params: {destination: "movie"}}
  }

.factory 'Player', ($resource) ->
  $resource 'api/players/:cmd/:type:playerId/:mediaId:action/:path', {}, {
    play: { method: 'GET', params: { cmd: "play"}}
    resume: { method: 'GET', params: { cmd: "resume"}}
    playing: { method: 'GET', params: { cmd: "playing.json"}, isArray: true}
    ctrl: { method: 'GET', params: { cmd: "ctrl"}}
  }

.factory 'Media', ($resource) ->
  $resource 'api/medias/:request.json', {}, {
    inProgress: { method: 'GET', params: { request: "inProgress"}, isArray: true}
  }

.factory 'Admin', ($resource) ->
  $resource 'api/admin/:type/:id/:action', {}, {
    ctrl: { method: 'GET'}
  }