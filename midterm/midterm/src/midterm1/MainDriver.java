package midterm1;


import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainDriver  {
	private static int ncard;
    public static PrintWriter pw;
    private static int computerScore = 0;
    private static int ourScore = 0;
      
    public static void main(String [] args)throws IOException{
             
              //Scanner keyboard = new Scanner(System.in);
              //System.out.println("Enter file location right hurr");
              //String s = keyboard.nextLine();
              String s = "E:\\midterm_out.txt";
              s = s.intern();
              FileOutputStream output = new FileOutputStream(s);
              pw = new PrintWriter(output);
              
              System.out.println("Computer showing this devious hand:");
                            
              BlackJack b;
              b = new BlackJack();
              System.out.println("Computer shows: ");
              int dealer1 = b.get_BJ_Value(b.getCard(true))[0];
              int dealer2 = b.get_BJ_Value(b.getCard(false))[0];
              //System.out.println(dealer2);
              computerScore = dealer1 + dealer2;
             
              //Go to blackjack to figure out 2 random cards from array located in Deck
              //Show 1 random card
              System.out.println("One card facing down");
              System.out.println("This is your hand:");
              int ours1 = b.get_BJ_Value(b.getCard(true))[0];
              int ours2 = b.get_BJ_Value(b.getCard(true))[0];
              ourScore = ours1 + ours2;
              System.out.println("Your Score: " + ourScore);
              //Go to blackjack to figure out 2 random cards from array located in Deck
              //Show both random cards
              //Go to blackjack to figure out values of each card, and add them together to show running total
              System.out.println("Do you want to hit? Press 1, but otherwise press 2");
              //User picks if they want to hit or stand
              //If they hit, repeat the process of getting a random card
              //If they stand, then blackjack declares a winner by who has the highest score below 21
              //We're not worrying about busting yet
       }
}
