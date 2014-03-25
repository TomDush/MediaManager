'use strict';


angular.module('mediamanager').controller('PlayerCtrl', function ($scope, $state, Player, $interval) {


    function updatePlayers() {

        $scope.players = Player.playing({}, function (val) {
            $scope.player = val[0];
            $scope.playing = $scope.player;
            $scope.progression = Math.round(100 * $scope.player.position / $scope.player.length);
        });
    }

    function playerCmd(cmd) {
        Player.ctrl({action: cmd, playerId: $scope.player.id});
    }

    // ** Define button action
    $scope.stepBack = function () {
        playerCmd("JUMP_BACK");
    };
    $scope.stop = function () {
        playerCmd("STOP");
    };
    $scope.togglePause = function () {
        playerCmd("TOGGLE_PAUSE");
    };
    $scope.stepForward = function () {
        playerCmd("JUMP_FORWARD");
    };

    // Start listening ...
    $interval(updatePlayers, 5000);

    // Force first update
    updatePlayers();
});
