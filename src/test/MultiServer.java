package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultiServer{
	
	Map<String, PrintWriter> clientMap;
	static ServerSocket serverSocket = null;
	static Socket socket = null;
	
	public MultiServer() {
		clientMap = new HashMap<String, PrintWriter>();
		Collections.synchronizedMap(clientMap);
	}
	
	public void init() {
		
		try {	
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");
			
			while(true) {
				socket = serverSocket.accept();
				System.out.println(socket.getInetAddress()+":"+socket.getPort());
						
				Thread mst = new MultiServerT(socket);
				mst.start();
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				serverSocket.close();
			}
			catch (Exception e) {			
				e.printStackTrace();
			}
		}	
	}
	
	public void sendAllMsg(String name, String msg) {
		
		Iterator<String> it = clientMap.keySet().iterator();		

		while(it.hasNext()) {
			try {
				PrintWriter it_out = (PrintWriter) clientMap.get(it.next());
				msg = URLEncoder.encode(msg, "UTF-8");
				name = URLEncoder.encode(name, "UTF-8");
				
				if(name.equals("")) {					
					it_out.println(msg);
				}
				else {					
					it_out.println("["+ name +"] "+ msg);
				}
			} 
			catch(Exception e) {
				System.out.println("예외:"+e);
			}			
		}
	}
	 
	public static void main(String[] args) {
		MultiServer ms = new MultiServer();
		ms.init();
	}		
	
	class MultiServerT extends Thread {
		
		Socket socket;
		PrintWriter out = null;
		BufferedReader in = null;
		
		public MultiServerT(Socket socket) {
			this.socket = socket;
			try {
				out = new PrintWriter(this.socket.getOutputStream(), true);				
				in = new BufferedReader(new InputStreamReader(
						this.socket.getInputStream(), "UTF-8"));
			} 
			catch (Exception e) {
				System.out.println("예외:"+ e);
			}
		}
		
		@Override
		public void run() {
			
			String name = ""; 
			String s = "";
			
			try {
				name = URLDecoder.decode(in.readLine(), "UTF-8");

				sendAllMsg("", name + "님이 입장하셨습니다.");			
				clientMap.put(name, out);  
				
				System.out.println(name + "> 접속");
				System.out.println("현재 접속자 수는 "+clientMap.size()+"명 입니다.");
				
				while (in!=null) { 
					s = URLDecoder.decode(in.readLine(), "UTF-8");					
					if ( s == null ) break;									
					
					System.out.println(name + " >> " + s);
					sendAllMsg(name, s);//첫번째 인자 포함해서 메소드호출
				}
			} 
			catch (Exception e) {
				System.out.println("예외:"+ e);
			}
			finally {
				clientMap.remove(name); 
				sendAllMsg("", name + "님이 퇴장하셨습니다.");				 
				System.out.println(name + " [" + Thread.currentThread().getName() +  "] 퇴장");
				System.out.println("현재 접속자 수는 "+clientMap.size()+"명 입니다.");

				try {
					in.close();
					out.close();
					socket.close();
				} 
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}		
	}
}

//	public static void main(String[] args) {
//		
//		PrintWriter out = null;
//		BufferedReader in = null;
//		String s = "";
//		String name = "";
//		
//		IConnectImpl jdbc = new IConnectImpl("kosmo", "1234");
//
//		try {
//			serverSocket = new ServerSocket(9999);
//			System.out.println("서버가 시작되었습니다.");
//
//			socket = serverSocket.accept();
//
//			out = new PrintWriter(socket.getOutputStream(), true);
//			in = new BufferedReader(
//					new InputStreamReader(socket.getInputStream(), "UTF-8"));
//
//			if(in != null) {
//				name = in.readLine();
//				name = URLDecoder.decode(name, "UTF-8");
//				System.out.println(name +" 접속");
//				out.println("> "+ name +"님이 접속했습니다.");
//			}
//
//			while(in != null) {
//				s = in.readLine();
//				s = URLDecoder.decode(s, "UTF-8");
//				if(s==null) {
//					break;
//				}
//				if(s.indexOf("광고") != -1) {
//					out.println("금지단어이므로 출력되지 않습니다");
//					continue;
//				}
//				System.out.println(name +" ==> "+ s);
//				out.println(">  "+ name +" ==> "+ s);
//				
//				String query = "INSERT INTO chatting_tb VALUES"
//						+ "(chatting_seq.nextval, ?, ?, SYSDATE)";
//				jdbc.psmt = jdbc.con.prepareStatement(query);
//				
//				jdbc.psmt.setString(1, name);
//				jdbc.psmt.setString(2, s);
//				
//				int affected = jdbc.psmt.executeUpdate();
//				System.out.println(affected + "행이 입력되었습니다.");
//			}
//
//			System.out.println("Bye...!!!");
//		}
//		catch (Exception e) {
//			System.out.println("예외1:"+ e);
//		}
//		finally {
//			try {
//				in.close();
//				out.close();
//				socket.close();
//				serverSocket.close();
//			}
//			catch (Exception e) {
//				System.out.println("예외2:"+ e);
//			}
//		}
//	}
//}
