package textprocess.interfaces;

import textprocess.structures.Components;
import java.io.IOException;


/**
 * Represents an interface that should
 * be implemented by any class handling
 * producer, transformer, and consumer 
 * thread executions and management.
 * 
 * @author David Olorundare
 *
 */
public interface IController 
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	
	// No Private variables
		
		
	//============================================ CONSTRUCTOR =============================================================
		

	// No Constructor
		
	
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Perform operations on a given set of data components (producers,
	 * transformers, consumers) structured as a pipeline; and handles 
	 * their thread-execution and management.
	 * 
	 * @param dataComps set of data components (in a pipeline) to be executed.
	 * 
	 *  @throws InterruptedException if the thread execution is interrupted.
	 * 
	 * @throws IOException	if an error occurs while reading data from the buffer-pipe.
	 * 
	 */
	public void operate(Components dataComps) throws InterruptedException, IOException;
	
	
	/**
	 * Shuts down the execution of the consumers, transformers, 
	 * and producers.
	 * 
	 * @throws InterruptedException if the thread execution is interrupted.
	 * 
	 */
	public void shutDownProgram() throws InterruptedException;
		
		
	//============================================ PRIVATE METHODS ==================================================
		
	// No Private methods
		
	
}
