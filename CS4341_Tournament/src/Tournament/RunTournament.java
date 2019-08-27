/**
 * This is main class of the program. It is used to
 * execute tournament.
 * 
 * @author Oleksandr Narykov
 *
 */

package Tournament;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

public class RunTournament {
	public static void main(String[] args) {
		try {
			Tournament tournament = new Tournament();
			PrintStream out = null;
			try {
				out = new PrintStream(
					     new FileOutputStream("results/tournament.txt", true));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			tournament.setOut(out);
			tournament.run();
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}