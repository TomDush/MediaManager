angular.module('mediamanager')
.directive 'genres', ->
  #return
  restrict: 'E'
  scope:
    genres: '='
  template: '<span ng-repeat="g in genres" >' +
    '<a ui-sref="medias.list({genres : g})">{{g}}</a><span ng-hide="$last">,</span> </span>' +
    '<a class="icon-search child-hover" title="Search similar" ui-sref="medias.list({genres : genres})"/>'

.directive 'poster', ['$interval', 'dateFilter', ($interval, dateFilter) ->
  # return
  restrict: 'E'
  template: '<img ng-src="{{poster}}?size=MINI" err-src="{{noPoster}}" />'
  link: (scope, element, attrs) ->
    scope.size = attrs.size

    # If poster isn't defined
    noPoster = "/img/no-poster-w";
    if (scope.size == "MINI") noPoster += "92"
    else noPoster += "185"

    noPoster += ".jpg";

    scope.noPoster = noPoster

    # Poster url
    scope.poster = if attrs.url? then attrs.url else scope.noPoster
]

.directive 'mediatitle', ->
  # return
  restrict: 'E'
  scope:
    media: '='
  template: '{{media.title}} <span ng-show="media.release">({{media.release |date:\'yyyy\'}})</span>'

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

#    .directive('timeprogressbar', function () {
#        function link(scope, element, attrs) {
#            // Functions...
#            function updateProgression() {
#                console.log("Length=" + scope.length + " ; position=" + scope.position + " ; player=" + JSON.stringify(scope.player));
#                scope.progression = Math.round(100 * scope.position / scope.length);
#            }
#
#            scope.$watch(attrs.position, function (value) {
#                scope.position = value;
#                updateProgression();
#            });
#            scope.$watch(attrs.player, function (value) {
#                console.log("player=" + JSON.stringify(scope.player));
#                scope.player = value;
#            });
#
#            // Set values from attributes
#            scope.length = attrs.length;
#            scope.position = attrs.position;
#            scope.player = attrs.player;
#
#            updateProgression();
#
#        }
#
#        return {
#            restrict: 'E',
#//            link: link,
#            scope: {
#                status: "="
#            },
#            template: '<div class="progress progress-striped active" ng-show="status.length">' +
#                '       <span style="position:absolute">{{status.position | time}} / {{status.length | time}}</span>' +
#                '       <div class="bar" style="width: {{status.position | progress:status.length}}%;"></div>' +
#                '</div>'
#        };
#    })
.directive 'errSrc', ->
  #return
  link: (scope, element, attrs) ->
    element.bind 'error', ->
      element.attr 'src', attrs.errSrc

.filter 'rate', ($filter) ->
  (input, votes) ->
    out = if !input || input == 0 then "" else $filter('number')(input * 100.0, 0) + " %"

    if (votes)
      out += " (" + votes + " votes)"

    out

.filter 'progress', ($filter) ->
  (input, length) ->
    Math.round(100 * input / length)

.filter 'time', ->
  minutes = (sec, force) ->
    mins = (input % 3600) // 60;
    if mins > 0 || force then "#{mins}' " else ""

  hours = (seconds) ->
    h = input // 3600
    if h > 0 then "#{h}h " else ""

  (input) ->
    if not input? then ""

    "#{hours input}#{minutes input, input > 3600}#{input % 60}\""