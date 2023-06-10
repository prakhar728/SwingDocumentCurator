import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;


public class Main {


    private static Map<DefaultMutableTreeNode, UrlInfo> urlInfoMap = new HashMap<>();
    private static Map<String, DefaultMutableTreeNode> treeNodeMap = new HashMap<>();


    private static String currentUrl = "http://doxydonkey.blogspot.in";

    public static void main(String[] args) {


        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem menuItem = new JMenuItem("Save");
        menu.add(menuItem);


        JTextArea urlTextArea = new JTextArea(1, 30);
        urlTextArea.setText(currentUrl);
        urlTextArea.setEditable(true);
        urlTextArea.setMinimumSize(new Dimension(150, 24));
        urlTextArea.setMargin(new Insets(4, 8, 4, 8));

        JButton goButton = new JButton("Go!");
        goButton.setPreferredSize(new Dimension(60, 33));
        JPanel urlPanel = new JPanel();
        urlPanel.setLayout(new FlowLayout());
        urlPanel.add(urlTextArea);
        urlPanel.add(goButton);


        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        JScrollPane editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setPreferredSize(new Dimension(600, 600));
        editorScrollPane.setMinimumSize(new Dimension(10, 10));


        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Urls");
        DefaultTreeModel treeModel = new DefaultTreeModel(top);
        treeModel.addTreeModelListener(new MyTreeModelListener(urlInfoMap));

        JTree urlTree = new JTree(treeModel);
        urlTree.setEditable(true);
        urlTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        urlTree.setShowsRootHandles(true);
        JScrollPane treeScrollPane = new JScrollPane(urlTree);
        treeScrollPane.setPreferredSize(new Dimension(240, 600));


        JTextArea summaryTextArea = new JTextArea();
        summaryTextArea.setLineWrap(true);
        JScrollPane summaryScrollPane = new JScrollPane(summaryTextArea);
        summaryScrollPane.setPreferredSize(new Dimension(600, 150));
        summaryScrollPane.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));


        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(urlPanel, BorderLayout.NORTH);
        panel.add(treeScrollPane, BorderLayout.WEST);
        panel.add(editorScrollPane, BorderLayout.CENTER);
        panel.add(summaryScrollPane, BorderLayout.SOUTH);


        JFrame frame = new JFrame("Create snippets from URLs");
        frame.setJMenuBar(menuBar);
        frame.add(panel);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Save your snippets");
                if (fc.showSaveDialog((editorPane)) == JFileChooser.APPROVE_OPTION) {
                    HTMLWriter.writeToHTML(fc.getSelectedFile().getAbsolutePath(),
                            urlInfoMap.values());
                }
            }
        });

        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    currentUrl = urlTextArea.getText();
                    // note that this line below has the magic - the url is loaded into
                    // the editorPane window. This can throw an exception, so we need a catch
                    editorPane.setPage(currentUrl);

                    // has this URL been added before?
                    // if not: create a URLInfo object for it, put it in the tree, and
                    // select it
                    // if yes : select it
                    DefaultMutableTreeNode childNode = null;
                    if (treeNodeMap.containsKey(currentUrl)) {
                        childNode = treeNodeMap.get(currentUrl);
                    } else {
                        childNode = new DefaultMutableTreeNode(currentUrl);
                        treeNodeMap.put(currentUrl, childNode);
                        treeModel.insertNodeInto(childNode, top, top.getChildCount());
                    }

                    if (!urlInfoMap.containsKey(childNode)) {
                        urlInfoMap.put(childNode, new UrlInfo(currentUrl));
                    }

                    // now all that's left is to select the node (whether newly created or not)
                    TreePath pathToNode = new TreePath(childNode.getPath());
                    urlTree.scrollPathToVisible(pathToNode);
                    urlTree.setSelectionPath(pathToNode);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        urlTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                JTree tree = (JTree) e.getSource();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)
                        tree.getLastSelectedPathComponent();

                currentUrl = urlInfoMap.get(selectedNode).getUrl();


                if (selectedNode.isLeaf()) {
                    urlTextArea.setText(currentUrl);
                    try {
                        editorPane.setPage(currentUrl);
                        summaryTextArea.setText(urlInfoMap.get(selectedNode).getSummary());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }


            }
        });

        summaryTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSummary();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSummary();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSummary();
            }

            private void updateSummary() {
                urlInfoMap.get(treeNodeMap.get(currentUrl)).setSummary(summaryTextArea.getText());
            }
        });

    }
}