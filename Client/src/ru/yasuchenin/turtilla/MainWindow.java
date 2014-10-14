package ru.yasuchenin.turtilla;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

import java.net.*;
import java.io.*;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTextField signText;
	private JTextField virusText;
	private JTextArea descriptionText;
	private JTextPane textArea;
	private JLabel label;
	private Socket socket;
	private InputStream sin;
	private OutputStream sout;
	private DataInputStream in;
	private ObjectOutputStream outStream;
	private File file;
	private static final Integer ADD_SIGN_TASK=1;
	private static final Integer ANALYSE_TASK=2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void Acction() {
		long fileLength=file.length();
	    InetAddress ia;
		try {
			ia = InetAddress.getByName("localhost");
			//ia = InetAddress.getByName("213.239.220.108"); 
			socket = new Socket(ia, 1488);
			sin = socket.getInputStream();
            sout = socket.getOutputStream();
           
            ObjectOutputStream out = new ObjectOutputStream(sout);
            ObjectInputStream inpStream = new ObjectInputStream(sin);
            out.writeObject(ANALYSE_TASK);
            out.writeLong(fileLength);
            FileInputStream fileStream=new FileInputStream(file);
            byte [] buffer = new byte[1024];
            while (fileLength > 0) {
                int i = fileStream.read(buffer);
                out.write(buffer, 0, i);
                fileLength-=i;
            }
            out.flush();
            fileStream.close();	
            int count = inpStream.readInt();
            textArea.setText("�����qw"+file.getName()+"\"."+"\n");
            textArea.setText(textArea.getText()+"Полученные сигнатуры:\n");
            for(int i=0; i<count; i++) {
            	SignInfo sinfo = new SignInfo();
	            sinfo=(SignInfo)inpStream.readObject();
	            textArea.setText(textArea.getText()+sinfo.getDescription()+"\n");
            }
            textArea.setText(textArea.getText()+"Анализ завершен.");   
		}catch (IOException x) {System.out.println("CONNECTION ERROR!!!");} 
		catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setTitle("ReverseTortilla-78");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(400, 300, 473, 449);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 473, 427);
		contentPane.add(tabbedPane);
		JPanel panel = new JPanel();
		tabbedPane.addTab("Добавить сигнатуру", panel);
		panel.setLayout(null);
		
		label = new JLabel("Сигнатура:");
		label.setBounds(12, 6, 123, 34);
		panel.add(label);
		
		signText = new JTextField();
		signText.setBounds(6, 38, 417, 22);
		panel.add(signText);
		signText.setColumns(10);
		
		JLabel label_1 = new JLabel("Семейство:");
		label_1.setBounds(12, 64, 144, 34);
		panel.add(label_1);
		
		virusText = new JTextField();
		virusText.setColumns(10);
		virusText.setBounds(6, 95, 417, 22);
		panel.add(virusText);
		
		JLabel label_2 = new JLabel("Описание функционала:");
		label_2.setBounds(12, 124, 217, 34);
		panel.add(label_2);
		
		descriptionText = new JTextArea();
		descriptionText.setLineWrap(true);
		descriptionText.setBounds(12, 158, 411, 175);
		panel.add(descriptionText);
		
		JButton btnNewButton = new JButton("Отправить");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SignInfo signInf = new SignInfo();
				signInf.setSign(signText.getText());
				signInf.setVirus(virusText.getText());
				signInf.setDescription(descriptionText.getText());
				try {
				InetAddress ia = InetAddress.getByName("localhost");
				//InetAddress ia = InetAddress.getByName("213.239.220.108");
				socket = new Socket(ia, 1488);
				sin = socket.getInputStream();
	            sout = socket.getOutputStream();
	            in = new DataInputStream(sin);
	            outStream = new ObjectOutputStream(sout);
	            outStream.writeObject(ADD_SIGN_TASK);
	            outStream.writeObject(signInf);
	            outStream.flush();
				}
				catch(Exception x) {}
			}
		});
		btnNewButton.setBounds(157, 346, 117, 29);
		panel.add(btnNewButton);
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Анализ файла", panel_1);
		tabbedPane.setEnabledAt(1, true);
		panel_1.setLayout(null);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 35, 440, 340);
		panel_1.add(scrollPane);
		
		textArea = new JTextPane();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		textArea.setTransferHandler(new TransferHandler(null) {
			
			@Override
			public boolean canImport(TransferSupport support) {
				
				return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
				|| support.isDataFlavorSupported(DataFlavor.stringFlavor);
			}
			
			@Override
			public boolean importData(TransferSupport support) {
				
				Transferable t = support.getTransferable();
				try {
					
					if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
	
						Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
						
						@SuppressWarnings("unchecked")
						List<File> files = (List<File>) o;
						file=files.get(0);
						Acction();
						//textArea.setText(files.get(0).getName());
						//StringBuilder sb = new StringBuilder("<ul>");
						//for (File file : files)
							//sb.append("<li>" + file);
						
						//textArea.setText("<html>" + files.size() +
							//	" files dropped<br>" + sb);
					}
					else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						
						Object o = t.getTransferData(DataFlavor.stringFlavor);
						String str = o.toString();
						
						label.setText(
							"<html>Hell now how many files you trying to drop..<br>" +
								str);
					}
					
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return super.importData(support);
			}
		});
		
		JButton button = new JButton("Выбрать файл");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser analyseFile = new JFileChooser();
				int ret = analyseFile.showDialog(null, "Select File");     
				if (ret == JFileChooser.APPROVE_OPTION) {
				    file = analyseFile.getSelectedFile();
					Acction();
				}
			}
		});
		button.setBounds(156, 6, 140, 29);
		panel_1.add(button);
		
	}
}
