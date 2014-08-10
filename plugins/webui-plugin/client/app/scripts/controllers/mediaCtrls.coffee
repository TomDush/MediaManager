'use strict';


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
      $scope.movie.recovery = null;

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