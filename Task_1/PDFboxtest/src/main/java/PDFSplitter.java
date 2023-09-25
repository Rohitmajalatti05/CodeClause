import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.multipdf.Splitter;

class MainClass extends JFrame {
    private JTextField inputPathField;
    private JTextField outputPathField;
    private JTextField splitPageField;
    private JRadioButton splitSinglePageRadio;
    private JRadioButton splitPageRangeRadio;
    private JTextField rangeStartField;
    private JTextField rangeEndField;
    private JTextArea logTextArea;

    public MainClass() {
        super("PDF Splitter");

        inputPathField = new JTextField(30);
        outputPathField = new JTextField(30);
        splitPageField = new JTextField(5);
        logTextArea = new JTextArea(15, 30);
        logTextArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logTextArea);

        splitSinglePageRadio = new JRadioButton("Split at Page:");
        splitPageRangeRadio = new JRadioButton("Split Page Range:");
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(splitSinglePageRadio);
        radioGroup.add(splitPageRangeRadio);
        splitSinglePageRadio.setSelected(true);

        splitPageField.setEnabled(true);
        rangeStartField = new JTextField(5);
        rangeEndField = new JTextField(5);
        rangeStartField.setEnabled(false);
        rangeEndField.setEnabled(false);

        splitSinglePageRadio.addActionListener(e -> {
            splitPageField.setEnabled(true);
            rangeStartField.setEnabled(false);
            rangeEndField.setEnabled(false);
        });

        splitPageRangeRadio.addActionListener(e -> {
            splitPageField.setEnabled(false);
            rangeStartField.setEnabled(true);
            rangeEndField.setEnabled(true);
        });

        JButton browseInputButton = createStyledButton("Browse");
        JButton browseOutputButton = createStyledButton("Browse");
        JButton splitButton = createStyledButton("Split PDF");

        browseInputButton.addActionListener(e -> browseFile(inputPathField));
        browseOutputButton.addActionListener(e -> browseDirectory(outputPathField));
        splitButton.addActionListener(e -> splitPdf());

        JPanel inputPanel = createStyledPanel();
        inputPanel.add(createLabel("Input PDF:"));
        inputPanel.add(inputPathField);
        inputPanel.add(browseInputButton);

        JPanel outputPanel = createStyledPanel();
        outputPanel.add(createLabel("Output Directory:"));
        outputPanel.add(outputPathField);
        outputPanel.add(browseOutputButton);

        JPanel splitPanel = createStyledPanel();
        splitPanel.add(createLabel("Split Options:"));
        splitPanel.add(splitSinglePageRadio);
        splitPanel.add(splitPageField);
        splitPanel.add(splitPageRangeRadio);
        splitPanel.add(rangeStartField);
        splitPanel.add(createLabel("to"));
        splitPanel.add(rangeEndField);

        JPanel buttonPanel = createStyledPanel();
        buttonPanel.add(splitButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(inputPanel);
        mainPanel.add(outputPanel);
        mainPanel.add(splitPanel);
        mainPanel.add(buttonPanel);
        mainPanel.add(logScrollPane);

        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        add(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 122, 183));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(e -> button.setBackground(new Color(41, 101, 153)));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 101, 153));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 122, 183));
            }
        });
        return button;
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private void browseFile(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            textField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void browseDirectory(JTextField textField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            textField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void splitPdf() {
        String inputFilePath = inputPathField.getText();
        String outputDirectory = outputPathField.getText();
        String splitPageText = splitPageField.getText();

        try {
            int splitPage;
            int rangeStart;
            int rangeEnd;

            if (splitSinglePageRadio.isSelected()) {
                splitPage = Integer.parseInt(splitPageText);
                rangeStart = splitPage;
                rangeEnd = splitPage;
            } else {
                rangeStart = Integer.parseInt(rangeStartField.getText());
                rangeEnd = Integer.parseInt(rangeEndField.getText());
            }

            PDDocument document = PDDocument.load(new File(inputFilePath));
            List<PDDocument> splitDocuments = new Splitter().split(document);

            int pageNumber = 1;
            for (PDDocument splitDocument : splitDocuments) {
                if (pageNumber >= rangeStart && pageNumber <= rangeEnd) {
                    String outputFileName = outputDirectory + File.separator + "split_" + pageNumber + ".pdf";
                    splitDocument.save(outputFileName);
                    splitDocument.close();
                }
                pageNumber++;
            }

            document.close();
            logTextArea.append("PDF split successfully.\n");
        } catch (IOException | NumberFormatException e) {
            logTextArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainClass());
    }
}
