/*
 * Copyright (c) 2011. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

/**
 * User: crash
 * Date: 4/10/11
 * Time: 10:35 AM
 * This is a generic cometd connection handler.
 * Requirements:
 * There needs to be a config object defined on the same page that includes:
 * Example:
 * var config = {
 *  contextPath : '/casino',
 *  onConnectionEstablished : function(){alert('Hello World');},
 *  onConnectionBroken : function(){alert('Hello World');},
 *  onConnectionClosed : function(){alert('Hello World');},
 *  onHandshakeSuccess : function(){alert('Hello World');},
 *  onHandshakeFailure : function(message){alert(message);}
 * };
 */

// Function that manages the connection status with the Bayeux server
var _connected = false;
var cometd;

function initializeCometHandshake() {
    cometd = $.cometd;

    // enable web sockets
    cometd.websocketEnabled = true;

    cometd.onListenerException = function(exception, subscriptionHandle, isListener, message) {
        // Uh-oh, something went wrong, disable this listener/subscriber
        // Object "this" points to the CometD object
        if (isListener) {
            alert("Removing listener because of Exception: " + exception);
            this.removeListener(subscriptionHandle);
        }
        else {
            alert("Unsubscribing because of Exception: " + exception);
            this.unsubscribe(subscriptionHandle);
        }
    }

    // Disconnect when the page unloads
    $(window).unload(function() {
        cometd.disconnect(true);
    });

    var cometURL = location.protocol + "//" + location.host + config.contextPath + "/cometd";

//    alert(cometURL);

    cometd.configure({
        url: cometURL,
        logLevel: 'info'
    });

    cometd.addListener('/meta/handshake', _metaHandshake);
    cometd.addListener('/meta/connect', _metaConnect);
    cometd.addListener('/meta/disconnect', _metaDisconnect);
    cometd.addListener('/meta/subscribe', _metaSubscribe);
    cometd.addListener('/meta/unsubscribe', _metaUnsubscribe);
    cometd.addListener('/meta/publish', _metaPublish);
    cometd.addListener('/meta/unsuccessful', _metaUnsuccessful);

    cometd.handshake();
}

function _connectionEstablished(message) {
    if (config.onConnectionEstablished && jQuery.isFunction(config.onConnectionEstablished)) {
        config.onConnectionEstablished(message);
    } else {
        alert("onConnectionEstablished callback missing. Connection established.");
    }
}

function _connectionBroken(message) {
    if (config.onConnectionBroken && jQuery.isFunction(config.onConnectionBroken)) {
        config.onConnectionBroken(message);
    } else {
        alert("onConnectionBroken callback missing. Connection broken.");
    }
}

function _connectionClosed(message) {
    if (config.onConnectionClosed && jQuery.isFunction(config.onConnectionClosed)) {
        config.onConnectionClosed(message);
    } else {
        alert("onConnectionClosed callback missing. Connection closed.");
    }
}

function _metaConnect(message) {
    if (cometd.isDisconnected()) {
        _connected = false;
        _connectionClosed(message);
        return;
    }

    var wasConnected = _connected;
    _connected = message.successful === true;
    if (!wasConnected && _connected) {
        _connectionEstablished(message);
    }
    else if (wasConnected && !_connected) {
        _connectionBroken(message);
    }
}

// Function invoked when first contacting the server and
// when the server has lost the state of this client
function _metaHandshake(handshake) {
    if (handshake.successful === true) {
        if (config.onHandshakeSuccess && jQuery.isFunction(config.onHandshakeSuccess)) {
            config.onHandshakeSuccess(handshake);
        } else {
            alert("onHandshakeSuccess callback missing. Handshake succeeded.");
        }
    } else {
        if (config.onHandshakeFailure && jQuery.isFunction(config.onHandshakeFailure)) {
            config.onHandshakeFailure(handshake);
        } else {
            alert("onHandshakeFailure callback missing. Handshake failed.");
        }
    }
}

function _metaDisconnect(message) {
    if (config.onDisconnect && jQuery.isFunction(config.onDisconnect)) {
        config.onDisconnect(message);
    } else {
        alert("onDisconnect callback missing. Connection closed.");
    }
}

function _metaSubscribe(message) {
    if (config.onSubscribe && jQuery.isFunction(config.onSubscribe)) {
        config.onSubscribe(message);
    } else {
        alert("onSubscribe callback missing. Connection closed.");
    }
}

function _metaUnsubscribe(message) {
    if (config.onUnsubscribe && jQuery.isFunction(config.onUnsubscribe)) {
        config.onUnsubscribe(message);
    } else {
        alert("onUnsubscribe callback missing. Connection closed.");
    }
}

function _metaPublish(message) {
    if (config.onPublish && jQuery.isFunction(config.onPublish)) {
        config.onPublish(message);
    } else {
        alert("onPublish callback missing. Connection closed.");
    }
}

function _metaUnsuccessful(message) {
    if (config.onUnsuccessful && jQuery.isFunction(config.onUnsuccessful)) {
        config.onUnsuccessful(message);
    } else {
        alert("onUnsuccessful callback missing. Connection closed.");
    }
}