package chat4;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MultiClient {

	public static void main(String[] args) {
		
		System.out.print("이름을 입력하세요:");
		Scanner scanner = new Scanner(System.in);
		String s_name = scanner.nextLine();
		PrintWriter out = null;
		//서버의 메세지를 읽어오는 기능이 Receiver클래스(쓰레드)로 옮겨짐.
//		BufferedReader in = null;
		
		try {
			String ServerIP = "localhost";
			if(args.length > 0) {
				ServerIP = args[0];
			}
			Socket socket = new Socket(ServerIP, 9999);
			System.out.println("서버와 연결되었습니다...");
			
			Thread receiver = new Receiver(socket);
			receiver.start();
			
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println(s_name);
			
			
			while(out != null) {
				try {
					String s2 = scanner.nextLine();
					if(s2.equalsIgnoreCase("Q")) {
						break;
					} else {
						out.println(s2);
					}
				}
				catch(Exception e) {
					System.out.println("예외:" + e);
				}
			}
			
			out.close();
			socket.close();
		}
		catch(Exception e) {
			System.out.println("예외발생[MultiClient]" + e);
		}
	}
}
