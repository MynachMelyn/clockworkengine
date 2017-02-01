
package com.clockwork.network.message;

import com.clockwork.network.Message;
import com.clockwork.network.serializing.Serializable;

/**
 * GZIPCompressedMessage is the class that you need to use should you want to
 *  compress a message using Gzip.
 *
 */
@Serializable()
public class GZIPCompressedMessage extends CompressedMessage {
    public GZIPCompressedMessage() {
        super();
    }

    public GZIPCompressedMessage(Message msg) {
        super(msg);
    }
}
