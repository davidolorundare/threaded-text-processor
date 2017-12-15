package textprocess.interfaces;

import java.io.IOException;


/**
 * An interface that defines producer operations.
 * 
 * The Data-source class is a producer, and implements
 * this interface.
 * 
 * Producer have their incoming pipe as null,
 * and their outgoing pipe sends Strings.
 * 
 * @author David Olorundare
 *
 */
public interface IProducer 
{

	
	/**
	 *  Defines how this Producer should transform 
	 *  the read-in data
	 *  
	 * @param item textual data to be processed.
	 * 
	 * @throws InterruptedException	if the thread execution is interrupted.
	 * 
	 * @throws IOException if an error occurs while operating on the data.
	 * 
	 */
	public void process() throws InterruptedException, IOException;
	
	
	/**
	 *  Defines how this Producer should write out
	 *  data to an attached outgoing buffer-pipe.
	 *  
	 * @param queue outgoing buffer-pipe of this Producer.
	 * 
	 * @throws InterruptedException	if the thread execution is interrupted.
	 * 
	 * @throws IOException if an error occurs while writing data to the buffer-pipe.
	 * 
	 */
	public void writeOutput() throws InterruptedException, IOException;
	
	
}
