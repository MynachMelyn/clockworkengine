
package clockworktest.network;

import com.clockwork.network.Server;
import com.clockwork.network.MessageConnection;
import com.clockwork.network.Network;
import com.clockwork.network.MessageListener;
import com.clockwork.network.Message;
import com.clockwork.network.AbstractMessage;
import com.clockwork.network.Client;
import com.clockwork.network.serializing.Serializable;
import com.clockwork.network.serializing.Serializer;
import java.io.IOException;

public class TestThroughput implements MessageListener<MessageConnection> { //extends MessageAdapter {

    private static long lastTime = -1;
    private static long counter = 0;
    private static long total = 0;
    // Change this flag to test UDP instead of TCP
    private static boolean testReliable = false;
    private boolean isOnServer;

    public TestThroughput(boolean isOnServer) {
        this.isOnServer = isOnServer;
    }
    
    @Serializable
    public static class TestMessage extends AbstractMessage {

        public TestMessage() {
            setReliable(testReliable);
        }
    }

    @Override
    public void messageReceived(MessageConnection source, Message msg) {

        if (!isOnServer) {
            // It's local to the client so we got it back
            counter++;
            total++;
            long time = System.currentTimeMillis();
//System.out.println( "total:" + total + "  counter:" + counter + "  lastTime:" + lastTime + "  time:" + time );
            if (lastTime < 0) {
                lastTime = time;
            } else if (time - lastTime > 1000) {
                long delta = time - lastTime;
                double scale = delta / 1000.0;
                double pps = counter / scale;
                System.out.println("messages per second:" + pps + "  total messages:" + total);
                counter = 0;
                lastTime = time;
            }
        } else {
            if (source == null) {
                System.out.println("Received a message from a not fully connected source, msg:" + msg);
            } else {
//System.out.println( "sending:" + msg + " back to client:" + source );
                // The 'reliable' flag is transient and the server doesn't
                // (yet) reset this value for us.
                ((com.clockwork.network.Message) msg).setReliable(testReliable);
                source.send(msg);
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        Serializer.registerClass(TestMessage.class);

        // Use this to test the client/server name version check
        //Server server = Network.createServer( "bad name", 42, 5110, 5110 );
        Server server = Network.createServer(5110, 5110);
        server.start();

        Client client = Network.connectToServer("localhost", 5110);
        client.start();

        client.addMessageListener(new TestThroughput(false), TestMessage.class);
        server.addMessageListener(new TestThroughput(true), TestMessage.class);

        Thread.sleep(1);

        TestMessage test = new TestMessage();
//        for( int i = 0; i < 10; i++ ) {
        while (true) {
//System.out.println( "sending." );
            client.send(test);
        }

        //Thread.sleep(5000);
    }
}
