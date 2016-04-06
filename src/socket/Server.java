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

			ssPalawan.setSoTimeout(20000);
			ssMarinduque.setSoTimeout(20000);
			ssCentral.setSoTimeout(20000);
		} catch( IOException ioe ) {
			ioe.printStackTrace();
		}
	}
	
	public void start() throws IOException{
		int n = 0;
		Socket curr;
		System.out.println("Starting...");
		while(n<3){
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
								boolean retrieveSuccess = false;
								if (cIp != null){ 
									// send a request for data to central
									Socket data = new Socket(cIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF(message);
									dos.close();
									data.close();
									
									// wait for the data
									try{
										System.out.println("Waiting for data from central...");
										data = ssCentral.accept();
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
										retrieveSuccess = true;
									} catch (Exception e){
										System.out.println("Timed out. Attempting to retrieve data from Marinduque...");
									}
								}
								if(!retrieveSuccess && mIp != null){ 
									// send a request for data to marinduque
									Socket data = new Socket(mIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF(message);
									dos.close();
									data.close();
									
									// wait for the data
									try{
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
										retrieveSuccess = true;
									} catch(Exception e){
										System.out.println("Timed out waiting for data from Marinduque. Unable to read");
									}
								} 
								if(!retrieveSuccess){ // both are dead
									System.out.println("Failed to retrieve from both sources.");
									Socket data = new Socket(pIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF("Unable to read@"+split[3]);
									dos.close();
									data.close();
								}
							// code for writing
							} else if(split[1].startsWith("UPDATE")) {
								// TODO we might still want to handle Palawan writing to Marinduque
								if(cIp != null){
									if("Palawan".equals(split[3])){
										System.out.println("Local write (Palawan)");
										// send update request to central 
										System.out.println("Sending request to central...");
										Socket data = new Socket(cIp, 6969);
										DataOutputStream dos = new DataOutputStream(data.getOutputStream());
										dos.writeUTF(message+"@auto");
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
									} else {
										System.out.println("Global write (Palawan)");
										if(cIp!=null && mIp!=null){
											String centralOk = "";
											String marinOk = "";
											
											try{
												// send update request to central 
												System.out.println("Sending request to central...");
												Socket data = new Socket(cIp, 6969);
												DataOutputStream dos = new DataOutputStream(data.getOutputStream());
												dos.writeUTF(message+"@dontauto");
												dos.close();
												data.close();
												
												// receive confirmation from central
												System.out.println("Waiting for confirmation from central...");
												data = ssCentral.accept();
												DataInputStream din = new DataInputStream(data.getInputStream());
												centralOk = din.readUTF();
												din.close();
												data.close();
											} catch(Exception e){
												System.out.println("Failed to write to Central");
												centralOk = "fail";
											}
											
											if(!"fail".equals(centralOk)){
												try{
												// send update request to marinduque 
												System.out.println("Sending request to marinduque...");
												Socket data = new Socket(mIp, 6969);
												DataOutputStream dos = new DataOutputStream(data.getOutputStream());
												dos.writeUTF(message+"@dontauto");
												dos.close();
												data.close();
												
												// receive confirmation from marinduque
												System.out.println("Waiting for confirmation from marinduque...");
												data = ssMarinduque.accept();
												DataInputStream din = new DataInputStream(data.getInputStream());
												marinOk = din.readUTF();
												din.close();
												data.close();
												} catch(Exception e){
													System.out.println("Failed to write to Marinduque");
													marinOk = "fail";
												}
											}
											String commitOrNot = "Rollback";
											System.out.println(centralOk);
											System.out.println(marinOk);
											if("OK".equals(centralOk) && "OK".equals(marinOk)){
												commitOrNot = "Commit";
											}
											// tell central to commit
											System.out.println("Sending commit command to central...");
											Socket data = new Socket(cIp, 6969);
											DataOutputStream dos = new DataOutputStream(data.getOutputStream());
											dos.writeUTF(commitOrNot);
											dos.close();
											data.close();
											
											// tell marinduque to commit
											System.out.println("Sending commit command to marinduque...");
											data = new Socket(mIp, 6969);
											dos = new DataOutputStream(data.getOutputStream());
											dos.writeUTF(commitOrNot);
											dos.close();
											data.close();
										}
										
									}
								} else { // central is dead
									Socket data = new Socket(pIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF("GG@"+split[2]);
									dos.close();
									data.close();
								}
							}
							break;
						case "Marinduque":
							if(split[1].startsWith("SELECT")){
								boolean retrieveSuccess = false;
								if (cIp != null){ 
									// send a request for data from Central
									Socket data = new Socket(cIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF(message);
									dos.close();
									data.close();
									
									// wait for the data
									try{
										System.out.println("Waiting for data from central...");
										data = ssCentral.accept();
										ObjectInputStream ois = new ObjectInputStream(data.getInputStream());
										CachedRowSetImpl rsw = (CachedRowSetImpl) ois.readObject();
										ois.close();
										data.close();
										
										// send data back to Marinduque
										System.out.println("Got data! Sending back to requester...");
										data = new Socket(mIp, 6969);
										dos = new DataOutputStream(data.getOutputStream());
										dos.writeUTF("Sending data@"+split[3]);
										dos.close();
										data.close();
										data = new Socket(mIp, 6969);
										ObjectOutputStream oos = new ObjectOutputStream(data.getOutputStream());
										oos.writeObject(rsw);
										oos.close();
										data.close();
										retrieveSuccess = true;
									} catch (Exception e){
										System.out.println("Timed out. Attempting to retrieve data from Palawan...");
									}
								}
								if(!retrieveSuccess && pIp != null){ 
									// send a request for data from Palawan
									Socket data = new Socket(pIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF(message);
									dos.close();
									data.close();
									
									// wait for the data
									try{
										System.out.println("Waiting for data...");
										data = ssPalawan.accept();
										ObjectInputStream ois = new ObjectInputStream(data.getInputStream());
										CachedRowSetImpl rsw = (CachedRowSetImpl) ois.readObject();
										ois.close();
										data.close();
										
										// send data back to Marinduque
										System.out.println("Got data! Sending back to requester...");
										data = new Socket(mIp, 6969);
										dos = new DataOutputStream(data.getOutputStream());
										dos.writeUTF("Sending data@"+split[3]);
										dos.close();
										data.close();
										data = new Socket(mIp, 6969);
										ObjectOutputStream oos = new ObjectOutputStream(data.getOutputStream());
										oos.writeObject(rsw);
										oos.close();
										data.close();
										retrieveSuccess = true;
									} catch(Exception e){
										System.out.println("Timed out waiting for data from Marinduque. Unable to read");
									}
								} 
								if(!retrieveSuccess){ // both are dead
									System.out.println("Failed to retrieve from both sources.");
									Socket data = new Socket(mIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF("Unable to read@"+split[3]);
									dos.close();
									data.close();
								}
							// code for writing
							} else if(split[1].startsWith("UPDATE")) {
								// TODO we might still want to handle Marinduque writing to Palawan
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
									
									// send ok to Marinduque
									System.out.println("Received confirmation: "+ok);
									data = new Socket(mIp, 6969);
									dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF(ok+"@"+split[2]);
									dos.close();
									data.close();
								} else { // central is dead
									Socket data = new Socket(mIp, 6969);
									DataOutputStream dos = new DataOutputStream(data.getOutputStream());
									dos.writeUTF("GG@"+split[2]);
									dos.close();
									data.close();
								}
							}
							break;
						case "Central":
							if(split[1].startsWith("UPDATE")) {
								// TODO we might still want to handle Palawan writing to Marinduque
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
							}
							break;
					}
				}
			} catch( IOException ioe ) {
				ioe.printStackTrace();
			}
		}
	}

}
