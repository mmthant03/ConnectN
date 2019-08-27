/**
 * This is group scheme for the tournament. It would
 * define pairings of players in ONE vs ALL fashion.
 * It is also responsible for maintaining leaderboard.
 * 
 * @author Oleksandr Narykov
 *
 */

package Tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class GroupScheme implements TournamentScheme {
	HashMap<String, Integer> results;
	ArrayList<ArrayList<String>> tournamentTable;
	int numberOfMatches;
	int counter;
	
	public GroupScheme(List<String> participants) {
		results = new HashMap<String, Integer>();
		for (String participant : participants) {
			results.put(participant, 0);
		}
		
		tournamentTable = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < participants.size()-1; ++i) {
			for (int j = i+1; j < participants.size(); ++j) {
				ArrayList<String> pair = new ArrayList<String>();
				pair.add(participants.get(i));
				pair.add(participants.get(j));
				tournamentTable.add(pair);
			}
		}
		
		numberOfMatches = tournamentTable.size();
	}

	static <K,V extends Comparable<? super V>> 
    List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());

		Collections.sort(sortedEntries, 
				new Comparator<Entry<K,V>>() {
					@Override
					public int compare(Entry<K,V> e1, Entry<K,V> e2) {
						return e2.getValue().compareTo(e1.getValue());
				}
			}
		);

		return sortedEntries;
}
	
	@Override
	public List<String> getNextCompetitors() {
		if(counter >= numberOfMatches){
			System.out.println("All matches were played");
			return null;
		}
		counter += 1;
		return tournamentTable.get(counter - 1);
	}

	@Override
	public void addPoints(String playerName, int points) {
		results.replace(playerName, results.get(playerName) + points);
	}

	@Override
	public List<Entry<String, Integer>> getLeaderboard() {

		return entriesSortedByValues(results);
	}
}
