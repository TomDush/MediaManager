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
  $resource '/api/:destination/:ctrl:id.json', {destination: "movies"}, {
    last: { method: 'GET', params: { ctrl: 'last', size: '@size' } },
    random: { method: 'GET', params: { ctrl: 'random', size: '@size' } },
    list: { method: 'GET', params: { ctrl: 'list', size: '@size' } },
    find: {method: 'GET', params: {destination: "movie"}}
  }

.factory 'Player', ($resource) ->
  $resource '/api/players/:cmd/:type:playerId/:mediaId:action/:path', {}, {
    play: { method: 'GET', params: { cmd: "play"}}
    resume: { method: 'GET', params: { cmd: "resume"}}
    playing: { method: 'GET', params: { cmd: "playing.json"}, isArray: true}
    ctrl: { method: 'GET', params: { cmd: "ctrl"}}
  }

.factory 'Media', ($resource) ->
  $resource '/api/medias/:request.json', {}, {
    inProgress: { method: 'GET', params: { request: "inProgress"}, isArray: true}
  }

.factory 'Admin', ($resource) ->
  $resource '/api/admin/:type/:id/:action', {}, {
    ctrl: { method: 'GET'}
  }

# Navigate on server files - MOCK
.factory 'Paths', ->
  # Get list of files under given paths, can filter out files.
  # Java side: remove paths which are children of other path.
  list: (paths, deep, withFiles = true) ->
    console.log "PATHS:LIST paths=#{JSON.stringify paths} ; deep=#{deep} ; withFile=#{withFiles}"
    if paths.length > 1
      console.log "Return /mnt/local/data and /mnt/remote/hoster"
      [
        name: '/mnt/local/data'
        children: [
          name: 'Movies_VF'
          children: [
            {name: 'HD', lazy: true}
            {name: 'DVD'}
          ]
        ,
          name: 'Movies_VO'
          children: [
            {name: 'VOST'}
            {name: 'US'}
          ]
        ,
          name: 'Sagas'
        ]
      ,
        name: '/mnt/remote/hoster'
        children: [
          name: 'Movies'
          children: [
            {name: 'Movies_VF', lazy: true}
            {name: 'Movies_VO', lazy: true}
          ]
        ]
      ]
    else if paths.length == 1 && paths[0] == '/mnt/local/data/Movies_VF/HD'
      console.log "/mnt/local/data/Movies_VF/HD"
      [
        name: '/mnt/local/data/Movies_VF/HD'
        children: [
          {name: 'Star Wars', lazy: true}
          {name: 'Star Trek', lazy: true}
          {name: 'Indiana Jones', lazy: true}
        ]
      ]
    else []
  # Get tree above this path - when root has been sent in single path, deploy it
  above: (path, withFiles = true) ->
    console.log "PATHS:ABOVE path=#{path} ; withFiles=#{withFiles}"
    if path == '/mnt/local/data' || path == '/mnt/remote/hoster'
      name: '/'
      children: [
        {name: 'home', lazy: true}
        {name: 'dev', lazy: true}
        {name: 'mnt', children: [
          name: 'local',
          children: [
            {name: 'data', lazy: true}
            {name: 'temp', lazy: true}
          ]
          ,
          {name: 'remote', lazy: true}
        ]}
        {name: 'opt', lazy: true}
        {name: 'var', lazy: true}
      ]