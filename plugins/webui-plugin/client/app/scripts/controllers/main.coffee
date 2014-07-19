'use strict';


angular.module('mediamanager')
.controller 'TopMenuCtrl', ($scope, $location) ->
  $scope.pageTitle = "Medima - Manage yours medias !"
  $scope.active = (path) ->
    return "active" if path == $location.path()

  $scope.entries = [
    {href:'/', name:'Home', icon:'home'}
    {href:'/movies', name:'Movies', icon:'film'}
    {href:'/search', name:'Advanced search', icon:'search'}
    {href:'/controls', name:'Control', icon:'cog'}
    {href:'/settings', name:'Settings', icon:'wrench'}
  ]

  $scope.icon = (icon) ->
    "glyphicon glyphicon-#{icon}"

#  $scope.onSearch = (title) ->
#    $state.go "medias.list", {title: title}, {inherit: false}

angular.module('mediamanager').controller 'HomeCtrl', ($scope ) ->
  $scope.foo = 'bar'
###, Movie, Media, Player###
#  $scope.lastMovies = Movie.last({ seen: 'UNSEEN', size: 15});
#  $scope.random = Movie.random({size: 10, notNullFields: "POSTER"});
#
#  # Currently, only movies are managed...
#  $scope.inProgress = Media.inProgress();
#
#  $scope.resume = (recovery) ->
#    Player.resume
#      type: recovery.mediaSummary.mediaType
#      mediaId: recovery.mediaSummary.id