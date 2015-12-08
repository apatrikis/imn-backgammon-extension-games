package net.ichmags.backgammon.game.impl;

import java.util.Arrays;
import java.util.List;

import net.ichmags.backgammon.exception.ExitException;
import net.ichmags.backgammon.interaction.ICommand;
import net.ichmags.backgammon.interaction.ICommandProvider;
import net.ichmags.backgammon.interaction.pojo.MoveCommand;
import net.ichmags.backgammon.setup.CheckerColor;
import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.IDicesChoice;
import net.ichmags.backgammon.setup.IPlayer;
import net.ichmags.backgammon.setup.impl.DiceGenerator;
import net.ichmags.backgammon.setup.impl.DicesChoice;
import net.ichmags.backgammon.setup.impl.Player;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GiulTest {
	
	private IPlayer player1;
	private IPlayer player2;
	
	@Before
	public void setUp() {
		player1 = new Player().initialize("Tester 1", IPlayer.ID.ONE, IPlayer.Type.LOCAL, Player.Level.AVERAGE, CheckerColor.WHITE);
		player2 = new Player().initialize("Tester 2", IPlayer.ID.TWO, IPlayer.Type.LOCAL, Player.Level.AVERAGE, CheckerColor.BLACK);
	}
	
	@Test
	public void testDoublingBoostNumberOfRotations() {
		Giul testGiul = new Giul();
		testGiul.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{1, 1});
		testGiul.dices.roll();
		
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices); // not enough rotations: 4
		
		boolean check = testGiul.hasDoublingBoost(testGiul.dices);
		Assert.assertFalse("Not boost: to few rotations", check);
		
		testGiul.getStatistics().addDices(testGiul.dices); // enough rotations: 5
		
		check = testGiul.hasDoublingBoost(testGiul.dices);
		Assert.assertTrue("Boost: enough rotations", check);
	}
	
	@Test
	public void testDoublingBoostStopOneNotPlayable() {
		Giul testGiul = new Giul() {
			protected List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(17, 17, 17);
			};
			
			protected List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(1, 7);
			};
		};
		testGiul.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{1, 1});
		testGiul.dices.roll();
		
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices); // enough rotations: 5
		
		boolean check = testGiul.checkIfAnyMoveIsPossible(player1, testGiul.dices);
		Assert.assertTrue("A move is possible", check);
		
		DicesChoice dicesList = testGiul.findPlayableDices(player1, testGiul.dices);
		Assert.assertTrue("Single dices option", dicesList.isSingleOption());
		IDices playableDices = dicesList.getOption1();
		
		Assert.assertFalse("Not all dices are played", playableDices.allUsed());
		Assert.assertEquals("Used dices mismatch", 1, playableDices.usedCount());
		
		check = testGiul.hasDoublingBoost(playableDices);
		Assert.assertFalse("Not boost: unplayable dice", check);
	}
	
	@Test
	public void testDoublingBoostStopAllNotPlayable() {
		Giul testGiul = new Giul() {
			protected List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(17, 17, 17);
			};
			
			protected List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(1, 6);
			};
		};
		testGiul.initialize(player1, player2, null);
		DiceGenerator.get().load(new int[]{1, 1});
		testGiul.dices.roll();
		
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices); // enough rotations: 5
		
		boolean check = testGiul.checkIfAnyMoveIsPossible(player1, testGiul.dices);
		Assert.assertFalse("No move is possible", check);
		
		check = testGiul.hasDoublingBoost(testGiul.dices);
		Assert.assertFalse("Not boost: no playable dices", check);
	}
	
	@Test
	public void testDoublingBoostNextPlayer()
	throws ExitException {
		Giul testGiul = new Giul() {
			protected List<Integer> getCheckerPositionsPlayer1() {
				return Arrays.asList(17, 17);
			};
			
			protected List<Integer> getCheckerPositionsPlayer2() {
				return Arrays.asList(1, 9);
			};
		};
		testGiul.initialize(player1, player2, new ICommandProvider() {
			
			private int counter;
			
			@Override
			public ICommand getCommand() throws ExitException {
				counter++;
				return new MoveCommand((counter < 3) ? 17 : 18, 1);
			}
			
			@Override
			public IDices chooseDices(IDicesChoice dicesChoice) { return null; }
		});
		DiceGenerator.get().load(new int[]{1, 1});
		
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		testGiul.getStatistics().addDices(testGiul.dices);
		
		boolean check = testGiul.nextMoves(player1);
		Assert.assertTrue("Not finished", check);
		Assert.assertTrue("Double dices", testGiul.dices.isDoubleDices());
		Assert.assertEquals("Dice value mismatch", 2, testGiul.dices.get(3).getValue());
		Assert.assertTrue("All dices used", testGiul.dices.allUsed());
		Assert.assertEquals("Dices blocked", IDice.Status.BLOCKED, testGiul.dices.get(3).getStatus());
	}
}
