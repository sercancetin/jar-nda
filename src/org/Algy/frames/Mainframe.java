package org.Algy.frames;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.SyntaxDocument;
import jsyntaxpane.Token;
import jsyntaxpane.TokenType;

import org.Algy.Utils.IterEnumAdapter;
import org.Algy.controllers.MainController;
import org.Algy.dialogs.AboutDialog;
import org.Algy.dialogs.BatchRenameDialog;
import org.Algy.dialogs.CommandRenameDialog;
import org.Algy.dialogs.IDialogOK;
import org.Algy.dialogs.RenamerDialog.RenamingType;
import org.Algy.models.CachedJarModel;

import sun.jdbc.odbc.JdbcOdbcBatchUpdateException;

public class Mainframe extends JFrame implements ISyntaxContextProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6310960608361805598L;
	private JEditorPane dtrpnVoidMain;
	
	private CachedJarModel curJarModel;
	private JTree tree;
	private MainController controller;
	
	/**
	 * Create the frame.
	 */
	public Mainframe(CachedJarModel jarModel, MainController controller) {
		
		curJarModel = jarModel;
		this.controller = controller; // replace to observer pattern laters
		
		setTitle("jar-nda");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1053, 843);
		
		JPanel headerPane = new JPanel();
		getContentPane().add(headerPane, BorderLayout.NORTH);
		headerPane.setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		headerPane.add(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmOpenFile = new JMenuItem("Open File");
		mnFile.add(mntmOpenFile);
		
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mnFile.add(mntmQuit);
		
		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		JMenuItem mntmDeobfucate = new JMenuItem("DeObfucate");
		mnTools.add(mntmDeobfucate);
		
		JMenuItem mntmRenameSymbol = new JMenuItem("Rename Symbol");
		mnTools.add(mntmRenameSymbol);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		mntmAbout = new JMenuItem("About");
		mnHelp.add(mntmAbout);
		
		JToolBar toolBar = new JToolBar();
		headerPane.add(toolBar, BorderLayout.SOUTH);
		
		JButton btnOpen = new JButton("");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileProcess(true);
			}
		});
		btnOpen.setIcon(new ImageIcon(Mainframe.class.getResource("/com/sun/java/swing/plaf/windows/icons/TreeOpen.gif")));
		toolBar.add(btnOpen);
		
		JButton btnSave = new JButton("");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				fileProcess(false);
			}
		});
		btnSave.setIcon(new ImageIcon(Mainframe.class.getResource("/com/sun/java/swing/plaf/windows/icons/FloppyDrive.gif")));
		toolBar.add(btnSave);
		
		JButton btnDeobufucate = new JButton("DeObufucate");
		btnDeobufucate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deObfucateButton();
			}
		});
		btnDeobufucate.setIcon(new ImageIcon(Mainframe.class.getResource("/com/sun/java/swing/plaf/windows/icons/ListView.gif")));
		toolBar.add(btnDeobufucate);
		
		JButton btnRename = new JButton("Rename");
		btnRename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				renameButton();
			}
		});
		btnRename.setIcon(new ImageIcon(Mainframe.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
		toolBar.add(btnRename);
		
		JButton btnRenamebatch = new JButton("RenameBatch");
		btnRenamebatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				renameBatchButton();
			}
		});
		btnRenamebatch.setIcon(new ImageIcon(Mainframe.class.getResource("/sun/print/resources/oneside.png")));
		toolBar.add(btnRenamebatch);
		
		JButton btnRenamecmd = new JButton("RenameCmd");
		btnRenamecmd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renameCmdButton();
			}
		});
		toolBar.add(btnRenamecmd);
		
		JPanel footerPane = new JPanel();
		getContentPane().add(footerPane, BorderLayout.SOUTH);
		footerPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		footerPane.add(panel, BorderLayout.NORTH);
		
		JEditorPane logPane = new JEditorPane();
		footerPane.add(logPane, BorderLayout.SOUTH);
		
		JPanel centeredPane = new JPanel();
		getContentPane().add(centeredPane, BorderLayout.CENTER);
		centeredPane.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.35);
		centeredPane.add(splitPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(tabbedPane);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		tabbedPane.addTab("New tab", null, scrollPane, null);
		
		dtrpnVoidMain = new JEditorPane();
		dtrpnVoidMain.setEditable(false);
		dtrpnVoidMain.setContentType("text/java");
		scrollPane.setViewportView(dtrpnVoidMain);
		
		DefaultSyntaxKit.initKit();
		dtrpnVoidMain.setContentType("text/java");
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setToolTipText("");
		splitPane.setLeftComponent(scrollPane_1);
		
		tree = new JTree();
		tree.setRootVisible(false);
		scrollPane_1.setViewportView(tree);
		
		tree.setModel(new MyTreeModel(null));
		tree.setCellRenderer(new MyTreeNodeRenderer());
		initComponent();
	}

	BatchRenameDialog batchRenameDialog = new BatchRenameDialog();
	private void renameBatchButton()
	{
		batchRenameDialog.setLocationRelativeTo(this);
		batchRenameDialog.setVisible(true);
		
	}
	CommandRenameDialog cmdRenameDlg = new CommandRenameDialog(new IDialogOK() {
		@Override
		public void onOK() {
			onOKInRenameCmdDlg();
		}
	});
	private void renameCmdButton()
	{
		cmdRenameDlg.setLocationRelativeTo(this);
		cmdRenameDlg.setVisible(true);
		cmdRenameDlg.setAlwaysOnTop(true);
	}
	private void onOKInRenameCmdDlg() 
	{
		String txt = cmdRenameDlg.getText();
		if(txt == null) txt ="";
		try {
			
			controller.rename(txt);
		} catch (Exception e) {
			ShowErrorMessage(e.getMessage());
		}
	}
	private void ShowErrorMessage(String msg)
	{
		JOptionPane.showMessageDialog(this, msg, "error", JOptionPane.ERROR_MESSAGE);
	}
	
	private void modelNotOpenError()
	{
		JOptionPane.showMessageDialog(this, "open jar/dex first", "error", JOptionPane.ERROR_MESSAGE);
	}
	private void deObfucateButton()
	{
		if(!controller.modelOpenned())
		{
			modelNotOpenError();
			return;
		}
		try {
			controller.deobfucate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void renameButton()
	{
		if(!controller.modelOpenned())
		{
			modelNotOpenError();
			return;
		}
		try {
			controller.singleRename();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void fileProcess(boolean open)
	{
		File file = showFileDialog(open);
		if(file != null)
			try {
				if(open)
					controller.openFile(file);
				else
					controller.saveFileAs(file, true);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
			}
		
	}
	private void initComponent()
	{
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				controller.treeItemSelection((MyTreeNode) e.getPath().getLastPathComponent());
			}
		});
		
		mntmOpenFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				fileProcess(true);
			}
		});
		dtrpnVoidMain.addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent event) {
				onCaretChanged(event);
			}
		});
		updateTreeModel();
		
		/*
		 * help menu
		 */
		mntmAbout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AboutDialog dlg = new AboutDialog();
				dlg.setModal(true);
				dlg.setVisible(true);
			}
		});
	}
	
	private EditorContext myEditorContext = new EditorContext();
	public void onCaretChanged(CaretEvent event)
	{		
		SyntaxDocument document = (SyntaxDocument) dtrpnVoidMain.getDocument();
		
		if(document != null)
		{
			Token tok = document.getTokenAt(event.getDot());
			
			if( tok != null)
			{
				Token leftest , rightest;
				TokenPointer resleft = new TokenPointer(), resright = new TokenPointer();
				String fullToken = getFullToken(tok, document, resleft, resright);
				leftest = resleft.tok;
				rightest = resright.tok;
				
				
				Token prevToken = document.getPrevToken(leftest);
				Token nextToken = document.getNextToken(rightest);
				
				if(tree.getSelectionPath().getLastPathComponent() != null)
				{
					MyTreeNode node = (MyTreeNode) tree.getSelectionPath().getLastPathComponent();
					
					myEditorContext.type = RenamingType.Package;
					if(node.getType() == MyTreeNode.TREENODE_PACKAGE)
						myEditorContext.literal = node.getClassName().replace('/', '.');
					else if(node.getClassName().lastIndexOf("/") != -1 )
						myEditorContext.literal = node.getClassName().substring(0, node.getClassName().lastIndexOf("/")).replace('/', '.');
					else
						myEditorContext.literal = "";
				}
				else
					myEditorContext.literal = "";
				
				myEditorContext.typeSure =false;
				if(prevToken != null && nextToken != null)
				{
					String prevTokenName = prevToken.getString(document);
					String nextTokenName = nextToken.getString(document); 
					if(prevTokenName.equals("class") || prevTokenName.equals("interface") || prevTokenName.equals("extends") || prevTokenName.equals("implements"))
					{
						myEditorContext.typeSure = true;
						myEditorContext.type = RenamingType.Class;
						if(fullToken.contains("."))
							myEditorContext.literal = fullToken;
						else
							myEditorContext.literal += "." + fullToken;
						
						String tokName =  tok.getString(document);
						System.out.println("class or interface:" + tokName);
					}
					
					if(prevTokenName.equals("import") || prevTokenName.equals("package"))
					{
						myEditorContext.type = RenamingType.Package;
						myEditorContext.typeSure = true;
						myEditorContext.literal = fullToken;
						
						System.out.println("pakage : " + fullToken);
					}
					
					if(nextTokenName.equals("("))
					{
						boolean commaState = false;
					}
				}
				System.out.println(myEditorContext);
			}
		}
		
	}
	
	
	private class TokenPointer {
		public Token tok;
	}
	//status == 0 : identifier or type, status == 1 : .
	public String getLeftFullToken(Token tok, SyntaxDocument document, int status, boolean selfContaining, TokenPointer leftest_out) // a.b.c.d.
	{
		if (tok == null)
			return "";
		if(tok.type != TokenType.IDENTIFIER && !isTokenType(tok) && !(tok.type ==TokenType.OPERATOR && tok.getString(document).equals(".")))
			return "";
		if(tok.type == TokenType.IDENTIFIER || isTokenType(tok))
		{
			// identifier
			if(status != 0)
				return "";
		}
		else
		{
			// "." operator
			if(status != 1)
				return "";
		}
		
		leftest_out.tok = tok;
		String tokStr = tok.getString(document);
		
		return getLeftFullToken(document.getPrevToken(tok), document, (status==0)?1:0, true, leftest_out) +(selfContaining?tokStr : "");
	}
	private boolean isTokenType(Token tok)
	{
		return tok.type == TokenType.TYPE || tok.type == TokenType.TYPE2 || tok.type == TokenType.TYPE;
	}
	public String getRightFullToken(Token tok, SyntaxDocument document, int status, boolean selfContaining, TokenPointer rightest_out ) // a.b.c.d.
	{
		if (tok == null)
			return "";
		if(tok.type != TokenType.IDENTIFIER && !isTokenType(tok) && !(tok.type ==TokenType.OPERATOR && tok.getString(document).equals(".")))
			return "";
		
		if(tok.type == TokenType.IDENTIFIER ||  isTokenType(tok))
		{
			// identifier
			if(status != 0)
				return "";
		}
		else
		{
			// "." operator
			if(status != 1)
				return "";
		}
		String tokStr = tok.getString(document);
		rightest_out.tok = tok;
		return (selfContaining?tokStr:"") + getRightFullToken(document.getNextToken(tok), document, (status==0)?1:0, true, rightest_out);
	}
	public String getFullToken(Token tok, SyntaxDocument document, TokenPointer leftest_out, TokenPointer rightest_out)
	{
		int status;
		if(tok.type == TokenType.IDENTIFIER || isTokenType(tok))
			status = 0;
		else if(tok.type ==TokenType.OPERATOR && tok.getString(document).equals("."))
			status = 1;
		else
			status = -1;
		
		String res = "";
		if(status == 0 || status == 1)
			res = getLeftFullToken(tok, document, status,false, leftest_out) + tok.getString(document) + getRightFullToken(tok, document,status, false, rightest_out);
		else
			res = tok.getString(document);
		
		if(leftest_out.tok == null)
			leftest_out.tok = tok;
		if(rightest_out.tok == null)
			rightest_out.tok = tok;
		return res;
	}
	public void clearMainEditorText()
	{
		dtrpnVoidMain.setText("");
	}
	
	public void setMainEditorText(String content)
	{
		dtrpnVoidMain.setText(content);
	}
	
	public void setTreeContent(MyTreeNode node)
	{
		tree.setModel(new MyTreeModel(node));
	}
	public JTree getTree()
	{
		return tree;
	}
	public void updateTreeModel()
	{
		if(curJarModel != null && curJarModel.isAnalyzed())
		{
			HashMap<String, MyTreeNode> classNameToNode = new HashMap<>();
			
			ArrayList<MyTreeNode> topNodes = new ArrayList<>(); 
			
			TreeSet<String> classNames = new TreeSet<>(curJarModel.getClassFiles().keySet());// Sorted Set
			for (String className: classNames)
			{
				
				String shortName = className.substring( className.lastIndexOf("/") + 1);
				MyTreeNode p = new MyTreeNode(null, className, shortName,
						(!shortName.contains("$"))?MyTreeNode.TREENODE_CLASS : MyTreeNode.TREENODE_INNERCLASS );
				
				while(true)
				{
					int ed = className.lastIndexOf("/");
					if(ed != -1)
						className = className.substring(0, ed);
					else
						className = "";
					
					if(classNameToNode.containsKey(className))
					{
						MyTreeNode parent;
						parent = classNameToNode.get(className);
						p.setParent(parent);
						parent.getChildrenList().add(p);
						break;
					}
					if(ed == -1)
					{
						topNodes.add(p);
						//not added root 
						break;
					}
					
					MyTreeNode high = new MyTreeNode(null, className, className.substring(className.lastIndexOf("/")+1),
							MyTreeNode.TREENODE_PACKAGE);
					
					high.getChildrenList().add(p);
					p.setParent(high);
					
					classNameToNode.put(className, high);
					
					p = high;
					
				}
			}
			MyTreeNode root = new MyTreeNode(null, "", "", 0);
			root.getChildrenList().addAll(topNodes);
			
			tree.setModel(new MyTreeModel(root));
		}
	}

	public CachedJarModel getCurJarModel() {
		return curJarModel;
	}

	public void setCurJarModel(CachedJarModel curJarModel) {
		this.curJarModel = curJarModel;
	}
	
	private String openFileRecentPath = null;
	private JMenuItem mntmOpenFile;
	private JMenuItem mntmAbout;
	
	/**
	 * 
	 * @param open
	 * @return if approve button clicked, return file, otherwise return null
	 */
	public File showFileDialog(boolean open)
	{
		JFileChooser dlg;
		if(openFileRecentPath == null)
			dlg = new JFileChooser();
		else
			dlg = new JFileChooser(openFileRecentPath);
		
		dlg.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "Jar File(*.jar)";
			}
			
			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				return f.getName().endsWith(".jar") || f.isDirectory();
			}
		});
		dlg.addChoosableFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "Android Dex File(*.dex)";
			}
			
			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				return f.getName().endsWith(".dex") || f.isDirectory();
			}
		});			
		
		int status;
		if(open)
			status = dlg.showOpenDialog(this);
		else
		{
			
			status =dlg.showSaveDialog(this);
			
		}
		if(status != JFileChooser.APPROVE_OPTION) return null;

		openFileRecentPath = dlg.getSelectedFile().getParent();
		
		return dlg.getSelectedFile();
	}
	
	public void selectPath(String path, int type)
	{
		//TODO : create it
		MyTreeNode node =  (MyTreeNode) tree.getModel().getRoot();
		
		boolean foundItem = false;
		ArrayList<MyTreeNode> list = new ArrayList<MyTreeNode>();
		list.add(node);
		while(!foundItem)
		{
			@SuppressWarnings("unchecked")
			Iterator<MyTreeNode> iter = new IterEnumAdapter<MyTreeNode>(node.children());
			
			MyTreeNode profitChild = null;
			while(iter.hasNext())
			{
				MyTreeNode child = iter.next();
				if(path.equals(node.getClassName()) && type == node.getType())
				{
					//found
					foundItem = true;
					list.add(child);
					break;
				}
				else if(path.startsWith(child.getClassName()))
				{
					profitChild = child;
					list.add(child);
					break;
				}
			}
			if(profitChild == null)
				//not found
				break;
			node = profitChild;
		}
		tree.setSelectionPath(new TreePath(list.toArray(new MyTreeNode[list.size()])));
	}
	
	public void onTreeValueChanged()
	{
		
	}
	
	@Override
	public EditorContext getContext() {
		// TODO Auto-generated method stub
		return myEditorContext;
	}
}
