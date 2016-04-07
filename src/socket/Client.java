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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Entity;

import com.sun.rowset.CachedRowSetImpl;

import dbutils.DBManager;

public class Client {
	
	public static final String PALAWAN = "Palawan";
	public static final String MARINDUQUE = "Marinduque";
	public static final String CENTRAL = "Central";
	
	private ServerSocket ss;
	private String serverIp;
	private int isolationLevel = 4;
	private String clientName;
	private String dbName;
	private int sharedPortNo = 6968;
	private int portNo;
	private volatile HashMap<String, ArrayList<Entity>> rsMap = new HashMap<>();
	private volatile HashMap<String, Connection> connectionsMap = new HashMap<>();
	private CachedRowSetImpl rsw;
	private volatile int nRunningTransactions;
	private String password;
	
	public Client(String serverIp, String branchName) throws IOException{
		ss = new ServerSocket(6969);
		ss.setSoTimeout(15000);
		this.serverIp = serverIp;
		clientName = branchName;
		
		if(branchName.equals(PALAWAN)) {
			dbName = "db_hpq_palawan";
			portNo = 6969;
		} else if(branchName.equals(MARINDUQUE)) {
			dbName = "db_hpq_marinduque";
			portNo = 6970;
		} else if(branchName.equals(CENTRAL)) {
			dbName = "db_hpq_central";
			portNo = 6971;
		}
		
		// send connection to Server
		new Thread(new IncomingThread()).start();
		Socket initSocket = new Socket(serverIp, sharedPortNo);
		DataOutputStream dout = new DataOutputStream(initSocket.getOutputStream());
		dout.writeUTF(clientName);
		initSocket.close();
		
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void case1(ArrayList<String> transactions) throws Exception {
//		nRunningTransactions = 0;
		ArrayList<Thread> threads = new ArrayList<>();
		for(String cur: transactions){
			System.out.println("Running: "+cur);
			Thread t = new Thread(new TransactionThread(cur));
			threads.add(t);
			t.start();
		}
//		while(nRunningTransactions<transactions.size());
		for(Thread t: threads){
			t.join();
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
					if(clientName.equals(split[2]) || "Central".equals(clientName)){
						ResultSet rs = executeRead(split[1]);
						putIntoMap(split[3], rs);
					} else {
						Socket s = new Socket(serverIp, sharedPortNo);
						DataOutputStream dout = new DataOutputStream(s.getOutputStream());
						dout.writeUTF(cur);
						dout.close();
						s.close();
						
						while(!rsMap.containsKey(split[3]));
						System.out.println("Received data!");
						s.close();
					}
				} else if(!"Central".equals(split[0]) && split[1].startsWith("UPDATE")) {
					Connection connection;
					if(password == null) {
						connection = new DBManager(dbName).getConnection();
					} else {
						connection = new DBManager(dbName, password).getConnection();
					}
					Statement statement = null;
					int[] results = null;
					try {
						statement = connection.createStatement();
						statement.addBatch("Start transaction;");
						statement.addBatch(split[1]);
						results = statement.executeBatch();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					System.out.println("Finished Writing!");
					
					if(results[1]>=1){
						Socket sk = new Socket(serverIp, sharedPortNo);
						DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
						dos.writeUTF(cur+"@"+clientName);
						dos.close();
						sk.close();
						
						putIntoMap(split[2], connection);
					} else {
						System.out.println("Global write. Sending to server...");
						Socket sk = new Socket(serverIp, sharedPortNo);
						DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
						dos.writeUTF(cur+"@"+theOther(clientName));
						dos.close();
						sk.close();
					}
				} else {
					System.out.println("Central update");
					Socket sk = new Socket(serverIp, sharedPortNo);
					DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
					dos.writeUTF(cur+"@Central");
					dos.close();
					sk.close();
				}
			} catch (Exception e){
				e.printStackTrace();
			}
			addTransaction();
		}

		private String theOther(String client) {
			switch(client){
				case PALAWAN: return MARINDUQUE;
				case MARINDUQUE: return PALAWAN;
			}
			return null;
		}
		
	}
	
	ResultSet executeRead(String query){
		Connection connection;
		if(password == null) {
			connection = new DBManager(dbName).getConnection();
		} else {
			connection = new DBManager(dbName, password).getConnection();
		}
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.prepareStatement(query);
			resultSet = statement.executeQuery();
			return resultSet;
//			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	boolean executeWrite(String query){
//		try {
//			Connection connection = new DBManager(dbName).getConnection();
//			PreparedStatement statement = null;
//			statement = connection.prepareStatement(query);
//			int res = statement.executeUpdate();
//			connection.close();
//			return true;
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
	
	class IncomingThread implements Runnable{

		@Override
		public void run() {
			while (true) {
				try {
					Socket s = ss.accept();
					DataInputStream din = new DataInputStream(s.getInputStream());
					try {
						String msgin = din.readUTF();
						System.out.println("Received " + msgin);
						String[] split = msgin.split("@");
						if ("CentralWrite".equals(split[0])) {
							System.out.println("Writing to Central");
							Connection connection;
							if (password == null) {
								connection = new DBManager(dbName).getConnection();
							} else {
								connection = new DBManager(dbName, password).getConnection();
							}
							Statement stmt = connection.createStatement();
							int res = stmt.executeUpdate(split[1]);
							if (res > 0) {
								Socket sk = new Socket(serverIp, portNo);
								DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
								dos.writeUTF("OK");
								dos.close();
								sk.close();
							} else {
								Socket sk = new Socket(serverIp, portNo);
								DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
								dos.writeUTF("GG");
								dos.close();
								sk.close();
	    					}
	                    }else if(split[0].equals("Unable to read")){
	                    	putNullResult(split[1]);
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
	                    } else if("Merge".equals(split[0])){
	                    	System.out.println("Receiving data...");
	                    	s = ss.accept();
	                    	ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
							try {
									rsw = (CachedRowSetImpl) ois.readObject();
									ResultSet rs = rsw.getOriginal();
									putIntoMap(split[4], rs);
							} catch (ClassNotFoundException e1) {
								e1.printStackTrace();
							}
							System.out.println("Unlocked Result Set");
	                    	
							ResultSet rs = null;
							System.out.println(split[2]);
	                    	if(split[2].contains("db_hpq_"+Client.MARINDUQUE.toLowerCase())) {
	                    		rs = executeRead(split[2].replaceAll("db_hpq_"+Client.MARINDUQUE.toLowerCase(), "db_hpq_"+theOther(Client.MARINDUQUE).toLowerCase()));
	                    	} else if(split[2].contains("db_hpq_"+Client.PALAWAN.toLowerCase())) {
	                    		rs = executeRead(split[2].replaceAll("db_hpq_"+Client.PALAWAN.toLowerCase(), "db_hpq_"+theOther(Client.PALAWAN).toLowerCase()));
	                    	}
							ArrayList<Entity> entities = new ArrayList<Entity>();
							entities.addAll(rsMap.get(split[4]));
							
							try {
								while(rs.next()){
									Entity cur = new Entity(
											rs.getInt(1),
											rs.getInt(2),
											rs.getInt(3),
											rs.getInt(4),
											rs.getInt(5),
											rs.getInt(6),
											rs.getInt(7),
											rs.getInt(8),
											rs.getInt(9),
											rs.getInt(10),
											rs.getInt(11),
											rs.getInt(12),
											rs.getInt(13),
											rs.getInt(14));
									entities.add(cur);
								}
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							
							rsMap.put(split[4], entities);
							
	                    } else if (split[0].startsWith("OK")){
	                    	Connection c = connectionsMap.get(split[1]);
	                    	c.createStatement().execute("commit;");
	                    	c.close();
	                    	connectionsMap.remove(split[1]);
	                    } else if (split[0].startsWith("GG")){
	                    	Connection c = connectionsMap.get(split[1]);
	                    	c.createStatement().execute("rollback;");
	                    	c.close();
	                    	connectionsMap.remove(split[1]);
	                    } else {
		                    if(split[1].startsWith("SELECT")){
		                    	ResultSet rs = executeRead(split[1]);
		                    	CachedRowSetImpl rsw = new CachedRowSetImpl();
		                    	rsw.populate(rs);
		                    	
		                    	Socket data = new Socket(serverIp, portNo);
								ObjectOutputStream oos = new ObjectOutputStream(data.getOutputStream());
								oos.writeObject(rsw);
								oos.close();
								data.close();
		                    } else { 
		                    	System.out.println("Starting write");
		                    	boolean success = true;
		                    	Connection connection;
		    					if(password == null) {
		    						connection = new DBManager(dbName).getConnection();
		    					} else {
		    						connection = new DBManager(dbName, password).getConnection();
		    					}
		    					Statement statement = connection.createStatement();
		    					int[] results = null;
		    					try {
		    						statement.addBatch("Start transaction;");
		    						statement.addBatch(split[1]);
		    						results = statement.executeBatch();
		    					} catch (SQLException e) {
		    						e.printStackTrace();
		    						success = false;
		    					}
		    					if("Central".equals(split[0])){
		    						if(success){
		    							System.out.println("Transaction success!");
			                    		Socket sk = new Socket(serverIp, portNo);
			                    		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
			                    		dos.writeUTF(results[1]+"");
			                    		dos.close();
			                    		sk.close();
			                    		if(results[1]==0){
			                    			split[4] = "auto";
			                    		}
		    						} else {
		    							System.out.println("Transaction fail!");
			                    		Socket sk = new Socket(serverIp, portNo);
			                    		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
			                    		dos.writeUTF("0");
			                    		dos.close();
			                    		sk.close();
		    						}
		    					}
		    					else {
			                    	if(success){
			                    		System.out.println("Transaction success!");
			                    		Socket sk = new Socket(serverIp, portNo);
			                    		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
			                    		dos.writeUTF("OK");
			                    		dos.close();
			                    		sk.close();
			                    	} else {
			                    		System.out.println("Transaction fail!");
			                    		Socket sk = new Socket(serverIp, portNo);
			                    		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
			                    		dos.writeUTF("GG");
			                    		dos.close();
			                    		sk.close();
			                    	}
		    					}
		                    	
		                    	if("dontauto".equals(split[4])){
		                    		Socket skt = ss.accept();
		                    		DataInputStream dis = new DataInputStream(skt.getInputStream());
		                    		String result = dis.readUTF();
		                    		System.out.println("Received commit command "+result);
		                    		dis.close();
		                    		skt.close();
		                    		
		                    		if("Commit".equals(result)){
		                    			System.out.println("Committing...");
			                    		statement.execute("commit;");
		                    		} else {
		                    			System.out.println("Rolling back...");
		                    			statement.execute("rollback;");
		                    		}
		                    	} else {
		                    		statement.execute("commit;");
		                    	}
		                    	System.out.println("Closing connection...");
		                    	connection.close();
		                    }
	                    }
					} catch(Exception e){
						e.printStackTrace();
					}
					s.close();
                	}catch(Exception e){
                	}
                } 
		}
		
		private String theOther(String client) {
			switch(client){
				case PALAWAN: return MARINDUQUE;
				case MARINDUQUE: return PALAWAN;
			}
			return null;
		}
	}
	
	private synchronized void putNullResult(String id) {
		rsMap.put(id, null);
	}
	
	private synchronized void putIntoMap(String id, ResultSet rs){
		ArrayList<Entity> e = new ArrayList<>();
		try {
			while(rs.next()){
				Entity cur = new Entity(
						rs.getInt(1),
						rs.getInt(2),
						rs.getInt(3),
						rs.getInt(4),
						rs.getInt(5),
						rs.getInt(6),
						rs.getInt(7),
						rs.getInt(8),
						rs.getInt(9),
						rs.getInt(10),
						rs.getInt(11),
						rs.getInt(12),
						rs.getInt(13),
						rs.getInt(14));
				e.add(cur);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		rsMap.put(id, e);
	}
	
	public synchronized void putIntoMap(String id, Connection c){
		connectionsMap.put(id, c);
	}
	
	public synchronized void addTransaction(){
		nRunningTransactions++;
	}
	
	public ArrayList<Entity> getById(String id){
		return rsMap.get(id);
	}
	
	public void sendCrashMessage() throws UnknownHostException, IOException {
		Socket sk = new Socket(serverIp, sharedPortNo);
		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
		dos.writeUTF(clientName + " has died.");
		dos.close();
		sk.close();
	}
	
}
