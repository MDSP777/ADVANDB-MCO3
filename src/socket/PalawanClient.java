package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dbutils.DBManager;
import model.ResultSetWrapper;

public class PalawanClient extends Client {
	private ServerSocket ss;
	private String serverIp;
	private int isolationLevel = 4;
	
	public PalawanClient(String serverIp) throws IOException{
		ss = new ServerSocket(6969);
		this.serverIp = serverIp;
		
		// send connection to Server
		Socket initSocket = new Socket(serverIp, 6969);
		DataOutputStream dout = new DataOutputStream(initSocket.getOutputStream());
		dout.writeUTF("Palawan");
		initSocket.close();
		
	}
	
	public void case1(ArrayList<String> transactions) throws Exception {
		for(String cur: transactions){
			String[] split = cur.split("@");
			if("Palawan".equals(split[0])){
				Connection connection = new DBManager("db_hpq_palawan").getConnection();
				PreparedStatement statement = null;
				ResultSet resultSet = null;
				try {
					statement = connection.prepareStatement(split[1]);
					resultSet = statement.executeQuery();
					// TODO do something with the resultset
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				Socket s = new Socket(serverIp, 6969);
				DataOutputStream dout = new DataOutputStream(s.getOutputStream());
				dout.writeUTF(cur);
				dout.close();
				s.close();
				
				s = ss.accept();
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				ResultSetWrapper rsw = (ResultSetWrapper) ois.readObject();
				ResultSet rs = rsw.getRs();
				// TODO do something with the resultset
				s.close();
			}
		}
	}
}
