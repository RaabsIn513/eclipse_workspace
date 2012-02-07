package school.work;

import java.io.IOException;

public class Select2 {

	public static void main(String[] args)
	{
		System.out.println("hell0");
		
		int[] myList = { 2, 3, 4, 3, 3 };
		
		System.out.println("Before:");
		print( myList );
		System.out.println("After:");
		try {
			System.out.println(fastmedian(myList));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("badSwap");
		int x = 8;
		int y = 13;
		System.out.println("x: " + x );
		System.out.println("y: " + y );
		badSwap( x, y );
		System.out.println("x: " + x );
		System.out.println("y: " + y );
		System.out.println("goodSwap");
		System.out.println("x: " + x );
		System.out.println("y: " + y );
		goodSwap( (Object)x, (Object)y );
		System.out.println("x: " + x );
		System.out.println("y: " + y );
	}
	
	public static int select2( int[] L, int low, int high, int t, int x )
	{
		if( high - low + 1 <= 5)
		{
			// ad hoc method here...
		}
		
		int q = (high - low + 1)/5;
		
		try {
			fastmedian( L );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -1;
	}
	
	public static void badSwap(int var1, int var2)
	{
	  int temp = var1;
	  var1 = var2;
	  var2 = temp;
	}
		
	public static int fastmedian(int[] a) throws IOException
	{
		if( a.length != 5 )
		{
			throw new IOException("fastmedian accepts only lists of size 5");
		}
		int t = 0;
		int y = 0;
		// swap a[1] with a[0];
		if (a[0]<a[1])
		    t=a[1]; a[1]=a[0]; a[0]=t;
		// swap a[3] with a[2];
		if (a[2]<a[3])
		    t=a[2]; a[2]=a[3]; a[3]=t;

		// swap a[0] with a[2] and swap a[1] with a[3]
		if (a[0] < a[2])
		{
			t=a[0]; a[0]=a[2]; a[2]=t;
		    t=a[1]; a[1]=a[3]; a[3]=t;
		}

		// swap a[1] with a[4]
		if (a[1] < a[4])
		      t=a[1]; a[1]=a[4]; a[4]=t;

		if (a[1]>a[2])
		{	    
			if (a[2]>a[4])
		        y=a[2];
		    else
		        y=a[4];
		}
		else if (a[1]>a[3])
		{        
			y=a[1];
		}
		else
			y=a[3];
		return y;
	}

	public static void print( int[] a )
	{
		for( int i = 0; i < a.length; i++ )
		{
			if( i != a.length -1 )
				System.out.println( a[i] + ", ");
			else
				System.out.println( a[i] );
		}
	}
	
}
