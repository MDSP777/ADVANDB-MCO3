package socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.sun.rowset.CachedRowSetImpl;

import model.ResultSetWrapper;

public class Server {
	private ServerSocket ss;
	private String mIp;
	private String pIp;
	private String cIp;
	public static final String SERVER_NAME = "Server";

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Server s = new Server();
		s.start();
		s.serve();
	}

	public Server() {
		try {
			ss = new ServerSocket(6969);
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}
	
	public void start() throws IOException{
		int n = 0;
		Socket curr;
		while(n<3){
			curr = ss.accept();
			String ip = curr.getInetAddress().getHostAddress();
			DataInputStream dis = new DataInputStream(curr.getInputStream());
			String name = dis.readUTF();
			switch(name){
				case "Palawan":
					pIp = ip;
					System.out.println("Palawan connected!");
					n++;
					break;
				case "Marinduque":
					mIp = ip;
					System.out.println("Marinduque connected!");
					n++;
					break;
				case "Central":
					cIp = ip;
					System.out.println("Central connected!");
					n++;
					break;
			}
		}
		System.out.println("All connected!");
	}

	public void serve() throws ClassNotFoundException {
		Socket curr;
		while(true) {
			try {
				curr = ss.accept();
				DataInputStream dis = new DataInputStream(curr.getInputStream());
				String message = dis.readUTF();
				System.out.println("GOT " + message);
				String[] split = message.split("@");
				switch(split[0]){
					case "Palawan":
						if(split[1].startsWith("SELECT")){
							if (false){ // TODO central is alive
								// send a request for data from central
								Socket data = new Socket(cIp, 6969);
								DataOutputStream dos = new DataOutputStream(data.getOutputStream());
								dos.writeUTF(message);
								dos.close();
								data.close();
								
								// wait for the data
								data = ss.accept();
								ObjectInputStream ois = new ObjectInputStream(data.getInputStream());
								CachedRowSetImpl rsw = (CachedRowSetImpl) ois.readObject();
								data.close();
								
								// send data back to Palawan
								data = new Socket(pIp, 6969);
								ObjectOutputStream oos = new ObjectOutputStream(data.getOutputStream());
								oos.writeObject(rsw);
								oos.close();
								data.close();
							} else if(true){ // TODO marinduque is alive
								// send a request for data from marinduque
								Socket data = new Socket(mIp, 6969);
								DataOutputStream dos = new DataOutputStream(data.getOutputStream());
								dos.writeUTF(message);
								dos.close();
								data.close();
								
								// wait for the data
								data = ss.accept();
								ObjectInputStream ois = new ObjectInputStream(data.getInputStream());
								ResultSetWrapper rsw = (ResultSetWrapper) ois.readObject();
								ois.close();
								data.close();
								
								// send data back to Palawan
								data = new Socket(pIp, 6969);
								ObjectOutputStream oos = new ObjectOutputStream(data.getOutputStream());
								oos.writeObject(rsw);
								oos.close();
								data.close();
							} else { // both are dead
								Socket data = new Socket(mIp, 6969);
								DataOutputStream dos = new DataOutputStream(data.getOutputStream());
								dos.writeUTF("Unable to read");
								dos.close();
								data.close();
							}
						// code for writing
						} else if(split[1].startsWith("UPDATE")) {
							// not sure about any of this shit below
							switch(split[0]){
								case "Palawan":
									if(true){ // TODO if central is alive
										// send update request to central 
										Socket data = new Socket(cIp, 6969);
										DataOutputStream dos = new DataOutputStream(data.getOutputStream());
										dos.writeUTF(message);
										dos.close();
										data.close();
										
										// receive confirmation from central
										data = new Socket(cIp, 6969);
										DataInputStream din = new DataInputStream(data.getInputStream());
										String ok = din.readUTF();
										din.close();
										data.close();
										
										// send ok to palawan
										if("OK".equals(ok)){
											data = ss.accept();
											dos = new DataOutputStream(data.getOutputStream());
											dos.writeUTF("OK");
											dos.close();
											data.close();
										} else {
											// TODO tell palawan to kill himself
										}
									} else { // central is dead
										// TODO tell palawan to kill himself
									}
									break;
								case "Marinduque":
									
									break;
							}
						}
						break;
					case "Marinduque":
						
						break;
					case "Central":

						break;
				}
			} catch( IOException ioe ) {
				ioe.printStackTrace();
			}
		}
	}

	public void sendMessage(Socket s,String message) {
		try {
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF(message);
			dos.close();
			s.close();
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}
}
