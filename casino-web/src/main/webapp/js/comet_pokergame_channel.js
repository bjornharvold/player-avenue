/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

/**
 * User: crash
 * Date: 4/10/11
 * Time: 10:35 AM
 * Responsible for listening to the broadcasts from the public game channel. This should be called after a
 * successful handshake.
 *
 * Requirements:
 * var json = {
 *     url = 'comet url',
 * };
 */
var pokergameChannelRoot = "/services/game/";

function listenToPokerGameChannel(json) {
    var url = pokergameChannelRoot + json.pokergameId;

    cometd.subscribe(url, function(message) {
        // something was published. let's check the event so we can forward the event to
        // the correct process method.

        switch (message.data.event) {
            case "PLAYER_RESPONSE_REQUIRED_EVENT":
                onPlayerResponseRequired(message.data);
                break;
            case "WAITING_FOR_BIG_BLIND_EVENT":
                onWaitingForBigBlind(message.data);
                break;
            case "DEALING_FLOP_EVENT":
                onDealFlop(message.data);
                break;
            case "DEALING_POCKET_CARDS_EVENT":
                onDealPocketCards(message.data);
                break;
            case "WAITING_FOR_SMALL_BLIND_EVENT":
                onWaitingForSmallBlind(message.data);
                break;
            case "DEALING_RIVER_EVENT":
                onDealRiver(message.data);
                break;
            case "DEALING_TURN_EVENT":
                onDealTurn(message.data);
                break;
            case "POST_SMALL_BLIND_EVENT":
                onPostSmallBlind(message.data);
                break;
            case "POST_BIG_BLIND_EVENT":
                onPostBigBlind(message.data);
                break;
            case "DEALING_HAND_EVENT":
                onDealNewHand(message.data);
                break;
            case "FAILURE_EVENT":
                onFailure(message.data);
                break;
            case "CALL_EVENT":
                onCall(message.data);
                break;
            case "CHECK_EVENT":
                onCheck(message.data);
                break;
            case "RAISE_EVENT":
                onRaise(message.data);
                break;
            case "FOLD_EVENT":
                onFold(message.data);
                break;
            case "LEAVE_EVENT":
                onLeave(message.data);
                break;
            case "QUEUE_PLAYER_EVENT":
                onQueuePlayer(message.data);
                break;
            case "TIMEOUT_EVENT":
                onTimeout(message.data);
                break;
            case "SHOWDOWN_EVENT":
                onShowdown(message.data);
                break;
            case "END_GAME_EVENT":
                onEndGame(message.data);
                break;
        }
    });
}

function onPlayerResponseRequired(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onWaitingForSmallBlind(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onWaitingForBigBlind(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onDealNewHand(json) {
    $("#privateCards").empty();
    $("#publicCards").empty();
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
    $("#players").empty();

    if (json.players) {
        var table = $("#players").append('<table></table>').append('<tr></tr>');
        $.each(json.players, function(i, player) {
            table.append('<td align="center"><img src="' + s3Url + player.avatar + '" alt="avatar"/><br/>' + player.nickname + ' (seat: ' + player.seatNumber +')</td>');
        });
    }
}

function onDealPocketCards(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onDealFlop(json) {
    $("#publicCards").empty();
    $("#publicCards").append(json.cards.card1 + ", " + json.cards.card2 + ", " + json.cards.card3);
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onDealTurn(json) {
    $("#publicCards").empty();
    $("#publicCards").append(json.cards.card1 + ", " + json.cards.card2 + ", " + json.cards.card3 + ", " + json.cards.card4);
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onDealRiver(json) {
    $("#publicCards").empty();
    $("#publicCards").append(json.cards.card1 + ", " + json.cards.card2 + ", " + json.cards.card3 + ", " + json.cards.card4 + ", " + json.cards.card5);
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onShowdown(json) {
    if (json.winners) {
        $("#winners").empty();
        var ul = $("<ul></ul>");
        $.each(json.winners, function(i, winner) {
            ul.append('<li>' + winner.nickname + ' : ' + winner.handName + ' : $' + winner.amount +'</li>');
        });
        $("#winners").append(ul);
    }
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onEndGame(json) {
    $("#winners").empty();
    var ul = $("<ul></ul>");
    ul.append('<li>' + json.nickname + ' : $' + json.amount +'</li>')
    $("#winners").append(ul);
    
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onPostSmallBlind(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
    updatePot(json);
}

function onPostBigBlind(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
    updatePot(json);
}

function onCall(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
    updatePot(json);
}

function onCheck(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onRaise(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
    updatePot(json);
}

function onFold(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onLeave(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onQueuePlayer(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onTimeout(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onFailure(json) {
    $("#gameLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function updatePot(json) {
    $("#pot").empty();
    $("#pot").append(json.pot);
}