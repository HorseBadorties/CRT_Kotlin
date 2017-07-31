package de.toto.tts;

import de.toto.game.Position;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.util.data.audio.AudioPlayer;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import javax.sound.sampled.AudioInputStream;
import java.util.Set;

public class MaryTTS implements TextToSpeach {

    static {
        Logger.getRootLogger().getLoggerRepository().resetConfiguration();
        ConsoleAppender console = new ConsoleAppender(); //create appender
        //configure the appender
        String PATTERN = "%d [%p|%c|%C{1}] %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.WARN);
        console.activateOptions();
        Logger.getRootLogger().addAppender(console);
    }

    private MaryInterface marytts;

    public MaryTTS() throws MaryConfigurationException {
        marytts = new LocalMaryInterface();
        try {
            setVoice("dfki-prudence-hsmm");
//        	setVoice("cmu-slt-hsmm");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            TextToSpeach tts = new MaryTTS();
            System.out.println(tts.getAvailableVoices());
            tts.setVoice("dfki-prudence-hsmm");
//			tts.announceChessMove("0-0-0"); Thread.sleep(1000);
//			tts.announceChessMove("0-0-0#"); Thread.sleep(1000);
//			tts.announceChessMove("0-0+"); Thread.sleep(1000);
//			tts.announceChessMove("Nf3"); Thread.sleep(1000);
//			tts.announceChessMove("Nxf3+"); Thread.sleep(1000);
//			tts.announceChessMove("gxh8=R+"); Thread.sleep(1000);
//			tts.announceChessMove("g8=Q#");
            tts.say("<t ph=\"'ba-t@n\">Button</t>");
            Thread.sleep(3000);
            tts.say("A! 1");
            Thread.sleep(3000);
            tts.say("A? 1?");
            Thread.sleep(3000);
            tts.say("A-1");
            Thread.sleep(3000);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /* (non-Javadoc)
     * @see de.toto.tts.TextToSpeach#setVoice(java.lang.String)
	 */
    @Override
    public void setVoice(String voiceName) {
        marytts.setVoice(voiceName);
    }

    //"dfki-prudence-hsmm", "cmu-slt-hsmm"
    /* (non-Javadoc)
	 * @see de.toto.tts.TextToSpeach#getAvailableVoices()
	 */
    @Override
    public Set<String> getAvailableVoices() {
        return marytts.getAvailableVoices();
    }

    /* (non-Javadoc)
	 * @see de.toto.tts.TextToSpeach#say(java.lang.String)
	 */
    @Override
    public void say(String input) throws Exception {
        AudioInputStream audio = marytts.generateAudio(input);
        AudioPlayer ap = new AudioPlayer();
        ap.setAudio(audio);
        ap.start();
        System.out.println("I said: '" + input + "'");
    }
    
    /* (non-Javadoc)
	 * @see de.toto.tts.TextToSpeach#announcePosition(de.toto.game.Position)
	 */

    /* (non-Javadoc)
	 * @see de.toto.tts.TextToSpeach#announceChessMove(java.lang.String)
	 */
    @Override
    public void announceChessMove(String move) {
        try {
            StringBuilder input = new StringBuilder();
            if (move.startsWith("0-0-0")) {
                input.append("long castles");
            } else if (move.startsWith("0-0")) {
                input.append("short castles");
            } else {
                for (int i = 0; i < move.length(); i++) {
                    char c = move.charAt(i);
                    if (i == 0) {
                        if (c >= 'A' && c <= 'Z') {
                            input.append(translatePiece(c)).append(" ");
                        } else {
                            input.append(c).append(" ");
                        }
                    } else if ((c >= 'a' && c <= 'h') || (c >= '1' && c <= '8')) {
                        input.append(c).append(" ");
                    } else if (c == 'x') {
                        input.append("takes ");
                    }
                }
            }
            promotion(move, input);
            checkOrMate(move, input);
            say(input.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void announcePosition(Position p) {
        try {
            say(p.describe());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void promotion(String move, StringBuilder input) {
        if (move.contains("=")) {
            Character promotionPiece = move.charAt(move.indexOf("=") + 1);
            input.append(" promotes to ").append(translatePiece(promotionPiece));
        }
        input.append("!");
    }

    private String translatePiece(Character pieceCharacter) {
        if ('K' == pieceCharacter) {
            return "King";
        } else if ('Q' == pieceCharacter) {
            return "Queen";
        } else if ('R' == pieceCharacter) {
            return "Rook";
        } else if ('B' == pieceCharacter) {
            return "Bishop";
        } else if ('N' == pieceCharacter) {
            return "Knight";
        } else return "Pawn";
    }

    private void checkOrMate(String move, StringBuilder input) {
        if (move.endsWith("#")) {
            input.append(" Mate!");
        } else if (move.endsWith("+")) {
            input.append(" Check.");
        }
    }


}
