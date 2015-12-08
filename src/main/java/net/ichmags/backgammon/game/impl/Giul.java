/*
 * www.ichmags.net - Backgammon
 */
package net.ichmags.backgammon.game.impl;

import net.ichmags.backgammon.game.IGame;
import net.ichmags.backgammon.interaction.ICommandProvider;
import net.ichmags.backgammon.setup.IDice;
import net.ichmags.backgammon.setup.IDices;
import net.ichmags.backgammon.setup.IPlayer;

/**
 * {@code Giul} is a {@link Fevga} variation with the following special feature:
 * <ul>
 * <li>after the second turn if a {@link IPlayer} has double {@link IDices} and can play all moves, he can
 * play the next higher dices as well, e. g. if a player has double 3 and can move four times, he can
 * play double 4, and so on. See {@link IGame#hasDoublingBoost(IDices)}.
 * </ul>
 * 
 * @author Anastasios Patrikis
 */
public class Giul extends Fevga {

	/**
	 * Default constructor.
	 * 
	 * <b>Call {@link #initialize(IPlayer, IPlayer, ICommandProvider)} to make the instance usable.</b>
	 */
	public Giul() {
		// noting to do
	}
	
	@Override
	public String getName() {
		return "Giul";
	}
	
	@Override
	public boolean hasDoublingBoost(IDices dices) {
		// only after the 3rd time of a player's turn
		if(this.getStatistics().getRotations() < 5) {
			return false;
		}
		
		if((dices.isDoubleDices() == false) || (dices.get(0).getValue() == 6)) {
			return false; // must be double and less then 6, else no doubling is possible
		} else {
			for(IDice dice : dices.get()) {
				if(IDice.Status.BLOCKED.equals(dice.getStatus())) {
					return false; // if a dice is blocked it could no be used for a move
				}
			}
		}
		
		return true;
	}
}
