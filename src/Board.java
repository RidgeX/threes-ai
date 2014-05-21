import java.util.*;

/**
 * A class that represents the state of a board.
 */
public class Board {
	int[][] values;
	List<Integer> tiles;
	int tileIndex;
	boolean isGameOver;
	
	/**
	 * Create a new empty board.
	 */
	public Board() {
		values = new int[4][4];
		tiles = new ArrayList<Integer>();
		tileIndex = 0;
		isGameOver = false;
	}
	
	/**
	 * Create a copy of an existing board.
	 * @param b The board to be copied
	 */
	public Board(Board b) {
		values = new int[4][4];
		for (int y = 0; y < 4; y++) {
			System.arraycopy(b.values[y], 0, values[y], 0, 4);
		}
		tiles = b.tiles;
		tileIndex = b.tileIndex;
		isGameOver = false;
	}
	
	/**
	 * Returns the total score of the board.
	 * @return The sum of the individual tile scores
	 */
	public int getScore() {
		int score = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				int v = values[y][x];
				if (v == 1 || v == 2) {
					score++;
				} else if (v > 2) {
					score += Math.pow(3, log2(v / 3) + 1);
				}
			}
		}
		return score;
	}
	
	/**
	 * Returns a heuristic penalty applied to the DFS scores.
	 * @return The sum of the individual tile penalties
	 */
	public int getPenalty() {
		int score = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				int v = values[y][x];
				if (v > 0) {
					// Examine neighbouring values
					int vu = (y > 0 ? values[y-1][x] : 0);
					int vd = (y < 3 ? values[y+1][x] : 0);
					int vl = (x > 0 ? values[y][x-1] : 0);
					int vr = (x < 3 ? values[y][x+1] : 0);
					
					// Add points for any pairs of tiles that can be merged
					if ((v == 1 && vu == 2) || (v == 2 && vu == 1) || (v > 2 && v == vu)) score += 1;
					if ((v == 1 && vd == 2) || (v == 2 && vd == 1) || (v > 2 && v == vd)) score += 1;
					if ((v == 1 && vl == 2) || (v == 2 && vl == 1) || (v > 2 && v == vl)) score += 1;
					if ((v == 1 && vr == 2) || (v == 2 && vr == 1) || (v > 2 && v == vr)) score += 1;
					
					// Penalise low-value tiles for being trapped between:
					//  - Two high-value tiles
					//  - A high-value tile and an edge
					if ((x == 0 || (vl > 2 && v < vl)) && (x == 3 || (vr > 2 && v < vr))) score -= 5;
					if ((y == 0 || (vu > 2 && v < vu)) && (y == 3 || (vd > 2 && v < vd))) score -= 5;
					
					// Bonus points if any adjacent tile is twice in value
					if (v > 2 && (v*2 == vu || v*2 == vd || v*2 == vl || v*2 == vr)) score += 1;
				} else {
					// Add points for empty spaces
					score += 2;
				}
			}
		}
		return score;
	}
	
	/**
	 * Returns the base-2 log of the given value.
	 * @param v The specified value
	 * @return The value expressed as a power of two
	 */
	private int log2(int v) {
		if (v <= 0) {
			throw new AssertionError("Positive value expected");
		}
		return 31 - Integer.numberOfLeadingZeros(v);
	}
	
	/**
	 * Returns a string representation of the board.
	 * @return The board values
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (x > 0) sb.append(" ");
				sb.append(values[y][x]);
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}
}
