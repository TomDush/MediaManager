'use strict';


angular.module('mediamanager').controller('MainCtrl', function ($scope, $state) {
    $scope.pageTitle = "Medima - Manage yours medias !";

    $scope.onSearch = function (title) {
        $state.go("medias.list", {title: title}, {inherit: false});
    };
});

angular.module('mediamanager').controller('HomeCtrl', function ($scope, Movie, Media, Player) {
    $scope.lastMovies = Movie.last({ seen: 'UNSEEN', size: 15});
    $scope.random = Movie.random({size: 10, notNullFields: "POSTER"});

    // Currently, only movies are managed...
    $scope.inProgress = Media.inProgress();

    $scope.resume = function (recovery) {
        Player.resume({type: recovery.mediaSummary.mediaType, mediaId: recovery.mediaSummary.id});
    };
});
