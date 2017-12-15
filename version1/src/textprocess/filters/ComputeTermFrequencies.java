package textprocess.filters;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import textprocess.interfaces.AbstractDataComponent;
import textprocess.interfaces.IConsumer;


/**
 * 
 * This class represents a word-term
 * frequency counter that computes 
 * rate of occurrence of each word-term
 * read in the text from the incoming 
 * buffered-pipe, and prints out the
 * 10 most common term frequencies.
 * 
 * @author David Olorundare
 *
 */
public class ComputeTermFrequencies extends AbstractDataComponent implements IConsumer
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	
	// Represents an instrumented start operating time for this component.
	public AtomicLong startTime = new AtomicLong(1L);
		
	// Represents an instrumented stop operating time for this component.
	public AtomicLong stopTime = new AtomicLong(2L);
	
	// Represents the text content read-in from the incoming pipe.
	private AtomicReference<String> content = new AtomicReference<String>(" ");

	
	//============================================ CONSTRUCTOR =============================================================
		
	
	/**
	 * Constructor of the ComputeAndPrintTermFrequency class.
	 */
	public ComputeTermFrequencies() { super(); }
		
	
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Creator method that returns a new ComputeAndPrintTermFrequency class instance.
	 * 
	 * @return	newly created ComputeAndPrintTermFrequency.
	 */
	public synchronized static ComputeTermFrequencies createComputeAndPrintTermFrequency() 
	{
		return new ComputeTermFrequencies();
	}
	
	
	/**
	 * Runs a loop that reads in each line of
	 * text in the file and compute the occurrence
	 * rates of terms in text.
	 */
	public void run() 
	{
		try
		{
			// Record the time-taken to execute the filter
			startTime.set(System.nanoTime());
			
			readInput();  
			
			stopTime.set(System.nanoTime());
			logger.recordData("ComputeTermFrequencyFilter", startTime.get(), stopTime.get());
			componentWaiter.countDown();
		}
		catch (InterruptedException | IOException e) 
		{ 
			//e.printStackTrace(); 
			Thread.currentThread().interrupt();
		} 
	}

	
	/**
	 * Reads data from the incoming buffer-pipe, and passes
	 * it to a text processor.
	 * 
	 * @throws InterruptedException	if the thread execution is interrupted.
	 * 
	 * @throws IOException	if an error occurs while reading from the incoming buffered-pipe.
	 */
	public synchronized void readInput() throws InterruptedException, IOException 
	{
		while (getDataSourceEOFlag())
		{
			content.set(incomingPipe.take());
			
			// Ensure thread-timing uniformity (a speed-limiter)
			//Thread.sleep(100);
			
			process();
			// Discard the text to free up memory.
			content.set(null);
		}
	}

	
	/**
	 * For each line of text in the incoming pipe
	 * compute the rate of occurrence of each word-term.
	 * 
	 * @param text data from an incoming pipe.
	 */
	public synchronized void process() 
	{
		// for each word in a line, tally-up its frequency.
		// do sentence-segmentation first.
		CopyOnWriteArrayList<String> result = textProcessor.sentenceSegmementation(content.get());
		
		// then do tokenize each word and count the terms.
		textProcessor.tokenizeAndCount(result);
	}

	
	//============================================ PRIVATE METHODS ==================================================
	
	// No Private methods.
}
