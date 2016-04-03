package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UTFDataFormatException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.rowset.CachedRowSetImpl;

import dbutils.DBManager;
import model.ResultSetWrapper;

public class PalawanClient extends Client {
	private ServerSocket ss;
	private String serverIp;
	private int isolationLevel = 4;
	private String clientName = "Palawan";
	private String dbName = "db_hpq_palawan";
	private String message;
	private CachedRowSetImpl rsw;
	private boolean resultSetReceived;
	
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
			String[] split = cur.split("@");
			if(clientName.equals(split[0])){
				ResultSet rs = executeRead(split[1]);
				while(rs.next()){
					System.out.println(rs.getInt(1));
				}
			} else {
				Socket s = new Socket(serverIp, 6969);
				DataOutputStream dout = new DataOutputStream(s.getOutputStream());
				dout.writeUTF(cur);
				dout.close();
				s.close();
				
				while(!resultSetReceived);
				resultSetReceived = false;
				ResultSet rs = rsw.getOriginal();
				while(rs.next()){
					System.out.println(rs.getInt(1));
				}
				s.close();
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
			connection.close();
			return resultSet;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	int executeWrite(String query){
		
		// TODO add shiz for start and end of transaction
		Connection connection = new DBManager(dbName).getConnection();
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
			int res = statement.executeUpdate();
			connection.close();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
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
	                    String[] split = msgin.split(" ");
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
	                    	// TODO write 
	                    }
					} catch(UTFDataFormatException e){
						ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
						try {
								rsw = (CachedRowSetImpl) ois.readObject();
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						}
						resultSetReceived = true;
					}
					s.close();
                } 
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}
}
