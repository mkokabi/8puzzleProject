
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static Comparator<Node> MinPQComparator = new Comparator<Node>() {
        @Override
        public int compare(Node n1, Node n2) {
            int priority1 = n1.Board.manhattan() + n1.Moves;
            int priority2 = n2.Board.manhattan() + n2.Moves;
            return Integer.compare(priority1, priority2);
        }
    };
    private MinPQ<Node> pq = new MinPQ<Node>(MinPQComparator);

    private int m;

    private class TestThread extends Thread {

        Board board;
        Solver mainSolver;

        public TestThread(Board board, Solver mainSolver) {
            this.board = board;
            this.mainSolver = mainSolver;
        }

        @Override
        public void run() {
            Solver solver = new Solver(this.board, true, mainSolver);
            isTwinSolvable = solver.isSolvable();
            if (isTwinSolvable) {
                mainSolver.isTwinSolvable = true;
            }
        }
    }

    private boolean isTwinSolvable = false;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        this(initial, false, null);
    }
    
    private TestThread feasibleTestThread;
    private Solver mainSolver;

    private Solver(Board initial, boolean isTwin, Solver mainSolver) {
        //System.out.println("--Solving--");
        Node initialNode = new Node(null, initial, 0);
        if (!isTwin) {
            Board twinBoard = initialNode.Board.twin();
            feasibleTestThread = new TestThread(twinBoard, this);
            feasibleTestThread.start();
        } else {
            this.mainSolver = mainSolver;
        }
        //pq.insert(initialNode);
        Board newState = initial;
        if (!newState.isGoal()) {
            //insertNeighbsInPQ(newState, initialNode);
            insertNeighbsInPQ(initialNode, isTwin);
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

//    private void insertNeighbsInPQ(Board newState, Node prevNode) {
    private void insertNeighbsInPQ(Node curNode, boolean isTwin) {
        if (!isTwin && isTwinSolvable) {
            return;
        }
        if (isTwin && mainSolver.foundSolution) {
            return;
        }
        //pq = pq = new MinPQ<Node>(MinPQComparator);
        m++;
//        System.out.format("--------------moves:%d \n", m);
//        //System.out.println("from \n" + prevNode.Board.toString());
//        System.out.println("from \n" + curNode.Board.toString());
        int n = 0;
        //for (Board neighBoard : newState.neighbors()) {
        //Node minNode = pq.isEmpty() ? curNode : pq.min();
        Node minNode = curNode;
        int priority = minNode.Moves + minNode.Board.manhattan();
        Queue<Node> tempNodeList = new Queue<>();
        while (true) {
            for (Board neighBoard : minNode.Board.neighbors()) {
                //for (Board neighBoard : curNode.Board.neighbors()) {
//                System.out.println("n:" + n++);
//                System.out.print(neighBoard.toString());
//                System.out.format("manhattan:%d hamming:%d \n", neighBoard.manhattan(), neighBoard.hamming());
                if (foundSolution) {
                    return;
                }
                if (neighBoard.isGoal()) {                    
                    foundSolution = true;
                    solutionResult = new Stack<>();
                    solutionResult.push(neighBoard);
                    //Node pn = prevNode;
                    Node pn = minNode;
                    while (pn != null) {
                        solutionResult.push(pn.Board);
                        pn = pn.PrevNode;
                    }
                    return;
                }
                if (minNode.PrevNode != null && neighBoard.equals(minNode.PrevNode.Board)) {
                    //if (prevNode.PrevNode != null && neighBoard.equals(prevNode.PrevNode.Board)) {
//                    System.out.format("return, moves:%d\n", m);
                    continue;
                }
                final Node newNode = new Node(minNode, neighBoard, m);
                pq.insert(newNode);
                tempNodeList.enqueue(newNode);
//            if (neighBoard.hamming() >= prevNode.Board.hamming()
//                    && neighBoard.manhattan() >= prevNode.Board.manhattan()) {
//                System.out.format("continue\n", m);
//                continue;
//            }
            }
            if (!pq.isEmpty()) {
                minNode = pq.delMin();
                if (minNode.Moves + minNode.Board.manhattan() > priority) {
                    break;
                }
                boolean found = false;
                for (Node node : tempNodeList) {
                    if (node.equals(minNode)) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }
            } else {
                break;
            }
        }
//        for (Node node : tempNodeList) {
//            pq.insert(node);
//        }
//        System.out.println("minNode board=" + minNode.Board.toString());
//        System.out.format("manhattan:%d hamming:%d \n", minNode.Board.manhattan(), minNode.Board.hamming());
        //newState = minNode.Board;        
        //insertNeighbsInPQ(newState, minNode);
        insertNeighbsInPQ(minNode, isTwin);
    }

    // is the initial board solvable?
    public boolean isSolvable() {
        return foundSolution;
    }

    // min number of moves to solve initial board; -1 if no solution
    public int moves() {
        if (isTwinSolvable) {
            return -1;
        }
        if (foundSolution && solutionResult.size() == 0) {
            return 0;
        }
        return foundSolution ? solutionResult.size() - 1 : -1;
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
