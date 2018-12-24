package solver;

import java.awt.Color;
import java.io.File;
import static java.lang.Integer.max;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Board {

    private int g;
    private int board[][];
    private Board parent = null;
    private int boardSize;
    private int[] neighborColors;
    public int currentColor;
    private boolean visited[][];

    public Board(int[][] colors, Board parent, int g) {
        // construct a board from an N-by-N
        this.board = colors;											//array of colors	
        this.parent = parent;						 					// (where colors[i][j] = color in								 					//row i, column j)
        currentColor = board[0][0];
        this.g = g;
        boardSize = colors[0].length;
        visited = new boolean[boardSize][boardSize];
        //System.out.println(currentColor + "\n\n");
    }

    public void changeColor(int color) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                visited[i][j] = false;
            }
        }
        recChangeColor(color, 0, 0);
        currentColor = board[0][0];
    }

    private void recChangeColor(int color, int i, int j) {
        visited[i][j] = true;
        if (board[i][j] != currentColor) {
            return;
        }
        board[i][j] = color;
        if (i + 1 < boardSize && visited[i + 1][j] == false) {
            recChangeColor(color, i + 1, j);
        }
        if (i - 1 >= 0 && visited[i - 1][j] == false) {
            recChangeColor(color, i - 1, j);
        }
        if (j + 1 < boardSize && visited[i][j + 1] == false) {
            recChangeColor(color, i, j + 1);
        }
        if (j - 1 >= 0 && visited[i][j - 1] == false) {
            recChangeColor(color, i, j - 1);
        }
    }
    private int numComponent = 0;
    public int[] clusterNo = {0, 0, 0, 0, 0, 0, 0};

    public int[][] componentNumbering() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                visited[i][j] = false;
            }
        }
        numComponent = 0;
        clusterSize = 0;
        int[][] comBoard = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (visited[i][j] == false) {
                    recComponentNumbering(numComponent++, board[i][j], comBoard, i, j);
                    if (i != 0 || j != 0) {
                        clusterNo[board[i][j]]++;
                    }
                }
            }
        }
        return comBoard;
    }
    private void recComponentNumbering(int componentNo, int color, int[][] comBoard, int i, int j) {
        if (board[i][j] != color) {
            return;
        }
        visited[i][j] = true;
        comBoard[i][j] = componentNo;
        if (i + 1 < boardSize && visited[i + 1][j] == false) {
            recComponentNumbering(componentNo, color, comBoard, i + 1, j);
        }
        if (i - 1 >= 0 && visited[i - 1][j] == false) {
            recComponentNumbering(componentNo, color, comBoard, i - 1, j);
        }
        if (j + 1 < boardSize && visited[i][j + 1] == false) {
            recComponentNumbering(componentNo, color, comBoard, i, j + 1);
        }
        if (j - 1 >= 0 && visited[i][j - 1] == false) {
            recComponentNumbering(componentNo, color, comBoard, i, j - 1);
        }
    }
    private int clusterSize;
    public boolean[] neighborColors() {
        
        boolean[] nColors = new boolean[7];
        for (int i = 0; i < 7; i++) {
            nColors[i] = false;
        }
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                visited[i][j] = false;
            }
        }
        clusterSize = 0;
        recNeighborColors(nColors, 0, 0);
        return nColors;
    }
    private void recNeighborColors(boolean[] neighborColors, int i, int j) {
        visited[i][j] = true;
        if (board[i][j] != currentColor) {
            neighborColors[board[i][j]] = true;
            return;
        }
        clusterSize++;
        if (i + 1 < boardSize && visited[i + 1][j] == false) {
            recNeighborColors(neighborColors, i + 1, j);
        }
        if (i - 1 >= 0 && visited[i - 1][j] == false) {
            recNeighborColors(neighborColors, i - 1, j);
        }
        if (j + 1 < boardSize && visited[i][j + 1] == false) {
            recNeighborColors(neighborColors, i, j + 1);
        }
        if (j - 1 >= 0 && visited[i][j - 1] == false) {
            recNeighborColors(neighborColors, i, j - 1);
        }
    }

    public int[] otherColorsInBoard() {
        int[] colorsInBoard = new int[6];
        boolean color[] = new boolean[7];
        for (int i = 0; i < 7; i++) {
            color[i] = false;
        }
        int temp = currentColor;
        changeColor(0);
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                // System.out.print(board[i][j]);
                color[board[i][j]] = true;
            }
        }
        changeColor(temp);
        int k = 0;
        for (int i = 1; i < 7; i++) {
            if (color[i] == true) {
                colorsInBoard[k++] = i;
            }
        }
        int[] returnValue = new int[k];
        for (int i = 0; i < k; i++) {
            returnValue[i] = colorsInBoard[i];
        }
        return returnValue;
    }

    public int f() {
        return g + heuristic(); //for normal A*
        //return heuristic(); //for greedy approach
    }
    // returns the estimated distance from current board to final state using heuristic1

    public int heuristic() {
        //return max(heuristic1(),heuristic2());
        //return heuristic1();
        return heuristic2();
        //return heuristic3();

    }

    private int heuristic1() {
        return ucsComponents();
    }
    // returns the estimated distance from current board to final state using heuristic2

    private int heuristic2() {
        return otherColorsInBoard().length;
    }
    
    private int heuristic3(){
        neighborColors();
        return -clusterSize;
    }

    private class myObject {

        public myObject(int i, int j, int cost) {
            this.i = i;
            this.j = j;
            this.cost = cost;
        }

        public boolean equals(myObject o) {
            if (i == o.i && j == o.j) {
                return true;
            }
            return false;
        }
        public int i;
        public int j;
        public int cost;
    }

    class MyComparator implements Comparator<myObject> {

        @Override
        public int compare(myObject o1, myObject o2) {
            return o1.cost - o2.cost;
        }

    }
    PriorityQueue<myObject> myPQ = new PriorityQueue<>(400, new MyComparator());
    private int f = 0;

    public int ucsComponents() {
        int[][] componentArray = componentNumbering();
        int[] costEstimate = {0, 0, 0};
        /*myPQ.add(new myObject(boardSize-1,0,0));
            while(!myPQ.isEmpty())
            {
                myObject node = myPQ.poll();
                if(node.i-1>=0)
                {
                    if(componentArray[node.i-1][node.j] == componentArray[0][0])
                    {
                       costEstimate[0] = node.cost+1;
                       break;
                    }
                    myObject child = new myObject(node.i-1, node.j, node.cost);
                    if(componentArray[node.i][node.j] != componentArray[child.i][child.j])
                        child.cost++;
                    myPQ.add(child);
                }
                if(node.j+1 < boardSize)
                {
                    if(componentArray[node.i][node.j+1] == componentArray[0][0])
                    {
                       costEstimate[0] = node.cost+1;
                       break;
                    }
                    myObject child = new myObject(node.i, node.j+1, node.cost);
                    if(componentArray[node.i][node.j] != componentArray[child.i][child.j])
                        child.cost++;
                    myPQ.add(child);
                }
            }*/
        myPQ.clear();
        myPQ.add(new myObject(boardSize - 1, boardSize - 1, 0));
        f++;
        while (!myPQ.isEmpty()) {
            //System.out.println("main loop" + f);
            myObject node = myPQ.poll();
            if (node.i - 1 >= 0) {
                if (componentArray[node.i - 1][node.j] == componentArray[0][0]) {
                    costEstimate[1] = node.cost + 1;
                    break;
                }
                myObject child = new myObject(node.i - 1, node.j, node.cost);
                if (componentArray[node.i][node.j] != componentArray[child.i][child.j]) {
                    child.cost++;
                }
                Iterator it = myPQ.iterator();
                myObject p = myPQ.peek();
                while (it.hasNext()) {
                    //System.out.println("Inner loop");
                    p = (myObject) it.next();
                    if (p.equals(child)) {
                        break;
                    }
                }
                if (p != null) {
                    if (p.equals(child) && myPQ.contains(child)) {
                        if (child.cost < p.cost) {
                            myPQ.remove(p);
                            myPQ.add(child);
                        }
                    } else {
                        myPQ.add(child);
                    }
                } else {
                    myPQ.add(child);
                }

            }
            if (node.j - 1 >= 0) {
                if (componentArray[node.i][node.j - 1] == componentArray[0][0]) {
                    costEstimate[1] = node.cost + 1;
                    break;
                }
                myObject child = new myObject(node.i, node.j - 1, node.cost);
                if (componentArray[node.i][node.j] != componentArray[child.i][child.j]) {
                    child.cost++;
                }
                Iterator it = myPQ.iterator();
                myObject p = myPQ.peek();
                while (it.hasNext()) {
                    //System.out.println("Inner loop");
                    p = (myObject) it.next();
                    if (p.equals(child)) {
                        break;
                    }
                }
                if (p != null) {
                    if (p.equals(child) && myPQ.contains(child)) {
                        if (child.cost < p.cost) {
                            myPQ.remove(p);
                            myPQ.add(child);
                        }
                    } else {
                        myPQ.add(child);
                    }
                } else {
                    myPQ.add(child);
                }
            }
        }
        /*myPQ.clear();
            myPQ.add(new myObject(0,boardSize-1,0));
            while(!myPQ.isEmpty())
            {
                myObject node = myPQ.poll();
                if(node.i+1 < boardSize)
                {
                    if(componentArray[node.i+1][node.j] == componentArray[0][0])
                    {
                       costEstimate[2] = node.cost+1;
                       break;
                    }
                    myObject child = new myObject(node.i+1, node.j, node.cost);
                    if(componentArray[node.i][node.j] != componentArray[child.i][child.j])
                        child.cost++;
                    myPQ.add(child);
                }
                if(node.j-1 >= 0)
                {
                    if(componentArray[node.i][node.j-1] == componentArray[0][0])
                    {
                       costEstimate[2] = node.cost+1;
                       break;
                    }
                    myObject child = new myObject(node.i, node.j-1, node.cost);
                    if(componentArray[node.i][node.j] != componentArray[child.i][child.j])
                        child.cost++;
                    myPQ.add(child);
                }
            }*/
        return max(costEstimate[0], max(costEstimate[1], costEstimate[2]));
    }
    // is this board the goal board? i.e., all color same. 

    public boolean isGoal() {
        int currentColor = board[0][0];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] != currentColor) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] getCopy() {
        int[][] nxtBoard = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                nxtBoard[i][j] = board[i][j];
            }
        }
        return nxtBoard;
    }
    // all neighboring boards

    public ArrayList<Board> neighbors() {
        int[] colorsInBoard = otherColorsInBoard();
        boolean[] neighbor = neighborColors();
        ArrayList<Board> neighborList = new ArrayList<Board>();
        for (int i = 0; i < colorsInBoard.length; i++) {
            if (neighbor[colorsInBoard[i]] == false) {
                continue;
            }
            int[][] newColors = getCopy();
            Board newBoard = new Board(newColors, this, g + 1);
            newBoard.changeColor(colorsInBoard[i]);
            neighborList.add(newBoard);
        }
        return neighborList;
    }

    public void printNeighborColors() {
        int[] colors = otherColorsInBoard();
        boolean[] neighbors = neighborColors();
        for (int i = 0; i < colors.length; i++) {
            if (neighbors[colors[i]] == true) {
                System.out.println(colors[i]);
            }
        }
    }
    // does this board equal y?

    public boolean equals(Object y) {
        Board yBoard = (Board) y;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] != yBoard.board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int get_g() {
        return g;
    }

    public Board getParent() {
        return parent;
    }

    // string representation of the
    //board (in the output format specified below)
    public String toString() {
        String str = "\ng: " + g + " h:" + heuristic() + "\n";
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                str += (board[i][j] + " ");
            }
            str += "\n";
        }
        return str;
//		System.out.println();

    }

    // for testing purpose
    /*public static void main(String[] args) 
	{
            Scanner in=null;
            try 
            {
                in = new Scanner(new File("input.txt"));
            } 
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Board initial = null;
            for(int k = 0; k<3; k++)
            {
                int N = in.nextInt();
                if(N==0) return;

                int[][] colors = new int[N][N];
                for (int i = 0; i < N; i++)
                    for (int j = 0; j < N; j++)
                        colors[i][j] = in.nextInt();
                initial = new Board(colors,null,0);
            }
            System.out.println(initial.toString());
            //initial.printNeighborColors();
            //initial.changeColor(3);
            //System.out.println(initial.toString());
            //initial.changeColor(4);
            //System.out.println(initial.toString());
            ArrayList<Board> neighborList = initial.neighbors();
            //System.out.println(neighborList.size());
           // int v = 0;
           // System.out.println(neighborList.get(v).toString());
            for(int i = 0; i < neighborList.size(); i++)
            {
                 System.out.println(neighborList.get(i).toString());
            }
            ArrayList<Board> newNeighborList = neighborList.get(1).neighbors();
           // for(int i = 0; i < newNeighborList.size(); i++)
           // {
           //      System.out.println(newNeighborList.get(i).toString());
           // }
            System.out.println(newNeighborList.get(newNeighborList.size()-1).toString());
            int[][] components = newNeighborList.get(newNeighborList.size()-1).componentNumbering();
            String str = "components: \n";
            for (int i=0; i<components.length;i++)
            {
                for(int j=0;j<components.length;j++)
                {
                    str += (components[i][j]+" ");
                }
                str +="\n";
            }
            System.out.println(str);
            //for(int i = 0; i<newNeighborList.get(newNeighborList.size()-1).clusterNo.length; i++)
            //System.out.println(i+ ":" + newNeighborList.get(newNeighborList.size()-1).clusterNo[i] + "\n");
            for(int i = 0; i<newNeighborList.size(); i++)
            System.out.println(newNeighborList.get(i).ucsComponents());
	}*/
}
