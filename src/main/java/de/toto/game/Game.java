package de.toto.game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import de.toto.pgn.PGNReader;

public class Game {
	
	private static Logger log = Logger.getLogger("Game");
	
	private int dbId;
	protected Position currentPosition;
	private Map<String, String> tags = new HashMap<String, String>();	
	
	private List<GameListener> listener = new ArrayList<GameListener>();
	
	public void addGameListener(GameListener l) {
		listener.add(l);
	}
	
	public void removeGameListener(GameListener l) {
		listener.remove(l);
	}
	
	public Game() {
		
	}
	
	public Game(Position startPosition) {
		currentPosition = startPosition;
		firePositionChangedEvent();
	}
	
	
	public Game(Game other) {
		for (Position p : other.getPosition().getLine(other.findStartPosition())) {
			addMove(p.getMove(), p.getFen());			
		};
		if (currentPosition == null) {
			currentPosition = new Position(); // from startposition
		}
		firePositionChangedEvent();
	}
	
	protected void firePositionChangedEvent() {
		GameEvent e = new GameEvent(this);
		for (GameListener l : listener) {
			l.positionChanged(e);
		}
	}
	
	public void start() {
		currentPosition = new Position();
		firePositionChangedEvent();
	}
	
	/**
	 * Starts a new variation for the last move
	 * 
	 */
	public Position newVariation(String move) {
		currentPosition = new Position(currentPosition.getPrevious(), move, null, true);		
		return currentPosition;
	}
	
	/**
	 * Go back to last move of parent variation 
	 */
	public Position endVariation() {
		int variationLevel = currentPosition.getVariationLevel();
		do {
			goBack();
		} while (variationLevel == currentPosition.getVariationLevel());
		goForward();
		return currentPosition;
	}
	
	public Position addMove(String move) {
		return addMove(move, null);
	}
		
	public Position addMove(String move, String fen) {
		currentPosition = new Position(currentPosition, move, fen);
		firePositionChangedEvent();
		return currentPosition;
	}
	
	public Position addMoves(List<String> moves) {
		return addMoves(moves.toArray(new String[0]));		
	}
	
	public Position addMoves(String[] moves) {
		for (String move : moves) {
			addMove(move);
		}
		return currentPosition;
	}
	
	public Position getPosition() {
		return currentPosition;
	}
	
	
	private Position findStartPosition() {
		Position p = currentPosition;
		while (p.hasPrevious()) {
			p = p.getPrevious();
		}
		return p;
	}
	
	public Position gotoStartPosition() {
		currentPosition = findStartPosition();
		firePositionChangedEvent();
		return currentPosition;
	}
	
	public Position gotoPosition(Position p) {		
		currentPosition = p;
		firePositionChangedEvent();
		return currentPosition;
	}
	
	/**
	 * Go forward one move, following the main line 
	 */
	public void goForward() {
		if (!currentPosition.hasNext()) return;
		currentPosition = currentPosition.getNext();
		firePositionChangedEvent();	
	}
	
	public boolean hasNext() {
		return currentPosition.hasNext();
	}
		
	public void goBack() {
		if (currentPosition.hasPrevious()) {
			currentPosition = currentPosition.getPrevious();
			firePositionChangedEvent();
		}
	}
	
	public boolean hasPrevious() {
		return currentPosition.hasPrevious();
	}
		
	public void addTag(String name, String value) {
		tags.put(name, value);
	}
	
	public String getTagValue(String tagName) {
		return tags.get(tagName);
	}
	
	public Set<Position> getAllPositions(Position startPosition) {
		Set<Position> result = new HashSet<Position>();
		Position p = startPosition;		
		p = findNextPosition(p, startPosition);
		while (p != null) {
			result.add(p);
			p = findNextPosition(p, startPosition);
		}		
		return result;
	}
	
	public Set<Position> getAllPositions() {		
		return getAllPositions(gotoStartPosition());
	}
	
	
	
	public Position doMove(String move) {
		if (getPosition().hasNext()) {
			for (Position p : getPosition().getVariations()) {				
				if (p.getMove().startsWith(move)) {
					currentPosition = p;
					firePositionChangedEvent();
					return currentPosition;
				}
			}
		}
		return null;
	}
	
	public boolean isCorrectMove(String move) {
		if (getPosition().hasNext()) {			
			for (Position p : getPosition().getVariations()) {
				if (move.equals("0-0")) {
					if (p.getMove().startsWith("0-0") && !p.getMove().startsWith("0-0-0")) {
						return true;
					}
				} else {
					if (p.getMove().startsWith(move)) {
						return true;
					}				
				}
			}
		}		
		return false;
	}
	
	private Position findNextPosition(Position p, Position startPosition) {		
		if (p.hasVariations()) {
			// enter first variation 
			return p.getVariations().get(1);
		} else if (p.hasNext()) {
			return p.getNext();
		} else {
			if (p.getVariationLevel() == 0) {
				// end of game
				return null;
			} else {
				// end of variation - go back to start of variation and look for the next
				Position headOfVariation = p;
				Position previous = p.getPrevious();
				while (previous.getVariationLevel() != p.getVariationLevel()-1) {
					if (previous.getVariationLevel() == p.getVariationLevel()) headOfVariation = previous;
					previous = previous.getPrevious();
					if (previous.hasVariation(startPosition)) return null;
				}				
				// now look for the next variation
				List<Position> variations = previous.getVariations();
				int indexOfHeadOfVariation = variations.indexOf(headOfVariation);
				if (indexOfHeadOfVariation < variations.size()-1) {
					return variations.get(indexOfHeadOfVariation+1);
				} else {
					return previous.getNext();
				}				
			}			 
		}
	}
	
	public Set<Position> getVariationEndpoints(Position startPosition) {
		Set<Position> result = getAllPositions(startPosition);
		Iterator<Position> it = result.iterator();
		while (it.hasNext()) {			
			Position p = it.next();
			if (p.hasNext()) {
				it.remove();
			}
		}		
		return result;
	}
	
	
	public void mergeIn(Game other) {
		other.gotoStartPosition(); 
		gotoStartPosition();
		
		Position first = getPosition();
		Position second = other.getPosition();
		
		while (second.hasNext()) {
			second = second.getNext();
			if (second == null) break;
			
			if (!first.hasVariation(second)) {				
				for (Position variation : second.getPrevious().getVariations()) {
					if (!first.hasVariation(variation)) {
						first.addVariation(variation);
						variation.setComment(other.toString());
						log.info(String.format("merged %s as variation of %s", variation, first));
					}
				}								
				break;
			} else {
				first = first.getVariation(second);
			}				
		}
	}
	
	public void merge() {
		List<Position>  allPositions = new ArrayList<Position>(getAllPositions()); 		
		for (int i = 0; i < allPositions.size(); i++) {
			int indexOfSame = findSame(allPositions.get(i), allPositions, i+1);
			if (indexOfSame >= 0) {
				System.out.println(String.format("found %s at %d and %d", allPositions.get(i).getFen(), i, indexOfSame));
				Position current = allPositions.get(i);
				Position same = allPositions.get(indexOfSame);
				for (Position childrenOfSame : same.getVariations()) {
					childrenOfSame.setPrevious(current);
					//current.addVariation(childrenOfSame);
				}
				same.getPrevious().removeNextPosition(same);
				allPositions.remove(indexOfSame);				
			}
		}		
	}
	
	private int findSame(Position p, List<Position> positions, int index) {
		for (int i = index; i < positions.size(); i++) {
			if (p.isSamePositionAs(positions.get(i))) return i;
		}
		return -1;
	}
	
	@Override
	public String toString() {
		String eloWhite = getTagValue("WhiteElo");
		String eloBlack = getTagValue("BlackElo");
		String result = getTagValue("Result");
		return String.format("%s %s - %s %s %s - %s", 
				getTagValue("White"),
				StringUtils.isEmpty(eloWhite) ? "" : eloWhite,
				getTagValue("Black"),
				StringUtils.isEmpty(eloBlack) ? "" : eloBlack,
				StringUtils.isEmpty(result) ? "" : ": "  + result,
				getTagValue("Event")); 
	}
	
	public Position findNovelty1(Game other) {
		Position currentPositionBackup = currentPosition;
		try {
			other.gotoStartPosition(); 
			gotoStartPosition();
			
			Position first = getPosition();
			Position second = other.getPosition();
			
			for (;;) {
				if (!second.hasNext()) return null;
				second = second.getNext();
				if (second == null) return null;
				if (!first.hasVariation(second)) {					
					log.info(String.format("findNovelty in %s: %s", other, second));
					return second;					
				} else {
					first = first.getVariation(second);
				}				
			}
		} finally {
			currentPosition = currentPositionBackup;
		}
	}
	
	public Position findNovelty(Game other) {
		Position otherPosition = other.gotoStartPosition();
		
		//go to other's last Position 
		while (otherPosition.hasNext()) {
			otherPosition = otherPosition.getNext();
		}
		
		//find first match 
		while (otherPosition != null) {
			if (this.contains(otherPosition)) break;
			otherPosition = otherPosition.getPrevious();
		}
		
		//novelty is the next move after our match
		Position novelty = null;
		if (otherPosition == null) {
			novelty = other.gotoStartPosition();			
		} else if (otherPosition.hasNext()) {
			novelty = otherPosition.getNext();
		} else {
			novelty = otherPosition;
		}
		log.info(String.format("findNovelty in %s: %s", other, novelty));
		return novelty;
		
	}
		
	/**
	 * 
	 * @return all Positions with a "#REP" comment which are child positions of the given position
	 */
	public Set<Position> getRepertoirePositions(Position startPosition) {
		Set<Position> repertoirePositions = new HashSet<Position>();
		if (isRepertoirePosition(startPosition)) {
			repertoirePositions.add(startPosition);
		}
		for (Position p : getAllPositions(startPosition)) {
			if (isRepertoirePosition(p)) {
				repertoirePositions.add(p);
			}
		}
		return repertoirePositions;
	}
	
	private boolean isRepertoirePosition(Position p) {
		return p.getComment() != null && p.getComment().contains("#REP");
	}
	
	public boolean isRelevant(Game aGame, Position... relevantPositions) {		
		return aGame.contains(relevantPositions);				
	}
	
	public boolean contains(Position... otherPositions) {
		for (Position p : getAllPositions()) {
			for (Position o : otherPositions) {
				if (p.isSamePositionAs(o)) return true;
			}
		}
		return false;
	}
	
	public String getUCIEngineMoves() {
		StringBuilder result = new StringBuilder();
		Position position = findStartPosition();
		while (position.hasNext()) {
			position = position.getNext();
			result.append(position.getMoveAsEngineMove()).append(" ");			
		}
		return result.toString();
	}
	
	public String getUCIStartFEN() {
		Position startPosition = findStartPosition();
		if (startPosition.isStartPosition()) {
			return null;
		} else {
			return startPosition.getFen();
		}
	}
	
	public String toPGN() {
		StringBuilder result = new StringBuilder();
		
		result.append(tagsToPGN());
		
		Position p = findStartPosition();
		if (p.isStartPosition() && p.hasNext()) {
			p = p.getNext();
		}
		while (p != null) {
			if (p.whiteMoved()) result.append(p.getMoveNumber()).append(". ");
			result.append(p.getMoveAsSan()).append(" ");
			if (!StringUtils.isBlank(p.getComment())) {
				result.append("{").append(p.getComment()).append("} ");
			}
			if (p.hasNext()) {
				p = p.getNext();
			} else {
				p = null;
			}
		}
		result.append(getTagValue("Result")).append("\n");
		return result.toString();
	}
	
	private String tagsToPGN() {
		StringBuilder result = new StringBuilder();
		Map<String, String> ourTags = new HashMap<String, String>(tags);
		String value = ourTags.remove("Event");
		appendTag(result, "Event", StringUtils.isBlank(value) ? "*" : value);
		value = ourTags.remove("Date");
		appendTag(result, "Date", StringUtils.isBlank(value) ? PGNReader.toPGNTimestamp(System.currentTimeMillis()) : value);
		value = ourTags.remove("White");
		appendTag(result, "White", StringUtils.isBlank(value) ? "*" : value);
		value = ourTags.remove("Black");
		appendTag(result, "Black", StringUtils.isBlank(value) ? "*" : value);
		value = ourTags.remove("Result");
		appendTag(result, "Result", StringUtils.isBlank(value) ? "*" : value);
		value = ourTags.remove("WhiteElo");
		appendTag(result, "WhiteElo", StringUtils.isBlank(value) ? "*" : value);
		value = ourTags.remove("BlackElo");
		appendTag(result, "BlackElo", StringUtils.isBlank(value) ? "*" : value);
		
		for (Entry<String, String> tag : ourTags.entrySet()) {
			appendTag(result, tag.getKey(), tag.getValue());
		}
		if (result.length() > 0) {
			result.append("\n");
		}
		return result.toString();
	}
	
	private void appendTag(StringBuilder pgn, String name, String value) {
		pgn.append("[").append(name).append(" \"").append(value).append("\"]\n");
	}

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
	
	public static void saveToFile(File file, boolean append, Game...games) {
		FileWriter writer = null;
		try {		
			
			writer = new FileWriter(file, append);
			System.out.println("saving to " + file);				
			for (Game g : games) {
				writer.write(g.toPGN() + "\n");
			}
			writer.flush();		
		} catch (IOException ioEx) {
			throw new RuntimeException(ioEx);		
		} finally {						
			try {
				if (writer != null) writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public static void saveToFile(File file, boolean append, List<Game> games) {
		saveToFile(file, append, games.toArray(new Game[0]));		
	}
		
	
	
}
