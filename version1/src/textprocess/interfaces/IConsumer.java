package textprocess.interfaces;

import java.io.IOException;


/**
 * An interface that has defines consumer operations.
 * 
 * Implemented by the Compute Term Frequency (filter)
 * and the Print Term Frequency (sink) classes; both 
 * are consumers.
 * 
 * @author David Olorundare
 *
 */
public interface IConsumer
{

	/**
	 *  Defines how this consumer should read
	 *  data from an attached incoming buffer-pipe.
	 *  
	 * @param queue incoming buffer-pipe of this consumer.
	 * 
	 * @throws InterruptedException if the thread execution is interrupted.
	 * 
	 * @throws IOException	if an error occurs while reading data from the buffer-pipe.
	 * 
	 */
	public void readInput() throws InterruptedException, IOException;
	
	
	/**
	 *  Defines how this consumer should process 
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
	

}
