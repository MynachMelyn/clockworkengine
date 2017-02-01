
package clockworktest.network;

import com.clockwork.network.Server;
import com.clockwork.network.HostedConnection;
import com.clockwork.network.MessageListener;
import com.clockwork.network.Network;
import com.clockwork.network.Message;
import com.clockwork.network.AbstractMessage;
import com.clockwork.network.serializing.Serializable;
import com.clockwork.network.serializing.Serializer;

/**
 *  A simple test chat server.  When SM implements a set
 *  of standard chat classes this can become a lot simpler.
 *
 *  @version   $Revision$
 */
public class TestChatServer {
    // Normally these and the initialized method would
    // be in shared constants or something.

    public static final String NAME = "Test Chat Server";
    public static final int VERSION = 1;
    public static final int PORT = 5110;
    public static final int UDP_PORT = 5110;

    public static void initializeClasses() {
        // Doing it here means that the client code only needs to
        // call our initialize. 
        Serializer.registerClass(ChatMessage.class);
    }

    public static void main(String... args) throws Exception {
        initializeClasses();

        // Use this to test the client/server name version check
        Server server = Network.createServer(NAME, VERSION, PORT, UDP_PORT);
        server.start();

        ChatHandler handler = new ChatHandler();
        server.addMessageListener(handler, ChatMessage.class);

        // Keep running basically forever
        synchronized (NAME) {
            NAME.wait();
        }
    }

    private static class ChatHandler implements MessageListener<HostedConnection> {

        public ChatHandler() {
        }

        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof ChatMessage) {
                // Keep track of the name just in case we 
                // want to know it for some other reason later and it's
                // a good example of session data
                source.setAttribute("name", ((ChatMessage) m).getName());

                System.out.println("Broadcasting:" + m + "  reliable:" + m.isReliable());

                // Just rebroadcast... the reliable flag will stay the
                // same so if it came in on UDP it will go out on that too
                source.getServer().broadcast(m);
            } else {
                System.err.println("Received odd message:" + m);
            }
        }
    }

    @Serializable
    public static class ChatMessage extends AbstractMessage {

        private String name;
        private String message;

        public ChatMessage() {
        }

        public ChatMessage(String name, String message) {
            setName(name);
            setMessage(message);
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setMessage(String s) {
            this.message = s;
        }

        public String getMessage() {
            return message;
        }

        public String toString() {
            return name + ":" + message;
        }
    }
}
