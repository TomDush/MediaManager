'use strict';

## Define carousel directives using directly bootstrap - Angular-UI override lot of calls.
angular.module('mediamanager.carousel', [])
.directive 'carouselBs', ->
  restrict: 'E'
  templateUrl: '/views/directives/carousel-bs.html'
  transclude: true
  replace: true
  scope:
    id: '@'
  controller: ($scope) ->
    $scope.id = if $scope.id then $scope.id else 'generic-carousel'
    $scope.slides = []

    ## return true if it's first
    this.addSlide = (slide) ->
      $scope.slides.push(slide)
      $scope.slides.length == 1

    this

.directive 'slideBs', ->
  require: '^carouselBs'
  restrict: 'E'
  templateUrl: '/views/directives/slide.html'
  transclude: true
  replace: true
  scope: {}
  link: (scope, elem, attr, carouselCtrl) ->
    first = carouselCtrl.addSlide(elem)
    scope.isFirst = ->
      first

    if first
      elem.addClass('active')

# Disable ui.bootstrap carousel because there are in conflict
angular.module('ui.bootstrap.carousel', ['ui.bootstrap.transition'])
.controller 'CarouselController', ['$scope', '$timeout', '$transition', '$q', ($scope, $timeout, $transition, $q) ->

]
.directive 'carousel', ->
  {}