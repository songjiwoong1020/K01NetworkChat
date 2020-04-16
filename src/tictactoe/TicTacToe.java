package tictactoe;

import java.util.Scanner;


public class TicTacToe {
	
	/**
	 * 채팅과 연동하려면 아마 수정 많이 해야될거같습니다.
	 * 
	 * ticTacToeArr 3*3칸 배열설정.
	 * blank 배열 에 채워줄 공백 변수.
	 * realLast 너무 중구난방으로 짜서 마지막으로 게임 끝 확인해주는 변수.
	 */
	Scanner scanner = new Scanner(System.in);
	String[][] ticTacToeArr = new String[3][3];
	String blank = " ";//처음부터 공백으로하면 보기 불편해서 만들땐 다른 문자를 넣어서 확인하는 용도로만들었습니다.
	boolean realLast = true;
	
	/**
	 * 생성자.
	 * 생성과 동시에 배열값을 멤버변수인 blank로 채워줍니다.
	 */
	public TicTacToe() {
		for(int i=0; i<ticTacToeArr.length; i++) {
			for(int j=0; j<ticTacToeArr[i].length; j++) {
				ticTacToeArr[i][j] = blank;
			}
		}
	}
	
	/**
	 * 이 메소드만 호출하면 게임이 가능하게끔 만들었습니다.
	 */
	public void gameStart() {
		System.out.println("tictactoe게임을 시작합니다^^");
		System.out.println("(입력방식: \"x좌표\", \"y좌표\" ex>1,1 ->왼쪽 최하단)");
		showGame();
		//연동시 플레이어 각각의 이름 받아서 쓸생각.
		System.out.print("첫번째 유저 이름:");
		String userX = scanner.nextLine();
		System.out.print("두번째 유저 이름:");
		String userO = scanner.nextLine();
		
		while(realLast) {
			
			try {
				turn1(userX, "first");
				endGame("X", userX);
				if(realLast) {
					while(true) {
						try {
							turn1(userO, "second");
							endGame("O", userO);
							break;
						}
						catch(Exception e) {
							System.out.println("올바르게 입력해주세요.");
//							e.printStackTrace();
						}
					}
				}
			}
			catch(Exception e) {
				System.out.println("올바르게 입력해주세요.");
//				e.printStackTrace();
			}
		}
		
	}
	
	public void turn1(String player, String turn) {
		System.out.println(player + "님의 차례입니다.");
		String nowPlayer = scanner.nextLine();
		while(!trun2(nowPlayer, turn)) {
			System.out.println(player + "님의 차례입니다.");
			nowPlayer = scanner.nextLine();
		}
	}
	
	public boolean trun2(String player, String turn) {
		int xPoint = Integer.parseInt(player.split(",")[0]);
		int yPoint = Integer.parseInt(player.split(",")[1]);
		
		if(yPoint == 3) yPoint = 1;
		else if(yPoint == 1) yPoint = 3;
		
		if(overlapCheck(xPoint, yPoint)) {
			if(turn == "first") {
				ticTacToeArr[yPoint-1][xPoint-1] = "X";
			} else if(turn == "second") {
				ticTacToeArr[yPoint-1][xPoint-1] = "O";
			}
			return true;
		}else {
			showGame();
			return false;
		}
		
	}
	
	public boolean overlapCheck(int x, int y) {
		if(!(ticTacToeArr[y-1][x-1] == blank)) {
			System.out.println("해당 칸은 이미 차있습니다.");
			return false;
		} else {
			return true;
		}
	}
	
	public void endGame(String ox, String user) {
		
		boolean endCheck = false;
		int endCount1 = 0;// \대각선
		int tieCount = 0;
		for(int i=0; i<ticTacToeArr.length; i++) {
			int endCount2 = 0;// 가로
			int endCount3 = 0;// 세로
			if(ticTacToeArr[i][i].equals(ox)) {
				endCount1++;
//				System.out.println("endCount1(대각선\\) = " + endCount1);
			}
			for(int j=0; j<ticTacToeArr[i].length; j++) {
				if(ticTacToeArr[i][j].equals(ox)) {
					endCount2++;
				}
				if(ticTacToeArr[j][i].equals(ox)) {
					endCount3++;
				}
//				System.out.println("endCount2(가로) = " + endCount2);
//				System.out.println("endCount3(세로) = " + endCount3);
//				System.out.println("--------------");
				if(ticTacToeArr[i][j] != blank) {
					tieCount++;
				}
			}
			if(endCount1 == 3 || endCount2 == 3 || endCount3 == 3
					|| (ticTacToeArr[0][2]==ox&&ticTacToeArr[1][1]==ox&&ticTacToeArr[2][0]==ox)) {
				endCheck = true;
				break;
			}
			
		}
//		System.out.println("tieCount = " + tieCount);
		
		if(endCheck) {
			endShowGame(user);
			realLast = false;
		} else if(tieCount == 9) {
			tieShowGame();
			realLast = false;
		} else {
			showGame();
		}
	}
	
	public void showGame() {
		for(int i=0; i<ticTacToeArr.length; i++) {
			System.out.print("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ\n|");
			for(int j=0; j<ticTacToeArr[i].length; j++) {
				System.out.print(" " + ticTacToeArr[i][j] + " |");
			}
			System.out.println();
		}
		System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
	}
	
	public void endShowGame(String user) {
		System.out.println();
		System.out.println("=" + user + "의 승리^^=");
		for(int i=0; i<ticTacToeArr.length; i++) {
			System.out.print("ㅡ*ㅡ*ㅡ*ㅡ*ㅡ*ㅡ\n‡");
			for(int j=0; j<ticTacToeArr[i].length; j++) {
				System.out.print(" " + ticTacToeArr[i][j] + " ‡");
			}
			System.out.println();
		}
		System.out.println("ㅡ*ㅡ*ㅡ*ㅡ*ㅡ*ㅡ");
	}
	
	public void tieShowGame() {
		System.out.println();
		System.out.println("=무승부^^=");
		for(int i=0; i<ticTacToeArr.length; i++) {
			System.out.print("ㅡ~ㅡ~ㅡ~ㅡ~ㅡ~ㅡ\n§");
			for(int j=0; j<ticTacToeArr[i].length; j++) {
				System.out.print(" " + ticTacToeArr[i][j] + " §");
			}
			System.out.println();
		}
		System.out.println("ㅡ~ㅡ~ㅡ~ㅡ~ㅡ~ㅡ");
	}
}



/**
 * [0][0] -> 1,3 [0][1] -> 2,3 [0][2] -> 3,3
 * [1][0] -> 1,2 [1][1] -> 2,2 [1][2] -> 3,2
 * [2][0] -> 1,1 [2][1] -> 2,1 [2][2] -> 3,1
 * 
*/
