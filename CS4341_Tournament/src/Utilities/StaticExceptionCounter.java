/**
 * This is class that counts exeptions which
 * occured during player battles in tournament.
 * It is static in it's nature.
 * 
 * 
 * @author Oleksandr Narykov
 *
 */


package Utilities;

import java.util.HashMap;

public class StaticExceptionCounter {
	private static HashMap<String, Integer> exceptions = new HashMap<String, Integer>();
	
	public static void addException(String playerName) {
		Integer n_exceptions = exceptions.get(playerName);
		if (n_exceptions == null) {
			exceptions.put(playerName, 1);
		}
		else {
			exceptions.replace(playerName, n_exceptions, n_exceptions+1);
		}
	}
	
	public static HashMap<String, Integer> getExceptionsStatistics() {
		return exceptions;
	}
}