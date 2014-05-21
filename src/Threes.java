import java.io.*;
import java.util.*;

/**
 * An AI program that can play Threes.
 * @author Ridge Shrubsall (21112211), Kimberley Siew (21125071)
 */
public class Threes {
	/**
	 * Debug flag (enables a human player to input moves).
	 */
	private static final boolean DEBUG = false;
	
	/**
	 * The maximum depth limit when searching for a move.
	 */
	private static final int MAX_DEPTH_LIMIT = 8;
	
	/**
	 * Main method to play a given board and output a sequence of moves
	 * that result in the highest possible score.
	 * @param args The filenames of the input and output files
	 */
	public static void main(String[] args) {
		// Check number of arguments
		if (args.length != 2) {
			System.err.println("Usage: java Threes <input-file> <output-file>");
			System.exit(1);
		}
		
		// Run program
		File inputFile = new File(args[0]);
		File outputFile = new File(args[1]);
		new Threes(inputFile, outputFile);
	}
	
	/**
	 * Construct a new AI player.
	 * @param inputFile The input file
	 * @param outputFile The output file
	 */
	public Threes(File inputFile, File outputFile) {
		try {
			// Read the input file
			Board boardInitial = readInput(inputFile);
			
			// Open the output file for writing
			BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
			
			// Print header comments
			out.write("Input: " + inputFile + "\n\n");
			System.out.println("Input: " + inputFile);
			System.out.println();
			
			// Generate and print moves
			int moveCount = 0;
			long startTime = System.currentTimeMillis();
			
			Board bestBoard = null;
			String bestMoves = "";
			int bestScore = 0;
			
			if (DEBUG) {
				Board board = new Board(boardInitial);
				StringBuilder sb = new StringBuilder();
				while (true) {
					System.out.println(board.toString());
					Move userMove = readMove();
					board = makeMove(board, userMove);
					if (board.isGameOver) break;
					sb.append(userMove.key);
					moveCount++;
				}
				String moves = sb.toString();
				System.out.println();
				int score = board.getScore();
				bestBoard = board;
				bestMoves = moves;
				bestScore = score;
			} else {
				for (int depth = 1; depth <= MAX_DEPTH_LIMIT; depth++) {
					Board board = new Board(boardInitial);
					System.out.println("Searching with lookahead " + depth);
					System.out.print("Moves: ");
					StringBuilder sb = new StringBuilder();
					while (true) {
						Move bestMove = findBestMove(board, depth);
						board = makeMove(board, bestMove);
						if (board.isGameOver) break;
						sb.append(bestMove.key);
						System.out.print(bestMove.key);
						moveCount++;
					}
					String moves = sb.toString();
					System.out.println();
					int score = board.getScore();
					System.out.println("Score: " + score);
					System.out.println();
					if (bestBoard == null || score > bestScore) {
						bestBoard = board;
						bestMoves = moves;
						bestScore = score;
					}
				}
			}
			
			long endTime = System.currentTimeMillis();
			double timeSecs = (endTime - startTime) / 1000.0;
			System.out.println("Total moves: " + moveCount);
			System.out.println("Time taken: " + timeSecs + " seconds");
			double movesPerSec = moveCount / timeSecs;
			System.out.println((int) movesPerSec + " moves per second");
			System.out.println();
			
			out.write(bestMoves + "\n");
			System.out.println("Best moves written to '" + outputFile.getName() + "'.");
			
			// Print best score
			out.write(bestScore + "\n");
			System.out.println("Best score: " + bestScore);
			System.out.println();
			
			// Print final board
			System.out.println("Final board:");
			System.out.print(bestBoard.toString());
			
			// Close output
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Finds the best move for the given board.
	 * @param board The current board
	 * @param depth The depth counter
	 * @return The best move found at the current depth
	 */
	private Move findBestMove(Board board, int depth) {
		Move bestMove = null;
		int bestScore = 0;
		for (Move move : Move.values()) {
			int score = findBestScore(makeMove(board, move), depth - 1);
			if (bestMove == null || score > bestScore) {
				bestMove = move;
				bestScore = score;
			}
		}
		return bestMove;
	}
	
	/**
	 * Finds the best scoring branch of moves for the given board.
	 * @param board The current board
	 * @param depth The depth counter
	 * @return The best score found at the current depth
	 */
	private int findBestScore(Board board, int depth) {
		if (depth == 0 || board.isGameOver) {
			return board.getScore() + board.getPenalty();
		} else {
			int bestScore = 0;
			for (Move move : Move.values()) {
				int score = findBestScore(makeMove(board, move), depth - 1);
				if (score > bestScore) {
					bestScore = score;
				}
			}
			return bestScore;
		}
	}
	
	/**
	 * Returns the next state of the board after making a move.
	 * @param board The current board
	 * @param move The direction to move the tiles
	 * @return The resulting board
	 */
	private Board makeMove(Board board, Move move) {
		// Make a copy of the existing board
		Board b = new Board(board);
		
		// Check that there are tiles remaining to be placed
		if (b.tileIndex == b.tiles.size()) {
			b.isGameOver = true;
			return b;
		}
		
		// Try sliding each row/column of the board
		List<Slide> slides = new ArrayList<Slide>();
		switch (move) {
			case UP:
			case DOWN:
				for (int i = 0; i < 4; i++) {
					if (slideColumn(b, i, move)) {
						slides.add(new Slide(b, i, move));
					}
				}
				break;
			
			case LEFT:
			case RIGHT:
				for (int i = 0; i < 4; i++) {
					if (slideRow(b, i, move)) {
						slides.add(new Slide(b, i, move));
					}
				}
				break;
		}
		
		// Check that at least one slide was successful
		if (slides.isEmpty()) {
			b.isGameOver = true;
			return b;
		}
		
		// Order the slides lexicographically
		Collections.sort(slides);
		
		// Place the next tile on the lowest scoring slide
		Slide s = slides.get(0);
		int t = b.tiles.get(b.tileIndex++);
		switch (move) {
			case UP:
				b.values[3][s.index] = t;
				break;
			case DOWN:
				b.values[0][s.index] = t;
				break;
			case LEFT:
				b.values[s.index][3] = t;
				break;
			case RIGHT:
				b.values[s.index][0] = t;
				break;
		}
		
		// Return the new board state
		return b;
	}
	
	/**
	 * Slides the specified board column either up or down.
	 * @param b The board to be updated
	 * @param x The column number
	 * @param move The direction to slide the column
	 * @return true if the slide was successful
	 */
	private boolean slideColumn(Board b, int x, Move move) {
		boolean slide = false;
		switch (move) {
			case UP:
				for (int y = 1; y <= 3; y++) {
					int va = b.values[y-1][x];
					int vb = b.values[y][x];
					if (va == 0 && vb > 0) {
						// Move tile upwards
						b.values[y-1][x] = vb;
						b.values[y][x] = 0;
						slide = true;
					} else if ((va == 1 && vb == 2) || (va == 2 && vb == 1) || (va > 2 && va == vb)) {
						// Merge with upmost tile
						b.values[y-1][x] = va + vb;
						b.values[y][x] = 0;
						slide = true;
					}
				}
				break;
			
			case DOWN:
				for (int y = 2; y >= 0; y--) {
					int va = b.values[y+1][x];
					int vb = b.values[y][x];
					if (va == 0 && vb > 0) {
						// Move tile downwards
						b.values[y+1][x] = vb;
						b.values[y][x] = 0;
						slide = true;
					} else if ((va == 1 && vb == 2) || (va == 2 && vb == 1) || (va > 2 && va == vb)) {
						// Merge with downmost tile
						b.values[y+1][x] = va + vb;
						b.values[y][x] = 0;
						slide = true;
					}
				}
				break;
			
			default:
				throw new AssertionError("Column slide expected");
		}
		return slide;
	}
	
	/**
	 * Slides the specified board row either left or right.
	 * @param b The board to be updated
	 * @param y The row number
	 * @param move The direction to slide the row
	 * @return true if the slide was successful
	 */
	private boolean slideRow(Board b, int y, Move move) {
		boolean slide = false;
		switch (move) {
			case LEFT:
				for (int x = 1; x <= 3; x++) {
					int va = b.values[y][x-1];
					int vb = b.values[y][x];
					if (va == 0 && vb > 0) {
						// Move tile leftwards
						b.values[y][x-1] = vb;
						b.values[y][x] = 0;
						slide = true;
					} else if ((va == 1 && vb == 2) || (va == 2 && vb == 1) || (va > 2 && va == vb)) {
						// Merge with leftmost tile
						b.values[y][x-1] = va + vb;
						b.values[y][x] = 0;
						slide = true;
					}
				}
				break;
			
			case RIGHT:
				for (int x = 2; x >= 0; x--) {
					int va = b.values[y][x+1];
					int vb = b.values[y][x];
					if (va == 0 && vb > 0) {
						// Move tile rightwards
						b.values[y][x+1] = vb;
						b.values[y][x] = 0;
						slide = true;
					} else if ((va == 1 && vb == 2) || (va == 2 && vb == 1) || (va > 2 && va == vb)) {
						// Merge with rightmost tile
						b.values[y][x+1] = va + vb;
						b.values[y][x] = 0;
						slide = true;
					}
				}
				break;
			
			default:
				throw new AssertionError("Row slide expected");
		}
		return slide;
	}
	
	/**
	 * Reads the initial state of the board.
	 * @param inputFile The input file
	 * @return The initial board
	 */
	private Board readInput(File inputFile) throws IOException {
		Board b = new Board();
		BufferedReader in = new BufferedReader(new FileReader(inputFile));
		
		// Skip comments
		in.readLine();
		in.readLine();
		
		// Read the board values
		Scanner sc = new Scanner(in);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				b.values[y][x] = sc.nextInt();
			}
		}
		
		// Read the list of tiles
		while (sc.hasNextInt()) {
			b.tiles.add(sc.nextInt());
		}
		
		sc.close();
		return b;
	}
	
	/**
	 * Reads a single move from standard input (for debugging purposes).
	 * @return The move made by the user
	 */
	private Move readMove() throws IOException {
		int c;
		do {
			c = System.in.read();
		} while (c != 'U' && c != 'D' && c != 'L' && c != 'R');
		return Move.keys.get((char) c);
	}
}
