package chat7;

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
import java.util.Scanner;
import java.util.StringTokenizer;

public class MultiServer extends IConnectImpl{
	
	static String fName = null;//귓속말 고정을 위해 만들었는데 잘못 만든듯
	static ServerSocket serverSocket = null;
	static Socket socket = null;
	Map<String, PrintWriter> clientMap;
	StringTokenizer token;
		
	public MultiServer() {
		super("kosmo", "1234");//jdbc 연동을 위해서 추가함
		clientMap = new HashMap<String, PrintWriter>();
		Collections.synchronizedMap(clientMap);
	}
	
	//서버 초기화
	public void init() {
		
		try {
			serverSocket = new ServerSocket(9999);
			System.out.println("서버가 시작되었습니다.");
			
			while(true) {
				socket = serverSocket.accept();
				Thread mst = new MultiServerT(socket);
				mst.start();
			}
		}
		catch(Exception e) {
			System.out.println("예외>Server>init-1:" + e);
		}
		finally {
			try {
				serverSocket.close();
			}
			catch(Exception e) {
				System.out.println("예외>Server>init-2:" + e);
			}
		}
	}
	
	public static void main(String[] args) {
		MultiServer ms = new MultiServer();
		ms.init();
	}
	
	//내부클래스
	class MultiServerT extends Thread{
		
		Socket socket;
		PrintWriter out = null;
		BufferedReader in = null;
		public MultiServerT(Socket socket) {
			this.socket = socket;
			try {
				out = new PrintWriter(this.socket.getOutputStream(), true);
				in = new BufferedReader(
						new InputStreamReader(this.socket.getInputStream(), "UTF-8")
						);
			}
			catch(Exception e) {
				System.out.println("예외>Server>MultiServerT생성자:" + e);
			}
		}
		@Override
		public void run() {
			
			// "";으로 해둔 이유가 있나?
			String name = "";
			String s = "";
			
			try {
				name = in.readLine();
				name = URLDecoder.decode(name, "UTF-8");
				boolean fixCheck = false;//귓속말 고정할때 쓰려고 만든건데 다른 방식으로 할 예정
				
				
				sendAllMsg("", name + "님이 입장하셨습니다.");
				
				clientMap.put(name, out);
				
				System.out.println(name + " 접속");
				System.out.println("현재 접속자 수는 " + clientMap.size() + "명 입니다.");
				
				while(in != null) {
					s = in.readLine();
					s = URLDecoder.decode(s, "UTF-8");
					String[] sSplit = s.split(" ");//혹시 스플릿을 자주 쓰게될지 몰라서 만들었는데 아직은 쓸일이 없어보임
					
					
					if(s==null) break;
					
					//클라이언트로부터 받은 문자가 /로 시작하면 진입. -> /는 명령어로 분류
					if(s.startsWith("/")) {
//						out.println("\n=/입력시 진입=\n");
						System.out.println(name + "(이)가 명령어 실행");// 서버에서 System.out.println()은 서버에서 출력됨.
						//이터레이터와 토큰준비
						token = new StringTokenizer(s);
						Iterator<String> it = clientMap.keySet().iterator();
						
						//클라이언트로부터 받은 문자가 /list와 같다면 진입 간단히 이터레이터로 만들었습니다.
						if(s.equalsIgnoreCase("/list")) {
							System.out.println("list");
							out.println("\n===현재 접속자 목록===\n");
							while(it.hasNext()) {
							
							/*
							위에서 out = new PrintWriter(this.socket.getOutputStream(), true); 이라고 선언되어있어서
							서버에서 out.println()은  PrintWriter를 통해 클라이언트로 전달되기 때문에(MultiServer -> Receiver -> MultiClient)
							해당 클라이언트에서 출력됨
							 */
							out.println("<" + it.next() + ">");
							}
							System.out.println();
						/*
						스플릿은 배열 형태로 저장됨. 지금같은경우 " "을 기준으로 저장
						그래서 " "앞에 첫번째 배열 즉 " "앞에 첫 문자가 /to와 같다면 진입
						 */
						} else if(s.split(" ")[0].equalsIgnoreCase("/to")) {
//							out.println("\n=/to 입력시 진입=\n");
							boolean wCheck = true;
							//토큰의 갯수가 3개이상이면 진입. /to ooo ooo 일 경우에 진입함.
							if(token.countTokens() >= 3 ) {
//								out.println("\n=토큰 수 3개이상일때 진입=\n");
								String wName;
								while(it.hasNext()) {
//									out.println("\n=이터레이터 while문 진입=\n");
									wName = it.next();
//									out.println("\n=wName > " + wName + "=\n");
									/*
									스플릿을 이용해서 클라이언트에게 받은 문자를 3칸으로 나누고 이터레이터를 통해
									스플릿 두번째 칸과 일치하는 이름이 있다면 진입.
									 */
									if(s.split(" ", 3)[1].equals(wName)) {
//										out.println("\n=귓속말 대상 존재시 진입=\n");
//										out.println("\n=wName > " + wName + "=\n");
										//스플릿 3번째 칸부터 클라이언트가 보내는 메시지 내용.
										String ws = s.split(" ", 3)[2];
										System.out.printf("%s가 %s에게 귓속말 >> %s", name, wName, ws);
										whisperMsg(wName, ws, name);
										
										//jdbc
										String query = "INSERT INTO chating_tb VALUES (seq_chating_num.nextval, ?, ?, TO_CHAR(SYSDATE, 'yyyy-mm-dd hh24:mi:ss'))";//시분초 나오게하고싶어서 데이트타입을 못씀
										psmt = con.prepareStatement(query);
										
										psmt.setString(1, name + ">> to" + wName);
										psmt.setString(2, ws);
										
//										int affected = psmt.executeUpdate();
//										System.out.println(affected + "행이 입력되었습니다.");
										
										wCheck = false;
//										out.println("귓속말 대상 :" + wName + "\n귓속말 내용 :" + ws + "\n귓속말 보내는 사람 :" + name);
										break;
									} else {
//										out.println("\n=귓속말 대상 존재 안할시 진입=\n");
//										out.println("\n=wName > " + wName + "=\n");
									}
									
								} 
								//위에서 만든 wCheck로 대상 존재 여부 판단.
								if(wCheck) {
									out.println("대상이 존재하지 않습니다.");
								}
							//토큰이 두개일시 진임. /to ooo 일 경우에만 진입함. 귓속말 고정을 위해 만듬
							//근데 미완성이고 너무 조잡해짐
							} else if(token.countTokens() == 2 )	{
								out.println("고정 진입");
								
								while(it.hasNext()) {
									fName = it.next();
									if(s.split(" ", 2)[1].equals(fName)) {
										wCheck = false;
										out.println(fName + "에게 귓속말 고정을 할까요?\n(고정하시려면 yes, 아니면 아무키나 눌러주세요)");
										String wf = in.readLine();
										if(wf.equalsIgnoreCase("YES")) {
											out.println("고정");
											fixCheck = true;
										} else {
											out.println("귓속말 고정을 취소합니다.");
										}
									}
								}
								if(wCheck) {
									out.println("대상이 존재하지 않습니다.");
								}
							}
							// /to로 진입했지만 설정해준 설정과 다를경우 진입
							else {
								out.println("잘못된 귓속말 형식입니다");
								out.println("/to \"이름\" \"내용\" -> 귓속말 보내기");
								out.println("/to \"이름\" -> 귓속말 보내기 대상에게 고정");
//								out.println(it.next());
//								out.println("\n=토큰 수 3개 미만일시 진입=\n");
							}
						//귓속말 고정을 위해만듬
						} else if(fixCheck && s.equalsIgnoreCase("/unlock")) {
							out.println("귓속말 고정을 풉니다");
							fixCheck = false;
						} else if(!fixCheck && s.equalsIgnoreCase("/unlock")) {
							out.println("귓속말 고정을 상태가 아닙니다.");
						}
						// /로 진입했지만 설정해준 내용과 일치하지 않으면 진입
						else {
							out.println("잘못된 명령어 입니다");
							out.println("명령어 목록");
							out.println("/list -> 현재 접속자 목록");
							out.println("/to \"이름\" \"내용\" -> 귓속말 보내기");
							out.println("/to \"이름\" -> 귓속말 보내기 대상에게 고정");
							out.println("/q or /Q ->종료");
						}
					//이프문은 귓속말 고정을 위해서 만듬. 이프문 빼면 이게 원래 있던 내용.
					} else if(!fixCheck) {
						System.out.println(name + " >> " + s);
						sendAllMsg(name, s);
						
						//jdbc
						String query = "INSERT INTO chating_tb VALUES (seq_chating_num.nextval, ?, ?, TO_CHAR(SYSDATE, 'yyyy-mm-dd hh24:mi:ss'))";//시분초 나오게하고싶어서 데이트타입을 못씀
						psmt = con.prepareStatement(query);
						
						psmt.setString(1, name);
						psmt.setString(2, s);
						
//						int affected = psmt.executeUpdate();
//						System.out.println(affected + "행이 입력되었습니다.");
					} else if(fixCheck) {
						out.println("고정 해제>/unlock");
						out.println(fName);
						System.out.printf("%s가 %s에게 귓속말 >> %s", name, fName, s);
						whisperMsg(fName, s, name);
						
						
					}
					
				}
				
			}
			catch(Exception e) {
				System.out.println("예외>Server>run:" + e); 
			}
			finally {
				clientMap.remove(name);
				sendAllMsg("", name + "님이 퇴장하셨습니다.");
				System.out.println(name + " [" + Thread.currentThread().getName() + "] 퇴장");
				System.out.println("현재 접속자 수는 " + clientMap.size() + "명 입니다.");
				try {
					in.close();
					out.close();
					socket.close();
				}
				catch (Exception e) {
					System.out.println("예외>Server>run-finally:" + e);
				}
			}
		}
		
		/**
		 * 전체 메세지 메소드
		 * name = 메세지 입력 한 사람의 이름
		 * msg = 입력 한 메세지 내용
		 * 
		 * it = 반복자
		 * nameCheck = 본인에게 본인이 보낸 메세지를
		 * 		가지 않게 하기 위한 변수.
		 * it_out = 
		 */
		public void sendAllMsg(String name, String msg) {
			
			Iterator<String> it = clientMap.keySet().iterator();
			String nameCheck;
			while(it.hasNext()) {
				nameCheck = it.next();
				
				if(!name.equals(nameCheck)) {
					try {
						PrintWriter it_out = (PrintWriter)clientMap.get(nameCheck);
						if(name.equals("")) {
							it_out.println(URLEncoder.encode(msg, "UTF-8"));
						} else {
							it_out.println("[" + name + "]:" + msg);
						}
					}
					catch(Exception e) {
						System.out.println("예외>Server>sendAllMsg:" + e);
					}
				}else {
				}
					
					
			}
		}
		
		/**
		 * 귓속말 메소드
		 * sName = 귓속말 보내는 사람의 이름
		 * msg = 입력 한 귓속말 내용
		 * rName = 귓속말 받는 사람의 이름
		 * 
		 * whisper = 
		 */
		public void whisperMsg(String sName, String msg, String rName) {
//			out.println("귓속말 메소드 진입");
			
			try {
				
				PrintWriter whisper = (PrintWriter)clientMap.get(sName);
				
				if(sName.equals(rName)) {
					whisper.println("[본인에게]:" + msg);
				} else {
					whisper.println("[" + rName + "]님의 귓속말:" + msg);
				}
				
			}
			catch(Exception e) {
				System.out.println("예외>Server>whiserMsg:" + e);
			}
		}
	}
}
