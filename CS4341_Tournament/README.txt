#
#	Author: Oleksandr Narykov
#

To run CS4341_Tournament for Connect-N players you may need third-party 
libraries that could be found in CS4341_Tournament/lib. Just add them to
your build path.

To run your tournament just  put all players into the "Players" package.
Program would scan this package and add all found players to the tourna-
ment. Player name in tournament is it's class name.
"Player" class is a base class for all players, it have to remain in
"Players" package. "SimplePlayer" may serve as a baseline during your
tournament.
"SimplePlayer1" and "SimplePlayer2" are for demonstration purpose only
and can be removed.
ATTENTION: all players should be extended from "Player" class.

You can find parameters for the battle between two players at the top of
"Tournament" class. Modify them as you see fit.

Records of each battle between Player1 and Player2 would go into the "log"
folder under the name "Player1_vs_Player2_log.txt".

In "results" folder you would find summary of the tournament. First, you would
see list of the participants. Second, there would be records of the outcome of
individual battles: 1 - first player wins, 2 - second player wins, 0 - tie.
Third, there would be a leaderboard with the final scores.

You can use cleanup.sh to delete all results.