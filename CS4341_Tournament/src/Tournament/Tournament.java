/**
 * This is tournament class which is responsible
 * for the overall execution of tournament, including
 * loading player classes, configuring individual
 * battles and obtaining results.
 * 
 * @author Oleksandr Narykov
 *
 */

package Tournament;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import Players.Player;
import Referee.Referee;
import Utilities.ClassFinder;
import Utilities.StaticExceptionCounter;

public class Tournament {
	// Match parameter
	// You can modify them
	private int battlesLimit = 8;
	private int battlesToDecreaseTimeLimit = 20;
	private int timesDecreased = 0;
	private int timeLimit = 10;
	private int boardRows = 6;
	private int boardColumns = 7;
	private int winNumber = 4;
	private int numberOfChangingParams = 3;
	private int battleDurationLimit = 3600;
	// End of modifications
	private PrintStream out;
	
	public PrintStream getOut() {
		return out;
	}

	public void setOut(PrintStream out) {
		this.out = out;
	}

	private List<String> getPlayers() {
		 List<Class<?>> allClasses = 
				 ClassFinder.find("Players");
		 LinkedList<String> players = new LinkedList<String>();			 
		 
		 for(Class<? extends Object> klazz : allClasses) {
			 String name = klazz.getName().split("\\.")[1];
			 
			 if (klazz != Player.class && Player.class.isAssignableFrom(klazz)) {
				 players.add(name);
			 }
			 
		/*	 if(!contains(name) && !name.contains("$")
				&& !name.contains("Tree")
				&& !name.contains("MiniMax")
				&& !name.contains("Board")
				&& !name.contains("Heuristic")
				&& !name.contains("Eval")
				&& !name.contains("Utility")
				&& !name.contains("Action")) {
				 
			 }*/
		 }
		 
		 return players;
	}
	
	private int doBattle(String playerName1, String playerName2) 
								throws InstantiationException, 
								IllegalAccessException, IllegalArgumentException, 
								InvocationTargetException, ClassNotFoundException, 
								NoSuchMethodException, SecurityException {

		int initialBoardRows = boardRows;
		int initialBoardColumns = boardColumns;
		int initialWinNumber = winNumber;
		int initialTimeLimit = timeLimit;
		String playersPackage = "Players.";
		
		boolean dominating = false;
		int numberOfBattles = 0;
		int finalResult = 0;
		
		Class<?> clazz1 = Class.forName(playersPackage + playerName1);
		Constructor<?> constructor1 = clazz1.getConstructor(String.class, Integer.TYPE, Integer.TYPE);
		Class<?> clazz2 = Class.forName(playersPackage + playerName2);
		
		//System.out.println(playerName1);
		//System.out.println(playerName2);
		
		Constructor<?> constructor2 = clazz2.getConstructor(String.class, Integer.TYPE, Integer.TYPE);
		Referee referee1 = new Referee();
		Referee referee2 = new Referee();
		PrintStream refereeOut = null;
		try {
			refereeOut = new PrintStream(
				     new FileOutputStream("log/" + playerName1 + "_vs_" + playerName2 + "_log.txt", true));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		referee1.setOut(refereeOut);
		referee2.setOut(refereeOut);
		
		while ((!dominating) && (numberOfBattles < battlesLimit)){
			
			referee1.setWinNumber(winNumber); //CRUTCH!!!
			referee2.setWinNumber(winNumber); //CRUTCH!!!
			Player player11 = (Player) constructor1.newInstance(playerName1, 1, timeLimit);
			Player player12 = (Player) constructor1.newInstance(playerName1, 2, timeLimit);
			
			Player player21 = (Player) constructor2.newInstance(playerName2, 1, timeLimit);
			Player player22 = (Player) constructor2.newInstance(playerName2, 2, timeLimit);

			referee1.initMatch(boardRows, boardColumns, winNumber, timeLimit, player11, player22);
			Callable<Object> judge1 = new Callable<Object>() {
			 	public Object call() {
				  return referee1.judge();
			 	}
			};			
			ExecutorService service = Executors.newSingleThreadExecutor();
			
			final Future<Object> future1 = service.submit(judge1);
			int result1 = -1;
			try {
				result1 = (int) future1.get(battleDurationLimit, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
			judge1 = null;
			
			referee2.initMatch(boardRows, boardColumns, winNumber, timeLimit, player21, player12);
			Callable<Object> judge2 = new Callable<Object>() {
			 	public Object call() {
				  return referee2.judge();
			 	}
			};
			//out.print("| " + result1 + " ");
			final Future<Object> future2 = service.submit(judge2);
			int result2 = -1;
			try {
				result2 = (int) future2.get(battleDurationLimit, TimeUnit.SECONDS);
			//	out.print(" " + result2 + " |");
				if (result2 == 1) {
					result2 = 2;
				}
				else if (result2 == 2){
					result2 = 1;
				}
					
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}			
			judge2 = null;
			
			if ((result1 == -1) || (result2 == -1)) {
				return 0;
			}
			
			if ((result1 == result2) &&  (result1 != 0)) {
				dominating = true;
				finalResult = result1;
			}
			
			if ((result1 == 0) && (result2 != 0)) {
				dominating = true;
				finalResult = result2;
			}
			
			if ((result1 != 0) && (result2 == 0)) {
				dominating = true;
				finalResult = result1;
			}
			
			
			switch (numberOfBattles % numberOfChangingParams){
				case 0:
					boardRows += 2;
				case 1:
					boardColumns += 2;
				case 2:
					if(winNumber < Math.min(boardRows, boardColumns)){
						winNumber += 1;
					}
			}
			if ((numberOfBattles/battlesToDecreaseTimeLimit > timesDecreased) && (timeLimit > 1)) {
				timeLimit -= 1;
			}
			numberOfBattles += 1;
		}
		
		refereeOut.println();
		refereeOut.println("-----");
		refereeOut.println();
		refereeOut.close();
		
		boardRows = initialBoardRows;
		boardColumns = initialBoardColumns;
		winNumber = initialWinNumber;
		timeLimit = initialTimeLimit;
		
		return finalResult;
	}
	
	public void run() throws ClassNotFoundException, NoSuchMethodException, 
								SecurityException, InstantiationException, 
								IllegalAccessException, IllegalArgumentException, 
								InvocationTargetException {
		LinkedList<String> playerNames = (LinkedList<String>) getPlayers();
		for (String playerName : playerNames) {
			out.println(playerName);
		}
		TournamentScheme scheme = new GroupScheme(playerNames);
		List<String> nextMatch = null;
		while ((nextMatch = scheme.getNextCompetitors()) != null) {
			String player1 = nextMatch.get(0);
			String player2 = nextMatch.get(1);
			out.print("Current battle: " + player1 + " vs " + player2);
			PrintStream sysOut = System.out;
			System.setOut(new PrintStream(new OutputStream() {
				@Override public void write(int b) throws IOException {}
			}));
			int result = doBattle(player1, player2);
			switch (result) {
				case 1:	
					scheme.addPoints(player1, 3);
					scheme.addPoints(player2, 0);
					break;
				case 2:
					scheme.addPoints(player1, 0);
					scheme.addPoints(player2, 3);
					break;
				case 0:
					scheme.addPoints(player1, 1);
					scheme.addPoints(player2, 1);
					break;
			}
			System.setOut(sysOut);
			out.println("	| " + Integer.toString(result));
		}
		
		for(Map.Entry<String, Integer> entry : scheme.getLeaderboard()){
			StringBuilder sb = new StringBuilder();
			Formatter formatter = new Formatter(sb, Locale.US);
			formatter.format("%s : %d", entry.getKey(), entry.getValue());
			out.println(formatter.toString());
			formatter.close();
		}
		
		PrintStream exceptionsOut = null; 
		try {
			exceptionsOut = new PrintStream(
				     new FileOutputStream("log/exceptions_log.txt", true));
			} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		HashMap<String, Integer> statistics = StaticExceptionCounter.getExceptionsStatistics();
		for(Map.Entry<String, Integer> entry : statistics.entrySet()){
			StringBuilder sb = new StringBuilder();
			Formatter formatter = new Formatter(sb, Locale.US);
			formatter.format("%s : %d", entry.getKey(), entry.getValue());
			exceptionsOut.println(formatter.toString());
			formatter.close();
		}
	}
}