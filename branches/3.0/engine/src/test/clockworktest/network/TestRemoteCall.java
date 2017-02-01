

package clockworktest.network;

import com.clockwork.app.SimpleApplication;
import com.clockwork.export.Savable;
import com.clockwork.network.Client;
import com.clockwork.network.Network;
import com.clockwork.network.Server;
import com.clockwork.network.rmi.ObjectStore;
import com.clockwork.network.serializing.Serializer;
import com.clockwork.network.serializing.serializers.SavableSerializer;
import com.clockwork.scene.Spatial;
import java.io.IOException;
import java.util.concurrent.Callable;

public class TestRemoteCall {

    private static SimpleApplication serverApp;

    /**
     * Interface implemented by the server, exposing
     * RMI calls that clients can use.
     */
    public static interface ServerAccess {
        /**
         * Attaches the model with the given name to the server's scene.
         * 
         * @param model The model name
         * 
         * @return True if the model was attached.
         * 
         * @throws RuntimeException If some error occurs.
         */
        public boolean attachChild(String model);
    }

    public static class ServerAccessImpl implements ServerAccess {
        public boolean attachChild(String model) {
            if (model == null)
                throw new RuntimeException("Cannot be null. .. etc");

            final String finalModel = model;
            serverApp.enqueue(new Callable<Void>() {
                public Void call() throws Exception {
                    Spatial spatial = serverApp.getAssetManager().loadModel(finalModel);
                    serverApp.getRootNode().attachChild(spatial);
                    return null;
                }
            });
            return true;
        }
    }

    public static void createServer(){
        serverApp = new SimpleApplication() {
            @Override
            public void simpleInitApp() {
            }
        };
        serverApp.start();

        try {
            Server server = Network.createServer(5110);
            server.start();

            ObjectStore store = new ObjectStore(server);
            store.exposeObject("access", new ServerAccessImpl());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException{
        Serializer.registerClass(Savable.class, new SavableSerializer());

        createServer();

        Client client = Network.connectToServer("localhost", 5110);
        client.start();

        ObjectStore store = new ObjectStore(client);
        ServerAccess access = store.getExposedObject("access", ServerAccess.class, true);
        boolean result = access.attachChild("Models/Oto/Oto.mesh.xml");
        System.out.println(result);
    }
}
