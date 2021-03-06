//package socket;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.UTFDataFormatException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import model.Entity;
//
//import com.sun.rowset.CachedRowSetImpl;
//
//import dbutils.DBManager;
//
//public class PalawanClient extends Client {
//	private ServerSocket ss;
//	private String serverIp;
//	private int isolationLevel = 4;
//	private String clientName = "Palawan";
//	private String dbName = "db_hpq_palawan";
//	private int sharedPortNo = 6968;
//	private int portNo = 6969;
//	private volatile HashMap<String, ArrayList<Entity>> rsMap = new HashMap<>();
//	private volatile HashMap<String, Connection> connectionsMap = new HashMap<>();
//	private CachedRowSetImpl rsw;
//	private volatile int nRunningTransactions;
//	
//	public PalawanClient(String serverIp) throws IOException{
//		ss = new ServerSocket(6969);
//		this.serverIp = serverIp;
//		
//		// send connection to Server
//		new Thread(new IncomingThread()).start();
//		Socket initSocket = new Socket(serverIp, sharedPortNo);
//		DataOutputStream dout = new DataOutputStream(initSocket.getOutputStream());
//		dout.writeUTF(clientName);
//		initSocket.close();
//		
//	}
//	
//	public void case1(ArrayList<String> transactions) throws Exception {
////		nRunningTransactions = 0;
//		ArrayList<Thread> threads = new ArrayList<>();
//		for(String cur: transactions){
//			System.out.println("Running: "+cur);
//			Thread t = new Thread(new TransactionThread(cur));
//			threads.add(t);
//			t.start();
//		}
////		while(nRunningTransactions<transactions.size());
//		for(Thread t: threads){
//			t.join();
//		}
//	}
//	
//	class TransactionThread implements Runnable {
//		private String cur;
//		
//		public TransactionThread(String t){
//			cur = t;
//		}
//		
//		@Override
//		public void run() {
//			try{
//				String[] split = cur.split("@");
//				if(split.length>=2 && split[1].startsWith("SELECT")){
//					if(clientName.equals(split[2]) || "Central".equals(clientName)){
//						ResultSet rs = executeRead(split[1]);
//						putIntoMap(split[3], rs);
//					} else {
//						Socket s = new Socket(serverIp, sharedPortNo);
//						DataOutputStream dout = new DataOutputStream(s.getOutputStream());
//						dout.writeUTF(cur);
//						dout.close();
//						s.close();
//						
//						while(!rsMap.containsKey(split[3]));
//						System.out.println("Received data!");
//						s.close();
//					}
//				} else if(split[1].startsWith("UPDATE")) {
//					Connection connection = new DBManager(dbName).getConnection();
//					Statement statement = null;
//					try {
//						statement = connection.createStatement();
//						statement.addBatch("Start transaction;");
//						statement.addBatch(split[1]);
//						statement.executeBatch();
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//					System.out.println("Finished Writing!");
//					
//					Socket sk = new Socket(serverIp, sharedPortNo);
//					DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
//					dos.writeUTF(cur);
//					dos.close();
//					sk.close();
//					
//					putIntoMap(split[2], connection);
//				}
//			} catch (Exception e){
//				e.printStackTrace();
//			}
//			addTransaction();
//		}
//		
//	}
//	
//	ResultSet executeRead(String query){
//		Connection connection = new DBManager(dbName).getConnection();
//		PreparedStatement statement = null;
//		ResultSet resultSet = null;
//		try {
//			statement = connection.prepareStatement(query);
//			resultSet = statement.executeQuery();
//			return resultSet;
////			connection.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
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
//	
//	class IncomingThread implements Runnable{
//
//		@Override
//		public void run() {
//			 try {
//                while(true){
//					Socket s = ss.accept();
//					DataInputStream din = new DataInputStream(s.getInputStream());
//					try{
//	                    String msgin = din.readUTF();
//	                    System.out.println("Received "+msgin);
//	                    String[] split = msgin.split("@");
//	                    if(split[0].equals("Unable to read")){
//	                    	rsw = new CachedRowSetImpl();
//	                    } else if(split[0].startsWith("Sending data")){
//	                    	System.out.println("Receiving data...");
//	                    	s = ss.accept();
//	                    	ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
//							try {
//									rsw = (CachedRowSetImpl) ois.readObject();
//									ResultSet rs = rsw.getOriginal();
//									putIntoMap(split[1], rs);
//							} catch (ClassNotFoundException e1) {
//								e1.printStackTrace();
//							}
//							System.out.println("Unlocked Result Set");
//	                    } else if (split[0].startsWith("OK")){
//	                    	Connection c = connectionsMap.get(split[1]);
//	                    	c.createStatement().execute("commit;");
//	                    	c.close();
//	                    	connectionsMap.remove(split[1]);
//	                    } else if (split[0].startsWith("GG")){
//	                    	Connection c = connectionsMap.get(split[1]);
//	                    	c.createStatement().execute("rollback;");
//	                    	c.close();
//	                    	connectionsMap.remove(split[1]);
//	                    } else {
//		                    if(split[1].startsWith("SELECT")){
//		                    	ResultSet rs = executeRead(split[1]);
//		                    	if(rs==null){
//		                    		// TODO send crash message
//		                    	}
//		                    	CachedRowSetImpl rsw = new CachedRowSetImpl();
//		                    	rsw.populate(rs);
//		                    	
//		                    	Socket data = new Socket(serverIp, portNo);
//								ObjectOutputStream oos = new ObjectOutputStream(data.getOutputStream());
//								oos.writeObject(rsw);
//								oos.close();
//								data.close();
//		                    } else { 
//		                    	boolean success = executeWrite(split[1]);
//		                    	if(success){
//		                    		Socket sk = new Socket(serverIp, portNo);
//		                    		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
//		                    		dos.writeUTF("OK");
//		                    		dos.close();
//		                    		sk.close();
//		                    	} else {
//		                    		Socket sk = new Socket(serverIp, portNo);
//		                    		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
//		                    		dos.writeUTF("GG");
//		                    		dos.close();
//		                    		sk.close();
//		                    	}
//		                    }
//	                    }
//					} catch(Exception e){
//						e.printStackTrace();
//					}
//					s.close();
//                } 
//            } catch (IOException ex) {
//                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//            } 
//		}
//		
//	}
//	
//	public synchronized void putIntoMap(String id, ResultSet rs){
//		ArrayList<Entity> e = new ArrayList<>();
//		try {
//			while(rs.next()){
//				Entity cur = new Entity(
//						rs.getInt(1),
//						rs.getInt(2),
//						rs.getInt(3),
//						rs.getInt(4),
//						rs.getInt(5),
//						rs.getInt(6),
//						rs.getInt(7),
//						rs.getInt(8),
//						rs.getInt(9),
//						rs.getInt(10),
//						rs.getInt(11),
//						rs.getInt(12),
//						rs.getInt(13),
//						rs.getInt(14));
//				e.add(cur);
//			}
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
//		rsMap.put(id, e);
//	}
//	
//	public synchronized void putIntoMap(String id, Connection c){
//		connectionsMap.put(id, c);
//	}
//	
//	public synchronized void addTransaction(){
//		nRunningTransactions++;
//	}
//	
//	public ArrayList<Entity> getById(String id){
//		return rsMap.get(id);
//	}
//	
//	public void sendCrashMessage() throws UnknownHostException, IOException {
//		Socket sk = new Socket(serverIp, sharedPortNo);
//		DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
//		dos.writeUTF(clientName + " has died.");
//		dos.close();
//		sk.close();
//	}
//	
//}
