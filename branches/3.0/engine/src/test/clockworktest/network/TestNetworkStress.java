

package clockworktest.network;

import com.clockwork.network.Server;
import com.clockwork.network.HostedConnection;
import com.clockwork.network.Network;
import com.clockwork.network.ConnectionListener;
import com.clockwork.network.Client;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestNetworkStress implements ConnectionListener {
    
    public void connectionAdded(Server server, HostedConnection conn) {
        System.out.println("Client Connected: "+conn.getId());
        //conn.close("goodbye");
    }

    public void connectionRemoved(Server server, HostedConnection conn) {
    }
    
    public static void main(String[] args) throws IOException, InterruptedException{
        Logger.getLogger("").getHandlers()[0].setLevel(Level.OFF);
        
        Server server = Network.createServer(5110);
        server.start();
        server.addConnectionListener(new TestNetworkStress());

        for (int i = 0; i < 1000; i++){
            Client client = Network.connectToServer("localhost", 5110);
            client.start();

            Thread.sleep(10);
            
            client.close();
        }
    }
}
