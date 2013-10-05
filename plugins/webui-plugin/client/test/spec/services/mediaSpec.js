'use strict';

var $httpBackend;

/* jasmine specs for controllers go here */
describe('MediaServices -', function () {

    beforeEach(function () {
        this.addMatchers({ toEqualData: function (expected) {
            return angular.equals(this.actual, expected);
        } });
    });

    beforeEach(module('mediaServices'));

    describe('Movie Controller', function () {

        var Movie;

        beforeEach(inject(function ($injector, _$httpBackend_) {
            $httpBackend = _$httpBackend_;

            Movie = $injector.get('Movie');
        }));

        it("should get list of last entered movies", function () {
            // Expected...
            var response = {page: 1, elements: [
                { title: '2012' },
                { title: 'Ironman' }
            ]};
            $httpBackend.expectGET('movies/last.json?order=last&size=5').respond(response);

            // Run
            var movies = Movie.last({ size: 5 });

            // Check
            $httpBackend.flush();
            expect(movies).toEqualData(response);
        });

        it("should get random list of movies", function () {
            var response = {page: 0, elements: [
                { title: '2012' },
                { title: 'Ironman' }
            ]};
            $httpBackend.expectGET('movies/random.json?size=10').respond(response);

            // Exec
            var movies = Movie.random();

            $httpBackend.flush();
            expect(movies).toEqualData(response);
        });

        it("should get list of last unseen movies", function () {
            var response = {page: 2, elements: [
                { title: '2012' },
                { title: 'Ironman' }
            ]};
            $httpBackend.expectGET('movies/last.json?order=last&seen=false&size=7').respond(response);

            var movies = Movie.last({ size: 7, seen: false });

            $httpBackend.flush();
            expect(movies).toEqualData(response);
        });

        it("should get movie details", function () {
            var response = { title: 'Ironman' };
            $httpBackend.expectGET('movie/MOVIE_ID.json').respond(response);

            var movie = Movie.find({ id: "MOVIE_ID"});

            $httpBackend.flush();
            expect(movie).toEqualData(response);
        });
    });
});