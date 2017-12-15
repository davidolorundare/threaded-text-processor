package textprocess.interfaces;

import java.io.IOException;


/**
 * An interface that defines transformer operations.
 * 
 * All the filters are transformers and implement this
 * interface.
 * 
 * Transformers take String in incoming pipes and
 * String as outgoing pipes
 * 
 * @author David Olorundare
 *
 */
public interface ITransformer
{
	
	/**
	 *  Defines how this Transformer should read
	 *  data from an attached incoming buffer-pipe.
	 *  
	 * @param queue incoming buffer-pipe of this Transformer.
	 * 
	 * @throws InterruptedException if the thread execution is interrupted.
	 * 
	 * @throws IOException	if an error occurs while reading data from the buffer-pipe.
	 * 
	 */
	public void readInput() throws InterruptedException, IOException;
	
	
	/**
	 *  Defines how this Transformer should transform 
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
	 *  Defines how this Transformer should write out
	 *  data to an attached outgoing buffer-pipe.
	 *  
	 * @param queue outgoing buffer-pipe of this Transformer.
	 * 
	 * @throws InterruptedException	if the thread execution is interrupted.
	 * 
	 * @throws IOException if an error occurs while writing data to the buffer-pipe.
	 * 
	 */
	public void writeOutput() throws InterruptedException, IOException;
	
	
}
