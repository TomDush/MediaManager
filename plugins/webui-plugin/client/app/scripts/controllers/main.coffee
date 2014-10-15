'use strict';


menu = [
  {href: '/', name: 'Home', icon: 'home'}
  {href: '/movies', name: 'Movies', icon: 'film'}
  {href: '/search', name: 'Advanced search', icon: 'search'}
  {href: '/files', name: 'File Manager', icon: 'folder-open'}
  {href: '/controls', name: 'Control', icon: 'cog'}
  {href: '/settings', name: 'Settings', icon: 'wrench', entries: [
    {href: '/settings/repo', name: 'Repositories'}
    {href: '/settings/favorite', name: 'Favorites'}
    {href: '/settings/param', name: 'Parameters'}
  ]}
]

angular.module('mediamanager')
.controller 'TopMenuCtrl', ($scope, $location, $window) ->
  $scope.pageTitle = "Medima - Manage yours medias !"
  $scope.active = (path) ->
    if path == '/'
      $location.path() == '/'
    else
      path == $location.path() || $location.path().indexOf(path) == 0

  $scope.entries = menu

  $scope.icon = (icon) ->
    "glyphicon glyphicon-#{icon}"

  $scope.$watch ->
    $window.innerWidth
  ,
  (val) ->
    $scope.xs = val < 768

  $scope.isXs = ->
    $scope.xs

  $scope.onSearch = (title) ->
    $location.url "/movies?search=#{title}"

.controller 'HomeCtrl', ($scope, Movie, Media, Player, $window) ->
  $scope.menu = menu
  $scope.icon = (icon) ->
    "glyphicon glyphicon-#{icon}"
  $scope.isFocused = false

  # Random movies - displayable posters
  displayable = ->
    if ($window.innerWidth > 1600)
      $scope.displayablePoster = 15
    else if $window.innerWidth > 1300
      $scope.displayablePoster = 12
    else
      $scope.displayablePoster = 9

  $(window).resize ->
    $scope.$apply displayable

  displayable()

  # Random movies - refresh
  $scope.refreshRandom = ->
    Movie.random {size: 15, notNullFields: "POSTER"}, (list) ->
      $scope.random = list

  $scope.refreshRandom()

  # Random movies - focus
  $scope.setRandomFocus = (movie) ->
    # Enrich movie
    Movie.find { id: movie.id }, (fullMovie) ->
      mergeObjs movie, fullMovie

    $scope.movieFocused = movie
    $scope.isFocused = true

  $scope.getArrowPosition = (movieId) ->
    if movieId? && $scope.random?
      # Find movie index
      index = -1

      i = 0
      for m in $scope.random.content
        if m.id == movieId
          index = i
        i++

      # Compute position
      posterWidth = $window.innerWidth / ($scope.displayablePoster + 1)
      pos = posterWidth / 2 + posterWidth * index - 40
      {'left': "#{pos}px"}
    else
      {'display': 'none'}

  ## Last movies - load
  $scope.lastMovies = Movie.last({ seen: 'UNSEEN', size: 15})

  # Last movies - selection management
  $scope.setLastSelected = (m) ->
    $scope.lastSelected = m?.id;

  $scope.isLastSelected = (m) ->
    $scope.lastSelected == m?.id
  $scope.hasPoster = (m) ->
    !m.poster?

  # Last movies - identification
  $scope.isNotIdentified = (m) ->
    !(m.poster? || m.release? || m.overview?)

  # In progress and restart action
  $scope.inProgress = Media.inProgress()

  $scope.resume = (recovery) ->
    Player.resume
      type: recovery.mediaSummary.mediaType
      mediaId: recovery.mediaSummary.id

#
# SETTINGS PAGE CONTROLLER
#
.controller 'SettingsCtrl', ($scope, $route, $rootScope, $location, $routeParams, RootDirectory, Favorite) ->
  $scope.tabs = [
    {id: 'repo', name: 'Repositories'}
    {id: 'favorite', name: 'Favorites'}
    {id: 'param', name: 'Parameters'}
  ]
  $scope.types = [
    {value: 'MOVIE', icon: 'film', name: 'Movies'}
    {value: 'SHOW', icon: 'camera', name: 'Shows'}
  ]

  ###
  Control URL for sub-page
  ###

  lastRoute = $route.current;
  $scope.$on '$locationChangeSuccess', ->
    if $route.current.$$route?.controller == 'SettingsCtrl'
      ## Stay on this route but change tab if needed
      $route.current = lastRoute
      path = $location.url().split '/'
      if path.length >= 3 && $scope.tab != path[2]
        $scope.tab = path[2]


  $scope.$watch 'tab', (newTab) ->
    $location.url "/settings/#{newTab}"

  # Default tab
  ids = $scope.tabs.map (e) ->
    e.id
  $scope.tab = if $routeParams.tab? && $routeParams.tab in ids then $routeParams.tab else 'repo'

  $scope.setTab = (t) ->
    $scope.tab = t

  # Loading data
  $scope.directories = RootDirectory.query()

  ###
  Directories Controller
  ###

  ## Control on selected directory
  $scope.setDirectory = (dir) ->
    $scope.directory = angular.copy dir
    $scope.directory.orig = dir
    $scope.treePaths = if $scope.directory.paths?.length > 0 then $scope.directory.paths else []
    $scope.showTree = false

  $scope.setNewDirectory = ->
    $scope.treePaths = []
    $scope.showTree = false
    $scope.directory =
      name: 'New Directory'
      mediaType: $scope.types[0].value

  $scope.setToDelete = (dir) ->
    $scope.toDelete = dir

  $scope.getIconClass = (icon) ->
    return "glyphicon glyphicon-#{icon}"

  # Save directory
  $scope.saveUpdate = ->
    return if not !!$scope.directory.name

    updated = $scope.directory
    while updated.name.indexOf(',') >= 0
      updated.name = updated.name.replace /\,/, ''

    # We must notify server when the name has changed
    if updated.orig && updated.name != updated.orig.name
      updated.oldName = updated.orig.name

    # Call server
    orig = updated.orig
    delete updated.orig
    updated.$save {}, ->
      # SUCCESS call back: update existing or add new in list
      if orig
        angular.copy updated, orig
      else
        $scope.directories.push updated
    ,
    ->
      console.log "An error occurred while saving Directory."

  # Ask server to refresh directories
  $scope.refreshDirectories = (name = null) ->
    if name
      RootDirectory.refresh {names: [name]}
    else
      RootDirectory.refresh()

  # Delete repository
  $scope.deleteRepository = (dir) ->
    dir.$delete {}, ->
      $scope.directories = $scope.directories.filter (d) ->
        d.name != name

  ## Modal controls
  $scope.showPaths = ->
    $scope.showTree = true
  $scope.removePath = (dir, path) ->
    dir.paths = dir.paths.filter (e) ->
      e != path
  $scope.addPath = (path) ->
    $scope.directory.paths ?= []
    if path not in $scope.directory.paths
      $scope.directory.paths.push path

  ###
  FAVORITE CTRL
  ###
  # TODO Manage errors to keep table up to date
  $scope.favorites = Favorite.query()

  $scope.deleteFavorite = (f) ->
    $scope.favorites = $scope.favorites.filter (e) ->
      e != f
    f.$delete()

  $scope.addFavorite = (path) ->
    favorite =
      name: basename path
      path: path

    Favorite.save favorite, (f) ->
      $scope.favorites.push f

  $scope.updateFavorite = (f) ->
    if f.name != f.oldName
      f.$save()
    $scope.editedFavorite = null

  $scope.setEditedFavorite = (f) ->
    $scope.cancelFavorite($scope.editedFavorite) if $scope.editedFavorite?

    f.oldName = f.name
    $scope.editedFavorite = f

  $scope.cancelFavorite = (f) ->
    f.name = f.oldName
    $scope.editedFavorite = null

#
# File Manager Controller
#
.controller 'FilesCtrl', ($scope, $location, $route, Paths, Favorite) ->

  ###
  Tree control
  ###

  # Default: Load from favorite
  $scope.loading = true
  $scope.favorites = Favorite.query {}, (favorites) ->
    if favorites?.length > 0
      $scope.noFavorite = false

      roots = favorites.map (e) ->
        e.path

      console.log "Get favorite content: #{roots}"
      Paths.list
        paths: roots
      ,
      (paths) ->
        $scope.root =
          name: 'Favorites'
          type: 'FOLDER'
          path: ''
        $scope.root.children = process paths, $scope.root

        $scope.current = $scope.root
        currentPath = $location.search()?.current

        if !!currentPath
          navigateTo $scope.root.children, currentPath, (file) ->
            $scope.current = file if file
            $scope.loading = false
        else
          $scope.loading = false

    else
      $scope.noFavorite = true

  $scope.open = (f, fct = null) ->
    if f.type == 'FOLDER'
      if f.lazy
        $scope.loading = true
        Paths.list
          paths: [f.path]
        , (path) ->
          if path && path[0]
            f.children = process path[0].children, f if path[0].children?
            f.lazy = false
          $scope.loading = false if !fct

          fct() if fct

      $scope.current = f
      $location.search 'current', f.path || null
      fct() if fct

  $scope.play = (f) ->
    if f.type == 'FOLDER'
      $scope.open f
    else
      console.log "Play #{f.name}"

  navigateTo = (files, path, fct) ->
    for f in files
      if f.name == path
        # Found!
        $scope.open f, ->
          fct f if fct
      else if path.indexOf(f.name) == 0
        localPath = path
        # Found ancestor, open it (to load if lazy) then continue search
        $scope.open f, ->
          navigateTo f.children, localPath.substr(f.name.length + 1), fct

        break


  # Prevent controller to be reloaded when location.search is changed.
  lastRoute = $route.current;
  $scope.$on '$locationChangeSuccess', ->
    if $route.current.$$route?.controller == 'FilesCtrl'
      $route.current = lastRoute
      # Previous support - update display if url has been manually changed
      if $location.search()?.current != $scope.current?.path
        if $location.search()?.current
          navigateTo $scope.root.children, $location.search()?.current
        else
          $scope.current = $scope.root


  # Create bi-directional tree
  process = (files, parent) ->
    for f in files
      # Full path
      f.path = parent.path
      f.path += '/' if !!f.path
      f.path += f.name

      # Pretty name
      f.getName = ->
        if !!this.favoriteName
          "#{this.favoriteName} (#{this.name})"
        else
          this.name

      # Children
      if f.type == 'FOLDER' || f.children?.length > 0 || f.isDirectory || f.lazy
        f.type = 'FOLDER'
        f.parent = parent

        if f.children?.length > 0
          process f.children, f

      # Used to sort folder first
      f.sortKey = (if f.type == 'FOLDER' then "0 " else "1 ") + f.name

    files

  ###
  Display helpers
  ###

  $scope.getIcon = (f) ->
    icon = ""
    if f.type == 'VIDEO'
      icon = "glyphicon glyphicon-facetime-video"
    else if f.type in ['JPG', 'JPEG', 'PNG', 'BMP', 'GIF']
      icon = "glyphicon glyphicon-picture"
    else if f.type == 'FOLDER'
      icon = "glyphicon glyphicon-folder-open"
    else
      icon = "glyphicon glyphicon-file"

    ## Make it in green if this file is part of medias
    if f.movieId?
      icon += "glyphicon glyphicon-film success"

    icon

  $scope.getType = (f) ->
    type = ""
    if f.type == 'VIDEO'
      type = "Video file"
    else if f.type in ['JPG', 'JPEG', 'PNG', 'BMP', 'GIF']
      type = "Picture"
    else if f.type == 'FOLDER'
      type = "Folder"
    else if !!f.name
      ext = f.name
      while ext.indexOf('.') >= 0
        ext = ext.substring ext.indexOf('.') + 1

      type = "#{ext} File"
    else
      type = "Undefined"

    type

  $scope.getSize = (f) ->
    if f.type == 'FOLDER'
      if f.lazy then "-" else f.children?.length
    else
      f.size

  $scope.breadcrumb = (file) ->
    if file
      breadcrumb = [file]
      it = file
      while it.parent
        breadcrumb.push it.parent
        it = it.parent

      breadcrumb.reverse()
    else
      []

## UTILITIES
mergeObjs = (obj1, obj2) ->
  for key, val of obj2
    obj1[key] = val

#
# Select the end of
basename = (path) ->
  p = path
  while p.indexOf('/') > -1
    console.log "Path = #{path} ; p=#{p} ; index=#{p.indexOf '/'}"
    p = p.substr p.indexOf('/') + 1

  p

#
# Remove all data which can't or mustn't be send as GET params and serialise Arrays.
#
serializeObj = (obj) ->
  result = angular.copy obj
  for key, val of obj
    if key.indexOf('$') >= 0
      delete result[key]
    if Array.isArray val
      result[key] = val.toString()

  result