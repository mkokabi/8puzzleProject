
import java.util.Comparator;
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
            int priority1 = n1.Board.manhattan() + n1.Moves;
            int priority2 = n1.Board.manhattan() + n2.Moves;
            return Integer.compare(priority1, priority2);
        }
    });

    private int m;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        //System.out.println("--Solving--");
        Node initialNode = new Node(null, initial, 0);
        pq.insert(initialNode);
        Board newState = initial;
        if (!newState.isGoal()) {
            insertNeighbsInPQ(newState, initialNode);
            if (foundSolution) {
                return;
            }
        } else {
            foundSolution = true;
            solutionResult = new Stack<Board>();
        }
    }

    private Stack<Board> solutionResult;
    private boolean foundSolution = false;

    private void insertNeighbsInPQ(Board newState, Node prevNode) {
        pq.delMin();
        m++;
        System.out.format("moves:%d \n", m);
        System.out.println("from \n" + prevNode.Board.toString());
        int n = 0;
        for (Board neighBoard : newState.neighbors()) {
            System.out.println("n:" + n++);
            System.out.print(neighBoard.toString());
            System.out.format("manhattan:%d hamming:%d \n", neighBoard.manhattan(), neighBoard.hamming());
            if (foundSolution) {
                return;
            }
            if (neighBoard.isGoal()) {
                foundSolution = true;
                solutionResult = new Stack<>();
                solutionResult.push(neighBoard);
                Node pn = prevNode;
                while (pn != null) {
                    solutionResult.push(pn.Board);
                    pn = pn.PrevNode;
                }
//                Iterator<Node> nodes = pq.iterator();
//                while (nodes.hasNext()) {
//                    Node pn = nodes.next();
//                    solutionResult.enqueue(pn.Board);
//                    pn = pn.PrevNode;
//                }
                return;
            }
            if (prevNode.PrevNode != null && neighBoard.equals(prevNode.PrevNode.Board)) {
                System.out.format("return, moves:%d\n", m);
                continue;
            }
            final Node newNode = new Node(prevNode, neighBoard, m);
            pq.insert(newNode);
            if (neighBoard.hamming() >= prevNode.Board.hamming()
                    && neighBoard.manhattan() >= prevNode.Board.manhattan()) {
                System.out.format("continue\n", m);
                continue;
            }
            insertNeighbsInPQ(neighBoard, newNode);
        }
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return foundSolution;
    }

    // min number of moves to solve initial board; -1 if no solution
    public int moves() {
        return foundSolution ? m : -1;
    }

    // sequence of boards in a shortest solution; null if no solution
    public Iterable<Board> solution() {
        if (!foundSolution) {
            return null;
        }
        return new Iterable<Board>() {

            @Override
            public Iterator<Board> iterator() {
                return solutionResult.iterator();
            }
        };
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

        //StdOut.println(initial.twin());
        //Solver solver = new Solver(initial.twin());
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
