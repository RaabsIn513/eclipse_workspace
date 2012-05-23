package midterm1;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public  class Deck {
	boolean CardDealt;
	protected String[] cards;
   
   public Deck()throws IOException
   {
      FileInputStream fis = new FileInputStream("E:\\52.txt");
      Scanner infile = new Scanner(fis);
      cards = new String[52];
      int j = 0;
      // Populate the cards array
          while(infile.hasNext()){
	         cards[j] = infile.nextLine();
	         j++;
          }
   }
               
   public String deal_card(){
       // A. Get random card that hasn't been drawn
	   // B. set selected card as drawn in the cards array
	   // C. and return drawn card
	   
	   // A:
	   Random r = new Random();
	   int x = r.nextInt(52);
	   while(cards[x].equals("drawn")){
		   x = r.nextInt(52);
	   }
	   // B:
	   String tmp = cards[x];
	   cards[x]  = "drawn";
       // C:
	   return tmp;
   }


}
