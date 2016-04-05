package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UTFDataFormatException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.rowset.CachedRowSetImpl;

import dbutils.DBManager;

public class PalawanClient extends Client {
	private ServerSocket ss;
	private String serverIp;
	private int isolationLevel = 4;
	private String clientName = "Palawan";
	private String dbName = "db_hpq_palawan";
	private volatile HashMap<String, ResultSet> rsMap = new HashMap<>();
	private CachedRowSetImpl rsw;
	private volatile boolean resultSetReceived;
	private volatile int transactionId = 0;
	
	public PalawanClient(String serverIp) throws IOException{
		ss = new ServerSocket(6969);
		this.serverIp = serverIp;
		
		// send connection to Server
		new Thread(new IncomingThread()).start();
		Socket initSocket = new Socket(serverIp, 6969);
		DataOutputStream dout = new DataOutputStream(initSocket.getOutputStream());
		dout.writeUTF(clientName);
		initSocket.close();
		
	}
	
	public void case1(ArrayList<String> transactions) throws Exception {
		for(String cur: transactions){
			System.out.println(cur);
			int id = getTransactionId();
			new Thread(new TransactionThread(cur+"@"+id)).start();
		}
	}
	
	class TransactionThread implements Runnable {
		private String cur;
		
		public TransactionThread(String t){
			cur = t;
		}
		
		@Override
		public void run() {
			try{
				String[] split = cur.split("@");
				if(split.length>=2 && split[1].startsWith("SELECT")){
					if(clientName.equals(split[2])){
						ResultSet rs = executeRead(split[1]);
						putIntoMap(split[3], rs);
						while(rs.next()){
							System.out.println(rs.getInt(1));
						}
					} else {
						Socket s = new Socket(serverIp, 6969);
						DataOutputStream dout = new DataOutputStream(s.getOutputStream());
						dout.writeUTF(cur);
						dout.close();
						s.close();
						
						while(!rsMap.containsKey(split[2]));
						ResultSet rs = rsw.getOriginal();
						while(rs.next()){
							System.out.println(rs.getInt(1));
						}
						s.close();
					}
				} else if(split[1].startsWith("UPDATE")) {
					executeWrite(split[1]);
					System.out.println("Finished Writing!");
					
					Socket sk = new Socket(serverIp, 6969);
					DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
					dos.writeUTF(cur);
					dos.close();
					sk.close();
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	ResultSet executeRead(String query){
		Connection connection = new DBManager(dbName).getConnection();
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
//			connection.close();
			return resultSet;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	boolean executeWrite(String query){
		
		// TODO add shiz for start and end of transaction
		Connection connection = new DBManager(dbName).getConnection();
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			int res = statement.executeUpdate();
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public synchronized void unlockResultSet(){
		resultSetReceived = true;
	}
	
	public synchronized int getTransactionId(){
		transactionId++;
		return transactionId;
	}
	
	class IncomingThread implements Runnable{

		@Override
		public void run() {
			 try {
                while(true){
					Socket s = ss.accept();
					DataInputStream din = new DataInputStream(s.getInputStream());
					try{
	                    String msgin = din.readUTF();
	                    System.out.println("Received "+msgin);
	                    String[] split = msgin.split("@");
	                    if(split[0].equals("Unable to read")){
	                    	rsw = new CachedRowSetImpl();
	                    	resultSetReceived = true;
	                    } else if(split[0].startsWith("Sending data")){
	                    	System.out.println("Receiving data...");
	                    	s = ss.accept();
	                    	ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
							try {
									rsw = (CachedRowSetImpl) ois.readObject();
									ResultSet rs = rsw.getOriginal();
									putIntoMap(split[1], rs);
							} catch (ClassNotFoundException e1) {
								e1.printStackTrace();
							}
							System.out.println("Unlocked Result Set");
	                    } else {
		                    if(split[1].startsWith("SELECT")){
		                    	ResultSet rs = executeRead(split[1]);
		                    	CachedRowSetImpl rsw = new CachedRowSetImpl();
		                    	rsw.populate(rs);
		                    	
		                    	Socket data = new Socket(serverIp, 6969);
								ObjectOutputStream oos = new ObjectOutputStream(data.getOutputStream());
								oos.writeObject(rsw);
								oos.close();
								data.close();
		                    } else { 
		                    	boolean success = executeWrite(split[1]);
		                    	if(success){
		                    		Socket sk = new Socket(serverIp, 6969);
		                    		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
		                    		dos.writeUTF("OK");
		                    		dos.close();
		                    		sk.close();
		                    	} else {
		                    		Socket sk = new Socket(serverIp, 6969);
		                    		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
		                    		dos.writeUTF("GG");
		                    		dos.close();
		                    		sk.close();
		                    	}
		                    }
	                    }
					} catch(Exception e){
						e.printStackTrace();
					}
					s.close();
                } 
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } 
		}
		
	}
	
	public synchronized void putIntoMap(String id, ResultSet rs){
		rsMap.put(id, rs);
	}
	
	public void sendCrashMessage() throws UnknownHostException, IOException {
		Socket sk = new Socket(serverIp, 6969);
		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
		dos.writeUTF(clientName + "has died.");
		dos.close();
		sk.close();
	}
	
}
