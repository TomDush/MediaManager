angular.module('mediamanager')
    .directive('genres', function() {
    return {
        restrict: 'E',
        scope: {
            genres: '=genres'
        },
        template: '<span ng-repeat="g in genres" ><a ui-sref="medias.list({genres : g})">{{g}}</a><span ng-hide="$last">,</span> </span>' +
            '<a class="icon-search child-hover" title="Search similar" ui-sref="medias.list({genres : genres})"/>'
    };
})
    .filter('rate', function($filter) {
    return function(input, votes) {
        if(! input || input == 0) {
            return "";
        }

        var out = $filter('number')(input * 100.0, 0) + " %";

        if(votes) {
            out += " ("+votes + " votes)";
        }
        return out;
    }
});;