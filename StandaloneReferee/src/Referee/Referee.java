package Referee;

import java.io.PrintStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import Players.Player;
import Utilities.Move;
import Utilities.StateTree;

/**
 * This is the referee, it will manage the game and decide
 * if there is a winner or a disqualification.
 * You can study what would cause disqualification and
 * use existing interfaces how you see fit. 
 * 
 * @author Ethan Prihar
 *
 */
public class Referee
{
	private int boardRows; // rows the board has
	private int boardColumns; // columns the board has
	private int winNumber; // how many pieces you need in a row to win
	private int timeLimit; // the time, in seconds, allowed for each player to provide a move
	private Player player1; // you should create your own player class and use it here
	private Player player2;
	private StateTree board;
	Callable<Object> getPlayer1Move;
	Callable<Object> getPlayer2Move;
	private final ExecutorService service = Executors.newSingleThreadExecutor();
	private PrintStream out = null;
	
	public PrintStream getOut() {
		return out;
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}

	/*Referee(int boardRows, int boardColumns, int winNumber, int timeLimit, Player player1, Player player2) {
		this.setBoardRows(boardRows);
		this.setBoardColumns(boardColumns);
		this.setWinNumber(winNumber);
		this.timeLimit = timeLimit;
		this.setPlayer1(player1);
		this.setPlayer2(player2);
	}*/
	public Referee() {
		this.setBoardRows(0);
		this.setBoardColumns(0);
		this.setWinNumber(0);
		this.setTimeLimit(0); 
		this.setPlayer1(null);
		this.setPlayer2(null);
		out = System.out;
	}
	
	public void initMatch(int boardRows, int boardColumns, int winNumber, int timeLimit, Player player1, Player player2) {
		this.setBoardRows(boardRows);
		this.setBoardColumns(boardColumns);
		this.setWinNumber(winNumber);
		this.timeLimit = timeLimit;
		this.setPlayer1(player1);
		this.setPlayer2(player2);		
		
		getPlayer1Move = new Callable<Object>() {
			   public Object call() {
			      return getPlayer1().getMove(board);
			   }
			};
			
		getPlayer2Move = new Callable<Object>() {
			 	public Object call() {
				  return getPlayer2().getMove(board);
			 	}
			};
	}

	
	public int judge() {
		// Make the board and initialize variables
		board = new RefereeBoard(getBoardRows(), getBoardColumns(), getWinNumber(), 1, false, false, null);
		board.setOut(out);
		Move move = null;
		int winner = 0;
		// This while loop runs until there is a winner

		long initialFreeMemory = Runtime.getRuntime().freeMemory();
		out.println(player1.getName());
		out.println(player2.getName());
		
		long startTime = -1;
		long stopTime = -1;
		
		while(winner == 0)
		{
			if(board.turn == 1) // Player 1's turn
			{
				out.println(player1.getName() + "'s turn:");
				Future<Object> future = null;
				try {
					future = service.submit(getPlayer1Move);
					startTime = System.currentTimeMillis();
					move = (Move) future.get(timeLimit, TimeUnit.SECONDS);
				}
				catch (TimeoutException e)
		        {
					future.cancel(true);
					out.println(player1.getName() + " failed with timeout");
					out.println(player2.getName() + " wins!");
					return 2;
		        }
				catch (Exception e) {
					future.cancel(true);
					e.printStackTrace(out);
					out.println(player1.getName() + " failed with exception");
					out.println(player2.getName() + " wins!");
					return 2;
				}
				finally {
					future.cancel(true);
					stopTime = System.currentTimeMillis();
					double timePassed = (double)(stopTime - startTime) / 1000.0;
					out.println(player1.getName() + " took " + timePassed + " seconds to move.");
				}				
				/*System.setOut(new PrintStream(new OutputStream() {
				    @Override public void write(int b) throws IOException {}
				}));*/
				if(!board.validMove(move))
				{
					out.println(player1.getName() + " made an invalid move.");
					out.println(player2.getName() +" wins.");
					return 2;				
				}
				//System.setOut(out);
				String action;
				if(move.getPop())
					action = " popped a piece from column ";
				else
					action = " placed a piece in column ";
				out.println(player1.getName() + action + move.getColumn() + ".");
			}
			else if(board.turn == 2) // Player 2's turn
			{
				out.println(player2.getName() + "'s turn:");
				startTime = System.currentTimeMillis();
				Future<Object> future = null;
				try {
					future = service.submit(getPlayer2Move);
					move = (Move) future.get(timeLimit, TimeUnit.SECONDS);
				}
				catch (TimeoutException e)
		        {
					future.cancel(true);
					out.println(player2.getName() + " failed with timeout");
					out.println(player1.getName() + " wins!");
					return 1;
		        }
				catch (Exception e) {
					future.cancel(true);
					e.printStackTrace(out);
					out.println(player2.getName() + " failed with exception");
					out.println(player1.getName() + " wins!");
					return 1;
				}
				finally {
					future.cancel(true);
					stopTime = System.currentTimeMillis();
					double timePassed = (double)(stopTime - startTime) / 1000.0;
					out.println(player1.getName() + " took " + timePassed + " seconds to move.");
					out.println(player2.getName() + " took " + timePassed + " seconds to move.");
				}
				
				/*System.setOut(new PrintStream(new OutputStream() {
				    @Override public void write(int b) throws IOException {}
				}));*/
				if(!board.validMove(move))
				{
					out.println(player2.getName() + " made an invalid move.");
					out.println(player1.getName() +" wins.");
					return 1;
				}
				//System.setOut(out);
				String action;
				if(move.getPop())
					action = " popped a piece from column ";
				else
					action = " placed a piece in column ";
				out.println(player2.getName() + action + move.getColumn() + ".");
			}
			board.makeMove(move); // Makes the move after checking if it was valid
			board.display(); // Prints the board
			winner = checkForWinner(board); // Checks to see if anybody has won
			long currentFreeMemory = Runtime.getRuntime().freeMemory();
			if (currentFreeMemory < 0.07*initialFreeMemory) {
				out.println("Low memory! Cannot continue fight");
				return 0;
			}
		}
		switch(winner) // Displays appropriate win messages
		{
		case 1:
			out.println(player1.getName() +" wins.");
			return 1;
		case 2:
			out.println(player2.getName() +" wins.");
			return 2;
		}		
		out.println("Tie game.");
		return 0;
	}
	
	
	public static int checkForWinner(StateTree board)
	{
		int points = checkConnect(board); // see how many each player has in a row
		if(points > 0) // if player 1 has more in a row they win
			return 1;
		else if(points < 0) // if player 2 has more in a row they win
			return 2;
		else if(checkFull(board)) // if the board is full than it's a tie
			return 3;
		else // otherwise keep playing
			return 0;

	}
	
	// This counts how many n-in-a-rows each player has
	public static int checkConnect(StateTree board)
	{
		int winner = 0;
		int[] count = new int[4];
		int winTotal = 0;
		for(int i=0; i<board.rows; i++)
		{
			for(int j=0; j<board.columns; j++)
			{
				if(board.getBoardMatrix()[i][j] == 0)
				{
					winner = 0;
					for(int x=0; x<4; x++)
					{
						count[x] = 0;
					}
				}
				else
				{
					winner = board.getBoardMatrix()[i][j];
					for(int x=0; x<board.winNumber; x++)
					{
						if((j+x < board.columns) && (board.getBoardMatrix()[i][j+x] == winner))
							count[0]++;
						else
							count[0] = 0;
						if((i+x < board.rows) && (board.getBoardMatrix()[i+x][j] == winner))
							count[1]++;
						else
							count[1] = 0;
						if((i+x < board.rows) && (j+x < board.columns) && (board.getBoardMatrix()[i+x][j+x] == winner))
							count[2]++;
						else
							count[2] = 0;
						if((i-x >= 0) && (j+x < board.columns) && (board.getBoardMatrix()[i-x][j+x] == winner))
							count[3]++;
						else
							count[3] = 0;
					}
				}
				for(int x=0; x<4; x++)
				{
					if(count[x] == board.winNumber)
					{
						if(winner == 1)
							winTotal++;
						else if(winner == 2)
							winTotal--;
					}
					count[x] = 0;
				}
				winner = 0;
			}
		}
		return winTotal;
	}
	
	public static boolean checkFull(StateTree board)
	{
		for(int i=0; i<board.rows; i++)
		{
			for(int j=0; j<board.columns; j++)
			{
				if(board.getBoardMatrix()[i][j] == 0)
					return false;
			}
		}
		return true;
	}

	public int getBoardRows() {
		return boardRows;
	}

	public void setBoardRows(int boardRows) {
		this.boardRows = boardRows;
	}

	public int getBoardColumns() {
		return boardColumns;
	}

	public void setBoardColumns(int boardColumns) {
		this.boardColumns = boardColumns;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}

	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public int getWinNumber() {
		return winNumber;
	}

	public void setWinNumber(int winNumber) {
		this.winNumber = winNumber;
	}
	public void setTimeLimit(int i) {
		timeLimit = i;
	}
}
