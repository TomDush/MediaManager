'use strict';

describe('Controller -', function () {
    var scope;

    // load the controller's module
    beforeEach(module('mediamanager'));

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($rootScope) {
        scope = $rootScope.$new();
    }));

    describe('MainCtrl', function () {
        var MainCtrl, state;


        // Initialize the controller and a mock scope
        beforeEach(inject(function ($controller) {
            state = jasmine.createSpyObj('StateUI', ['go']);
            MainCtrl = $controller('MainCtrl', { $scope: scope, $state: state });
        }));

        it('should define page title', function () {
            expect(scope.pageTitle).toBeDefined();
        });

        it('should redirect search to media form', function () {
            // Run
            scope.onSearch("MovieTitle");

            // Check
            expect(state.go).toHaveBeenCalledWith("medias.list", {title: "MovieTitle"});
        });
    });


    describe('HomeCtrl', function () {
        var HomeCtrl, Movie;


        // Initialize the controller and a mock scope
        beforeEach(inject(function ($controller) {
            Movie = jasmine.createSpyObj('Movie', ['last', 'random', 'list', 'find']);
            HomeCtrl = $controller('HomeCtrl', { $scope: scope, Movie: Movie});
        }));

        it('should call correctly Movie object.', function () {
            expect(Movie.last).toHaveBeenCalledWith(jasmine.objectContaining({seen: false}));
            expect(Movie.random).toHaveBeenCalled();
        });

    });
});



