'use strict';


angular.module('mediamanager').controller('PlayerCtrl', function ($scope, $state, Player, $interval) {


    /** Get player in use and update scope*/
    function updatePlayers() {

        $scope.players = Player.playing({}, function (val) {
            // Get non-background player, with defined media.
            var toDisplay = null;
            angular.forEach(val, function (value, key) {
                if (toDisplay == null && !value.background && value.media) {
                    toDisplay = value;
                }

                // Force previously selected
                if (value.id == $scope.selected) {
                    toDisplay = value;
                }
            });
            $scope.player = toDisplay == null ? val[0] : toDisplay;

            // Set is-playing flag and compute progression
            $scope.playing = $scope.player != null;
            if ($scope.playing) {
                $scope.progression = Math.round(100 * $scope.player.position / $scope.player.length);
            }
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
    $interval(updatePlayers, 1000);

    // Force first update
    updatePlayers();

    // Define function used by UI
    $scope.otherPlayers = function () {
        return $scope.players.length > 1;
    }
    $scope.getDisplayName = function (player) {
        console.log("Display name: " + player.name);
        var name = player.name;
        if (player.media != null) {
            name += ": " + player.media.title;
        }
        return name;
    }
    $scope.select = function (player) {
        $scope.selected = player.id;
        $scope.player = player;
    }
});
