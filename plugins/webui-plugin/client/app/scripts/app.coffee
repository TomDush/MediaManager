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
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch'
    'mediaServices'
  ])
.config ($routeProvider, $locationProvider) ->
  $locationProvider.html5Mode true

  $routeProvider
  .when '/',
    templateUrl: 'views/home.html'
    controller: 'HomeCtrl'
  .when '/movies',
    templateUrl: 'views/medias.html'
    controller: 'HomeCtrl'
  .otherwise
      redirectTo: '/'

