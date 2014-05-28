import java.util.*;

/**
 * A program to generate some test inputs.
 */
public class TestGenV2 {
	/**
	 * The number of tiles to be added.
	 */
	private static final int NUM_TILES = 20000;
	
	private static Random random;
	private static int[][] values;
	private static Queue<Integer> tiles;
	
	/**
	 * Main function.
	 */
	public static void main(String[] args) {
		long seed = System.currentTimeMillis();
		random = new Random(seed);
		values = new int[4][4];
		tiles = new LinkedList<Integer>();
		
		// Set up initial board
		addDeck();
		for (int i = 0; i < 9; i++) {
			int x, y;
			do {
				x = random.nextInt(4);
				y = random.nextInt(4);
			} while (values[y][x] != 0);
			values[y][x] = tiles.remove();
		}
		
		// Generate test data
		System.out.println("Seed: " + seed);
		System.out.println();
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (x > 0) System.out.print(" ");
				System.out.print(values[y][x]);
			}
			System.out.println();
		}
		System.out.println();
		for (int i = 0; i < NUM_TILES; i++) {
			if (tiles.isEmpty()) addDeck();
			if (i > 0) System.out.print(" ");
			System.out.print(tiles.remove());
		}
		System.out.println();
	}
	
	/**
	 * Adds a new deck of tiles to the queue.
	 */
	private static void addDeck() {
		List<Integer> deck = new ArrayList<Integer>();
		for (int i = 0; i < 12; i++) {
			deck.add(1);
			deck.add(2);
		}
		for (int i = 0; i < 3; i++) {
			deck.add(3);
		}
		deck.add(6);
		deck.add(12);
		deck.add(24);
		Collections.shuffle(deck, random);
		tiles.addAll(deck);
	}
}
