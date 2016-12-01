#Plans for Blackjack


##Client Actions:
| Protocol		|	Description			|
| ------------- | --------------------- |
| J				| Join lobby			|
| S <seat>		| Sit at table			|
| B <wager>		| Bet wager				|
| H				| Hit hand				|
| T				| sTay hand				|
| P <wager>		| sPlit cards			|
| D <wager>		| Double down			|
| I <wager>		| Accept insurance		|
| E				| Accept even money		|
| N				| Decline I/E			|
	

##Server Actions:
| Protocol		| Description				|
| ------------- | ------------------------- |
| S				| list Seat options			|
| G				| it is your turn to play	|
| A				| The dealer is showing ace |
| E				| Offer Even money			|
| D				| The dealer got blackjack	|
| B				| You got blackjack			|
| O				| You busted				|
| W				| Your wager won			|
| P				| Your wager pushed			|
| L				| Your wager lost			|
| Y				| success					|
| N				| failure					|