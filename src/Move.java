import java.util.*;

/**
 * An enumeration of the possible board moves.
 */
public enum Move {
	UP('U'),
	DOWN('D'),
	LEFT('L'),
	RIGHT('R');
	
	public static Map<Character, Move> keys;
	static {
		keys = new HashMap<Character, Move>();
		for (Move m : values()) {
			keys.put(m.key, m);
		}
		keys = Collections.unmodifiableMap(keys);
	}
	
	Character key;
	
	/**
	 * Create a new move.
	 * @param key The output character
	 */
	private Move(Character key) {
		this.key = key;
	}
}
