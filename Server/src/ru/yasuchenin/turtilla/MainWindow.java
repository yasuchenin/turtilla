package ru.yasuchenin.turtilla;

import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextPane;
import javax.swing.JScrollBar;
import javax.swing.text.BadLocationException;

import java.awt.ScrollPane;

public class MainWindow {
	private int work=0;
	private ServerListener servListener;
	private Thread listenerThread;
	private JFrame frame;
	private static JTextPane debugPane;
	private JButton btnStart;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void debugPrint(String str) {
		try {
			debugPane.getStyledDocument().insertString(0, new java.util.Date()+"  "+str+"\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public MainWindow() {
		initialize();
		MainWindow.debugPrint("Aplication start.");
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 570, 423);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		btnStart = new JButton("Start");
		btnStart.setBounds(232, 340, 137, 55);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(work==0) {
					servListener = ServerListener.getInstance();
					listenerThread = new Thread(servListener);
					listenerThread.setDaemon(true);
					listenerThread.start();
					btnStart.setText("Stop");
					work=1;
				} else {
					try {
						ServerListener.closeSock();
					} catch (Exception e1) {}
					listenerThread.interrupt();
					btnStart.setText("Start");
					work=0;
				}
			}
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(btnStart);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(6, 6, 558, 328);
		frame.getContentPane().add(scrollPane);
		
		debugPane = new JTextPane();
		debugPane.setEditable(false);
		scrollPane.setViewportView(debugPane);
	}
}
