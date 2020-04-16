package tictactoe;
/**
게음은 방금 막 만들었습니다,,
귓속말 만들다고 하기 싫어져서 게임이나 만드려고,,
 */
public class TicTacToe {
	
	String[][] ticTacToeArr = new String[3][3];
	
	
	
	public void showGame() {
		
		System.out.println(ticTacToeArr[0][0] + " | " + ticTacToeArr[0][1] + " | " + ticTacToeArr[0][2]);
		System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
		System.out.println(ticTacToeArr[1][0] + " | " + ticTacToeArr[1][1] + " | " + ticTacToeArr[1][2]);
		System.out.println("ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ");
		System.out.println(ticTacToeArr[2][0] + " | " + ticTacToeArr[2][1] + " | " + ticTacToeArr[2][2]);
		
		
	}
	

}
