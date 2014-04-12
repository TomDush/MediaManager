angular.module('mediamanager')
    .directive('genres', function () {
        return {
            restrict: 'E',
            scope: {
                genres: '='
            },
            template: '<span ng-repeat="g in genres" >' +
                '<a ui-sref="medias.list({genres : g})">{{g}}</a><span ng-hide="$last">,</span> </span>' +
                '<a class="icon-search child-hover" title="Search similar" ui-sref="medias.list({genres : genres})"/>'
        };
    })
    .directive('poster', ['$interval', 'dateFilter', function ($interval, dateFilter) {

        function link(scope, element, attrs) {
            scope.size = attrs.size;

            // If poster isn't defined
            var noPoster = "/img/no-poster-w";
            if (scope.size == "MINI") noPoster += "92";
            else noPoster += "185";

            noPoster += ".jpg";

            scope.noPoster = noPoster;

            // Poster url
            if (attrs.url) {
                scope.poster = attrs.url;
            } else {
                scope.poster = scope.noPoster;
            }
        }

        return {
            restrict: 'E',
            template: '<img ng-src="{{poster}}?size=MINI" err-src="{{noPoster}}" />',
            link: link
        };
    }])
    .directive('mediatitle', function () {
        return {
            restrict: 'E',
            scope: {
                media: '='
            },
            template: '{{media.title}} <span ng-show="media.release">({{media.release |date:\'yyyy\'}})</span>'
        };
    })
    .directive('timeprogressbar', function () {
        function link(scope, element, attrs) {
            // Functions...
            function updateProgression() {
                console.log("Length=" + scope.length + " ; position=" + scope.position + " ; player=" + JSON.stringify(scope.player));
                scope.progression = Math.round(100 * scope.position / scope.length);
            }

            scope.$watch(attrs.position, function (value) {
                scope.position = value;
                updateProgression();
            });
            scope.$watch(attrs.player, function (value) {
                console.log("player=" + JSON.stringify(scope.player));
                scope.player = value;
            });

            // Set values from attributes
            scope.length = attrs.length;
            scope.position = attrs.position;
            scope.player = attrs.player;

            updateProgression();

        }

        return {
            restrict: 'E',
//            link: link,
            scope: {
                status: "="
            },
            template: '<div class="progress progress-striped active" ng-show="status.length">' +
                '       <span style="position:absolute">{{status.position | time}} / {{status.length | time}}</span>' +
                '       <div class="bar" style="width: {{status.position | progress:status.length}}%;"></div>' +
                '</div>'
        };
    })
    .directive('checkList', function () {
        return {
            scope: {
                list: '=checkList',
                value: '@'
            },
            link: function (scope, elem, attrs) {
                var handler = function (setup) {
                    var checked = elem.prop('checked');
                    var index = scope.list.indexOf(scope.value);

                    if (checked && index == -1) {
                        if (setup) elem.prop('checked', false);
                        else scope.list.push(scope.value);
                    } else if (!checked && index != -1) {
                        if (setup) elem.prop('checked', true);
                        else scope.list.splice(index, 1);
                    }
                };

                var setupHandler = handler.bind(null, true);
                var changeHandler = handler.bind(null, false);

                elem.bind('change', function () {
                    scope.$apply(changeHandler);
                });
                scope.$watch('list', setupHandler, true);
            }
        };
    })
    .directive('errSrc', function () {
        return {
            link: function (scope, element, attrs) {
                element.bind('error', function () {
                    element.attr('src', attrs.errSrc);
                });
            }
        }
    })
    .filter('rate', function ($filter) {
        return function (input, votes) {
            if (!input || input == 0) {
                return "";
            }

            var out = $filter('number')(input * 100.0, 0) + " %";

            if (votes) {
                out += " (" + votes + " votes)";
            }
            return out;
        }
    })
    .filter('progress', function ($filter) {
        return function (input, length) {
            return Math.round(100 * input / length);
        }
    })
    .filter('time', function () {
        return function (input) {
            if (!input) return "";

            var out = "";
            var hours = Math.floor(input / 3600);
            if (hours > 0) {
                out += hours + "h ";
            }

            var mins = Math.floor((input % 3600) / 60);
            if (mins > 0) {
                out += mins + "'";
            }
            out += input % 60 + "\"";

            return out;
        };
    })
;