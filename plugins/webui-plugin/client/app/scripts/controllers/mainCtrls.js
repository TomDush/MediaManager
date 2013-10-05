'use strict';


angular.module('mediamanager').controller('MainCtrl', function ($scope, $state) {
    $scope.pageTitle = "Medima - Manage yours medias !";

    $scope.onSearch = function (title) {
        $state.go("medias.list", {title: title});
    };
});

angular.module('mediamanager').controller('HomeCtrl', function ($scope, Movie) {
    $scope.lastMovies = Movie.last({ seen: false, size: 15});
    $scope.random = Movie.random({size: 10});
});
