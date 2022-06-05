import javafx.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Game
{
    public  int humanScore = 0;
    public  int computerScore = 0;

    public  int computerDistance = 0;

    public  HashMap <State , State> level1 = new HashMap<>();
    public  HashMap <State , State> level2 = new HashMap<>();
    public  HashMap <State , State> level3 = new HashMap<>();

    public  int [][] redTriangle = {{13,9}, {13,11}, {13,13}, {13,15}, {14,10}, {14,12}, {14,14}, {15,11}, {15,13}, {16,12}};
    public  int [][] greenTriangle  = {{0,12}, {1,11}, {1,13}, {2,10}, {2,12}, {2,14}, {3,9}, {3,11}, {3,13}, {3,15}};

    //get all children for a required parent
    public ArrayList<State> getAllChildren (State parent)
    {
        ArrayList<State> children = new ArrayList<>();

        for (Map.Entry<State, State> pair: level1.entrySet())
        {
            if(pair.getValue().equals(parent))
                children.add(pair.getKey());
        }

        for (Map.Entry<State, State> pair: level2.entrySet())
        {
            if(pair.getValue().equals(parent))
                children.add(pair.getKey());
        }

        for (Map.Entry<State, State> pair: level3.entrySet())
        {
            if(pair.getValue().equals(parent))
                children.add(pair.getKey());
        }
        return children;
    }

    // If the computer’s marbles reached the other end (opponent triangle), it returns 1
    // If the humans’ marbles reached the other end, it returns -1
    public  int checkForWinner(State state)
    {
        humanScore = 0;
        computerScore = 0;
        for(int i = 0; i < greenTriangle.length; i++)
        {
            if(state.star[greenTriangle[i][0]][greenTriangle[i][1]] == 'R')
                humanScore += 1;
        }

        for(int i = 0; i < redTriangle.length; i++)
        {
            if(state.star[redTriangle[i][0]][redTriangle[i][1]] == 'G')
                computerScore+=1;
        }

        if(computerScore == 10)
        {
            state.isTerminal = true;
            return 1;
        }
        else if(humanScore == 10)
        {
            state.isTerminal = true;
            return -1;
        }
        else
        {
            return 0;
        }
    }

    //change the specific marble's position to its desired new position and return a new star
    public  char [][] move(char [][] star, int i, int j, Pair<Integer, Integer> newPos)
    {
        char [][] tempStar = new char[17][25];
        for (int m = 0; m < star.length; m++)
        {
            for (int k = 0 ; k < star[0].length; k++)
                tempStar[m][k] = star[m][k];
        }

        tempStar[newPos.getKey()][newPos.getValue()] = tempStar[i][j];
        tempStar[i][j] = 'W';
        return tempStar;
    }

    //generate all children for a specific state
    public  ArrayList <State> constructTreeLevel(State state , ArrayList <Marble> marbles)
    {
        ArrayList <State> children = new ArrayList<>();

        for (int i = 0; i < marbles.size(); i++)
        {
            for (int j = 0; j < marbles.get(i).possibleMoves.size(); j++)
            {
                char [][] tempStar = move(state.star, marbles.get(i).currentPositionY, marbles.get(i).currentPositionX, marbles.get(i).possibleMoves.get(j));
                State state1 = new State(tempStar);
                children.add(state1);
            }

            for (int j = 0; j < marbles.get(i).possibleHops.size(); j++)
            {
                char [][] tempStar = move(state.star, marbles.get(i).currentPositionY, marbles.get(i).currentPositionX, marbles.get(i).possibleHops.get(j));
                State state1 = new State(tempStar);
                children.add(state1);
            }
        }
        return children;
    }

    //fill the levels of the tree
    public void constructTree(int level , State root)
    {
        //computer turn
        ArrayList <State> result = constructTreeLevel(root, root.computerMarbles);
        for (State state : result)
        {
            checkForWinner(state);
            if(level == 1 || state.isTerminal)       //leaf or terminal
                calculateUtility(state);

            level1.put(state , root);
        }

        if(level > 1)
        {
            // to construct level 2 of the tree
            for (State parentState : result)
            {
                if(parentState.isTerminal)
                    continue;

                ArrayList <State> result2 = constructTreeLevel(parentState , parentState.humanMarbles);
                for (State state2 : result2)
                {
                    checkForWinner(state2);
                    if(level == 2 || state2.isTerminal) //leaf or terminal
                        calculateUtility(state2);

                    level2.put(state2 , parentState);
                }
            }
        }
        if (level > 2)
        {
            // to construct level 3 of the tree
            for (State parentState : level2.keySet())
            {
                if(parentState.isTerminal)
                    continue;

                ArrayList <State> result2 = constructTreeLevel(parentState , parentState.computerMarbles);
                for (State state2 : result2)
                {
                    checkForWinner(state2);
                    if(level == 3 || state2.isTerminal)     //leaf or terminal
                        calculateUtility(state2);

                    level3.put(state2 , parentState);
                }
            }
        }
    }

    public  int calculateUtility(State state)
    {
        for (int i = 0; i < state.computerMarbles.size(); i++)
        {
            computerDistance += state.computerMarbles.get(i).distance;
        }

        int utility =  computerDistance;
        state.utility = utility;

        computerDistance = 0;

        return state.utility;
    }

    public int alphaBetaMinimax(State node, int depth, boolean isMin , int alpha , int beta)
    {
        if(depth == 0 || checkForWinner(node) == 1 || checkForWinner(node) == -1) //leaf or terminal state
        {
            return node.utility;
        }

        ArrayList<State> children = getAllChildren(node);

        if(isMin)
        {
            int betaValue = Integer.MAX_VALUE;
            for (State child : children)
            {
                int result = alphaBetaMinimax(child, depth - 1, false , alpha, beta);

                betaValue = Math.min(betaValue, result);
                child.utility = betaValue;

                beta = Math.min(betaValue , beta);

                if(beta <= alpha)
                {

                    break;
                }
            }
            return betaValue;
        }

        else
        {
            int alphaValue = Integer.MIN_VALUE;
            for (State child : children)
            {
                int result = alphaBetaMinimax(child, depth - 1, true , alpha , beta);

                alphaValue = Math.max(alphaValue , result);
                child.utility = alphaValue;

                alpha = Math.max(alpha , alphaValue);

                if(alpha >= beta)
                {
                  break;
                }
            }
            return alphaValue;
        }
    }

    public void clearTree()
    {
        level1.clear();
        level2.clear();
        level3.clear();
    }

    public State play(State currentState , boolean computerTurn , int level)
    {
        State newState = new State();

        if(computerTurn)  // computer turn
        {
            System.out.println("******************** Computer Turn ********************");
            constructTree(level, currentState);
            int utility = alphaBetaMinimax(currentState, level, true , Integer.MIN_VALUE , Integer.MAX_VALUE);
            ArrayList<State> children = getAllChildren(currentState);
            for (State child : children)
            {
                if(child.utility == utility)
                {
                    child.printStar();
                    newState = new State(child.star);
                    break;
                }
            }
            children.clear();
            clearTree();
        }

        else
        {
            while (true)
            {
                System.out.println("******************** Your Turn ********************");

                System.out.println("Enter The Desired Marble Indices:");
                Scanner input = new Scanner(System.in);

                System.out.print("Enter Marble row (starting from 0 from the Top): ");
                int marbleI = input.nextInt();

                System.out.print("Enter Marble column (starting from 0 from the left including '-'): ");
                int marbleJ = input.nextInt();
                System.out.println();

                boolean check = true;
                int num = 0;

                for (Marble marble : currentState.humanMarbles)
                {
                    if (marble.currentPositionY == marbleI && marble.currentPositionX == marbleJ)
                    {
                        if (marble.possibleMoves.size() != 0 && marble.possibleHops.size() != 0)
                        {
                            System.out.println("Possible Moves:");
                            for(int row = 0; row < marble.possibleMoves.size(); row ++)
                            {
                                System.out.println("(" + marble.possibleMoves.get(row).getKey() + " , " + marble.possibleMoves.get(row).getValue() + ")");
                            }

                            System.out.println();

                            System.out.println("Possible Hops:");
                            for(int row = 0; row < marble.possibleHops.size(); row ++)
                            {
                                System.out.println("(" + marble.possibleHops.get(row).getKey() + " , " + marble.possibleHops.get(row).getValue() + ")");
                            }
                            check = true;
                        }

                        else if (marble.possibleMoves.size() == 0 && marble.possibleHops.size() != 0)
                        {
                            System.out.println("No Possible Moves!");

                            System.out.println();

                            System.out.println("Possible Hops:");
                            for(int row = 0; row < marble.possibleHops.size(); row ++)
                            {
                                System.out.println("(" + marble.possibleHops.get(row).getKey() + " , " + marble.possibleHops.get(row).getValue() + ")");
                            }
                            check = true;
                        }

                        else if (marble.possibleMoves.size() != 0 && marble.possibleHops.size() == 0)
                        {
                            System.out.println("Possible Moves:");
                            for(int row = 0; row < marble.possibleMoves.size(); row ++)
                            {
                                System.out.println("(" + marble.possibleMoves.get(row).getKey() + " , " + marble.possibleMoves.get(row).getValue() + ")");
                            }

                            System.out.println();

                            System.out.println("No Possible Hops!");
                            check = true;
                        }
                        else
                        {
                            System.out.println("No Possible Moves!");
                            System.out.println("No Possible Hops!");
                            check = false;
                        }
                        break;
                    }
                    
                    else
                    {
                        check = false;
                    }
                    num ++;
                }
                
                if (check)
                {
                    while(true)
                    {
                        System.out.println();
                        System.out.println("Enter The Destination Marble Indices:");
                        System.out.print("Enter Marble row : ");
                        int newMarbleI = input.nextInt();
                        System.out.print("Enter Marble column : ");
                        int newMarbleJ = input.nextInt();
                        System.out.println();

                        Pair<Integer, Integer> pair = new Pair<>(newMarbleI, newMarbleJ);
                        if (currentState.humanMarbles.get(num).possibleMoves.contains(pair) || currentState.humanMarbles.get(num).possibleHops.contains(pair)) {
                            char[][] newStar = move(currentState.star, marbleI, marbleJ, pair);
                            newState = new State(newStar);
                            newState.printStar();
                            break;
                        }
                        else
                        {
                            System.out.println("Enter Valid indices for the new position");
                        }
                    }
                    break;
                }
                else
                {
                    System.out.println("Invalid indices for marble!");
                    System.out.println("Please Try Again!");
                }
            }
        }
        return newState;
    }

    public void startGame()
    {
        Scanner input = new Scanner(System.in);
        System.out.println("Choose the difficulty level that you want to play with");
        System.out.println("1- Easy");
        System.out.println("2- Medium");
        System.out.println("3- Hard");

        int choice = input.nextInt();
        int level = 1;

        if (choice == 1)
            level = choice;

        else if (choice == 2)
            level = 2;

        else if (choice == 3)
            level = 3;

        else
            System.out.println("Invalid choice!");


        if (choice == 1 || choice == 2 || choice == 3)
        {
            State state = new State(Main.initialStar);

            while (true)
            {
                State newState = play(state, true, level);
                if (checkForWinner(newState) == 1)
                {
                    System.out.println("Computer Wins The Game!");
                    System.out.println("Better Luck Next Time!");
                    break;
                }
                System.out.println("ComputerScore  " + computerScore);
                System.out.println("HumanScore  " + humanScore);


                state = play(newState, false, level);
                if (checkForWinner(state) == -1)
                {
                    System.out.println("You Win The Game!");
                    System.out.println("Congratulations :)");
                    break;
                }
                System.out.println("ComputerScore  " + computerScore);
                System.out.println("humanScore  " + humanScore);

            }
        }
    }
}
