import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Bernardo Lopes - a32040
 * @author Tiago Padrão - a33061
 */
public class RMIServer {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RemoteException {
        // Add:
        // -Djava.security.policy=server.policy
        // to the VM arguments when running the server
        if(System.getSecurityManager()==null) {
            System.setSecurityManager(new SecurityManager());
        }
        
        //1. create our remote object
        RemoteProtocol p = new RemoteProtocol();
        //2. create the registry
        Registry registry = LocateRegistry.createRegistry(1099);
        //3. export the object
        Protocol pp = (Protocol)UnicastRemoteObject.exportObject((Remote) p,0);
        //4. register the remote object of the registry
        registry.rebind("myProtocol", (Remote) pp);
        //5. create the shared directory
        pp.createDirectory(pp.getDefaultDirectoryPath());
    }
}
