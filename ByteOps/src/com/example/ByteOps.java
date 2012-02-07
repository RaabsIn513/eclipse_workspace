package com.example;

public class ByteOps {

	private static short Byte2Short(byte a, byte b )
	{
		
		short sh = (short)a;
		sh <<= 8;
		short ret = (short)(sh | b);
		return ret; 
	}
	/*
	private static double twoBytes2Double(byte a, byte b )
	{
		short A = (short) (a & 0x00FF); // unsign them...
		short B = (short) (b & 0x00FF);
		short sh = (short)A;
		sh <<= 8;
		short ret = (short)(sh | B);
		return (double)ret; 
	}
	*/
	private static double twoBytes2Double(byte a, byte b )
	{
		short A = (short) (a & 0x00FF); // unsign them...
		short B = (short) (b & 0x00FF);
		double ret = A + B;
		return (double)ret; 
	}
	public static void main( String[] args ){
		byte ms = -5;
		byte ls = 0x11;
		
		System.out.println("ms: " + Integer.valueOf(ms));
		System.out.println("ls: " + Integer.valueOf(ls));
		
		
		// concatenate to make as one number
		double concat = twoBytes2Double(ms, ls);
		
		System.out.println("concat: " + concat);
	}
}
