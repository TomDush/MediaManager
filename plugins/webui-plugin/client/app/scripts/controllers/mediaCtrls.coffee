'use strict';

#_ = (obj) -> new wrapper(obj)

angular.module('mediamanager')
.controller 'MovieCtrl', ($scope, $routeParams, Movie, Player, Admin) ->
  $scope.movieId = $routeParams.movieId

  $scope.loaded = false;

  # Crew profiles
  $scope.noProfile = "/img/no-profile-w45.jpg";
  $scope.resolveProfile = (img) ->
    if !!img then img else $scope.noProfile

  # Load movie
  Movie.find { id: $scope.movieId }, (movie) ->
    # Found
    $scope.loaded = true;
    $scope.movie = movie;
  ,
  ->
    # Not found (or any error...)
    $scope.loaded = true;
    $scope.movie = null

  # Player control
  $scope.play = ->
    console.log "Play movie #{$scope.movie.title}"
    Player.play({type: "MOVIE", mediaId: $scope.movie.id, path: $scope.movie.videoFiles[0].file});

  $scope.resume = ->
    console.log("Resume movie " + $scope.movie.title)
    Player.resume({type: "MOVIE", mediaId: $scope.movie.id});

  # Actions control
  $scope.admin = (request) ->
    request.id = $scope.movie.id;
    request.type = 'MOVIE';
    Admin.ctrl(request);

    if request.action == 'REMOVE_RESUME'
      $scope.movie.recovery = null

#
#  MOVIES LIST CONTROLLER
#
.controller 'MoviesCtrl', ($scope, $route, $location, Window, Genre, Movie) ->
  $scope.display = $location.search()?.display || 'icon'
  $scope.selectedGenres = {}

  $scope.all = {filter: 'all', seen: 'ALL', order: 'ALPHA', enriched: 'ALL'}
  $scope.latest = {filter: 'latest', seen: 'UNSEEN', order: 'LAST', enriched: 'ALL'}
  $scope.unidentified = {filter: 'unidentified', seen: 'ALL', order: 'ALPHA', enriched: 'NOT_ENRICHED'}

  $scope.filter = angular.copy $scope.all

  #
  # Genre loading and control
  #
  Genre.query (genres) ->
    $scope.genres = genres
    updateGenre()

  updateGenre = ->
    # Update genres
    $scope.selectedGenres = {}
    genres = $scope.filter.genres || $scope.genres
    for g in genres
      $scope.selectedGenres[g] = true

  getSelectedGenres = ->
    genres = []
    for g, v of $scope.selectedGenres
      genres.push g if v
    genres

  $scope.selectAllGenres = ->
    $scope.filter.genres = angular.copy $scope.genres
    updateGenre()

  $scope.clearGenres = ->
    $scope.filter.genres = []
    updateGenre()

  $scope.inverseGenres = ->
    current = $scope.filter.genres
    $scope.filter.genres = []
    for g in $scope.genres
      $scope.filter.genres.push g if current.indexOf(g) < 0
    updateGenre()

  #
  # Filter controls
  #
  $scope.filterIs = (filter) ->
    _.isEqual filter, $scope.filter

  $scope.selectFilter = (filter) ->
    $scope.filter = angular.copy filter
    updateGenre()

  $scope.toogleOrder = (options...) ->
    index = options.indexOf($scope.filter.order)
    $scope.filter.order = options[(index + 1) % options.length]

    # Refresh with new order
    $scope.applyFilter()

  $scope.$watch ->
    JSON.stringify $scope.selectedGenres
  ,
  (val) ->
    # Summarise genres
    delete $scope.filter.genres
    genres = getSelectedGenres()
    $scope.filter.genres = genres if genres.length > 0 && genres.length != $scope.genres.length

  $scope.applyFilter = ->
    # Apply filter
    $scope.filter.size = $scope.col * 5 || 10 # 5 lines if col is defined
    console.log "Filter: #{JSON.stringify $scope.filter}"
    Movie.query $scope.filter, (movies) ->
      $scope.movies = movies

  #
  # Display managing - Keep display in URL without reloading
  #
  lastRoute = $route.current;
  $scope.$on '$locationChangeSuccess', ->
    if $route.current.$$route?.controller == 'MoviesCtrl'
      $route.current = lastRoute
      $scope.display = $location.search()?.display if $location.search()?.display

  $scope.$watch 'display', (display) ->
    if display == 'icon'
      console.log "display = icon ; scope: #{$scope.display}"
      $location.search 'display', null
    else
      $location.search 'display', display

  #
  # Icon view controls
  #
  $scope.setSelected = (movie) ->
    if $scope.selected != movie
      $scope.selected = movie
    else
      $scope.selected = null

  # Define column number
  colRange =
    huge: 7
    xlarge: 5
    large: 4
    medium: 3
    small: 2
  Window.register $scope, (range) ->
    $scope.col = colRange[range];
  $scope.col = colRange[Window.getRange()]

  $scope.isLast = (index) ->
    (index + 1) % $scope.col == 0

  #
  # Search movies
  #
  $scope.applyFilter()

## Object is null or empty (no attribute), or array is empty
isEmpty = (obj) ->
  if obj then true else false

convertToArray = (value) ->
  if (value && value != null && Object.prototype.toString.call(value) != '[object Array]')
    value.split(",");

  else
    value;

compactObject = (o) ->
  for k,v of o
    if !v
      delete o[k]

  o
