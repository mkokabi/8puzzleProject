
import java.util.Comparator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mohsen
 */
public class Solver {

    private class Node {

        public Node PrevNode;
        public Board Board;
        public int Moves;

        public Node(Node PrevNode, Board Board, int Moves) {
            this.PrevNode = PrevNode;
            this.Board = Board;
            this.Moves = Moves;
        }
    }

    private MinPQ<Node> pq = new MinPQ<Node>(new Comparator<Node>() {
        @Override
        public int compare(Node n1, Node n2) {
            int priority1 = n1.Board.hamming() + n1.Moves;
            int priority2 = n1.Board.hamming() + n2.Moves;
            return Integer.compare(priority1, priority2);
        }
    });

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        Node initialNode = new Node(null, initial, 0);
        pq.insert(initialNode);
        Board newState = initial;
        int i = 0;
        while (!newState.isGoal()) {
            insertNeighbsInPQ(newState, initialNode, i);
        }
    }

    private boolean foundSolution = false;
    
    private void insertNeighbsInPQ(Board newState, Node prevNode, int i) {
        pq.delMin();
        for (Board neighBoard : newState.neighbors()) {
            if (foundSolution) {
                return;
            }
            if (neighBoard.isGoal()){
                foundSolution = true;
                return;
            }
            if (neighBoard.equals(prevNode.Board)) {
                return;
            }
            final Node newNode = new Node(prevNode, neighBoard, ++i);
            pq.insert(newNode);
            insertNeighbsInPQ(neighBoard, newNode, i);
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return true;
    }

    // min number of moves to solve initial board; -1 if no solution
    public int moves() {
        return 0;
    }

    // sequence of boards in a shortest solution; null if no solution
    public Iterable<Board> solution() {
        throw new NotImplementedException();
    }

    // solve a slider puzzle (given below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                blocks[i][j] = in.readInt();
            }
        }
        Board initial = new Board(blocks);
        StdOut.print(initial);
        StdOut.println(initial.hamming());
        StdOut.println(initial.manhattan());

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        }
    }

}
