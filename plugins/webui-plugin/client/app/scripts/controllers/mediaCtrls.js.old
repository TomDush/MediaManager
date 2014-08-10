'use strict';


angular.module('mediamanager').controller('SearchCtrl', function ($scope, $stateParams, Movie, $http, $state) {
    // Load data ...
    $scope.master = {media: ['movies', 'shows'], genres: []};
    $scope.genres = [];
    $http.get('/api/medias/genres.json').success(function (data) {
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

        // Fire search event...
        if (!isEmpty(query)) {
            $state.go("medias.list", compactObject(query), {inherit: false});
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

angular.module('mediamanager').controller('ListCtrl', function ($scope, Movie, $stateParams, $state) {
    // Ctrl request arguments
    $scope.request = null;
//    $scope.$watch("request", function (newRequest) {
//       Event when request is changed...
//    }, true);

    $scope.selectPage = function (page) {
        $scope.request.index = page;

//        console.log("Refresh list with request : " + JSON.stringify($scope.request));
        $state.go("medias.list", {index: page});
    }

    $scope.noPoster = "/img/no-poster-w92.jpg";
    $scope.resolvePoster = function (img) {
        if (img /*&& img != ''*/) return img;

        return $scope.noPoster;
    }

    // Initialize request params
    if ($stateParams.genres || $stateParams.title || $stateParams.media) {
        if (!$stateParams.media || convertToArray($stateParams.media).indexOf("movies") >= 0) {
            var request = angular.copy($stateParams);
            request.genres = convertToArray($stateParams.genres);
            // Enable pagination
            if (!request.index) {
                request.index = 1;
            }

            $scope.request = request;

            console.log("Requesting : " + JSON.stringify(request));
            $scope.movies = Movie.list(compactObject(request));

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

angular.module('mediamanager').controller('MovieCtrl', function ($scope, Movie, $stateParams, Player, Admin) {

    $scope.movieId = $stateParams.movieId;
    $scope.movie = undefined; // undefined => empty request ; {} => not found ; {a lot of things} => found !

    $scope.notFound = function () {
        return $scope.movie != undefined && isEmpty($scope.movie);
    };
    $scope.loading = function () {
        return $scope.movieId != null && $scope.movie == undefined;
    };

    $scope.noProfile = "/img/no-profile-w45.jpg";
    $scope.resolveProfile = function (img) {
        if (img && img != '') return img;

        return $scope.noProfile;
    }

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

    // ** Player control
    $scope.play = function () {
        console.log("Play movie " + $scope.movie.title)
        Player.play({type: "MOVIE", mediaId: $scope.movie.id, path: $scope.movie.videoFiles[0].file});
    };
    $scope.resume = function () {
        console.log("Resume movie " + $scope.movie.title)
        Player.resume({type: "MOVIE", mediaId: $scope.movie.id});
    };

    // ** Actions control
    $scope.admin = function (request) {
        request.id = $scope.movie.id;
        request.type = 'MOVIE';
        Admin.ctrl(request);

        if (request.action == 'REMOVE_RESUME') {
            $scope.movie.recovery = null;
        }
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
    if (value && value != null && Object.prototype.toString.call(value) !== '[object Array]') {
        return value.split(",");
    }

    return value;
}

function compactObject(o) {
    Object.keys(o).forEach(function (k) {
        if (!o[k]) {
            delete o[k];
        }
    });
    return o;
}