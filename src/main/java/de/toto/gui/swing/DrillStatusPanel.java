package de.toto.gui.swing;

import de.toto.game.Drill;
import de.toto.game.Drill.DrillStats;
import de.toto.game.DrillEvent;
import de.toto.game.DrillListener;
import de.toto.game.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class DrillStatusPanel extends JPanel implements DrillListener {

    private Drill drill;
    private JProgressBar pbPositionCount;
    private JLabel lblLast;
    private AbstractButton btnShowMove;
    private AppFrame frame;

    private Action actionShowMove = new AbstractAction("Show Move") {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (drill != null && !drill.isInDrillHistory()) {
                Position repertoireAnswer = drill.getPosition().getNext();
                if (repertoireAnswer != null) {
                    btnShowMove.setEnabled(false);
                    String[] squareNames = repertoireAnswer.getMoveSquareNames();
                    frame.drawArrow(squareNames[0], squareNames[1], Color.MAGENTA);
                } else {
                    String message = "Repertoire move missing for current position";
                    JOptionPane.showMessageDialog(DrillStatusPanel.this, message, "Repertoire move", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    };

    public DrillStatusPanel(Drill drill, AppFrame frame) {
        super();
        this.frame = frame;
        this.drill = drill;
        drill.addDrillListener(this);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(Box.createRigidArea(new Dimension(0, 5)));
        pbPositionCount = new JProgressBar(JProgressBar.HORIZONTAL, 0, drill.getPositionCount());
        pbPositionCount.setStringPainted(true);
        pbPositionCount.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(pbPositionCount);

        add(Box.createRigidArea(new Dimension(0, 10)));
        lblLast = new JLabel();
        lblLast.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLast.setHorizontalAlignment(JLabel.CENTER);
        add(lblLast);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(Box.createVerticalGlue());
        btnShowMove = AppFrame.createButton(actionShowMove, "Idea", true, false);
        btnShowMove.setVerticalTextPosition(SwingConstants.CENTER);
        btnShowMove.setHorizontalTextPosition(SwingConstants.TRAILING);
        btnShowMove.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(btnShowMove);
        add(Box.createRigidArea(new Dimension(0, 5)));
        setBorder(BorderFactory.createTitledBorder("Drill Status"));
        updateProgress();
    }


    @Override
    public void setFont(Font f) {
        super.setFont(f);
        if (pbPositionCount != null) {
            pbPositionCount.setFont(f);
            lblLast.setFont(f);
            btnShowMove.setFont(f);
            ((javax.swing.border.TitledBorder) getBorder()).setTitleFont(f);
        }
    }

    public void setLast(String s) {
        lblLast.setText(s);
    }

    @Override
    public void drillEnded(DrillEvent e) {

    }

    @Override
    public void wasCorrect(DrillEvent e) {
        updateProgress();
        lblLast.setText("<html><font color=black>" + e.getLastMove() + " was the correct repertoire answer</font></html>");
    }

    @Override
    public void wasIncorrect(DrillEvent e) {
        updateProgress();
        lblLast.setText("<html><font color=red>" + e.getLastMove() + " wasn't the correct repertoire answer</font></html>");
    }

    @Override
    public void drillingNextVariation(DrillEvent e) {

    }

    private void updateProgress() {
        btnShowMove.setEnabled(true);
        DrillStats stats = drill.getDrillStats();
        pbPositionCount.setValue(stats.drilledPositions);
        pbPositionCount.setString(String.format("%d of %d positions drilled", stats.drilledPositions, drill.getPositionCount()));
    }

}
