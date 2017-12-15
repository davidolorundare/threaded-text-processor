package textprocess.filters;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import textprocess.interfaces.AbstractDataComponent;
import textprocess.interfaces.ITransformer;


/**
 * This class reads from each line of
 * text from an incoming buffer-pipe, 
 * and removes the non-alphabetic text
 * in the stream, and passing the result
 * further down a downstream outgoing buffer-pipe
 * 
 * @author David Olorundare
 *
 */
public class RemoveNonAlphaText extends AbstractDataComponent implements ITransformer
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	
	// Represents a temporary reference to a line of text.
	private volatile String processedLine;
	
	// Represents a line of text read from a pipe.
	private volatile String content = " ";
	
	// Represents an instrumented start operating time for this component.
	public AtomicLong startTime = new AtomicLong(1L);
		
	// Represents an instrumented stop operating time for this component.
	public AtomicLong stopTime = new AtomicLong(2L);
	
	
	//============================================ CONSTRUCTOR =============================================================
		

	/**
	 * Constructor of the RemoveNonAlphaText class.
	 */
	public RemoveNonAlphaText() { super(); }
		
		
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Creator method that returns a new RemoveStopWords class instance.
	 * 
	 * @return	newly created RemoveStopWords.
	 */
	public synchronized static RemoveNonAlphaText createRemoveNonAlphaText() 
	{
		return new RemoveNonAlphaText();
	}
	
	
	/**
	 * Runs a thread loop that reads in each line of
	 * text in the file, processes it by removing
	 * the non-alphabetic text, and writes the result 
	 * back into an output buffer-pipe, this cycle
	 * continues and terminates when the end 
	 * of the document is reached.
	 */
	public void run() 
	{
		while(getDataSourceEOFlag())
		{
			try 
			{
				// Record the time-taken to execute the filter
				startTime.set(System.nanoTime());
				
				readInput();
				writeOutput();
				
				// Ensure thread-timing uniformity (a speed-limiter)
				//Thread.sleep(100); 
				
				stopTime.set(System.nanoTime());
				logger.recordData("NonAlphabeticFilter", startTime.get(), stopTime.get());
			} 
			catch (InterruptedException | IOException e) 
			{ 
				//e.printStackTrace(); 
				Thread.currentThread().interrupt();
			}
		}
	}

	
	/**
	 * Read a line of text from an incoming buffer-pipe
	 * and act on it.
	 * 
	 * @throws InterruptedException if the thread execution is interrupted.
	 * 
	 * @throws IOException if an error occurs while reading the input stream.
	 */
	public synchronized void readInput() throws InterruptedException, IOException 
	{
		content = incomingPipe.take();
		process();
		// Discard the text to free up memory.
		content = null;
	}

	
	/**
	 * Process an incoming line of stream by removing
	 * all the non-alphabetic text from it.
	 * 
	 * @param	a line of text to be processed.
	 * 
	 */
	public synchronized void process() throws IOException 
	{
		processedLine = removeNonAlphaCharacters(content);
	}

	 
	/**
	 * Write out a processed line of text to
	 * an outgoing buffer-pipe.
	 * 
	 * @throws InterruptedException  if the thread execution is interrupted.
	 * 
	 */
	public synchronized void writeOutput() throws InterruptedException 
	{
		outgoingPipe.put(processedLine);
	}

		
	//============================================ PRIVATE METHODS ==================================================
		
	
	/** 
	 * Remove from a stream of text all non-alphabetic
	 * text.
	 * 
	 * @param text	line of text to be processed.
	 * 
	 * @return	a clean line of text with non-alphabetic text removed
	 * 
	 * @throws IOException if an error occurs while processing the input stream.
	 */
	private synchronized String removeNonAlphaCharacters(String text) throws IOException
	{
		// Split the line of text into individual word-terms and remove any non-alphabetic characters
		StringBuffer builder = new StringBuffer();
		String[] splitText = text.split(" ");
		
		for (int i = 0; i < splitText.length; i++)
		{
			splitText[i]= splitText[i].replaceAll("[^a-zA-Z]", " ");
			splitText[i]= splitText[i].replaceAll("[:,?'-]", " ");
		}
		
		for(String str : splitText)
		{
			builder.append(str); builder.append(" ");
		}
		
		String result = builder.toString();
		return result;
	}
	
	
}
