package com.example;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

public class dataGen implements Runnable {
	
    // encapsulates management of the observers watching this 
	// data source for update events:
    class MyObservable extends Observable {
    	
	    public void notifyObservers(dataGen b) {
	        super.notifyObservers(b);	        
	    }
	    
	    public void clearChanged()
	    {
	    	super.clearChanged();
	    }
	    
	    public void setChanged()
	    {
    		super.setChanged();
	    }
    }
    
    private int dataGenX = 0;
    private int dataGenY = 0;
    
    private MyObservable notifier;
    {
        notifier = new MyObservable();
    }
    
    public void run(){
    	Random rand = new Random();
    	
    	while( true )
    	{
    		try {
				Thread.sleep(10);
				
				dataGenY = rand.nextInt(100);		
				dataGenX = dataGenX + 1;
				
				notifier.setChanged();
				notifier.notifyObservers(this);
				notifier.clearChanged();
				
    		} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    public Number getX( )
    {
    	return (Number) dataGenX;
    }
    
    public Number getY( )
    {
    	return (Number) dataGenY;
    }
    
    public int getItemCount()
    {
    	return 10;
    }
    
    public void addObserver(Observer observer) {
        notifier.addObserver(observer);
    }
 
    public void removeObserver(Observer observer) {
        notifier.deleteObserver(observer);
    }
}
