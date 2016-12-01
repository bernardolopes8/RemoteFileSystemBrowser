import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Bernardo Lopes - a32040
 * @author Tiago Padrão - a33061
 */
interface Protocol extends Remote{
    
    /**
     * Returns the default shared directory path
     * 
     * @return absolute path of the shared directory
     * @throws RemoteException 
     */
    public String getDefaultDirectoryPath() throws RemoteException;
    
    /** 
     * Read the contents of a given directory
     * 
     * @param directoryName - absolute path of the chosen directory
     * @return an array with the list of files and directories
     * @throws RemoteException 
     */
    public File[] readDirectory(String directoryName) throws RemoteException; 
    
    /** 
     * Creates a new directory with the given name
     * 
     * @param directoryName - absolute path of the new directory
     * @throws RemoteException 
     */
    public void createDirectory(String directoryName) throws RemoteException; 
    
    /** 
     * Deletes the given file
     * 
     * @param name - absolute path of the file to delete
     * @throws RemoteException 
     */
    public void deleteFile(String name) throws RemoteException; 
    
    /** 
     * Deletes the given directory
     * 
     * @param name - absolute path of the directory to delete
     * @throws RemoteException 
     */
    public void deleteDirectory(String name) throws RemoteException; 
    
    /** 
     * Creates a new file with the given name
     * 
     * @param name - absolute path of the new file
     * @throws RemoteException 
     */
    public void createFile(String name) throws RemoteException; 
    
    /** 
     * Renames a file or directory to the given name
     * 
     * @param name - absolute path of the file/directory to rename
     * @param nameNew - absolute path of the renamed file/directory
     * @throws RemoteException 
     */
    public void rename(String name, String nameNew) throws RemoteException; 
    
    /** 
     * Checks if a file is a directory
     * 
     * @param file - file to check
     * @return true if it's a directory, false otherwise
     * @throws RemoteException 
     */
    public boolean isDirectory(File file) throws RemoteException;
    
    /** 
     * Checks if a file is a file and not a directory
     * 
     * @param file - file to check
     * @return true if it's a file, false otherwise
     * @throws RemoteException 
     */
    public boolean isFile(File file) throws RemoteException;
    
    /** 
     * Returns the length of a file in bytes
     * 
     * @param file - file to check
     * @return length in bytes
     * @throws RemoteException 
     */
    public long getLength(File file) throws RemoteException;
    
    /** 
     * Returns the date a file was last modified
     * 
     * @param file - file to check
     * @return milliseconds since epoch
     * @throws RemoteException 
     */
    public long getLastModifiedDate(File file) throws RemoteException;
}
