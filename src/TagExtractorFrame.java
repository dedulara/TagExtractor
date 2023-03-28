import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import static java.nio.file.StandardOpenOption.CREATE;

public class TagExtractorFrame extends JFrame
{
    JPanel wholePanel, topButtonPanel, textPanel, bottomButtonPanel;

    JButton pickFileButton, quitButton, pickFilterButton, saveButton, filterButton;

    JTextArea wordsTA;
    JScrollPane scrollPaneVariable;

    JFileChooser chooser = new JFileChooser();
    File selectedFile;
    String rec = "";
    String recS = "";
    File workingdirectory = new File(System.getProperty("user.dir"));
    Path file = Paths.get(workingdirectory.getPath() + "\\filteredFile.txt");
    ArrayList<String> savedStrings = new ArrayList<>();
    ArrayList<String> wordsAL = new ArrayList<>();
    ArrayList<String> stopWords = new ArrayList<>();


    public TagExtractorFrame()
    {
        wholePanel = new JPanel();
        wholePanel.setLayout(new BorderLayout());
        createTopButtonPanel();
        wholePanel.add(topButtonPanel, BorderLayout.NORTH);
        createTextPanel();
        wholePanel.add(textPanel, BorderLayout.CENTER);
        createBottomButtonPanel();
        wholePanel.add(bottomButtonPanel, BorderLayout.SOUTH);

        add(wholePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,600);
    }

    public void createTopButtonPanel()
    {
        topButtonPanel = new JPanel();
        topButtonPanel.setLayout(new GridLayout(1,2));
        pickFileButton = new JButton("Pick File");
        pickFilterButton = new JButton("Pick Filter File");
        pickFilterButton.setEnabled(false);
        filterButton = new JButton("Run");
        filterButton.setEnabled(false);

        pickFileButton.addActionListener((ActionEvent ae) ->
        {
            try
            {
                File workingDirectory = new File(System.getProperty("user.dir"));
                chooser.setCurrentDirectory(workingDirectory);
                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();
                    InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    while(reader.ready())
                    {
                        rec = reader.readLine();
                        String recTwo = rec.replace("_", "");
                        String[] words = recTwo.split("[^\\w']+");
                        if(recTwo.length() != 0)
                        {
                            for (String newString : words)
                            {
                                if(newString.length() != 0)
                                {
                                    String lowerWord = newString.toLowerCase();
                                    wordsAL.add(lowerWord);
                                }
                            }
                        }
                    }
                    reader.close();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Failed to choose a file to process.\nRun the program again.");
                    System.exit(0);
                }
                String fileName = String.valueOf(selectedFile);
                String [] lastFileName = fileName.split("\\\\");
                int length = lastFileName.length;

                wordsTA.setText("File Name: " + lastFileName[length-1] + "\n\n\n");
            }
            catch (FileNotFoundException e)
            {
                JOptionPane.showMessageDialog(null, "File not found!");
                e.printStackTrace();
            }
            catch (IOException e) {e.printStackTrace();}
            pickFilterButton.setEnabled(true);
        });

        pickFilterButton.addActionListener((ActionEvent ae) ->
        {
            try
            {
                File workingDirectory = new File(System.getProperty("user.dir"));
                chooser.setCurrentDirectory(workingDirectory);
                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                {
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();
                    InputStream in = new BufferedInputStream(Files.newInputStream(file, CREATE));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    while(reader.ready())
                    {
                        recS = reader.readLine();
                        stopWords.add(recS);
                    }
                    reader.close();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Failed to choose a file to process.\nRun the program again.");
                    System.exit(0);
                }
            }
            catch (FileNotFoundException e)
            {
                JOptionPane.showMessageDialog(null, "File not found!");
                e.printStackTrace();
            }
            catch (IOException e) {e.printStackTrace();}
            filterButton.setEnabled(true);
        });

        filterButton.addActionListener((ActionEvent ae) ->
        {
            Map<String, Integer> wordFrequency = new TreeMap<>();

            for(String w : wordsAL)
            {
                if(!stopWords.contains(w))
                {
                    if(wordFrequency.get(w) == null) {wordFrequency.put(w,1);}
                    else {wordFrequency.put(w, wordFrequency.get(w) + 1);}
                }
            }
            for(String keyString : wordFrequency.keySet())
            {
                wordsTA.append(keyString + " = " + wordFrequency.get(keyString) + "\n");
                savedStrings.add(keyString + " = " + wordFrequency.get(keyString));
            }
        });

        topButtonPanel.add(pickFileButton);
        topButtonPanel.add(pickFilterButton);
        topButtonPanel.add(filterButton);
    }

    public void createBottomButtonPanel()
    {
        bottomButtonPanel = new JPanel();
        bottomButtonPanel.setLayout(new GridLayout(1,2));
        saveButton = new JButton("Save File");
        quitButton = new JButton("Quit");

        saveButton.addActionListener((ActionEvent ae) ->
        {
            try
            {
                OutputStream out = new BufferedOutputStream(Files.newOutputStream(file, CREATE));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

                for (String rec : savedStrings)
                {
                    String recString = rec;
                    writer.write(recString, 0, recString.length());
                    writer.newLine();
                }
                writer.close();
                JOptionPane.showMessageDialog(null, "Data file written!");
            }
            catch (IOException e) {e.printStackTrace();}
        });

        quitButton.addActionListener((ActionEvent ae) -> System.exit(0));

        bottomButtonPanel.add(saveButton);
        bottomButtonPanel.add(quitButton);
    }

    public void createTextPanel()
    {
        textPanel = new JPanel();
        wordsTA = new JTextArea(30,30);
        wordsTA.setEditable(false);
        scrollPaneVariable = new JScrollPane(wordsTA);
        textPanel.add(scrollPaneVariable);
    }
}