package chat4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiServer {
	
	static ServerSocket serverSocket = null;
	static Socket socket = null;
	static PrintWriter out = null;
	static BufferedReader in = null;
	static String s = "";
		
	//생성자(나중에 씀)
	public MultiServer() {
	}
	
	public static void init() {
		
		String name = "";
	
		try {
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");
			
			socket = serverSocket.accept();
			System.out.println(socket.getInetAddress() + ":" + socket.getPort());
			
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream())
					);
			
			if(in != null) {
				name = in.readLine();
				System.out.println(name + "님 접속");
				out.println("> " + name + "님이 접속했습니다.");
			}
			
			while(in != null) {
				s = in.readLine();
				if(s==null) {
					break;
				}
				System.out.println(name + " ==> " + s);
				sendAllMsg(name, s);
			}
			
			System.out.println("Bye..!");
		}
		catch(Exception e) {
			System.out.println("예외1:" + e);
//			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
				socket.close();
				serverSocket.close();
			}
			catch (Exception e) {
				System.out.println("예외2:" + e);
//				e.printStackTrace();
			}
		}
	}
	
	public static void sendAllMsg(String name, String msg) {
		
		try {
			out.println("> " + name + " ==> " + msg);
		}
		catch(Exception e) {
			System.out.println("예외:" + e);
		}
	}
	
	public static void main(String[] args) {
		init();
	}

}
