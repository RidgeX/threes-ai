/**
 * A utility class for comparing slide scores.
 */
public class Slide implements Comparable<Slide> {
	int index;
	Move move;
	int[] values;
	
	/**
	 * Create a new set of slide scores.
	 * @param b The board
	 * @param i The column/row index
	 * @param move The slide direction
	 */
	public Slide(Board b, int i, Move move) {
		index = i;
		this.move = move;
		switch (move) {
			case UP:
				values = new int[] {b.values[3][i], b.values[2][i], b.values[1][i], b.values[0][i]};
				break;
			case DOWN:
				values = new int[] {b.values[0][i], b.values[1][i], b.values[2][i], b.values[3][i]};
				break;
			case LEFT:
				values = new int[] {b.values[i][3], b.values[i][2], b.values[i][1], b.values[i][0]};
				break;
			case RIGHT:
				values = new int[] {b.values[i][0], b.values[i][1], b.values[i][2], b.values[i][3]};
				break;
		}
	}
	
	/**
	 * Lexicographically compare two different sets of scores.
	 * @param s The set of scores being compared
	 * @return The difference in scores
	 */
	public int compareTo(Slide s) {
		return compareTo(s, 0);
	}
	
	/**
	 * Lexicographically compare two different sets of scores.
	 * @param s The set of scores being compared
	 * @param n The number of identical scores
	 * @return The difference in scores
	 */
	private int compareTo(Slide s, int n) {
		if (n == 4) {
			// All scores are identical - break ties on row/column number
			if (move == Move.UP || move == Move.RIGHT) {
				return index - s.index;
			} else {
				return s.index - index;
			}
		} else if (values[n] < s.values[n]) {
			return -1;
		} else if (values[n] > s.values[n]) {
			return 1;
		} else {
			return compareTo(s, n + 1);
		}
	}
}
