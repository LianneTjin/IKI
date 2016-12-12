/*
 * File: Yahtzee.java
 * ------------------
 *
 * Li-anne Tjin
 *
 * 11269448
 *
 * This program plays the Yahtzee game.
 */

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {
	
	public static void main(String[] args) {
		new Yahtzee().start(args);
	}

	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
	// To make sure the number of players entered isn't higher than four
		while(nPlayers > MAX_PLAYERS) {
			nPlayers = dialog.readInt("Maximum number of players is four!");
		}
	// The array to keep track of the scores of the players
		scores = new Integer[N_CATEGORIES][nPlayers];
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	private void playGame() {
	// To make the game run until each player has filled all the categories
		for(int i = 0; i < N_SCORING_CATEGORIES*nPlayers; i++) {
			rollDice();
			rollAgain();
			selectCategory();
			changePlayer();
		}
		calculateUpperScore();
		checkBonus();
		calculateLowerScore();
		calculateTotal();
		checkWinner();
	}


	/* To roll the dice the first time of a player's turn and generate 
		the random numbers on the dice and display them */

	private void rollDice() {
		display.printMessage(playerNames[player-1] +
			"'s turn! Click 'Roll Dice' button to roll the dice.");
		display.waitForPlayerToClickRoll(player);
		for(int i = 0; i < N_DICE; i++) {
			dice[i] = rgen.nextInt(1,6);
		}
		display.displayDice(dice);
	}

	// To re-roll the dice that have been selected and show their new values

	private void rollAgain() {
		for(int i = 0; i < turns; i++) {
			display.printMessage(
				"Select the dice you wish to re-roll and click 'Roll Again'.");
			display.waitForPlayerToSelectDice();
			changeDice();
			display.displayDice(dice);
		}
	}

	// To change the numbers of the dice that have been selected by the player

	private void changeDice() {
		for(int i = 0; i < N_DICE; i++) {
			if (display.isDieSelected(i)) {
				dice[i] = rgen.nextInt(1,6);
			}
		}
	}

	// To let the player select a category and update the score card

	private void selectCategory() {
		display.printMessage("Select a category for this roll.");
		int category = display.waitForPlayerToSelectCategory();
		int score = 0;
	// To make sure the player selects an empty category
		while(scores[category-1][player-1] != null) {
			display.printMessage("Please select an empty category!");
			category = display.waitForPlayerToSelectCategory();
		}
		numberCounter();
		highestCounter();
		secondHighestCounter();
	// To check if the dice match the choosen category and calculate the score
		switch(category) {
			case ONES:
				score = count[0];
				break;
			case TWOS:
				score = count[1]*2;
				break;
			case THREES:
				score = count[2]*3;
				break;
			case FOURS:
				score = count[3]*4;
				break;
			case FIVES:
				score = count[4]*5;
				break;
			case SIXES:
				score = count[5]*6;
				break;
			case THREE_OF_A_KIND:
				if(highestCount >= 3) {
					for(int i = 0; i < N_DICE; i++) {
						score += dice[i];
					}
				}
				break;
			case FOUR_OF_A_KIND:
				if(highestCount >= 4) {
					for(int i = 0; i < N_DICE; i++) {
						score += dice[i];
					}
				}
				break;
			case FULL_HOUSE:
				if(highestCount == 3 && secondHighestCount == 2) {
					score = 25;
				}
				break;
			case SMALL_STRAIGHT:
				boolean smallSequence1 = count[0] != 0 && count[1] != 0 && 
					count[2] != 0 && count[3] != 0;
				boolean smallSequence2 = count[1] != 0 && count[2] != 0 && 
					count[3] != 0 && count[4] != 0;
				boolean smallSequence3 = count[2] != 0 && count[3] != 0 && 
					count[4] != 0 && count[5] != 0;
				if(smallSequence1 || smallSequence2 || smallSequence3) {
					score = 30;
				}
				break;
			case LARGE_STRAIGHT:
				boolean largeSequence1 = count[0] != 0 && count[1] != 0 && 
					count[2] != 0 && count[3] != 0 && count[4] != 0;
				boolean largeSequence2 = count[1] != 0 && count[2] != 0 && 
					count[3] != 0 && count[4] != 0 && count[5] != 0;
				if(largeSequence1 || largeSequence2) {
					score = 40;
				}
				break;
			case YAHTZEE:
				if(highestCount == 5) {
					score = 50;
				}
				break;
			case CHANCE:
				for(int i = 0; i < N_DICE; i++) {
					score += dice[i];
				}
		}
		scores[category-1][player-1] = score;
		display.updateScorecard(category, player, score);
	}

	// To make an array containing how many times an number occurs on the dice

	private void numberCounter() {
		for(int i = 0; i < count.length; i++) {
			int counter = 0;
			for(int j = 0; j < N_DICE; j++) {
				if(dice[j] == i+1) {
					counter++;
				}
			}
			count[i] = counter;
		}
	}

	// To select the number that occurs the most

	private void highestCounter() {
		highestCount = 0;
		for(int i = 0; i < count.length; i++) {
			if(highestCount < count[i]) {
				highestCount = count[i];
				numberHighestCount = i;
			}
		}
	}

	// To select the number that occurs second most

	private void secondHighestCounter() {
		secondHighestCount = 0;
		for(int i = 0; i < count.length; i++) {
			if(secondHighestCount < count[i] && i != numberHighestCount) {
				secondHighestCount = count[i];
			}
		}
	}

	// To select the next player

	private void changePlayer() {
		if(player < nPlayers) {
			player++;
		} else {
			player = 1;
		}
	}

	// To calculate the upper score for each player

	private void calculateUpperScore() {
		for(int i = 0; i < nPlayers; i++) {
			int upperScore = 0;
			for(int j = 0; j < 6; j++) {
				upperScore += scores[j][i];
			}
			scores[UPPER_SCORE-1][i] = upperScore;
			display.updateScorecard(UPPER_SCORE, i+1, upperScore);
		}
	}

	// To check if the upper score is at least 63 and if so assign an extra bonus

	private void checkBonus() {
		for(int i = 0; i < nPlayers; i++) {
			if(scores[UPPER_SCORE-1][i] >= 63) {
				scores[UPPER_BONUS-1][i] = 35;
			} else {
				scores[UPPER_BONUS-1][i] = 0;
			}
			display.updateScorecard(UPPER_BONUS, i+1, scores[UPPER_BONUS-1][i]);
		}
	}

	// To calculate the lower score of each player

	private void calculateLowerScore() {
		for(int i = 0; i < nPlayers; i++) {
			int lowerScore = 0;
			for(int j = 8; j < 15; j++) {
				lowerScore += scores[j][i];
			}
			scores[LOWER_SCORE-1][i] = lowerScore;
			display.updateScorecard(LOWER_SCORE, i+1, lowerScore);
		}
	}

	// To calculate the total score of each player

	private void calculateTotal() {
		for(int i = 0; i < nPlayers; i++) {
			int totalScore = scores[UPPER_SCORE-1][i] + scores[UPPER_BONUS-1][i]
				+ scores[LOWER_SCORE-1][i];
			scores[TOTAL-1][i] = totalScore;
			display.updateScorecard(TOTAL, i+1, totalScore);
		}
	}

	// To check which player has the hightest score and make them the winner

	private void checkWinner() {
		int winningScore = 0;
		int winner = 0;
		for(int i = 0; i < nPlayers; i++) {
			if(winningScore < scores[TOTAL-1][i]) {
				winningScore = scores[TOTAL-1][i];
				winner = i;
			}
		}
		display.printMessage("Congratulations, " 
			+ playerNames[winner] + ", you're the winner with a total score of " 
			+ winningScore + "!");
	}
		
	// Private instance variables
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();

	// This array keep track of the numbers thrown with the dice
	private int[] dice = new int[N_DICE];
	// This array keeps track of the scores of the players
	private Integer[][] scores;
	// This variable keeps track of the player whos turn it is
	private int player = 1;
	// The amounth of re-rolls a player gets
	private static final int turns = 2;
	// This array keeps track of how many times a number occurs on the dice
	private int[] count = new int[6];
	/* This variable keeps track of how many times the number that occurs 
		most occurs */
	private int highestCount;
	// This variable keeps track of the number that occurs most
	private int numberHighestCount;
	/* This variable keeps track of how many times the number that occurs
		second most occurs */
	private int secondHighestCount;

}
