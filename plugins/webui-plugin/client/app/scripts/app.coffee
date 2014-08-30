'use strict'

###*
 # @ngdoc overview
 # @name newApp
 # @description
 # # newApp
 #
 # Main module of the application.
###
angular
.module('mediamanager', [
    'ngAnimate'
    'ngCookies'
    'ngResource'
    'ngRoute'
    'ngSanitize'
    'ngTouch'
#    'ui.bootstrap'
#    'ui.bootstrap.carousel'
    'mediaServices'
    'mediamanager.carousel'
  ])
.config ($routeProvider, $locationProvider) ->
  $locationProvider.html5Mode true
  $locationProvider.hashPrefix '!'

  $routeProvider
  .when '/',
    templateUrl: 'views/home.html'
    controller: 'HomeCtrl'
  .when '/movies',
    templateUrl: 'views/medias.html'
    controller: 'HomeCtrl'
  .when '/movies/:movieId',
    templateUrl: '/views/movie.html'
    controller: 'MovieCtrl'
  .otherwise
      redirectTo: '/'

