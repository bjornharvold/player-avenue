/*
 * Copyright (c) 2010. This beautifully written piece of code has been created by Bjorn Harvold.
 * Please do not use my code without explicit permission or I just might have to come by your office and ruin your day.
 */

(function($) {
    var cometd = $.cometd;

    $(document).ready(function() {
        function _connectionEstablished() {
            $('#body').append('<div>CometD Connection Established</div>');
        }

        function _connectionBroken() {
            $('#body').append('<div>CometD Connection Broken</div>');
        }

        function _connectionClosed() {
            $('#body').append('<div>CometD Connection Closed</div>');
        }

        // Function that manages the connection status with the Bayeux server
        var _connected = false;

        function _metaConnect(message) {
            if (cometd.isDisconnected()) {
                _connected = false;
                _connectionClosed();
                return;
            }

            var wasConnected = _connected;
            _connected = message.successful === true;
            if (!wasConnected && _connected) {
                _connectionEstablished();
            }
            else if (wasConnected && !_connected) {
                _connectionBroken();
            }
        }

        // Function invoked when first contacting the server and
        // when the server has lost the state of this client
        function _metaHandshake(handshake) {
            if (handshake.successful === true) {
                cometd.batch(function() {
                    cometd.subscribe('/service/echo', function(message) {
                        $('#body').append('<div>Server Says: ' + message.data.echo + '</div>');
                    });

                    // Publish on a service channel since the message is for the server only
                    cometd.publish('/service/echo', { echo: 'Ping!' });
                });
            } else {
                window.alert("Could not handshake with comet channel");
            }
        }

        // Disconnect when the page unloads
        $(window).unload(function() {
            cometd.disconnect(true);
        });

        var cometURL = location.protocol + "//" + location.host + config.contextPath + "/cometd";
        cometd.configure({
            url: cometURL,
            logLevel: 'debug'
        });

        cometd.addListener('/meta/handshake', _metaHandshake);
        cometd.addListener('/meta/connect', _metaConnect);

        // adding authentication on top of regular handshake
        cometd.handshake();
    });
})(jQuery);
