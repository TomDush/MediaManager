'use strict';

describe('Controller -', function () {
    var rootScope, scope, Movie;

    // load the controller's module
    beforeEach(module('mediamanager'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($rootScope) {
        rootScope = $rootScope;
        scope = $rootScope.$new();
        spyOn(scope, "$emit");

        Movie = jasmine.createSpyObj('Movie', ['last', 'random', 'list', 'find']);
    }));

    describe('SearchCtrl', function () {
        var SearchCtrl, genres, state;


        // Initialize the controller and a mock scope
        beforeEach(inject(function ($controller, _$httpBackend_) {
            genres = ['Action', 'Science Fiction', 'Comics', 'Comedy'];
            var stateParams = {media: ['shows'], genres: ['Action', 'Comedy'], title: "Ironman 1"};

            state = jasmine.createSpyObj('state', ['go']);

            _$httpBackend_.expectGET('/api/medias/genres.json').respond(genres);

            SearchCtrl = $controller('SearchCtrl', { $scope: scope, $stateParams: stateParams, $state: state });
        }));

        it('should initialize with stateParams', function () {
            expect(scope.request).toBeDefined();
            expect(scope.request).toEqual({media: ['shows'], genres: ['Action', 'Comedy'], title: "Ironman 1"});
        });

        it('should reset properly', function () {
            scope.reset();

            expect(scope.request).toEqual({media: ['movies', 'shows'], genres: scope.genres}); // FIXME define precisely genres
        });

        it('should send only needed params', function () {
            scope.submit();

            expect(state.go).toHaveBeenCalledWith('medias.list', {media: ['shows'], genres: ['Action', 'Comedy'], title: "Ironman 1"}, { inherit: false });

            // Other test : without genres...
            scope.selectAllGenres();
            expect(scope.request.genres).toEqual(scope.genres);

            scope.submit();
            expect(state.go).toHaveBeenCalledWith('medias.list', {media: ['shows'], title: "Ironman 1"}, { inherit: false });
        });
    });

    describe('MovieCtrl', function () {
        var MovieCtrl, controller;

        beforeEach(inject(function ($controller) {
            controller = $controller;
            MovieCtrl = $controller('MovieCtrl', {$scope: scope, Movie: Movie});
        }));

        it('should display nothing initially', function () {
            expect(scope.movie).toBeUndefined();
        });

        it('should search Movie on id change', function () {
            var movie = {title: 'Ironman 1'};
            Movie.find.andReturn(movie);

            scope.movieId = '123';

            scope.$digest();

            expect(Movie.find).toHaveBeenCalled();
            expect(Movie.find.calls[0].args[0]).toEqual({id: '123'});

            expect(scope.movie).toBeDefined();
            expect(scope.movie).toBe(movie);
        });

        it('should search movie defined in parameters', function () {
            // Simulate non existing media...
            Movie.find.andCallFake(function (request, success, fail) {
                fail("404 : media with id " + request + " wasn't found.");
            });

            MovieCtrl = controller('MovieCtrl', {$scope: scope, Movie: Movie, $stateParams: {movieId: 321}});
            scope.$digest();

            expect(Movie.find).toHaveBeenCalled();
            expect(Movie.find.calls[0].args[0]).toEqual({id: 321});
        });
    });

    describe('ListCtrl', function () {
        var ListCtrl, controller;

        beforeEach(inject(function ($controller) {
            controller = $controller;
            ListCtrl = $controller('ListCtrl', {$scope: scope, Movie: Movie, $stateParams: {}});
        }));

        it('should search by default random movies', function () {
            expect(Movie.random).toHaveBeenCalled();
            expect(Movie.list).not.toHaveBeenCalled();
        });

        it('should search with $stateParams params', function () {
            var request = {genres: ['Action']};
            var ListCtrl2 = controller('ListCtrl', {$scope: scope, Movie: Movie, $stateParams: request});
            scope.$digest();

            expect(Movie.list).toHaveBeenCalled();
        });
    });

});



