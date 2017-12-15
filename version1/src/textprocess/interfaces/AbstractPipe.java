package textprocess.interfaces;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;


/**
 * An abstract class representing
 * a Buffer Pipe object used for
 * temporarily holding text data
 * to be processed.
 * 
 * @author David Olorundare
 *
 */
public abstract class AbstractPipe<T> extends ArrayBlockingQueue<T>
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	
	// Used for serialization purposes
	private static final long serialVersionUID = 1L;
	
	// Represents the name of the pipe.
	private AtomicReference<String> pipeName = new AtomicReference<String>(" "); 
		
			
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 * Constructor of the AbstractPipe class.
	 * 
	 * @param capacity	size of the buffer.
	 */
	public AbstractPipe(int capacity) 
	{
		super(capacity);
	}
		
		
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Sets the name of the pipe,
	 * 
	 * @param value 	name to assign to this pipe.
	 * 
	 */
	public synchronized void setPipeName(String value) 
	{ 
		pipeName.set(value); 
	}	
	
	
	/**
	 * Returns the name of this pipe.
	 * 
	 * @return name of current pipe..
	 * 
	 */
	public synchronized String getPipeName() 
	{ 
		return pipeName.get(); 
	}
	
		
	//============================================ PRIVATE METHODS ==================================================
		
	// No Private methods
		
}
