'use strict';

// var request = { filter  : {
//   order: 'alpha', // 'last', 'random'
//   seen: true,
//   unseen: true,
//   title:'',
//   crew:'',
//   genres: ['action']},
//   size: 10, // number of expected elements
// pagination: {
//   index: 2, // page to display
//   pageSize: 10 // element by page
// };

// var result = {
//   page: 1,   // 0 if no pagination possible
//   pageSize: 10,  // elements by page
//   number: 5  // Number of pages
//   size: 48,  // Number of available elements
//   elements: [...] // Elements !
// }

angular.module('mediaServices', [ 'ngResource' ]).factory('Movie',
    function ($resource) {
        return $resource('api/:destination/:ctrl:id.json', {destination: "movies"}, {
            last: { method: 'GET', params: { ctrl: 'last',size: '@size || 10' } },
            random: { method: 'GET', params: { ctrl: 'random', size: '@size || 10' } },
            list: { method: 'GET', params: { ctrl: 'list', size: '@size || 10' } },
            find: {method: 'GET', params: {destination: "movie"}}
        });
    });
