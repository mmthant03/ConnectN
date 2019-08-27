/**
 * This is interface for the tournament scheme, i.e.,
 * pairing of players.
 * 
 * @author Oleksandr Narykov
 *
 */


package Tournament;

import java.util.List;
import java.util.Map.Entry;

public interface TournamentScheme {

	public List<String> getNextCompetitors();
	public void addPoints(String playerName, int points);
	public List<Entry<String, Integer>> getLeaderboard();
	
}
