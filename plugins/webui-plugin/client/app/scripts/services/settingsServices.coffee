'use strict';

angular.module 'mediamanager.services.settings', [ 'ngResource' ]
#
# Rest call to manage RootDirectories: CRUD + refresh
#
.factory 'RootDirectory', ($resource) ->
  $resource '/api/directory/:name', {name: '@name'},
    # $save: oldName must be specified if name (used as ID) as changed.
    # names can be specified to refresh only some directories, offline will be ignored in any case
    refresh: { method: 'GET', url: '/api/directory/refresh'}

#
# Rest call to manage Favorites: CRUD + refresh
#
.factory 'Favorite', ($resource) ->
  $resource '/api/favorite/:name', {name: '@name'}, []

#
# Rest call to manage parameters
#
.factory 'Parameter', ($resource) ->
  $resource '/api/parameter/:configId', {configId: '@configId'}, []

# Navigate on server files - MOCK
.factory 'Paths', ->
  #
  # Get list of files under given paths, can filter out files.
  # Java side: remove paths which are children of other path.
  # Args:
  #   paths: list of paths we're expecting
  #   deep: number of sub-level we want
  #   withFiles: false to exclude files (ie: only directories)
  #
  list: (args, callback = null, errorCallback = null) ->
    # Args
    paths = args.paths
    deep = args.deep
    deep ?= 3
    withFiles = if args.withFiles? then args.withFiles else true

    result = []
    console.log "PATHS:LIST paths=#{JSON.stringify paths} ; deep=#{deep} ; withFile=#{withFiles}"
    if arrayEqual paths, ['/mnt/data/toSort', '/mnt/usb-key']
      console.log "Return '/mnt/data/toSort', '/mnt/usb-key'"
      result = [
        name: '/mnt/data/toSort'
        favoriteName: 'Tempo'
        created: 1178147800000
        children: [
          {name: 'bt', lazy:true, isDirectory:true}
          {name: 'video', lazy:true, isDirectory:true}
        ]
      ,
        name: '/mnt/usb-key'
        favoriteName: 'USB Key'
        created: 1178246800000
        children: [
          name: 'Movies'
          children: [
            {name: 'Saga', lazy:true, isDirectory:true}
            {name: 'Indiana Jones 3 (1989) French DVDrip xvid (condom be).avi', type:'VIDEO', movieId: '53850613043c93a62c3aa723'}
            {name: 'Indiana jones poster.jpg', type:'JPG'}
          ]
        ,
          name: 'Shows'
          children: [
            {name: 'Fringe', lazy:true, isDirectory:true}
            {name: 'The Castle - Saison 06', children:[
              {name: 'the_castle_S06E01', type: 'VIDEO', created: 1178146800000}
            ]}
          ]
        ]
      ]
    else if paths.length > 1
      console.log "Return /mnt/local/data and /mnt/remote/hoster"
      result = [
        name: '/mnt/local/data'
        children: [
          name: 'Movies_VF'
          children: [
            {name: 'HD', lazy: true, isDirectory: true}
            {name: 'DVD', isDirectory: true}
          ]
        ,
          name: 'Movies_VO'
          children: [
            {name: 'VOST', isDirectory: true}
            {name: 'US', isDirectory: true}
          ]
        ,
          name: 'Sagas'
          isDirectory: true
        ]
      ,
        name: '/mnt/remote/hoster'
        children: [
          name: 'Movies'
          children: [
            {name: 'Movies_VF', lazy: true, isDirectory: true}
            {name: 'Movies_VO', lazy: true, isDirectory: true}
          ]
        ]
      ]
    else if paths.length == 1 && paths[0] == '/mnt/local/data/Movies_VF/HD'
      console.log "/mnt/local/data/Movies_VF/HD"
      result = [
        name: '/mnt/local/data/Movies_VF/HD'
        children: [
          {name: 'Star Wars', lazy: true, isDirectory: true}
          {name: 'Star Trek', lazy: true, isDirectory: true}
          {name: 'Indiana Jones', lazy: true, isDirectory: true}
        ]
      ]
    else if paths.length == 1 && paths[0] == '/mnt/usb-key/Movies/Saga'
      console.log "Return /mnt/usb-key/Movies/Saga"
      result = [
        name: '/mnt/usb-key/Movies/Saga'
        children: [
          {name: 'Star Wars', lazy: true, isDirectory: true}
          {name: 'Star Trek', lazy: true, isDirectory: true}
          {name: 'Indiana Jones', lazy: true, isDirectory: true}
        ]
      ]
    else if paths.length == 0
      # Send default path: home
      result = [
        name: '/mnt/home/yourself'
        children: [
          name: 'media'
          children: [
            {name: 'Movies'}
            {name: 'Shows'}
          ]
        ]
      ]

    if callback
      callback result

    result

  # Get tree above this path - when root has been sent in single path, deploy it
  above: (args, callback = null, errorCallback = null) ->
    # Args
    path = args.path
    withFiles = if args.withFiles? then args.withFiles else true

    result = {}
    console.log "PATHS:ABOVE path=#{path} ; withFiles=#{withFiles}"
    if path == '/mnt/local/data' || path == '/mnt/remote/hoster'
      result =
        name: '/'
        children: [
          {name: 'home', lazy: true}
          {name: 'dev', lazy: true}
          {name: 'mnt', children: [
            name: 'local',
            children: [
              {name: 'data', lazy: true}
              {name: 'temp', lazy: true}
            ],
            {name: 'remote', lazy: true}
          ]}
          {name: 'opt', lazy: true}
          {name: 'var', lazy: true}
        ]

    if callback
      callback result

    result


#
# Compare 2 arrays
#
arrayEqual = (a, b) ->
  a.length is b.length and a.every (elem, i) -> elem is b[i]