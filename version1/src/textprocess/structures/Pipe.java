package textprocess.structures;

import textprocess.interfaces.AbstractPipe;


/**
 * Represents a pipe-buffer in the
 * form of a blocking queue with a 
 * specific size, that holds text 
 * data to be processed by a text-filter.
 * 
 * @author David Olorundare
 * 
 */
public class Pipe<String> extends AbstractPipe<String>
{

	//============================================ PRIVATE VARIABLES =============================================================

	// No Private variables
	
	//============================================ CONSTRUCTOR =============================================================
		

	 // Used for Serialization
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor of the Pipe class.
	 * Creates a new pipe-buffer of 
	 * specified capacity.
	 */
	public Pipe(int capacity) 
	{
		super(capacity);
		
	}
		
		
	//============================================ PUBLIC METHODS =============================================================
	
	
	// No Public methods

	
	//============================================ PRIVATE METHODS ==================================================
		
	// No Private methods
}
