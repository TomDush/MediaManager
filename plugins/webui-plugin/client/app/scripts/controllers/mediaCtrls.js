'use strict';


angular.module('mediamanager').controller('SearchCtrl', function ($scope, $stateParams, Movie, $http, $state) {
    // Load data ...
    $scope.master = {media: ['movies', 'shows'], genres: []};
    $scope.genres = [];
    $http.get('/medias/genres.json').success(function (data) {
        $scope.genres = data;
        $scope.master.genres = angular.copy(data);
        if (!$stateParams.genres) {
            $scope.request.genres = angular.copy(data);
        }
    });

    // Form control
    $scope.reset = function () {
        $scope.request = angular.copy($scope.master);
    }

    $scope.submit = function () {
        // Clean request ...
        var query = angular.copy($scope.request);
        if (query.genres.length == $scope.genres.length || query.genres.length == 0) {
            delete query.genres;
        }
        if (query.media.length == $scope.master.media.length) {
            delete query.media;
        }
        if (!query.title) {
            delete query.title;
        }

        // Fire search event...
        if (!isEmpty(query)) {
            $state.go("medias.list", query, {inherit: false});
        }
    }

    $scope.selectAllGenres = function () {
        $scope.request.genres = angular.copy($scope.genres);
    };
    $scope.clearGenres = function () {
        $scope.request.genres = [];
    };
    $scope.inverseGenres = function () {
        var selected = $scope.request.genres;
        $scope.request.genres = [];
        for (var g in $scope.genres) {
            if (selected.indexOf($scope.genres[g]) < 0) {
                $scope.request.genres.push($scope.genres[g]);
            }
        }
    };


    // Initialize form and try to submit...
    $scope.reset();

    if ($stateParams.title) {
        $scope.request.title = $stateParams.title;
    }
    if ($stateParams.media) {
        $scope.request.media = convertToArray($stateParams.media);
    }
    if ($stateParams.genres) {
        $scope.request.genres = convertToArray($stateParams.genres);
    }
});

angular.module('mediamanager').controller('ParentCtrl', function ($scope, $stateParams, $state) {

});

angular.module('mediamanager').controller('ListCtrl', function ($scope, Movie, $stateParams) {
    // Ctrl request arguments
    $scope.request = null;
    $scope.$watch("request", function (newRequest) {
        if (newRequest != null) {
            console.log("Refresh list with request : " + JSON.stringify(newRequest));
            $scope.movies = Movie.list(newRequest);
        }
    }, true);

    $scope.selectPage = function (page) {
        if (!$scope.request.pagination) {
            $scope.request.pagination = {page: page};
        } else {
            $scope.request.pagination.page = page;
        }
    }

    // Initialize request params
    if ($stateParams.genres || $stateParams.title || $stateParams.media) {
        if (!$stateParams.media || convertToArray($stateParams.media).indexOf("movies") >= 0) {
            var request = angular.copy($stateParams);
            request.genres = convertToArray($stateParams.genres);

            $scope.request = request;
        } else {
            console.log("Must search something, but can't handle it... (must be shows)");
        }

    } else {
        console.log("Random display ...");
        $scope.movies = Movie.random({size: 20});
    }

    // Display facilities
    $scope.noMediaFound = function () {
        return isEmpty($scope.movies);
    };
});

angular.module('mediamanager').controller('MovieCtrl', function ($scope, Movie, $stateParams) {

    $scope.movieId = $stateParams.movieId;
    $scope.movie = undefined; // undefined => empty request ; {} => not found ; {a lot of things} => found !

    $scope.notFound = function () {
        return $scope.movie != undefined && isEmpty($scope.movie);
    };
    $scope.loading = function () {
        return $scope.movieId != null && $scope.movie == undefined;
    };

    $scope.$watch('movieId', function (oldValue, newValue) {
        if (newValue) {
            $scope.movie = Movie.find({ id: newValue }, function (movie) {
            }, function () {
                $scope.movie = {};
            });
        } else {
            $scope.movie = null;
        }
    });

    $scope.play = function () {
        console.log("Play movie " + $scope.movie.title)
    };
    $scope.resume = function () {
        console.log("Resume movie " + $scope.movie.title)
    };

});

/** Object is null or empty (no attribute), or array is empty */
function isEmpty(obj) {

    // null and undefined are empty
    if (obj == null) return true;
    // Assume if it has a length property with a non-zero value
    // that that property is correct.
    if (obj.length && obj.length > 0)    return false;
    if (obj.length === 0)  return true;

    for (var key in obj) {
        if (hasOwnProperty.call(obj, key))    return false;
    }

    // Doesn't handle toString and toValue enumeration bugs in IE < 9

    return true;
}

function convertToArray(value) {
    if (value && value != null && !Object.prototype.toString.call(value) === '[object Array]') {
        return value.split(",");
    }

    return value;
}