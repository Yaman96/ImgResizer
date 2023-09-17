package org.yaman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class MainGUI {
    private JFrame frame;
    private JTextField srcFolderField;
    private JTextField dstFolderField;
    private JProgressBar progressBar;


    public MainGUI() {
        frame = new JFrame("Photo Resizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel srcLabel = new JLabel("Source folder: ");
        srcFolderField = new JTextField();
        JButton srcBrowseButton = new JButton("Select");

        JLabel dstLabel = new JLabel("Destination folder: ");
        dstFolderField = new JTextField();
        JButton dstBrowseButton = new JButton("Select");

        JButton resizeButton = new JButton("Resize");

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        srcBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    srcFolderField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        dstBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    dstFolderField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        resizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String srcFolderPath = srcFolderField.getText();
                String dstFolderPath = dstFolderField.getText();


                File srcFolder = new File(srcFolderPath);
                File dstF = new File(dstFolderPath);
                System.out.println("DstFolder created: " + dstF.mkdir());


                File[] photos = srcFolder.listFiles();

                SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
                    @Override
                    protected Void doInBackground() throws Exception {

                        int threadCount = 0;
                        for (File photo : photos) {
                            new Thread(new ImgResizer(photo,400,dstFolderPath)).start();
                            threadCount++;
                        }
                        int progress = 0;
                        while (progress < 100) {
                            progress = ImgResizer.getReadyPhotosCount() * (100 / threadCount);
                            if (progress == (100 / threadCount) * threadCount) progress = 100;
                            publish(progress);
                        }
                        return null;
                    }

                    @Override
                    protected void process(java.util.List<Integer> chunks) {
                        for (int value : chunks) {
                            progressBar.setValue(value);
                        }
                    }

                    @Override
                    protected void done() {
                        progressBar.setValue(0);
                        ImgResizer.resetReadyPhotosCount();
                        JOptionPane.showMessageDialog(frame, "Photos have been successfully resized and saved.");
                    }
                };

                worker.execute();
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(srcLabel, constraints);

        constraints.gridx = 1;
        panel.add(srcFolderField, constraints);

        constraints.gridx = 2;
        panel.add(srcBrowseButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(dstLabel, constraints);

        constraints.gridx = 1;
        panel.add(dstFolderField, constraints);

        constraints.gridx = 2;
        panel.add(dstBrowseButton, constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(resizeButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 3;
        panel.add(progressBar, constraints);

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainGUI();
            }
        });
    }
}