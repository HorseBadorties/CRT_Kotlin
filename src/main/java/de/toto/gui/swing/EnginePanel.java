package de.toto.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.toto.engine.EngineListener;
import de.toto.engine.Score;
import de.toto.engine.UCIEngine;
import de.toto.game.Position;

public class EnginePanel extends JPanel implements EngineListener, ChangeListener, ActionListener {
	
	private AppFrame parent;
	private UCIEngine engine;
	private JLabel lblEngineName;
	private JSpinner multiPV;
	private JSpinner threads;
	private JList<String> listBestlines;
	private DefaultListModel<String> bestlines;
	private JButton btnChangeEngine;
	
	private static Preferences prefs = Preferences.userNodeForPackage(AppFrame.class);
	
	public EnginePanel(AppFrame parent, UCIEngine engine) {
		this.parent = parent;
		
		multiPV = new JSpinner(new SpinnerNumberModel(prefs.getInt(AppFrame.PREFS_ENGINE_MULTI_PV, 1), 1, 4, 1));
		multiPV.addChangeListener(this);			
		threads = new JSpinner(new SpinnerNumberModel(prefs.getInt(AppFrame.PREFS_ENGINE_THREADS, 1), 1, 2, 1));
		threads.addChangeListener(this);
		threads.setFocusable(false);
		bestlines = new DefaultListModel<String>();
		bestlines.setSize(4);
		listBestlines = new JList<String>(bestlines);
		btnChangeEngine = new JButton("...");
		btnChangeEngine.addActionListener(this);
		setLayout(new BorderLayout());
		JPanel pnlNorth = new JPanel();
		lblEngineName = new JLabel();
		pnlNorth.add(btnChangeEngine);
		pnlNorth.add(lblEngineName);
		pnlNorth.add(new JLabel("Lines: "));
		pnlNorth.add(multiPV);
		pnlNorth.add(new JLabel("Threads: "));
		pnlNorth.add(threads);
		add(pnlNorth, BorderLayout.PAGE_START);
		add(new JScrollPane(listBestlines), BorderLayout.CENTER);
		setNonFocusable(this);
		setEngine(engine);		
	}
	
	public void setEngine(UCIEngine engine) {
		if (this.engine != null) {
			this.engine.removeEngineListener(this);
		}
		this.engine = engine;
		if (engine != null) {
			engine.setMultiPV(prefs.getInt(AppFrame.PREFS_ENGINE_MULTI_PV, 1));
			engine.setThreadCount(prefs.getInt(AppFrame.PREFS_ENGINE_THREADS, 1));
			engine.addEngineListener(this);
			lblEngineName.setText("<html><b>" + engine.getName() + "      </b></html>");			
		}
		
	}
	
	private static void setNonFocusable(Container c) {
		c.setFocusable(false);
		for (Component child : c.getComponents()) {
			child.setFocusable(false);
			if (child instanceof Container) {
				setNonFocusable((Container)child);
			}
		}
	}

	@Override
	public void newEngineScore(UCIEngine e, Score s) {		
		if (bestlines.size() >= s.multiPV) {
			Position p = parent.getCurrentPosition();
			if (!s.fen.equals(p.getFen())) return;
			String scoreText = null;
			if (s.mate > 0) {
				scoreText = String.format("%d [M%d] %s", 
						s.depth, s.mate, s.bestLine);	
			} else {
				boolean whiteToMove = p.isWhiteToMove();
				boolean positiveScore = (whiteToMove && s.score >= 0) || (!whiteToMove && s.score < 0);	
				scoreText = String.format("%d [%s%.2f] %s", 
						s.depth, positiveScore ? "+" : "-", Math.abs(s.score), s.bestLine);	
			}					
			bestlines.set(s.multiPV - 1, scoreText);
		}
	}

	@Override
	public void engineMoved(UCIEngine e, String fen, String engineMove) {}
	
	@Override
	public void engineStopped(UCIEngine e) {
		bestlines.set(0, "<engine stopped>");
		for (int i = 1; i < bestlines.size(); i++) {
			bestlines.set(i, "");
		}		
	}

	// a spinner changed
	@Override
	public void stateChanged(ChangeEvent e) {
		if (!engine.isStarted()) return;
		if (e.getSource() == multiPV) {
			int newMultiPV = ((SpinnerNumberModel)multiPV.getModel()).getNumber().intValue();
			prefs.putInt(AppFrame.PREFS_ENGINE_MULTI_PV, newMultiPV);
			engine.setMultiPV(newMultiPV);			
			for (int i = newMultiPV; i < bestlines.size(); i++) {
				bestlines.set(i, "");
			}
		} else if (e.getSource() == threads) {	
			int newThreadCount = ((SpinnerNumberModel)threads.getModel()).getNumber().intValue();
			prefs.putInt(AppFrame.PREFS_ENGINE_THREADS, newThreadCount);
			engine.setThreadCount(newThreadCount);
		}		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		parent.changeEngine();
	}
	
	
}
