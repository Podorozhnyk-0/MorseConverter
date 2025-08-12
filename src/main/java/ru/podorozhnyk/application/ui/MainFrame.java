package ru.podorozhnyk.application.ui;

import ru.podorozhnyk.application.AuthorInfo;
import ru.podorozhnyk.application.exceptions.IllegalMorseSequenceException;
import ru.podorozhnyk.application.morse.*;

import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import static javax.swing.ScrollPaneConstants.*;
import static ru.podorozhnyk.application.morse.MorseUtils.DefaultDictionaries.*;

public class MainFrame extends JFrame {
    public static final Font FONT = new Font("Verdana", Font.PLAIN, 36);
    private static final MorseConverter CONVERTER = new MorseConverter(LATIN, CYRILLIC, NUMBERS, PUNCTUATION);
    private static final String BACKGROUND_COLOR = "#CCCCFF";
    private static final String BACKGROUND_COLOR_NORTH = "#9999DD";

    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private final Container contentPane;

    private JTextArea inputArea, outputArea;
    private JButton translateButton, playButton,
            stopButton, saveAudioButton, saveTextButton;
    private String translateMode;
    private String lastTranslatedText;
    private int translationsCounter;

    public MainFrame(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        contentPane = getContentPane();
        UIManager.put("Button.font", FONT);
        UIManager.put("TextArea.font", FONT);
        UIManager.put("Label.font", FONT);
        UIManager.put("List.font", FONT);
        setupUi();
        setVisible(true);
        translationsCounter = 1;
        translateMode = "TM";
    }

    private void setupUi() {
        JPanel northPanel = getNorthPanel();
        JPanel westPanel = getWestPanel();
        JPanel eastPanel = getEastPanel();

        contentPane.add(northPanel, BorderLayout.NORTH);
        contentPane.add(eastPanel, BorderLayout.CENTER);
        contentPane.add(westPanel, BorderLayout.WEST);

    }

    private JPanel getEastPanel() {
        JPanel eastPanel = new JPanel();
        eastPanel.setBackground(Color.decode(BACKGROUND_COLOR));
        Dimension eastSize = new Dimension(getWidth() / 4, getHeight());
        eastPanel.setPreferredSize(eastSize);
        eastPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        translateButton = new JButton("Translate");
        translateButton.setEnabled(false);
        translateButton.setPreferredSize(new Dimension(eastSize.width / 2 + 40, eastSize.height / 8));
        translateButton.addActionListener(actionEvent -> {
            switch (translateMode) {
                case "TM" -> {
                    if (inputArea.getText().isBlank())
                        break;
                    outputArea.setText(CONVERTER.convertToMorse(inputArea.getText()));
                    MorseAudioGenerator.generateMorseCode(outputArea.getText());
                    lastTranslatedText = inputArea.getText();
                }
                case "MT" -> {
                    try {
                        outputArea.setText(CONVERTER.convertFromMorse(inputArea.getText()));
                    } catch (IllegalMorseSequenceException e) {
                        JOptionPane.showMessageDialog(null, e.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    MorseAudioGenerator.generateMorseCode(inputArea.getText());
                    lastTranslatedText = outputArea.getText();
                }
            }
            saveAudioButton.setEnabled(true);
            saveTextButton.setEnabled(true);
        });
        playButton = new JButton("Play");
        playButton.setEnabled(false);
        playButton.setPreferredSize(new Dimension(eastSize.width / 4 + 18, eastSize.height / 16));

        playButton.addActionListener(actionEvent -> {
            MorseAudioGenerator.playMorse();
            stopButton.setEnabled(true);
        });

        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        stopButton.setPreferredSize(new Dimension(eastSize.width / 4 + 18, eastSize.height / 16));
        stopButton.addActionListener(actionEvent -> {
            MorseAudioGenerator.stop();
            stopButton.setEnabled(false);
        });

        JList<String> dictList = new JList<>();

        dictList.setListData(CONVERTER.getLoadedDictionaries().stream()
                .map(MorseDictionary::getName).toArray(String[]::new));
        dictList.setFont(FONT.deriveFont(28.f));
        JScrollPane listPane = new JScrollPane(dictList);
        listPane.setPreferredSize(new Dimension(eastSize.width / 2 - 20, eastSize.height / 4));
        listPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(Color.decode(BACKGROUND_COLOR));

        JButton upButton = new JButton("↑");
        upButton.addActionListener(actionEvent -> {
            if (dictList.getSelectedIndex() > 0) {
                int upperIndex = dictList.getSelectedIndex() - 1;
                int selectedIndex = dictList.getSelectedIndex();
                ArrayList<String> copyList = new ArrayList<>();
                for (int i = 0; i < dictList.getModel().getSize(); ++i) {
                    copyList.add(dictList.getModel().getElementAt(i));
                }
                copyList = (ArrayList<String>) Utils.swapElements(copyList, selectedIndex, upperIndex);
                dictList.setListData(copyList.toArray(String[]::new));
                dictList.setSelectedIndex(upperIndex);

                CONVERTER.setCurrentDictionaryOrder(copyList.toArray(String[]::new));
            }
        });

        JButton downButton = new JButton("↓");
        downButton.addActionListener(actionEvent -> {
            if (dictList.getSelectedIndex() < dictList.getModel().getSize() - 1) {
                int lowerIndex = dictList.getSelectedIndex() + 1;
                int selectedIndex = dictList.getSelectedIndex();
                ArrayList<String> copyList = new ArrayList<>();
                for (int i = 0; i < dictList.getModel().getSize(); ++i) {
                    copyList.add(dictList.getModel().getElementAt(i));
                }
                copyList = (ArrayList<String>) Utils.swapElements(copyList, selectedIndex, lowerIndex);
                dictList.setListData(copyList.toArray(String[]::new));
                dictList.setSelectedIndex(lowerIndex);

                CONVERTER.setCurrentDictionaryOrder(copyList.toArray(String[]::new));
            }
        });

        saveAudioButton = new JButton("Save Audio");
        saveAudioButton.setPreferredSize(new Dimension(eastSize.width / 2 + 40, eastSize.height / 16));
        saveAudioButton.setEnabled(false);
        saveAudioButton.addActionListener(actionEvent -> {
            if (!MorseAudioGenerator.isAudioStreamEmpty()) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy_HH:mm:ss");
                    String text = lastTranslatedText.replace(' ', '_').replace('\n', '_');
                    text = text.substring(0, Math.min(text.length(), 16));

                    File dir = new File("./audio");
                    dir.mkdir();
                    MorseAudioGenerator.saveToFile(Paths.get(dir.getPath(),
                            String.format("%s_%s.mp3", dateFormat.format(new Date()), text)).toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        saveTextButton = new JButton("Save Text");
        saveTextButton.setPreferredSize(new Dimension(eastSize.width / 2 + 40, eastSize.height / 16));
        saveTextButton.setEnabled(false);
        saveTextButton.addActionListener(actionEvent -> {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy_HH:mm:ss");
            String fileName = String.format("Translation_No%d_%s", translationsCounter++, dateFormat.format(new Date()));
            File dir = new File("./translations");
            dir.mkdir();
            File file = new File(Paths.get(dir.getPath(), fileName).toUri());
            try (FileWriter writer = new FileWriter(file)) {
               writer.write(String.format("Translation from %s to %s.\n", translateMode.equals("TM")? "text" : "morse",
                       translateMode.equals("TM")? "morse" : "text"));
               String morseText = translateMode.equals("TM")? outputArea.getText() : inputArea.getText();
               String text = translateMode.equals("TM")? lastTranslatedText : outputArea.getText();
               writer.write(String.format("Text: \"%s\"\nMorse: \"%s\"\n", text, morseText));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        navigationPanel.add(upButton);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, eastSize.height / 8 + 14)));
        navigationPanel.add(downButton);

        JButton infoButton = new JButton("i");
        infoButton.addActionListener(actionEvent -> {
            JOptionPane.showMessageDialog(null, String.format("Author: %s\nLink: %s",
                            AuthorInfo.AUTHOR_NAME, AuthorInfo.GITHUB_LINK),
                        "Creator info", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.decode(BACKGROUND_COLOR));
        infoPanel.setPreferredSize(new Dimension(eastSize.width / 2 + 40, eastSize.height / 16));
        infoPanel.add(Box.createRigidArea(new Dimension(eastSize.width / 2 - 13, 0)));
        infoPanel.add(infoButton, BorderLayout.EAST);

        eastPanel.add(translateButton);
        eastPanel.add(playButton);
        eastPanel.add(stopButton);
        eastPanel.add(saveAudioButton);
        eastPanel.add(saveTextButton);
        eastPanel.add(listPane);
        eastPanel.add(navigationPanel);
        eastPanel.add(Box.createRigidArea(new Dimension(10, eastSize.height / 4 - 10)));
        eastPanel.add(infoPanel);
        return eastPanel;
    }

    private JPanel getWestPanel() {
        JPanel westPanel = new JPanel(new FlowLayout());
        westPanel.setBackground(Color.decode(BACKGROUND_COLOR));
        westPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        Dimension westSize = new Dimension(getWidth() / 2 + 670, getHeight());
        westPanel.setPreferredSize(westSize);

        inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        JScrollPane inputPane = new JScrollPane(inputArea);
        inputPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        inputPane.setPreferredSize(new Dimension(westSize.width / 2 + 80, westSize.height - 320));
        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                checkEmptiness();
                char ch = Character.toUpperCase(inputArea.getText().charAt(event.getOffset()));
                Runnable task = () -> SwingUtilities.invokeLater(() ->
                {
                    try {
                        event.getDocument().remove(event.getOffset(), 1);
                    } catch (BadLocationException e) {
                        throw new RuntimeException(e);
                    }
                });

                if (translateMode.equals("TM")) {
                    if (!CONVERTER.containsTranslationCode(Character.toString(ch)) && ch != ' ' && ch != '\n') {
                        task.run();
                    }
                }
                else if (translateMode.equals("MT")) {
                    if (!MorseUtils.isPatternValid(Character.toString(ch))) {
                        task.run();
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                checkEmptiness();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                checkEmptiness();
            }

            private void checkEmptiness() {
                translateButton.setEnabled(!inputArea.getText().isBlank());
            }
        });

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                checkEmptiness();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                checkEmptiness();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                checkEmptiness();
            }

            private void checkEmptiness() {
                playButton.setEnabled(!outputArea.getText().isBlank());
            }
        });
        JScrollPane outputPane = new JScrollPane(outputArea);
        outputPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        outputPane.setPreferredSize(new Dimension(westSize.width / 2 - 100, westSize.height - 320));

        westPanel.add(inputPane);
        westPanel.add(outputPane);
        return westPanel;
    }

    private JPanel getNorthPanel() {
        JPanel northPanel = new JPanel();
        northPanel.setBackground(Color.decode(BACKGROUND_COLOR_NORTH));
        JButton translateModeButton = new JButton("Text -> Morse");
        translateModeButton.addActionListener(actionEvent -> {
            if ((translateMode.equals("TM") && outputArea.getText().isEmpty() && inputArea.getText().isEmpty())
                    || (translateMode.equals("MT") && inputArea.getText().isEmpty())) {
                translateMode = (translateMode.equals("TM"))? "MT" : "TM";
            }
            else {
                translateButton.getActionListeners()[0]
                        .actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                translateMode = (translateMode.equals("TM"))? "MT" : "TM";

                String inputText = inputArea.getText();
                String outputText = outputArea.getText();
                inputArea.setText(outputText);

                try {
                    outputArea.setText(translateMode.equals("TM")? inputText : CONVERTER.convertFromMorse(outputText));
                } catch (IllegalMorseSequenceException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            translateModeButton.setText(switch (translateMode) {
                case "TM" -> "Text -> Morse";
                case "MT" -> "Morse -> Text";
                default -> throw new AssertionError("Unexpected \"translateMode\" is " + translateMode);
            });
        });
        northPanel.add(translateModeButton);
        return northPanel;
    }
}
