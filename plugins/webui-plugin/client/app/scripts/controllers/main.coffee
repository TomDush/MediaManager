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
    return "active" if path == $location.path()

  $scope.entries = menu

  $scope.icon = (icon) ->
    "glyphicon glyphicon-#{icon}"

#  $scope.onSearch = (title) ->
#    $state.go "medias.list", {title: title}, {inherit: false}

.controller 'HomeCtrl', ($scope, Movie, Media, Player) ->
  $scope.menu = menu
  $scope.icon = (icon) ->
    "glyphicon glyphicon-#{icon}"

  $scope.lastMovies = Movie.last({ seen: 'UNSEEN', size: 15})
  $scope.refreshRandom = ->
    Movie.random {size: 10, notNullFields: "POSTER"}, (list) ->
      $scope.random = list

  # Currently, only movies are managed...
  $scope.inProgress = Media.inProgress()

  $scope.refreshRandom()

  $scope.resume = (recovery) ->
    Player.resume
      type: recovery.mediaSummary.mediaType
      mediaId: recovery.mediaSummary.id