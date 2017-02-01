
package com.clockwork.network.message;

import com.clockwork.network.AbstractMessage;
import com.clockwork.network.Message;
import com.clockwork.network.serializing.Serializable;

/**
 * CompressedMessage is a base class for all messages that
 *  compress others.
 *
 */
@Serializable()
public class CompressedMessage extends AbstractMessage {
    private Message message;

    public CompressedMessage() { }

    public CompressedMessage(Message msg) {
        this.message = msg;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
