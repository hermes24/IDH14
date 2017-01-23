package idh14.client;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.json.JSONException;

/**
 * File Sharing
 *
 * Luc Hermes | Eric Marsilje | Joost van Stuijvenberg
 *
 * Avans Hogeschool Breda - IDH14 November/December 2016
 */
public class ClientUI extends javax.swing.JFrame {

    private DiskHandler diskHandler;
    private String serverAddress;
    private int serverPort;
    private ServerHandler serverHandler;
    private String location = "C:\\client\\";

    /**
     * Creates new form ClientUI
     */
    public ClientUI() {
        initComponents();
        settingsPanel.setVisible(false);
        getLocalFileList(location);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        settingsButton = new javax.swing.JButton();
        listLocalFiles = new java.awt.List();
        listServerFiles = new java.awt.List();
        buttonUpdateLocalList = new javax.swing.JButton();
        buttonUploadLocalFile = new javax.swing.JButton();
        buttonDeleteLocalFile = new javax.swing.JButton();
        buttonUpdateServerList = new javax.swing.JButton();
        buttonDownloadServerFile = new javax.swing.JButton();
        buttonDeleteServerFile = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        messageBox = new javax.swing.JScrollPane();
        messageBoxText = new javax.swing.JTextArea();
        messageBoxLabel = new javax.swing.JLabel();
        settingsPanel = new javax.swing.JPanel();
        selectServerButton = new javax.swing.JButton();
        settingsLabel = new javax.swing.JLabel();
        mainMenuButton = new javax.swing.JButton();
        localFolderLabel = new javax.swing.JLabel();
        serverIPPortLabel = new javax.swing.JLabel();
        historyLabel = new javax.swing.JLabel();
        localFolderText = new javax.swing.JTextField();
        serverIPText = new javax.swing.JTextField();
        serverPortText = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        connectButton = new javax.swing.JButton();
        disconnectButton = new javax.swing.JButton();
        listHistory = new java.awt.List();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        settingsButton.setText("Settings");
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });

        buttonUpdateLocalList.setText("Update List ");
        buttonUpdateLocalList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateLocalListActionPerformed(evt);
            }
        });

        buttonUploadLocalFile.setText("Put");
        buttonUploadLocalFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUploadLocalFileActionPerformed(evt);
            }
        });

        buttonDeleteLocalFile.setText("Delete");
        buttonDeleteLocalFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteLocalFileActionPerformed(evt);
            }
        });

        buttonUpdateServerList.setText("Update List");
        buttonUpdateServerList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateServerListActionPerformed(evt);
            }
        });

        buttonDownloadServerFile.setText("Get");
        buttonDownloadServerFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDownloadServerFileActionPerformed(evt);
            }
        });

        buttonDeleteServerFile.setText("Delete");
        buttonDeleteServerFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeleteServerFileActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jLabel1.setText("Local");

        jLabel2.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        jLabel2.setText("Server");

        messageBoxText.setColumns(20);
        messageBoxText.setRows(5);
        messageBox.setViewportView(messageBoxText);

        messageBoxLabel.setText("MessageBox");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(152, 152, 152))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(settingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(listLocalFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(buttonUpdateLocalList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonUploadLocalFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonDeleteLocalFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 127, Short.MAX_VALUE)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(buttonUpdateServerList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonDownloadServerFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(buttonDeleteServerFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(messageBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(messageBoxLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(listServerFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(20, 20, 20)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonUpdateLocalList)
                            .addComponent(buttonUpdateServerList))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonUploadLocalFile)
                            .addComponent(buttonDownloadServerFile))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonDeleteLocalFile)
                            .addComponent(buttonDeleteServerFile))
                        .addGap(314, 314, 314))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(listServerFiles, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addComponent(messageBoxLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(messageBox, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(listLocalFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(settingsButton)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        selectServerButton.setText("Select Server");
        selectServerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectServerButtonActionPerformed(evt);
            }
        });

        settingsLabel.setText("Settings");

        mainMenuButton.setText("Main Menu");
        mainMenuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mainMenuButtonActionPerformed(evt);
            }
        });

        localFolderLabel.setText("Local Folder");

        serverIPPortLabel.setText("Server IP & Port");

        historyLabel.setText("History");

        localFolderText.setEditable(false);
        localFolderText.setText("C:\\");

            serverIPText.setText("127.0.0.1");

            serverPortText.setText("54321");

            browseButton.setText("Browse");
            browseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    browseButtonActionPerformed(evt);
                }
            });

            connectButton.setText("Connect");
            connectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    connectButtonActionPerformed(evt);
                }
            });

            disconnectButton.setText("Disconnect");
            disconnectButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    disconnectButtonActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
            settingsPanel.setLayout(settingsPanelLayout);
            settingsPanelLayout.setHorizontalGroup(
                settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(settingsPanelLayout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(settingsPanelLayout.createSequentialGroup()
                            .addGap(98, 98, 98)
                            .addComponent(settingsLabel))
                        .addGroup(settingsPanelLayout.createSequentialGroup()
                            .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(localFolderLabel)
                                .addComponent(serverIPPortLabel)
                                .addComponent(historyLabel))
                            .addGap(18, 18, 18)
                            .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(settingsPanelLayout.createSequentialGroup()
                                    .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(localFolderText)
                                        .addGroup(settingsPanelLayout.createSequentialGroup()
                                            .addComponent(serverIPText, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(serverPortText, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(18, 18, 18)
                                    .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(browseButton)
                                        .addGroup(settingsPanelLayout.createSequentialGroup()
                                            .addComponent(connectButton)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(disconnectButton))))
                                .addGroup(settingsPanelLayout.createSequentialGroup()
                                    .addComponent(selectServerButton)
                                    .addGap(613, 613, 613)
                                    .addComponent(mainMenuButton))
                                .addComponent(listHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addContainerGap(43, Short.MAX_VALUE))
            );
            settingsPanelLayout.setVerticalGroup(
                settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(settingsPanelLayout.createSequentialGroup()
                    .addGap(15, 15, 15)
                    .addComponent(settingsLabel)
                    .addGap(18, 18, 18)
                    .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(localFolderLabel)
                        .addComponent(localFolderText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(browseButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(serverIPPortLabel)
                        .addComponent(serverIPText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(serverPortText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(connectButton)
                        .addComponent(disconnectButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(historyLabel)
                        .addComponent(listHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(selectServerButton)
                        .addComponent(mainMenuButton))
                    .addContainerGap(19, Short.MAX_VALUE))
            );

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap()))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap()))
            );

            pack();
        }// </editor-fold>//GEN-END:initComponents

    private void mainMenuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mainMenuButtonActionPerformed
        mainPanel.setVisible(true);
        settingsPanel.setVisible(false);
        getLocalFileList(location);
    }//GEN-LAST:event_mainMenuButtonActionPerformed

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        mainPanel.setVisible(false);
        settingsPanel.setVisible(true);
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed

        // Create FileChooser windows 'dialog' to select local folder.
        // C:\ is default location when opening dialog. 
        JFileChooser dialog = new JFileChooser();
        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dialog.setAcceptAllFileFilterUsed(false);
        dialog.setCurrentDirectory(new java.io.File("C:\\"));

        // Extra logging.
        if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): " + dialog.getCurrentDirectory());
            System.out.println("getSelectedFile() : " + dialog.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }

        // Place selected folder in textfield & set location variable
        localFolderText.setText(dialog.getSelectedFile().toString());
        location = dialog.getSelectedFile().toString() + "\\";
    }//GEN-LAST:event_browseButtonActionPerformed

    private void buttonUpdateLocalListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateLocalListActionPerformed
        getLocalFileList(location);
    }//GEN-LAST:event_buttonUpdateLocalListActionPerformed

    private void buttonDeleteLocalFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteLocalFileActionPerformed

        // Verwijderen geselecteerde file in lokale folder
        // Als er geen file geselecteerd is krijgt de gebruiker een melding.
        if (listLocalFiles.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a local file");
        } else {

            Path uri = Paths.get(location + listLocalFiles.getSelectedItem());
            try {
                Files.delete(uri);
                listLocalFiles.remove(listLocalFiles.getSelectedIndex());
                System.out.println(uri + " is deleted");
            } catch (NoSuchFileException x) {
                System.err.format("%s: no such" + " file or directory%n", uri);
            } catch (DirectoryNotEmptyException x) {
                System.err.format("%s not empty%n", uri);
            } catch (IOException x) {
                // File permission problems are caught here.
                System.err.println(x);
            }

        }

    }//GEN-LAST:event_buttonDeleteLocalFileActionPerformed

    public void clientPopUpMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }


    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed

        // Server toevoegen aan history 
        // Eerst controleren of server er al in staat, zoja dan niet toevoegen
        // Server + Poort aan elkaar plakken zodat het mooi in het overzicht staat
        String server = new StringBuilder(serverIPText.getText()).append(":").append(serverPortText.getText()).toString();

        serverAddress = serverIPText.getText();
        serverPort = Integer.parseInt(serverPortText.getText());

        // Nu toevoegen
        int check = 0;

        if (listHistory.getItemCount() == 0) {
            listHistory.add(server);
            System.out.println("Server added: " + server);
            check++;
        } else {

            for (int i = 0; i < listHistory.getItemCount(); i++) {
                if (listHistory.getItem(i).equals(server)) {
                    check++;
                    break;
                }
            }
        }

        if (check == 0) {
            listHistory.add(server);
            System.out.println("Server added: " + server);
        }

        // Vervolgens connecten naar server 
        connectToServer();


    }//GEN-LAST:event_connectButtonActionPerformed

    private void selectServerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectServerButtonActionPerformed

        // Server uit history lijst selecteren 
        String[] ipAndPort = listHistory.getSelectedItem().split(":", 2);

        System.out.println("IP : " + ipAndPort[0]);
        System.out.println("Port : " + ipAndPort[1]);

        serverIPText.setText(ipAndPort[0]);
        serverPortText.setText(ipAndPort[1]);

    }//GEN-LAST:event_selectServerButtonActionPerformed

    private void buttonUpdateServerListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateServerListActionPerformed

        if (serverHandler == null) {
            JOptionPane.showMessageDialog(null, "Please connect to server first");
        } else {
            // Huidige lijst leegmaken 
            listServerFiles.clear();

            // Lijst met files ophalen vanuit server
            ArrayList<String> r = serverHandler.getServerFileList();

            for (String s : r) {
                listServerFiles.add(s);
            }

        }


    }//GEN-LAST:event_buttonUpdateServerListActionPerformed

    private void disconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectButtonActionPerformed
        serverHandler.stop(true);
    }//GEN-LAST:event_disconnectButtonActionPerformed

    private void buttonDownloadServerFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDownloadServerFileActionPerformed

        // Geen file geselecteerd is melding        
        if (listServerFiles.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a file");
        } else {
            // File geselecteerd dan proberen op te halen
            try {

                serverHandler.getFileFromServer(listServerFiles.getSelectedItem());
                buttonUpdateLocalListActionPerformed(evt);

            } catch (JSONException je) {
                System.out.println(je.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }//GEN-LAST:event_buttonDownloadServerFileActionPerformed

    private void buttonUploadLocalFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUploadLocalFileActionPerformed

        // Geen file geselecteerd is melding        
        if (listLocalFiles.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a file");
        } else {
            // File geselecteerd dan proberen te verzenden
            try {

                serverHandler.putFileToServer(listLocalFiles.getSelectedItem());
                buttonUpdateServerListActionPerformed(evt);

            } catch (JSONException je) {
                System.out.println(je.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }//GEN-LAST:event_buttonUploadLocalFileActionPerformed

    private void buttonDeleteServerFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeleteServerFileActionPerformed
        // Geen file geselecteerd is melding        
        if (listServerFiles.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(null, "Please select a file");
        } else {
            // File geselecteerd dan proberen te verzenden
            try {

                serverHandler.deleteFileFromServer(listServerFiles.getSelectedItem());

            } catch (JSONException je) {
                System.out.println(je.getMessage());
            } catch (IOException ex) {
                Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
    }//GEN-LAST:event_buttonDeleteServerFileActionPerformed

    public void setMessageBoxText(String message) {
        messageBoxText.append(message + "\n");
    }

    private void getLocalFileList(String location) {

        // Get files from local folder. C:\ will be used as default folder. 
        // Folder can be changed from settings menu within app.
        File folder = new File(location);
        File[] listOfFiles = folder.listFiles();
        listLocalFiles.clear();

        for (int i = 0; i < listOfFiles.length; i++) {
            listLocalFiles.add(listOfFiles[i].getName());
        }
    }

    private void connectToServer() {

        // Connecten naar server
        try {
            Socket c = new Socket(serverAddress, serverPort);
            serverHandler = new ServerHandler(c, this);
            serverHandler.start();

        } catch (Exception e) {
            System.err.println("Fout opgetreden in clientui thread: " + e.getMessage());
            e.printStackTrace();

        }
    }

    /**
     * Utility method voor codering naar Base64.
     */
    private static final String Base64Encoded(String source) {
        String result = null;
        try {
            byte[] b = source.getBytes("UTF-8");
            result = new String(Base64.getEncoder().encode(b));
        } catch (UnsupportedEncodingException uce) {
            result = "Encoding to Base64 failed.";
        }
        return result;
    }

    /**
     * Utility method voor decodering uit Base64.
     */
    private static final String Base64Decoded(String source) {
        return new String(Base64.getDecoder().decode(source));
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton buttonDeleteLocalFile;
    private javax.swing.JButton buttonDeleteServerFile;
    private javax.swing.JButton buttonDownloadServerFile;
    private javax.swing.JButton buttonUpdateLocalList;
    private javax.swing.JButton buttonUpdateServerList;
    private javax.swing.JButton buttonUploadLocalFile;
    private javax.swing.JButton connectButton;
    private javax.swing.JButton disconnectButton;
    private javax.swing.JLabel historyLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private java.awt.List listHistory;
    private java.awt.List listLocalFiles;
    private java.awt.List listServerFiles;
    private javax.swing.JLabel localFolderLabel;
    private javax.swing.JTextField localFolderText;
    private javax.swing.JButton mainMenuButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JScrollPane messageBox;
    private javax.swing.JLabel messageBoxLabel;
    private javax.swing.JTextArea messageBoxText;
    private javax.swing.JButton selectServerButton;
    private javax.swing.JLabel serverIPPortLabel;
    private javax.swing.JTextField serverIPText;
    private javax.swing.JTextField serverPortText;
    private javax.swing.JButton settingsButton;
    private javax.swing.JLabel settingsLabel;
    private javax.swing.JPanel settingsPanel;
    // End of variables declaration//GEN-END:variables

}
