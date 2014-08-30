'use strict';


menu = [
  {href: '/', name: 'Home', icon: 'home'}
  {href: '/movies', name: 'Movies', icon: 'film'}
  {href: '/search', name: 'Advanced search', icon: 'search'}
  {href: '/controls', name: 'Control', icon: 'cog'}
  {href: '/settings', name: 'Settings', icon: 'wrench'}
]

angular.module('mediamanager')
.controller 'TopMenuCtrl', ($scope, $location) ->
  $scope.pageTitle = "Medima - Manage yours medias !"
  $scope.active = (path) ->
    if path == '/'
      $location.path() == '/'
    else
      path == $location.path() || $location.path().indexOf(path) == 0

  $scope.entries = menu

  $scope.icon = (icon) ->
    "glyphicon glyphicon-#{icon}"

#  $scope.onSearch = (title) ->
#    $state.go "medias.list", {title: title}, {inherit: false}

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
      for m in $scope.random.elements
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
    ! m.poster?

  # Last movies - identification
  $scope.isNotIdentified = (m) ->
    ! (m.poster? || m.release? || m.overview?)

  # In progress and restart action
  $scope.inProgress = Media.inProgress()

  $scope.resume = (recovery) ->
    Player.resume
      type: recovery.mediaSummary.mediaType
      mediaId: recovery.mediaSummary.id


## UTILITIES
mergeObjs = (obj1, obj2) ->
  for key, val of obj2
    obj1[key] = val