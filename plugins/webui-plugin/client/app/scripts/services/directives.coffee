angular.module('mediamanager')
## Useful tools - filter and directives

# Split text and and 'tail' at this end
.filter 'cut', ->
  (value, wordwise, max, tail) ->
    if value?
      max = parseInt(max, 10);
      if (!max || value.length <= max)
        value;
      else
        value = value.substr(0, max);
        if (wordwise)
          lastspace = value.lastIndexOf(' ')
          if (lastspace != -1)
            value = value.substr(0, lastspace)

        value + (tail || ' …')
    else
      ''

# Replace image by another one if not found
.directive 'errSrc', ->
  #return
  link: (scope, element, attrs) ->
    element.bind 'error', ->
      element.attr 'src', attrs.errSrc

# Replace image by another one if not found
.filter 'basename', ->
  (value) ->
    index = value.lastIndexOf('/')
    if index == -1 then value else value.substr(index + 1)

## Directive and filters to extract all tags from media

# Extract genres, with link for search
.directive 'genres', ->
  #return
  restrict: 'E'
  scope:
    genres: '='
  template: '<span ng-repeat="g in genres" >' +
    '<a ui-sref="medias.list({genres : g})">{{g}}</a><span ng-hide="$last">,</span> </span>' +
    '<a class="icon-search child-hover" title="Search similar" ui-sref="medias.list({genres : genres})"/>'

# Media tags: video qualities, length, type
.directive 'tags', ->
  #return
  restrict: 'E'
  scope:
    media: '='
  templateUrl: '/views/directives/tags.html'

# Video tag: quality
.filter 'videoTags', ->
  (input, tail) ->
    res = ""
    if input?.length > 0
      for video in input
        quality = if video.quality? then " <span class='quality'>#{video.quality}</span>" else ""
        lang = if video.quality? then " <span class='quality'>#{video.lang}</span>" else ""
        res = "#{res}#{lang}#{quality}"

    if res.length > 0 then res + (tail || "") else res

# Add a play button based on video file available
.directive 'playButton', ->
  #return
  restrict: 'E'
  scope:
    media: '='
    btnSize: '@'
  templateUrl: '/views/directives/play.html'

## Direct display of some media properties

# Title with date if any
.filter 'mediatitle', ($filter) ->
  (input) ->
    if input?
      dateStr = if input.release? then " (#{$filter('date')(input.release, 'yyyy')})" else ""
      "#{input.title}#{dateStr}"
    else ""

# List of person name
.filter 'person', ->
  (input, withLink) ->
    res = ""
    if input?.length > 0
      for p in input
        if res.length > 0
          res += ", "
        if withLink
          res += "<a href='/search/crew/#{p.id}'>"
        res += "#{p.name}"
        if withLink
          res += "</a>"

    res

.directive 'personList', ->
  restrict: 'E'
  template: '<span class="persons" ng-bind-html="persons | limitTo:getLimit() | person:withLink"></span>'
  scope:
    persons: '='
    withLink: '@'
    limit: '@'
  link: ($scope)->
    $scope.getLimit = ->
      if $scope.limit then $scope.limit else $scope.persons?.length


# Img element for poster, if any, else choose no poster img with width 92 or 185.
.directive 'poster', ['$interval', 'dateFilter', ($interval, dateFilter) ->
  restrict: 'E'
  template: '<img ng-src="/{{poster()}}?size={{size}}" err-src="{{noPoster}}" />'
  scope:
    url: '='
    size: '@'
  link: ($scope) ->
    # If poster isn't defined
    width = if $scope.size == "MINI" then "92" else "185"
    $scope.noPoster = "img/no-poster-w#{width}.jpg"

    # Poster url
    $scope.poster = ->
      if $scope.url? then $scope.url else $scope.noPoster
]
#.directive 'poster', ['$interval', 'dateFilter', ($interval, dateFilter) ->
#  # return
#  restrict: 'E'
#  template: '<img ng-src="/{{poster(url, size}}?size={{size}}" err-src="{{noPoster}}" />'
#  scope:
#    url: '='
#    size: '='
#    noPoster: (size) ->
#      width = 185
#      if size == "MINI"
#        width = 92
#      else if size == "DISPLAY"
#        width = 342
#
#      "/img/no-poster-w#{width}.jpg"
#
#    poster: (url, size) ->
#      console.log "URl=#{url}"
#      #    scope.size = attrs.size
#
#      # If poster isn't defined
#      width = if scope.size == "MINI" then "92" else "185"
#
#      # Poster url
#      scope.poster = if scope.url? then scope.url else scope.noPoster


.directive 'checkList', ->
  #return
  scope:
    list: '=checkList'
    value: '@'
  link: (scope, elem, attrs) ->
    handler = (setup) ->
      checked = elem.prop('checked')
      index = scope.list.indexOf(scope.value)

      if (checked && index == -1)
        if (setup) elem.prop('checked', false)
        else scope.list.push(scope.value)
      else if (!checked && index != -1)
        if (setup) elem.prop('checked', true)
        else scope.list.splice(index, 1)

    setupHandler = handler.bind(null, true)
    changeHandler = handler.bind(null, false)

    elem.bind 'change', ->
      scope.$apply changeHandler
      scope.$watch 'list', setupHandler, true

.directive 'timeprogressbar', ->
  link = (scope, element, attrs) ->
    # Functions...
    updateProgression = ->
      scope.progression = Math.round(100 * scope.position / scope.length);

    scope.$watch attrs.position, (value) ->
      scope.position = value;
      updateProgression()

    scope.$watch attrs.player, (value) ->
      scope.player = value

    # Set values from attributes
    scope.length = attrs.length;
    scope.position = attrs.position;
    scope.player = attrs.player;

    updateProgression()

  {
  restrict: 'E'
  link: link
  scope:
    status: "="
  template: '<div class="progress" ng-show="status.length">' +
#    '       <span style="position:absolute">{{status.position | time}} / {{status.length | time}}</span>' +
    '<div class="progress-bar progress-bar-striped progress-bar-info active" role="progressbar" style="width: {{status.position | progress:status.length}}%;"
              aria-valuenow="{{status.position | progress:status.length}}" aria-valuemin="0" aria-valuemax="100">{{status.position | time}} / {{status.length | time}}</div>' +
    '</div>'
  }

.filter 'rate', ($filter) ->
  (input, votes) ->
    out = if !input || input == 0 then "" else $filter('number')(input * 100.0, 0) + " %"

    if (votes)
      out += " (" + votes + " votes)"

    out

.filter 'progress', ->
  (input, length) ->
    Math.round(100 * input / length)

.filter 'time', ->
  minutes = (sec, force) ->
    mins = (sec % 3600) // 60;
    if mins > 0 || force then "#{mins}' " else ""

  hours = (seconds) ->
    h = seconds // 3600
    if h > 0 then "#{h}h " else ""

  (input) ->
    if input?
      sec = if input % 60 == 0 then "" else "#{input % 60}\""
      "#{hours input}#{minutes input, input > 3600}#{sec}"
    else
      ""