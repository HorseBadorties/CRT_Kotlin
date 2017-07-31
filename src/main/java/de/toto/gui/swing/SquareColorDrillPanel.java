package de.toto.gui.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.toto.game.Square;

public class SquareColorDrillPanel extends AbstractDrillPanel {
	
	private Square currentSquare;
	private List<Square> squares;
	private Action actionWhite;	
	private Action actionBlack;
	
	
	public SquareColorDrillPanel(AppFrame appFrame) {
		super(appFrame);
		squares = new ArrayList<Square>();
		doNewRandomSquares();		
	}	
	
	@Override
	public void doNewRandomSquares() {		
		if (squares.isEmpty()) {
			squares.addAll(allSquares);
			Collections.shuffle(squares);
		}
		Square newSquare = squares.remove(0);
		currentSquare = newSquare;
//		setText(currentSquare.getName(), Color.BLACK);		
		btnFirst.setEnabled(true);
		btnSecond.setEnabled(true);	
		showText(currentSquare.getName());
		appFrame.announce(translateForAnnouncement(currentSquare));
	}

		
	public void check(boolean white) {
		btnFirst.setEnabled(false);
		btnSecond.setEnabled(false);		
		boolean correct = (white && currentSquare.isWhite()) || (!white && !currentSquare.isWhite()) ;
		if (!correct) {
			Sounds.wrong();
		}
		setText(String.format("%s is %s %s (%d/%d)",
				currentSquare.getName(),
				(correct ? "" : "NOT"),
				(white ? "white" : "black"),				
				(correct ? ++correctCounter : correctCounter),
				++counter), correct ? Color.BLACK : Color.RED);
		
		delay = 100;
		if (correct) {
			if (prefs.getBoolean(PREFS_SHOW_SUCCESS, true)) {
				highlightSquares(Color.GREEN, currentSquare);
				delay = 1000;
			}
		} else if (prefs.getBoolean(PREFS_SHOW_ERROR, true)) {
			highlightSquares(Color.RED, currentSquare);	
			delay = 1000;
		} 
				
		newRandomSquares();
				
	}	

	@Override
	public int getFirstKeyCode() {		
		return KeyEvent.VK_W;
	}


	@Override
	public int getSecondKeyCode() {
		return KeyEvent.VK_B;
	}


	@Override
	public Action getFirstAction() {
		if (actionWhite == null) {
			actionWhite = new AbstractAction("White") {
				@Override
				public void actionPerformed(ActionEvent e) {
					check(true);
				}
			};
		}
		return actionWhite;
	}


	@Override
	public Action getSecondAction() {
		if (actionBlack == null) {
			actionBlack = new AbstractAction("Black") {
				@Override
				public void actionPerformed(ActionEvent e) {
					check(false);
				}
			};
		}
		return actionBlack;
	}

	
}
