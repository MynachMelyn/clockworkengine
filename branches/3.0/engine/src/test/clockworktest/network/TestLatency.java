

package clockworktest.network;

import com.clockwork.network.Server;
import com.clockwork.network.HostedConnection;
import com.clockwork.network.MessageListener;
import com.clockwork.network.Network;
import com.clockwork.network.Message;
import com.clockwork.network.AbstractMessage;
import com.clockwork.network.Client;
import com.clockwork.network.serializing.Serializable;
import com.clockwork.network.serializing.Serializer;
import java.io.IOException;

public class TestLatency {

    private static long startTime;
    private static Client client;
    private static MovingAverage average = new MovingAverage(100);

    static {
        startTime = System.currentTimeMillis();
    }

    private static long getTime(){
        return System.currentTimeMillis() - startTime;
    }

    @Serializable
    public static class TimestampMessage extends AbstractMessage {

        long timeSent     = 0;
        long timeReceived = 0;

        public TimestampMessage(){
            setReliable(false);
        }

        public TimestampMessage(long timeSent, long timeReceived){
            setReliable(false);
            this.timeSent = timeSent;
            this.timeReceived = timeReceived;
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException{
        Serializer.registerClass(TimestampMessage.class);

        Server server = Network.createServer(5110);
        server.start();

        client = Network.connectToServer("localhost", 5110);
        client.start();
        
        client.addMessageListener(new MessageListener<Client>(){
            public void messageReceived(Client source, Message m) {
                TimestampMessage timeMsg = (TimestampMessage) m;

                long curTime = getTime();
                //System.out.println("Time sent: " + timeMsg.timeSent);
                //System.out.println("Time received by server: " + timeMsg.timeReceived);
                //System.out.println("Time recieved by client: " + curTime);

                long latency = (curTime - timeMsg.timeSent);
                System.out.println("Latency: " + (latency) + " ms");
                //long timeOffset = ((timeMsg.timeSent + curTime) / 2) - timeMsg.timeReceived;
                //System.out.println("Approximate timeoffset: "+ (timeOffset) + " ms");

                average.add(latency);
                System.out.println("Average latency: " + average.getAverage());

                long latencyOffset = latency - average.getAverage();
                System.out.println("Latency offset: " + latencyOffset);

                client.send(new TimestampMessage(getTime(), 0));
            }
        }, TimestampMessage.class);

        server.addMessageListener(new MessageListener<HostedConnection>(){
            public void messageReceived(HostedConnection source, Message m) {
                TimestampMessage timeMsg = (TimestampMessage) m;
                TimestampMessage outMsg = new TimestampMessage(timeMsg.timeSent, getTime());
                source.send(outMsg);
            }
        }, TimestampMessage.class);

        Thread.sleep(1);

        client.send(new TimestampMessage(getTime(), 0));
        
        Object obj = new Object();
        synchronized(obj){
            obj.wait();
        }
    }

}
