package ru.yasuchenin.turtilla;

import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.*;

public class ServerListener implements Runnable {
	private static ServerSocket serverSock;
	private static ServerListener instance = null;
	private static final int listenPort = 1488;
	private static final int ADD_SIGN_TASK=1;
	private static final int ANALYSE_TASK=2;
	private static final int UPDATE_SIGN_TASK=3;
	
	public static ServerListener getInstance() {
		if(instance==null)
			instance = new ServerListener();
		return instance;
	}
	
	public void run() {
		try {
			serverSock = new ServerSocket(listenPort);
			MainWindow.debugPrint("Server start.");
			while(!Thread.interrupted()) {
				Socket sock = serverSock.accept();
				new ConnectionHandler(sock);
			}
		} 
		catch (IOException x) {MainWindow.debugPrint("Server stop.");} 
	}
	
	public static void closeSock() throws IOException {
		serverSock.close();
	}
	
	private ServerListener() {
	}
	
	class ConnectionHandler implements Runnable 
	{
		private Socket sock;
		private SignInfo inputSign;
		private InputStream sin;
		private OutputStream sout;
		private ObjectInputStream inpStream;
		private ObjectOutputStream outStream;
		private long fileLength;
		
		public ConnectionHandler(Socket sock) {
			this.sock=sock;
			Thread thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
		}
		
		public void run() {
			inputSign = new SignInfo();
			try {
				SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
				Session hnSession = sessionFactory.openSession();
					sin = sock.getInputStream();
					sout = sock.getOutputStream();
					inpStream = new ObjectInputStream(sin);
					outStream = new ObjectOutputStream(sout);
					Integer task = (Integer) inpStream.readObject();
					switch(task) {
					case ADD_SIGN_TASK:
						MainWindow.debugPrint("Accept ADD_SIGN_TASK.");   
						inputSign = (SignInfo) inpStream.readObject();
						if(!inputSign.check()) {
							sock.close();
							return;
						}
						hnSession.beginTransaction();
						hnSession.save(inputSign);
						hnSession.getTransaction().commit();
						break;
					case ANALYSE_TASK:
						MainWindow.debugPrint("Accept ANALYSE_TAST.");
						fileLength = inpStream.readLong();
						byte[] bufferFile = new byte[(int) fileLength];
						int count=0, offset=0;
						while (fileLength > 0) {
				            count = inpStream.read(bufferFile, offset, (int)fileLength);
				            fileLength-= count;
				            offset+=count;
				        }
						Criteria crit = hnSession.createCriteria(SignInfo.class);
						List signList = crit.list();
						ArrayList<SignInfo> arlist = compareAllSigns(bufferFile, signList);
						outStream.writeInt(arlist.size());
						Iterator<SignInfo> it=arlist.iterator();
						while(it.hasNext()) 
							outStream.writeObject(it.next());	
						break;
					case UPDATE_SIGN_TASK:
						MainWindow.debugPrint("Accept UPDATE_SIGN_TASK.");
						inputSign = (SignInfo) inpStream.readObject();
						hnSession.beginTransaction();
						//hnSession.createCriteria(SignInfo.class).add(criterion)
						break;
					}	
					hnSession.close();
					outStream.flush();
					sock.close();
			} 
			catch (SQLException x) {System.out.println("SQL EXCEPTION!!!");}
			catch (IOException x) {System.out.println("IO EXCEPTION!!!");
			x.printStackTrace();} 
			catch (ClassNotFoundException e) {e.printStackTrace();}
		}
		
		private ArrayList<SignInfo> compareAllSigns(byte[] bufferFile, List signList) throws SQLException {
			Iterator it = signList.iterator();
			SignInfo currentSign;
			SignComparator signComp = new SignComparator(bufferFile);
			ArrayList<SignInfo> arlist = new ArrayList<SignInfo>(); 
			while(it.hasNext()) {
				try {
					currentSign = (SignInfo)it.next();
					if(signComp.searchSign(currentSign.getSign())) {
						arlist.add(currentSign);
					} 
				} catch(IllegalArgumentException ex) {continue;}
			}
			return arlist;
		}
	}
}	