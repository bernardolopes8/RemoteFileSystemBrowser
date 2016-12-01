import java.io.File;
import static java.lang.System.exit;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * @author Bernardo Lopes - a32040
 * @author Tiago Padrão - a33061
 */
public class RMIClient extends javax.swing.JFrame {
    private final Registry registry;
    private final Protocol p;
    private File[] files;
    private File currentDir;
    private final File defaultDir;
    private final String address;
    
    /**
     * Creates new form RMIClient
     * 
     * @throws java.rmi.RemoteException
     * @throws java.rmi.NotBoundException
     */
    public RMIClient() throws RemoteException, NotBoundException {
        address = JOptionPane.showInputDialog(new JFrame(), "Enter the server IP address:", "Remote File System Browser", JOptionPane.QUESTION_MESSAGE);
        if (address == null) exit(0);
        registry = LocateRegistry.getRegistry(address);
        
        p = (Protocol)registry.lookup("myProtocol");
        
        // Reads the default directory
        files = p.readDirectory(p.getDefaultDirectoryPath());
        currentDir = new File(p.getDefaultDirectoryPath());
        defaultDir = currentDir;
        
        initComponents();
    }
    
    /**
     * Returns a formatted date string from a milliseconds since epoch value
     * 
     * @param milliseconds - milliseconds since epoch
     * @return date string in the dd/MM/yyyy HH:mm:ss format
     */
    private String getDateString(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = sdf.format(new Date(milliseconds));
        return dateString;
    }
    
    /**
     * Returns a formatted size string from a byte size value
     * Based on http://stackoverflow.com/a/3758880/5502785
     * 
     * @param bytes - size in bytes
     * @return formatted string
     */
    private String formatSize(long bytes) {
        if (bytes < 1000) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1000));
        String pre = "kMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1000, exp), pre);
    }
    
    /**
     * Checks if a given file or directory exists in the chosen directory
     * 
     * @param directory - directory to check in
     * @param file - file or directory to check
     * @return true if the file or directory exists, false otherwise
     */
    private boolean fileExists(String directory, String file) {
        File[] contents = null;
        
        // Reads the directory
        try {
            contents = p.readDirectory(directory);
        } catch (RemoteException ex) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // For each item in the directory, check if it corresponds to the given file or directory
        for (File item:contents) {
            if (item.getName().equals(file)) return true;
        }
        
        return false;
    }
    
    /**
     * Lists the number of directories and files in a given directory
     * 
     * @param directory - directory to read
     * @return formatted string with the number of files and directories
     */
    private String directoryContents(File directory) throws RemoteException {
        int directoryCount = 0, fileCount = 0;
        String directories = "directories", files = "files";
        File[] contents = null;
        
        try {
            contents = p.readDirectory(directory.getAbsolutePath());
        } catch (RemoteException ex) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (File item:contents) {
            if (p.isDirectory(item)) {
                directoryCount++;
            }
            else {
                fileCount++;
            } 
        }
        
        if (directoryCount == 0 && fileCount == 0) return "Empty directory";
        if (directoryCount == 1) directories = "directory";
        if (fileCount == 1) files = "file";
        
        return directoryCount + " " + directories + " and " + fileCount + " " + files;
    }
    
    /**
     * Updates the list model
     * 
     * @return list model
     */
    private DefaultListModel refreshModel() {
        DefaultListModel model = new DefaultListModel();
        
        for (File file:files) {
            model.addElement(file.getName());
        }

        return model;
    }
    
    /**
     * Updates the properties table
     * 
     * @return table model
     */
    private DefaultTableModel refreshProperties() throws RemoteException {
        // Gets the selected value from the list
        String selectedValue = fileList.getSelectedValue();
        
        // Gets the file corresponding to the selected value
        File selectedItem = null;
        for (File file:files)
        {
            selectedItem = file;
            if (file.getName() == null ? selectedValue == null : file.getName().equals(selectedValue)) break;
        }
        
        // Creates a new table model with two columns
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Property");
        model.addColumn("Value");
        
        if (selectedItem == null) return model;
        
        // If the selected item is a directory creates rows with directory properties
        if (p.isDirectory(selectedItem)) {
            model.addRow(new String[]{"Type", "Directory"});
            model.addRow(new String[]{"Name", selectedItem.getName()});
            model.addRow(new String[]{"Contents", directoryContents(selectedItem)});
        }
        
        // If the selected item is a file creates rows with file properties
        else {
            model.addRow(new String[]{"Type", "File"});
            
            // Checks if the file name has a file extension
            if (selectedItem.getName().contains(".") && selectedItem.getName().indexOf(".") != 0) {
                model.addRow(new String[]{"Name", selectedItem.getName().substring(0, selectedItem.getName().indexOf("."))});
                model.addRow(new String[]{"File Extension", selectedItem.getName().substring(selectedItem.getName().indexOf(".")+1).toUpperCase()});
            }
            else {
                model.addRow(new String[]{"Name", selectedItem.getName()});
                model.addRow(new String[]{"File Extension", "Unknown"});
            }
            model.addRow(new String[]{"Size", formatSize(p.getLength(selectedItem))});
        }
        
        model.addRow(new Object[]{"Last modified", getDateString(p.getLastModifiedDate(selectedItem))});
        
        return model;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        openFolderButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileList = new javax.swing.JList<>();
        backButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();
        propertiesLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        propertiesTable = new javax.swing.JTable();
        renameButton = new javax.swing.JButton();
        newFolderButton = new javax.swing.JButton();
        newFileButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        openFolderButton.setText("Open");
        openFolderButton.setEnabled(false);
        openFolderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openFolderButtonMouseClicked(evt);
            }
        });

        DefaultListModel listModel = refreshModel();
        fileList.setModel(listModel);
        fileList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                fileListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(fileList);

        backButton.setText("Back");
        backButton.setEnabled(false);
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backButtonMouseClicked(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.setEnabled(false);
        deleteButton.setName(""); // NOI18N
        deleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                deleteButtonMouseClicked(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Raleway", 1, 24)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Remote File System ");
        titleLabel.setToolTipText("");

        propertiesLabel.setFont(new java.awt.Font("Raleway", 0, 18)); // NOI18N
        propertiesLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        propertiesLabel.setText("Properties ");

        DefaultTableModel tableModel;
        try {
            tableModel = refreshProperties();
            propertiesTable.setModel(tableModel);
        } catch (RemoteException ex) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        jScrollPane4.setViewportView(propertiesTable);

        renameButton.setText("Rename");
        renameButton.setEnabled(false);
        renameButton.setName(""); // NOI18N
        renameButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                renameButtonMouseClicked(evt);
            }
        });

        newFolderButton.setText("New Folder");
        newFolderButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                newFolderButtonMouseClicked(evt);
            }
        });

        newFileButton.setText("New File");
        newFileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                newFileButtonMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(newFolderButton)
                                .addGap(18, 18, 18)
                                .addComponent(newFileButton)
                                .addGap(18, 18, 18)
                                .addComponent(openFolderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(56, 56, 56)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                                    .addComponent(propertiesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(renameButton)
                                .addGap(18, 18, 18)
                                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(38, 38, 38)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(propertiesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(openFolderButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(deleteButton)
                        .addComponent(renameButton)
                        .addComponent(newFolderButton)
                        .addComponent(newFileButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(backButton)))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Opens the selected folder when the Open button is clicked
     * 
     * @param evt 
     */
    private void openFolderButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openFolderButtonMouseClicked
        // Checks if the button state is set to enabled
        if (!openFolderButton.isEnabled()) return;
        
        // Gets the selected value from the list
        String selectedValue = fileList.getSelectedValue();
        
        // Gets the file corresponding to the selected value
        File selectedItem = null;
        for (File file:files)
        {
            selectedItem = file;
            if (file.getName() == null ? selectedValue == null : file.getName().equals(selectedValue)) break;
        }
        
        try {
            // Reads the directory contents
            files = p.readDirectory(selectedItem.getAbsolutePath());
            
            // Updates the current directory
            currentDir = selectedItem;
            
            // Updates the list model
            fileList.setModel(refreshModel());
            
            // Enables the back button
            backButton.setEnabled(true);
        } catch (RemoteException ex) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_openFolderButtonMouseClicked
    
    /**
     * Returns to the parent directory of the current folder when the Back button is clicked
     * 
     * @param evt 
     */
    private void backButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backButtonMouseClicked
        // Checks if the button state is set to enabled
        if (!backButton.isEnabled()) return;
        
        else {
            try {
                // Gets the parent directory's path and reads its contents
                files = p.readDirectory(currentDir.getParentFile().getAbsolutePath());

                // Updates the current directory
                currentDir = currentDir.getParentFile();

                // Updates the list model
                fileList.setModel(refreshModel());
                
                // If the the current directory is the default directory the button is disabled
                if (currentDir.equals(defaultDir)) {
                    backButton.setEnabled(false);
        }
            } catch (RemoteException ex) {
                Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_backButtonMouseClicked
    
    /**
     * Deletes the selected file or directory when the Delete button is clicked
     * 
     * @param evt 
     */
    private void deleteButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_deleteButtonMouseClicked
        // Checks if the button state is set to enabled
        if (!deleteButton.isEnabled()) return;
        
        // Gets the selected value from the list
        String selectedValue = fileList.getSelectedValue();
        
        // Gets the file corresponding to the selected value
        File selectedItem = null;
        for (File file:files)
        {
            selectedItem = file;
            if (file.getName() == null ? selectedValue == null : file.getName().equals(selectedValue)) break;
        }
        
        try {
            // Gets the current directory's path
            String parent = currentDir.getAbsolutePath();
            
            if (selectedItem.isFile()) {
                // Deletes the selected file
                p.deleteFile(selectedItem.getAbsolutePath());
            }
            else {
                // Recursively deletes the selected folder
                p.deleteDirectory(selectedItem.getAbsolutePath());
            }
            
            // Updates the list model
            files = p.readDirectory(parent);
            fileList.setModel(refreshModel());
        } catch (RemoteException ex) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_deleteButtonMouseClicked

    /**
     * Checks if the selected list item has changed
     * 
     * @param evt 
     */
    private void fileListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_fileListValueChanged
        // Disables the Open, Rename and Delete buttons if nothing is selected
        if (fileList.isSelectionEmpty()) {
            openFolderButton.setEnabled(false);
            renameButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
        else {
            // Gets the selected value from the list
            String selectedValue = fileList.getSelectedValue();

            // Gets the file corresponding to the selected value
            File selectedItem = null;
            for (File file:files)
            {
                selectedItem = file;
                if (file.getName() == null ? selectedValue == null : file.getName().equals(selectedValue)) break;
            }
            
            // Cbecks if something is selected
            if (selectedItem != null) {
                try {
                    // Updates the properties table
                    propertiesTable.setModel(refreshProperties());
                } catch (RemoteException ex) {
                    Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    // Enables the Open button if the the selected item is a directory and disables it otherwise
                    if (p.isDirectory(selectedItem)) {
                        openFolderButton.setEnabled(true);
                    }
                    else {
                        openFolderButton.setEnabled(false);
                    }
                } catch (RemoteException ex) {
                    Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                // Enables the Rename and Delete buttons
                renameButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        }
    }//GEN-LAST:event_fileListValueChanged

    /**
     * Renames the selected file or directory 
     * 
     * @param evt 
     */
    private void renameButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_renameButtonMouseClicked
        // Checks if the button state is set to enabled
        if (!renameButton.isEnabled()) return;

        // Gets the selected value from the list
        String selectedValue = fileList.getSelectedValue();
        
        // Gets the file corresponding to the selected value
        File selectedItem = null;
        for (File file:files)
        {
            selectedItem = file;
            if (file.getName() == null ? selectedValue == null : file.getName().equals(selectedValue)) break;
        }
        
        try {
            // Gets the current directory's path
            String parent = currentDir.getAbsolutePath();
            String fileExtension = "";
            
            int copyNumber = 1;
            
            // Checks if the selected file has a valid extension
            if (selectedItem.getName().contains(".") && selectedItem.getName().indexOf(".") != 0) {
                fileExtension = selectedItem.getName().substring(selectedItem.getName().indexOf("."));
            }
            
            // Asks the user to input the new file name
            String name = JOptionPane.showInputDialog("Enter the new name:");
            
            // Checks if the new name is valid
            if (name != null && !"".equals(name)) {
                // Renames the file or directory
                String renamed = parent + "/" + name + fileExtension;
                
                if (!fileExists(parent, name + fileExtension)) p.rename(selectedItem.getAbsolutePath(), renamed);
                 else {
                    while (fileExists(parent, name + " (" + copyNumber + ")" + fileExtension)) {
                        copyNumber++;
                    }
                    
                    renamed = parent + "/" + name + " (" + copyNumber + ")" + fileExtension;
                    p.rename(selectedItem.getAbsolutePath(), renamed);
                }
                
                // Updates the list model
                files = p.readDirectory(parent);
                fileList.setModel(refreshModel());
            }
        } catch (RemoteException ex) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_renameButtonMouseClicked

    /**
     * Creates a new file with the given name and type
     * 
     * @param evt 
     */
    private void newFileButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_newFileButtonMouseClicked
        // Checks if the button state is set to enabled
        if (!newFileButton.isEnabled()) return;
        
        try {
            // Gets the current directory's path
            String parent = currentDir.getAbsolutePath();
            
            int copyNumber = 1;
            
            // Two-dimensional array with file types and corresponding file extensions
            String[][] fileExtensions = {{"Microsoft Word Document (.docx)", "Microsoft Excel Spreadsheet (.xlsx)", "Microsoft Powerpoint Presentation (.pptx)", "Text Document (.txt)", "Compressed Archive (.zip)"}, 
                                    {".docx", ".xlsx", ".pptx", ".txt", ".zip"}};
             
            // Gets the file type
            String fileType = (String) JOptionPane.showInputDialog(new JFrame(), 
                "What type of file would you like to create?",
                "File Type",
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                fileExtensions[0], 
                fileExtensions[0][0]);
            
            // Checks if the user selected a file type
            if (fileType != null) {
                int typeIndex = 0;
                for (String type:fileExtensions[0]) {
                    if (type.equals(fileType)) break;
                    typeIndex++;
                }
                
                String fileExt = fileExtensions[1][typeIndex];
                
                // Asks the user to input the file name
                String fileName = JOptionPane.showInputDialog("File name:");
                
                // Checks if the file name is valid
                if (fileName != null) {
                    if (!"".equals(fileName)) {
                        // If a file with the same name already exists, the new file is created as a copy
                        if (!fileExists(parent, fileName + fileExt)) p.createFile(parent + "/" + fileName + fileExt);
                        else {
                           while (fileExists(parent, fileName + " (" + copyNumber + ")" + fileExt)) {
                               copyNumber++;
                           }

                           p.createFile(parent + "/" + fileName + " (" + copyNumber + ")" + fileExt);
                        }
                    }
                    else {
                        if (!fileExists(parent, "New File" + fileExt)) p.createFile(parent + "/" + "New File" + fileExt);
                        else {
                           while (fileExists(parent, "New File" + " (" + copyNumber + ")" + fileExt)) {
                               copyNumber++;
                           }

                           p.createFile(parent + "/" + "New File" + " (" + copyNumber + ")" + fileExt);
                        }
                    }
                    
                    // Update the list model
                    files = p.readDirectory(parent);
                    fileList.setModel(refreshModel());
                }
            }
        } catch (RemoteException ex) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_newFileButtonMouseClicked

    /**
     * Creates a new directory with the given name
     * 
     * @param evt 
     */
    private void newFolderButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_newFolderButtonMouseClicked
        // Checks if the button state is set to enabled
        if (!newFolderButton.isEnabled()) return;
        
        try {
            // Gets the current directory's path
            String parent = currentDir.getAbsolutePath();
            
            int copyNumber = 1;
            
            // Asks the user to input the folder name
            String folderName = JOptionPane.showInputDialog("Folder name:");
            
            // Checks if the name is valid
            if (folderName != null) {
                if (!"".equals(folderName)) {
                    // Creates the folder with the chosen name
                    // If a folder with the same name already exists, the new folder is created as a copy
                     if (!fileExists(parent, folderName)) p.createDirectory(parent + "/" + folderName);
                     else {
                        while (fileExists(parent, folderName + " (" + copyNumber + ")")) {
                            copyNumber++;
                        }

                        p.createDirectory(parent + "/" + folderName + " (" + copyNumber + ")");
                    }
                }
                else {
                    // Creates the folder with a default name
                    if (!fileExists(parent, "New Folder")) p.createDirectory(parent + "/" + "New Folder");
                    else {
                        while (fileExists(parent, "New Folder (" + copyNumber + ")")) {
                            copyNumber++;
                        }

                        p.createDirectory(parent + "/" + "New Folder (" + copyNumber + ")");
                    }
                }
                
            // Updates the list model
            files = p.readDirectory(parent);
            fileList.setModel(refreshModel());
            }
        } catch (RemoteException ex) {
            Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_newFolderButtonMouseClicked

    /**
     * @param args the command line arguments
     * @throws java.rmi.RemoteException
     * @throws java.rmi.NotBoundException
     */
    public static void main(String args[]) throws RemoteException, NotBoundException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RMIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RMIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RMIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RMIClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Add:
                    // -Djava.security.policy=client.policy
                    // to the VM arguments when running the server
                    if(System.getSecurityManager()==null) {
                        System.setSecurityManager(new SecurityManager());
                    }
                    
                    new RMIClient().setVisible(true);
                } catch (RemoteException | NotBoundException ex) {
                    Logger.getLogger(RMIClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JList<String> fileList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JButton newFileButton;
    private javax.swing.JButton newFolderButton;
    private javax.swing.JButton openFolderButton;
    private javax.swing.JLabel propertiesLabel;
    private javax.swing.JTable propertiesTable;
    private javax.swing.JButton renameButton;
    private javax.swing.JLabel titleLabel;
    // End of variables declaration//GEN-END:variables
  
}
