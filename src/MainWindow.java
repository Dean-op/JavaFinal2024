import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import org.markdown4j.Markdown4jProcessor;

public class MainWindow {
    private JFrame mainFrame;
    private JMenuBar menuBar;
    private JMenu fileMenu, formatMenu, themeMenu;
    private JMenuItem newItem, openItem, saveItem, exportItem, exitItem;
    private JMenuItem heading1Item, heading2Item, heading3Item, heading4Item, bodyItem, codeBlockItem, quoteItem, italicItem, boldItem;
    private JMenuItem darkThemeItem, lightThemeItem;
    private JTextArea textArea;
    private JEditorPane previewPane;
    private DocumentManager documentManager;
    private TextFormatter textFormatter;
    private PdfExporter pdfExporter;
    private ThemeManager themeManager;
    private JButton refreshButton;
    private JLabel statusLabel;
    private boolean isDocumentModified = false;
    private File currentFile = null;

    public MainWindow() {
        documentManager = new DocumentManager();
        textFormatter = new TextFormatter();
        pdfExporter = new PdfExporter();
        themeManager = new ThemeManager();

        mainFrame = new JFrame("Notebook");
        mainFrame.setSize(1000, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();

        fileMenu = new JMenu("文件");
        newItem = new JMenuItem("新建Markdown文档");
        openItem = new JMenuItem("打开Markdown文档");
        saveItem = new JMenuItem("保存");
        exportItem = new JMenuItem("输出PDF格式文档");
        exitItem = new JMenuItem("退出");

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        formatMenu = new JMenu("格式");
        heading1Item = new JMenuItem("一级标题");
        heading2Item = new JMenuItem("二级标题");
        heading3Item = new JMenuItem("三级标题");
        heading4Item = new JMenuItem("四级标题");
        bodyItem = new JMenuItem("正文");
        codeBlockItem = new JMenuItem("代码块");
        quoteItem = new JMenuItem("引用");
        italicItem = new JMenuItem("斜体");
        boldItem = new JMenuItem("粗体");

        formatMenu.add(heading1Item);
        formatMenu.add(heading2Item);
        formatMenu.add(heading3Item);
        formatMenu.add(heading4Item);
        formatMenu.add(bodyItem);
        formatMenu.add(codeBlockItem);
        formatMenu.add(quoteItem);
        formatMenu.add(italicItem);
        formatMenu.add(boldItem);

        themeMenu = new JMenu("主题");
        darkThemeItem = new JMenuItem("暗黑主题");
        lightThemeItem = new JMenuItem("亮白主题");

        themeMenu.add(darkThemeItem);
        themeMenu.add(lightThemeItem);

        menuBar.add(fileMenu);
        menuBar.add(formatMenu);
        menuBar.add(themeMenu);

        mainFrame.setJMenuBar(menuBar);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("C:\\Users\\DeanNoteBook\\Pictures\\Saved Pictures\\a6cdbb09bd8b49cc91f98c0dfb6d312f.jpg");
                g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        JButton newButton = new JButton("新建");
        JButton openButton = new JButton("打开");

        newButton.addActionListener(e -> newDocument());

        openButton.addActionListener(e -> openDocument());

        // 将按钮居中
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        backgroundPanel.add(newButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        backgroundPanel.add(openButton, gbc);

        mainFrame.add(backgroundPanel);

        disableMenus();

        // 绑定菜单项的动作监听器
        newItem.addActionListener(e -> newDocument());

        openItem.addActionListener(e -> openDocument());

        saveItem.addActionListener(e -> saveDocument());

        exportItem.addActionListener(e -> exportToPdf());

        exitItem.addActionListener(e -> System.exit(0));

        darkThemeItem.addActionListener(e -> themeManager.applyDarkTheme(textArea, previewPane));

        lightThemeItem.addActionListener(e -> themeManager.applyLightTheme(textArea, previewPane));

        mainFrame.setVisible(true);
    }

    private void disableMenus() {
        formatMenu.setEnabled(false);
        themeMenu.setEnabled(false);
        newItem.setEnabled(true);
        openItem.setEnabled(true);
        saveItem.setEnabled(false);
        exportItem.setEnabled(false);
        exitItem.setEnabled(true);
    }

    private void enableMenus() {
        formatMenu.setEnabled(true);
        themeMenu.setEnabled(true);
        newItem.setEnabled(true);
        openItem.setEnabled(true);
        saveItem.setEnabled(true);
        exportItem.setEnabled(true);
        exitItem.setEnabled(true);
    }

    private void newDocument() {
        if (isDocumentModified) {
            int option = JOptionPane.showConfirmDialog(mainFrame, "当前文档未保存，是否保存？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                saveDocument();
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        mainFrame.getContentPane().removeAll();
        initializeEditorComponents();
        mainFrame.revalidate();
        mainFrame.repaint();
        currentFile = null; // 重置currentFile
        setDocumentModified(false);
    }

    private void openDocument() {
        if (isDocumentModified) {
            int option = JOptionPane.showConfirmDialog(mainFrame, "当前文档未保存，是否保存？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                saveDocument();
            } else if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        mainFrame.getContentPane().removeAll();
        initializeEditorComponents();
        documentManager.openDocument(mainFrame, textArea);
        mainFrame.revalidate();
        mainFrame.repaint();
        currentFile = documentManager.getCurrentFile(); // 获取当前文件
        setDocumentModified(false);
    }

    private void initializeEditorComponents() {
        textArea = new JTextArea();
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setDocumentModified(true);
                updatePreview();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setDocumentModified(true);
                updatePreview();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setDocumentModified(true);
                updatePreview();
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        previewPane = new JEditorPane();
        previewPane.setContentType("text/html");
        previewPane.setEditable(false);

        refreshButton = new JButton("刷新预览");
        refreshButton.addActionListener(e -> updatePreview());

        statusLabel = new JLabel(" ");

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, new JScrollPane(previewPane));
        splitPane.setDividerLocation(0.5);
        splitPane.setResizeWeight(0.5);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(splitPane, BorderLayout.CENTER);
        panel.add(refreshButton, BorderLayout.SOUTH);
        panel.add(statusLabel, BorderLayout.NORTH);

        mainFrame.getContentPane().add(panel);

        enableMenus();

        heading1Item.addActionListener(e -> textFormatter.formatText(textArea, "一级标题"));

        heading2Item.addActionListener(e -> textFormatter.formatText(textArea, "二级标题"));

        heading3Item.addActionListener(e -> textFormatter.formatText(textArea, "三级标题"));

        heading4Item.addActionListener(e -> textFormatter.formatText(textArea, "四级标题"));

        bodyItem.addActionListener(e -> textFormatter.formatText(textArea, "正文"));

        codeBlockItem.addActionListener(e -> textFormatter.formatText(textArea, "代码块"));

        quoteItem.addActionListener(e -> textFormatter.formatText(textArea, "引用"));

        italicItem.addActionListener(e -> textFormatter.formatText(textArea, "斜体"));

        boldItem.addActionListener(e -> textFormatter.formatText(textArea, "粗体"));

        enableMenus();
    }

    private void setDocumentModified(boolean modified) {
        isDocumentModified = modified;
        if (modified) {
            statusLabel.setText("文件已更改注意保存！");
            statusLabel.setForeground(Color.red);
        } else {
            statusLabel.setText(" ");
        }
    }

    private void updatePreview() {
        try {
            String markdownText = textArea.getText();
            String htmlText = new Markdown4jProcessor().process(markdownText);
            previewPane.setText(htmlText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDocument() {
        if (currentFile != null) {
            documentManager.saveDocument(textArea, currentFile);
            setDocumentModified(false);
        } else {
            currentFile = documentManager.saveDocumentAs(mainFrame, textArea);
            setDocumentModified(false);
        }
    }

    private void exportToPdf() {
        updatePreview(); // 在导出 PDF 前更新预览面板内容
        String htmlContent = previewPane.getText();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF Document");
        fileChooser.setSelectedFile(new File("document.pdf")); // 设置默认文件名
        int userSelection = fileChooser.showSaveDialog(mainFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getPath().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getPath() + ".pdf"); // 确保文件扩展名为 .pdf
            }
            System.out.println("Exporting to PDF: " + fileToSave.getAbsolutePath());
            pdfExporter.exportToPdf(htmlContent, fileToSave);
        } else {
            System.out.println("Export canceled or no file selected");
        }
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
