'use strict';

angular.module('mediamanager', ['ui.router', 'ui.bootstrap', 'mediaServices' ]).config(function($locationProvider, $stateProvider, $urlRouterProvider) {
    $locationProvider.html5Mode(true);


    $urlRouterProvider.otherwise("/");

    $stateProvider
        .state('home', {
            url: "/",
            views: {
                "menu": { templateUrl: "views/topMenu.html" },
                "content": { templateUrl: "views/home.html", controller : "HomeCtrl" }
            }
        })
        .state('medias', {
            abstract: true,
            views: {
                "menu": { templateUrl: "views/topMenu.html" },
                "content": { templateUrl: "layouts/mediaList.html", controller:"ParentCtrl" }
            }
        })
        .state('medias.list', {
            url: "/medias/?title&media&genres&movieId",
            views: {
                "searchForm": { templateUrl: "views/search.html", controller : "SearchCtrl" },
                "list": { templateUrl: "views/medias.html", controller:"ListCtrl" },
                "sheet": { templateUrl: "views/movie.html", controller : "MovieCtrl" }
            }
        })
        .state('sheet', {
            //url: "/",
            abstract: true,
            views: {
                "menu": { templateUrl: "views/topMenu.html" },
                "content": { templateUrl: "layouts/sheet.html" }
            }
        })
        .state('sheet.movie', {
            url: "/movie/:movieId",
            views: {
                "searchForm": { templateUrl: "views/search.html", controller : "SearchCtrl" },
                "sheet": { templateUrl: "views/movie.html", controller : "MovieCtrl" }
            }
        });
})
//;
//app
    .directive('checkList', function() {
    return {
        scope: {
            list: '=checkList',
            value: '@'
        },
        link: function(scope, elem, attrs) {
            var handler = function(setup) {
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

            elem.bind('change', function() {
                scope.$apply(changeHandler);
            });
            scope.$watch('list', setupHandler, true);
        }
    };
});