'use strict';

angular.module('mediamanager', ['ui.router', 'ui.bootstrap', 'mediaServices' ]).config(function ($locationProvider, $stateProvider, $urlRouterProvider) {
    $locationProvider.html5Mode(true);


    $urlRouterProvider.otherwise("/");

    $stateProvider
        .state('home', {
            url: "/",
            views: {
                "menu": { templateUrl: "views/topMenu.html" },
                "content": { templateUrl: "views/home.html", controller: "HomeCtrl" },
                "player": {templateUrl: "views/player.html", controller: "PlayerCtrl" }
            }
        })
        .state('medias', {
            abstract: true,
            views: {
                "menu": { templateUrl: "views/topMenu.html" },
                "content": { templateUrl: "layouts/mediaList.html", controller: "ParentCtrl" },
                "player": {templateUrl: "views/player.html", controller: "PlayerCtrl" }
            }/*,
             resolve:{
             reloadOnSearch: false // Do not reload this page when url/param changed.
             }*/
        })
        .state('medias.list', {
            url: "/medias/?title&media&genres&movieId&index",
            views: {
                "searchForm": { templateUrl: "views/search.html", controller: "SearchCtrl", reloadOnSearch: false },
                "list": { templateUrl: "views/medias.html", controller: "ListCtrl", reloadOnSearch: false },
                "sheet": { templateUrl: "views/movie.html", controller: "MovieCtrl", reloadOnSearch: false }
            }
        })
        .state('sheet', {
            //url: "/",
            abstract: true,
            views: {
                "menu": { templateUrl: "views/topMenu.html" },
                "content": { templateUrl: "layouts/sheet.html" },
                "player": {templateUrl: "views/player.html", controller: "PlayerCtrl" }
            }
        })
        .state('sheet.movie', {
            url: "/movie/:movieId",
            views: {
                "searchForm": { templateUrl: "views/search.html", controller: "SearchCtrl" },
                "sheet": { templateUrl: "views/movie.html", controller: "MovieCtrl" }
            }
        });
})
//;
//app

;