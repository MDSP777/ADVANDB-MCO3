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
	private ServerSocket ssPalawan;
	private ServerSocket ssMarinduque;
	private ServerSocket ssCentral;
	private ServerSocket ssShared;
	private String mIp = null;
	private String pIp = null;
	private String cIp = null;
	public static final String SERVER_NAME = "Server";

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		Server s = new Server();
		s.start();
		s.serve();
	}

	public Server() {
		try {
			ssShared = new ServerSocket(6968);
			ssPalawan = new ServerSocket(6969);
			ssMarinduque = new ServerSocket(6970);
			ssCentral = new ServerSocket(6971);

			ssPalawan.setSoTimeout(10000);
			ssMarinduque.setSoTimeout(10000);
			ssCentral.setSoTimeout(10000);
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}
	
	public void start() throws IOException{
		int n = 0;
		Socket curr;
		System.out.println("Starting...");
		while(n<2){
			curr = ssShared.accept();
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
				curr = ssShared.accept();
				DataInputStream dis = new DataInputStream(curr.getInputStream());
				String message = dis.readUTF();
				System.out.println("GOT " + message);
				if(message.equals("Palawan")) {
					pIp = curr.getInetAddress().getHostAddress();
					System.out.println("Palawan connected!");
				} else if(message.equals("Marinduque")) {
					mIp = curr.getInetAddress().getHostAddress();
					System.out.println("Marinduque connected!");
				} else if(message.equals("Central")) {
					cIp = curr.getInetAddress().getHostAddress();
					System.out.println("Central connected!");
				} else if(message.contains("has died")) {
					if(message.startsWith("Palawan")) {
						pIp = null;
					} else if(message.startsWith("Marinduque")) {
						mIp = null;
					} else if(message.startsWith("Central")) {
						cIp = null;
					}
				} else {
					String[] split = message.split("@");
					switch(split[0]){
						case "Palawan":
							if(split[1].startsWith("SELECT")){
								if (cIp != null){ 
									// send a request for data from central
									Socket data = new Socket(cIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF(message);
									dos.close();
									data.close();
									
									// wait for the data
									data = ssCentral.accept();
									ObjectInputStream ois = new ObjectInputStream(data.getInputStream());
									CachedRowSetImpl rsw = (CachedRowSetImpl) ois.readObject();
									data.close();
									
									// send data back to Palawan
									data = new Socket(pIp, 6969);
									ObjectOutputStream oos = new ObjectOutputStream(data.getOutputStream());
									oos.writeObject(rsw);
									oos.close();
									data.close();
								} else if(mIp != null){ 
									// send a request for data from marinduque
									Socket data = new Socket(mIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF(message);
									dos.close();
									data.close();
									
									// wait for the data
									// TODO what if Marinduque dies here? Need code to send unable to read message
									System.out.println("Waiting for data...");
									data = ssMarinduque.accept();
									ObjectInputStream ois = new ObjectInputStream(data.getInputStream());
									CachedRowSetImpl rsw = (CachedRowSetImpl) ois.readObject();
									ois.close();
									data.close();
									
									// send data back to Palawan
									System.out.println("Got data! Sending back to requester...");
									data = new Socket(pIp, 6969);
									dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF("Sending data@"+split[3]);
									dos.close();
									data.close();
									data = new Socket(pIp, 6969);
									ObjectOutputStream oos = new ObjectOutputStream(data.getOutputStream());
									oos.writeObject(rsw);
									oos.close();
									data.close();
								} else { // both are dead
									Socket data = new Socket(mIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF("Unable to read@"+split[3]);
									dos.close();
									data.close();
								}
							// code for writing
							} else if(split[1].startsWith("UPDATE")) {
								switch(split[0]){
									case "Palawan":
										if(cIp != null){ 
											// send update request to central 
											System.out.println("Sending request to central...");
											Socket data = new Socket(cIp, 6969);
											DataOutputStream dos = new DataOutputStream(data.getOutputStream());
											dos.writeUTF(message);
											dos.close();
											data.close();
											
											// receive confirmation from central
											
											System.out.println("Waiting for confirmation from central...");
											data = ssCentral.accept();
											DataInputStream din = new DataInputStream(data.getInputStream());
											String ok = din.readUTF();
											din.close();
											data.close();
											
											// send ok to palawan
											System.out.println("Received confirmation: "+ok);
											data = new Socket(pIp, 6969);
											dos = new DataOutputStream(data.getOutputStream());
											dos.writeUTF(ok+"@"+split[2]);
											dos.close();
											data.close();
										} else { // central is dead
											Socket data = new Socket(pIp, 6969);
											DataOutputStream dos = new DataOutputStream(data.getOutputStream());
											dos.writeUTF("GG@"+split[2]);
											dos.close();
											data.close();
										}
										break;
									case "Marinduque":
										
										break;
									case "Central":
										
										break;
								}
							}
							break;
						case "Marinduque":
							
							break;
						case "Central":
	
							break;
					}
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
