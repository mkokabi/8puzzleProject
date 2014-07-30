
import java.util.Iterator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mohsen
 */
public class Board {

    private int[][] blocks;
    private int N;
    private int emptyRow;
    private int emptyCol;

    // construct a board from an N-by-N array of blocks
    // (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        N = blocks.length;
        this.blocks = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.blocks[i][j] = blocks[i][j];
                if (blocks[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                }
            }
        }
    }

    // board dimension N
    public int dimension() {
        return N;
    }

    // number of blocks out of place
    public int hamming() {
        int result = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (blocks[i][j] == 0) {
                    continue;
                }
                if (distance(blocks[i][j], i, j) != 0) {
                    result++;
                }
            }
        }
        return result;
    }

    private int distance(int curValue, int curRow, int curCol) {
        int goalRow = 0;
        int goalCol = 0;
        if (curValue == 0) {
            goalRow = N - 1;
            goalCol = N - 1;
        } else {
            goalRow = (curValue - 1) / N;
            goalCol = (curValue - 1) % N;
        }
        return Math.abs(goalRow - curRow)
                + Math.abs(goalCol - curCol);
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        int result = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (blocks[i][j] == 0) {
                    continue;
                }
                result += distance(blocks[i][j], i, j);
            }
        }
        return result;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (blocks[i][j] == 0) {
                    continue;
                }
                if (distance(blocks[i][j], i, j) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    // a board obtained by exchanging two adjacent blocks in the same row
    public Board twin() {
        Board board = new Board(this.blocks);
        int r1 = 0;
        int c1 = 0;
        int c2 = c1 + 1;
        if (N == 2 && board.emptyRow == 0) {
            r1 = 1;
        } else {
            if (board.emptyRow == 0 && c2 < N - 1) {
                while (board.emptyCol == c1 || board.emptyCol == c2) {
                    c1++;
                    c2 = c1 + 1;
                }
            }
            if (c2 == N && r1 < N) {
                r1++;
                c1 = 0;
                c2 = c1 + 1;
            }
        }
        int tmp = board.blocks[r1][c1];
        board.blocks[r1][c1] = board.blocks[r1][c2];
        board.blocks[r1][c2] = tmp;
        return board;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }
        Board that = (Board) y;
        if (that.dimension() != this.dimension()) {
            return false;
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (that.blocks[i][j] != this.blocks[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void slide(Board board, int r1, int c1, int r2, int c2) {
        int tmp = board.blocks[r1][c1];
        board.blocks[r1][c1] = board.blocks[r2][c2];
        board.blocks[r2][c2] = tmp;
        board.emptyRow = r2;
        board.emptyCol = c2;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return new Iterable<Board>() {

            @Override
            public Iterator<Board> iterator() {
                Queue<Board> neighbs = new Queue<Board>();
                if (emptyRow > 0) {
                    Board board = new Board(blocks);
                    slide(board, emptyRow, emptyCol, emptyRow - 1, emptyCol);
                    neighbs.enqueue(board);
                }
                if (emptyRow < N - 1) {
                    Board board = new Board(blocks);
                    slide(board, emptyRow, emptyCol, emptyRow + 1, emptyCol);
                    neighbs.enqueue(board);
                }
                if (emptyCol > 0) {
                    Board board = new Board(blocks);
                    slide(board, emptyRow, emptyCol, emptyRow, emptyCol - 1);
                    neighbs.enqueue(board);
                }
                if (emptyCol < N - 1) {
                    Board board = new Board(blocks);
                    slide(board, emptyRow, emptyCol, emptyRow, emptyCol + 1);
                    neighbs.enqueue(board);
                }
                return neighbs.iterator();
            }
        };
    }

    // string representation of the board (in the output format specified below)
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(dimension());
        sb.append("\n");
        for (int i = 0; i < N; i++) {
            sb.append(" ");
            for (int j = 0; j < N; j++) {
                sb.append(this.blocks[i][j]);
                if (j != N - 1) {
                    sb.append("  ");
                } else {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
}
