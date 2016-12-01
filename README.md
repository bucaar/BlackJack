#Blackjack Protocol


##Client Actions:
| Protocol		|	Description			|
| ------------- |:---------------------:|
| J	[username]	| Join lobby			|
| S [seat]		| Sit at table			|
| B [wager]		| Bet wager				|
| H				| Hit hand				|
| T				| sTay hand				|
| P [wager]		| sPlit cards			|
| D [wager]		| Double down			|
| I [wager]		| Accept insurance		|
| E				| Accept even money		|
| N				| Decline I/E			|
	

##Server Actions:
| Protocol			| Description				|
| ----------------- |:-------------------------:|
| S [seat] [st.];	| list Seat options			|
| T [dollars]		| Place bets				|
| G					| it is your turn to play	|
| I					| Offer Insurance			|
| E					| Offer Even money			|
| D					| The dealer got blackjack	|
| B					| You got blackjack			|
| O					| You busted				|
| W					| Your wager won			|
| P					| Your wager pushed			|
| L					| Your wager lost			|
| Y	[success msg]	| success					|
| N	[failure msg]	| failure					|
