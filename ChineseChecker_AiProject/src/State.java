import javafx.util.Pair;

import java.util.ArrayList;

public class State
{
    public char [][] star;
    public boolean isTerminal;
    public int utility;

    public ArrayList<Marble> computerMarbles = new ArrayList<>();
    public ArrayList<Marble> humanMarbles = new ArrayList<>();

    public State () {}

    public State (char [][] star)
    {
        this.star = star;
        isTerminal = false;
        createMarbles();
        getPossibleMoves(this.computerMarbles);
        getPossibleMoves(this.humanMarbles);
        getPossibleHops(this.computerMarbles);
        getPossibleHops(this.humanMarbles);
    }

    //create marbles for the computer and the human with their indices
    public void createMarbles()
    {
        int rows = star.length;            //17
        int columns = star[0].length;     //25
        for(int i = 0 ; i < rows; i++)
        {
            for(int j = 0; j < columns; j++)
            {
                if(star[i][j] == 'G')
                {
                    Marble marble = new Marble(star[i][j] , j , i);
                    computerMarbles.add(marble);
                }
                else if(star[i][j] == 'R')
                {
                    Marble marble = new Marble(star[i][j] , j , i);
                    humanMarbles.add(marble);
                }

            }
        }
    }

    //generate all possible moves for a marble
    public void getPossibleMoves(ArrayList <Marble> marbles)
    {
        for (int m = 0; m < marbles.size() ; m++)
        {
            marbles.get(m).possibleMoves.clear();
            int i = marbles.get(m).currentPositionY;
            int j = marbles.get(m).currentPositionX;

            if (i + 1 < star.length && j + 1 < star[0].length && star[i + 1][j + 1] == 'W')
            {
                Pair<Integer, Integer> pair = new Pair<>(i + 1, j + 1);
                marbles.get(m).possibleMoves.add(pair);
            }

            if (i + 1 < star.length && j - 1>= 0 && star[i + 1][j - 1] == 'W')
            {
                Pair<Integer, Integer> pair = new Pair<>(i + 1, j - 1);
                marbles.get(m).possibleMoves.add(pair);
            }

            if (j + 2 < star[0].length && star[i][j + 2] == 'W')
            {
                Pair<Integer, Integer> pair = new Pair<>(i, j + 2);
                marbles.get(m).possibleMoves.add(pair);
            }

            if (j - 2 >= 0 && star[i][j - 2] == 'W')
            {
                Pair<Integer, Integer> pair = new Pair<>(i , j - 2);
                marbles.get(m).possibleMoves.add(pair);
            }

            if (i - 1 >= 0 && j - 1 >= 0 && star[i - 1][j - 1] == 'W')
            {
                Pair<Integer, Integer> pair = new Pair<>(i - 1, j - 1);
                marbles.get(m).possibleMoves.add(pair);
            }

            if (i - 1 >= 0 && j + 1 < star[0].length && star[i - 1][j + 1] == 'W')
            {
                Pair<Integer, Integer> pair = new Pair<>(i - 1 , j + 1);
                marbles.get(m).possibleMoves.add(pair);
            }
        }
    }

    //generate all possible hops for a marble in board
    public void calculatePossibleHops(int index, ArrayList<Pair<Integer,Integer>> possibleOptions, ArrayList<Pair<Integer,Integer>> possibleHops)
    {
        int check = 0;

        int i = possibleOptions.get(index).getKey();
        int j = possibleOptions.get(index).getValue();

        if (i + 1 < star.length && j + 1 < star[0].length && i + 2 < star.length && j + 2 < star[0].length &&
                    star[i + 1][j + 1] != 'W' && star[i + 2][j + 2] == 'W')
        {
            Pair<Integer, Integer> pair1 = new Pair(i + 2, j + 2);
            if(!possibleOptions.contains(pair1))
            {
                check++;
                possibleOptions.add(pair1);
            }
        }

        //bottomLeft condition
        if (i + 1 < star.length && j - 1 >= 0 && i + 2 < star.length && j - 2 >= 0 &&
                    star[i + 1][j - 1] != 'W' && star[i + 2][j - 2] == 'W' )
        {
            Pair<Integer, Integer> pair1 = new Pair(i + 2, j - 2);
            if(!possibleOptions.contains(pair1))
            {
                check++;
                possibleOptions.add(pair1);
            }
        }

        //right condition
        if (j + 2 < star[0].length && j + 4 < star[0].length && star[i][j + 2] != 'W' && star[i][j + 4] == 'W')
        {
            Pair<Integer, Integer> pair1 = new Pair<>(i, j + 4);
            if(!possibleOptions.contains(pair1))
            {
                check++;
                possibleOptions.add(pair1);
            }
        }

        //left condition
        if (j - 2 >= 0 && j - 4 >= 0 && star[i][j - 2] != 'W' && star[i][j - 4] == 'W')
        {
            Pair<Integer, Integer> pair1 = new Pair<>(i, j - 4);
            if(!possibleOptions.contains(pair1))
            {
                check++;
                possibleOptions.add(pair1);
            }
        }

        //topLeft condition
        if (i - 1 >= 0 && j - 1 >= 0 && i - 2 >= 0 && j - 2 >= 0 && star[i - 1][j - 1] != 'W' &&  star[i - 2][j - 2] == 'W')
        {
            Pair<Integer, Integer> pair1 = new Pair<>(i - 2, j - 2);
            if(!possibleOptions.contains(pair1))
            {
                check++;
                possibleOptions.add(pair1);
            }
        }

        //topRight condition
        if (i - 1 >= 0 && j + 1 < star[0].length && i - 2 >= 0 && j + 2 < star[0].length && star[i - 1][j + 1] != 'W' && star[i - 2][j + 2] == 'W')
        {
            Pair<Integer, Integer> pair1 = new Pair<>(i - 2, j + 2);
            if(!possibleOptions.contains(pair1))
            {
                check++;
                possibleOptions.add(pair1);
            }
        }

        if(check == 0 && index != 0)
            possibleHops.add(possibleOptions.get(index));

        int size = possibleOptions.size() - 1;

        if(index++ != size)
            calculatePossibleHops(index++ , possibleOptions, possibleHops);
        else
            return;
    }

    //  Function to generate all possible Hops for all marbles in board
    public void getPossibleHops(ArrayList <Marble> marbles)
    {
        ArrayList<Pair<Integer, Integer>> possibleOptions = new ArrayList<>();
        for (int m = 0; m < marbles.size(); m++)
        {
            marbles.get(m).possibleHops.clear();
            int i = marbles.get(m).currentPositionY;
            int j = marbles.get(m).currentPositionX;
            Pair<Integer, Integer> pair = new Pair<>(i, j);
            possibleOptions.clear();
            possibleOptions.add(pair);
            calculatePossibleHops(0, possibleOptions, marbles.get(m).possibleHops);
        }
    }

    public void printStar()
    {
        for(int i = 0 ; i < star.length; i++)
        {
            for(int j=0; j < star[i].length; j++)
            {
                if(star[i][j] == 'n')
                    System.out.print(' ');
                else
                    System.out.print(star[i][j]);
            }
            System.out.println();
        }
    }
}