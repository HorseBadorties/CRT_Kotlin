package de.toto.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.toto.game.Position;
import de.toto.game.Position.GraphicsComment;
import de.toto.game.Square;

public abstract class AbstractDrillPanel extends JPanel {
	
	protected AppFrame appFrame;
	protected List<Square> allSquares = new ArrayList<Square>(64);	
	protected Random random = new Random();
	protected JButton btnFirst, btnSecond;
	protected JTextField textfield;
	protected int counter, correctCounter;	
	protected Board board;
	protected JPanel pnlBoard;
	protected int delay = 1000;
	
	protected Preferences prefs = Preferences.userNodeForPackage(AppFrame.class);
	public static final String PREFS_SHOW_SUCCESS = "SHOW_VISUALIZATION_DRILL_SUCCESS";
	public static final String PREFS_SHOW_ERROR = "SHOW_VISUALIZATION_DRILL_ERROR";
	
	public AbstractDrillPanel(AppFrame appFrame) {
		this.appFrame = appFrame;
		
		
		Square[][] squares8x8 = Square.createEmpty8x8();
		for (int rank = 1; rank <= 8; rank++) {				
			for (int file = 1; file <= 8; file++) {
				allSquares.add(squares8x8[rank - 1][file - 1]);
			}
		}
		btnFirst = new JButton(getFirstAction());
		btnSecond = new JButton(getSecondAction());
		textfield = new JTextField(15);
		textfield.setEditable(false);
		
		JPanel pnlControls = new JPanel();
		pnlControls.add(textfield);
		pnlControls.add(btnFirst);
		pnlControls.add(btnSecond);
		
		board = new Board();
		board.setCurrentPosition(Position.EMPTY_BOARD);		
		board.setShowCoordinates(false);		
		pnlBoard = new JPanel(new BorderLayout());
		pnlBoard.add(board, BorderLayout.CENTER);
		pnlBoard.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		pnlBoard.setPreferredSize(new Dimension(300, 300));
		
		JPanel pnlOptions = new JPanel();
		JCheckBox cbShowBoardAlways = new JCheckBox(actionShowSuccess);
		cbShowBoardAlways.setSelected(prefs.getBoolean(PREFS_SHOW_SUCCESS, true));
		JCheckBox cbShowBoardOnError = new JCheckBox(actionShowError);
		cbShowBoardOnError.setSelected(prefs.getBoolean(PREFS_SHOW_ERROR, true));
		pnlOptions.add(cbShowBoardOnError);
		pnlOptions.add(cbShowBoardAlways);
		
		
		setLayout(new BorderLayout(5,5));		
		add(pnlControls, BorderLayout.PAGE_START);
		add(pnlBoard, BorderLayout.CENTER);
		add(pnlOptions, BorderLayout.PAGE_END);
		
		KeyStroke keyW = KeyStroke.getKeyStroke(getFirstKeyCode(), 0);
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyW, "first");		
		this.getActionMap().put("first",getFirstAction());
		KeyStroke keyB = KeyStroke.getKeyStroke(getSecondKeyCode(), 0);
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyB, "second");		
		this.getActionMap().put("second", getSecondAction());
			
	}
	
	protected Action actionShowSuccess = new AbstractAction("Show Success") {
		@Override
		public void actionPerformed(ActionEvent e) {			
			AppFrame.toggleBooleanPreference(PREFS_SHOW_SUCCESS);			
		}
	};
	
	protected Action actionShowError = new AbstractAction("Show Error") {
		@Override
		public void actionPerformed(ActionEvent e) {			
			AppFrame.toggleBooleanPreference(PREFS_SHOW_ERROR);			
		}
	};
	
	public static String translateForAnnouncement(Square s) {
		StringBuilder result = new StringBuilder();
		switch (s.getFileName().charAt(0)) {
		case 'a': result.append(".A"); break;
		case 'b': result.append("Bee"); break;
		case 'd': result.append("Dee"); break;
		default: result.append(s.getFileName());
		}
		result.append(" ").append(s.rank);
		return result.toString();
	}
	
	public void setText(String text, Color color) {
		textfield.setForeground(color);
		textfield.setText(text);
	}
		
	public void highlightSquares(Color c, Square...squares) {
		board.setShowBoard(true);
		board.setShowText(null);
		board.clearAdditionalGraphicsComment();
		for (Square s : squares) {
			board.addAdditionalGraphicsComment(new GraphicsComment(s, null, c));
		}
		board.repaint();	
		
	}
	
	public void highlightDiagonal(Color c, Square from, Square to, boolean drawArrow) {		
		board.setShowBoard(true);
		board.setShowText(null);
		board.clearAdditionalGraphicsComment();		
		board.addAdditionalGraphicsComment(new GraphicsComment(from, null, c));
		board.addAdditionalGraphicsComment(new GraphicsComment(to, null, c));
		if (drawArrow) {
			board.addAdditionalGraphicsComment(new GraphicsComment(from, to, c));		
		}
		board.repaint();
	}
	
	public void showText(String text) {		
		board.setShowBoard(false);
		board.setShowText(text);	
		board.repaint();	
	}
	
	protected void newRandomSquares() {
		SwingUtilities.invokeLater(
				new SwingWorker<Void, Void>() {
		
					@Override
					protected Void doInBackground() throws Exception {
						Thread.sleep(delay);
						return null;
					}
		
					@Override
					protected void done() {
						doNewRandomSquares();
					}
				});
	}
	
	public abstract int getFirstKeyCode();
	public abstract int getSecondKeyCode();
	public abstract Action getFirstAction();
	public abstract Action getSecondAction();
	public abstract void doNewRandomSquares();
	
}
