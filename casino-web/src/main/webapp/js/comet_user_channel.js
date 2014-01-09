/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

/**
 * User: crash
 * Date: 4/10/11
 * Time: 10:35 AM
 * Includes all the publish and subscribe message a user channel supports that are game related.
 */
var userChannelRoot = "/user";

function listenToUserChannel(json) {
    var url = userChannelRoot + "/event/" + json.userId;

    cometd.subscribe(url, function(message) {
        // something was published. let's check the event so we can forward the event to
        // the correct process method.

        switch (message.data.event) {
            case "QUEUE_PLAYER_EVENT":
                onUserQueuePlayer(message.data);
                break;
            case "REQUEUE_PLAYER_EVENT":
                onUserReQueuePlayer(message.data);
                break;
            case "DEALING_HAND_EVENT":
                onUserDealingHand(message.data);
                break;
            case "WAITING_FOR_SMALL_BLIND_EVENT":
                onUserWaitingForSmallBlind(message.data);
                break;
            case "WAITING_FOR_BIG_BLIND_EVENT":
                onUserWaitingForBigBlind(message.data);
                break;
            case "POST_SMALL_BLIND_EVENT":
                onUserPostSmallBlind(message.data);
                break;
            case "POST_BIG_BLIND_EVENT":
                onUserPostBigBlind(message.data);
                break;
            case "DEALING_POCKET_CARDS_EVENT":
                onUserDealingPocketCards(message.data);
                break;
            case "PLAYER_RESPONSE_REQUIRED_EVENT":
                onUserPlayerResponseRequired(message.data);
                break;
            case "FAILURE_EVENT":
                onUserFailure(message.data);
                break;
            case "CALL_EVENT":
                onUserCall(message.data);
                break;
            case "NOT_POST_SMALL_BLIND_EVENT":
                onUserNotPostSmallBlind(message.data);
                break;
            case "NOT_POST_BIG_BLIND_EVENT":
                onUserNotPostBigBlind(message.data);
                break;
            case "CHECK_EVENT":
                onUserCheck(message.data);
                break;
            case "RAISE_EVENT":
                onUserRaise(message.data);
                break;
            case "FOLD_EVENT":
                onUserFold(message.data);
                break;
            case "LEAVE_EVENT":
                onUserLeave(message.data);
                break;
            case "TIMEOUT_EVENT":
                onUserTimeout(message.data);
                break;
            case "SHOWDOWN_EVENT":
                onUserShowdown(message.data);
                break;
            case "END_GAME_EVENT":
                onUserEndGame(message.data);
                break;
        }
    });
}

function onUserQueuePlayer(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");

    enableLeave();
}

function onUserWaitingForSmallBlind(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserWaitingForBigBlind(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserPostBigBlind(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserPostSmallBlind(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserFailure(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserCall(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserCheck(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserRaise(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserFold(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserLeave(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");

    disableLeave();
}

function onUserReQueuePlayer(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");

    disableRequeue();
}

function onUserTimeout(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");

    // disable functions
    disableAllPlayerActions();

    // enable requeue action
    enableRequeue();
}

function onUserShowdown(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserEndGame(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserNotPostSmallBlind(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserNotPostBigBlind(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserPlayerResponseRequired(json) {
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");

    $("#countdown").css("display", "block");

    $("#countdown").countdown({
                until: json.time,
                format: "S",
                onExpiry: function() {
                    // disable functions
                    disableAllPlayerActions();

                    // hide timer
                    $("#countdown").css("display", "none");

                    // unbind all event event handlers
                    $("#countdown").countdown("destroy");
                }
            });

    // this is where we enable player action links
    $.each(json.availableActions, function(i, availableAction) {
        switch (availableAction) {
            case "CALL_ACTION":
                enableCall();
                break;
            case "RAISE_ACTION":
                enableRaise(json.maxAmount, json.minAmount);
                break;
            case "CHECK_ACTION":
                enableCheck();
                break;
            case "FOLD_ACTION":
                enableFold();
                break;
            case "LEAVE_ACTION":
                enableLeave();
                break;
        }
    });
}

function onUserDealingPocketCards(json) {
    $("#privateCards").empty();
    $("#privateCards").append(json.cards.card1 + ", " + json.cards.card2);
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function onUserDealingHand(json) {
    // set handId
    handId = json.handId;
    // set gamblerId
    gamblerId = json.gamblerId;
    $("#userLog").append("<p>" + $.JSON.encode(json) + "</p>");
}

function doCall() {
    var json = createCommonJSONPayload("CALL_ACTION");

    if (json.userId && json.pokergameId && json.handId && json.gamblerId && json.action) {
        doCommonAction(json);
    } else {
        alert("Missing data to call");
    }
}

function doCheck() {
    var json = createCommonJSONPayload("CHECK_ACTION");

    if (json.userId && json.pokergameId && json.handId && json.gamblerId && json.action) {
        doCommonAction(json);
    } else {
        alert("Missing data to check");
    }
}

function doRaise(ijson) {
    var json = createCommonJSONPayload("RAISE_ACTION");
    json.amount = ijson.amount.replace(/\$/g, '');

    alert("raise amount: " + json.amount);

    if (json.userId && json.pokergameId && json.handId && json.gamblerId && json.action && json.amount) {
        doCommonAction(json);
    } else {
        alert("Missing data to raise");
    }
}

function doFold() {
    var json = createCommonJSONPayload("FOLD_ACTION");

    if (json.userId && json.pokergameId && json.handId && json.gamblerId && json.action) {
        doCommonAction(json);
    } else {
        alert("Missing data to fold");
    }
}

function doLeave() {
    var json = createCommonJSONPayload("LEAVE_ACTION");

    if (json.userId && json.pokergameId && json.playerId && json.action) {
        doCommonAction(json);
    } else {
        alert("Missing data to leave game");
    }
}

function doRequeue() {
    var json = createCommonJSONPayload("REQUEUE_PLAYER_ACTION");

    if (json.userId && json.pokergameId && json.gamblerId && json.action) {
        doCommonAction(json);
    } else {
        alert("Missing data to requeue player");
    }
}

/**
 * Requirements:
 * var json = {
 *     pokergameId: 'pokergameId',
 *     playerId: 'playerId',
 *     buyin: 100.00,
 *     seatNumber: 1,
 *     mustHaveSeat: false
 * };
 * @param incoming
 */
function doQueuePlayer(incoming) {
    var json = {
        buyin: incoming.buyin,
        seatNumber: incoming.seatNumber,
        mustHaveSeat: incoming.mustHaveSeat,
        pokergameId: incoming.pokergameId,
        playerId: incoming.playerId,
        action: "QUEUE_PLAYER_ACTION",
        userId: incoming.userId
    };

    if (json.userId && json.pokergameId && json.playerId && json.buyin && json.seatNumber && json.mustHaveSeat) {
        publishCometAction(json);
    } else {
        alert("Missing data to make call");
    }
}

function publishCometAction(json) {
    cometd.publish(userChannelRoot + "/action/" + json.userId, json);
}

/**
 * This code will work as long as we have only one game on the page at the time.
 * @param action
 */
function createCommonJSONPayload(action) {
    var json = {
        pokergameId: pokergameId,
        handId: handId,
        gamblerId: gamblerId,
        action: action,
        userId: userId,
        playerId: playerId
    };

    return json;
}

function doCommonAction(json) {
    publishCometAction(json);

    disableAllPlayerActions();

    // hide timer
    $("#countdown").css("display", "none");

    // unbind all event event handlers
    $("#countdown").countdown("destroy");
}
function disableAllPlayerActions() {
    disableCall();
    disableCheck();
    disableFold();
    disableRaise();
}

function enableCheck() {
    $("#checkAction").attr("href", "#");
    $("#checkAction").click(function() {
        $("#systemLog").append("<p>I want to check!</p>");
        doCheck()
    });
}

function disableCheck() {
    $("#checkAction").unbind("click");
    $("#checkAction").removeAttr("href");
}

function enableCall() {
    $("#callAction").attr("href", "#");
    $("#callAction").click(function() {
        $("#systemLog").append("<p>I want to call!</p>");
        doCall()
    });
}

function disableCall() {
    $("#callAction").unbind("click");
    $("#callAction").removeAttr("href");
}

function enableRaise(max, min) {
    $("#raiseAction").attr("href", "#");
    $("#raiseAction").click(function() {
        $("#systemLog").append("<p>I want to raise!</p>");
        doRaise({
                    amount: $("#amount").val()
                });
    });
    $("#amount").val("$" + min);
    $("#slider").slider("option", "disabled", false);
    $("#slider").slider("option", "max", max);
    $("#slider").slider("option", "min", min);
}

function disableRaise() {
    $("#raiseAction").unbind("click");
    $("#raiseAction").removeAttr("href");
    $("#slider").slider("option", "disabled", true);
    $("#amount").val("");
}

function enableFold() {
    $("#foldAction").attr("href", "#");
    $("#foldAction").click(function() {
        $("#systemLog").append("<p>I want to fold!</p>");
        doFold()
    });
}

function disableFold() {
    $("#foldAction").unbind("click");
    $("#foldAction").removeAttr("href");
}

function enableLeave() {
    // we might have bound before - so resetting here first
    $("#leaveAction").unbind("click");

    $("#leaveAction").attr("href", "#");
    $("#leaveAction").click(function() {
        $("#systemLog").append("<p>I want to leave!</p>");
        doLeave()
    });
}

function disableLeave() {
    $("#leaveAction").unbind("click");
    $("#leaveAction").removeAttr("href");
}

function enableRequeue() {
    $("#requeueAction").attr("href", "#");
    $("#requeueAction").click(function() {
        $("#systemLog").append("<p>I am back!</p>");
        doRequeue()
    });
}

function disableRequeue() {
    $("#requeueAction").unbind("click");
    $("#requeueAction").removeAttr("href");
}