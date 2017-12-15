package textprocess.sink;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import textprocess.interfaces.AbstractDataComponent;
import textprocess.interfaces.IConsumer;
import textprocess.logging.DataInstrumentation;

/**
 * Prints the 10 most common 
 * term frequencies in descending 
 * order.
 * In the case of a tie, all tied-
 * terms are printed in alphabetic
 * order
 * 
 * @author David Olorundare
 *
 */
public final class PrintTermFrequencies extends AbstractDataComponent implements IConsumer
{
	//============================================ PRIVATE VARIABLES =============================================================

	
	// Represents an instrumented start operating time for this component.
	public AtomicLong startTime = new AtomicLong(1L);
		
	// Represents an instrumented stop operating time for this component.
	public AtomicLong stopTime = new AtomicLong(2L);
	
	// Holds an instance to this class
	private volatile static PrintTermFrequencies instance;

	// Holds a reference to a response-time logger instance, used for getting all recorded component response-times.
	DataInstrumentation instrument = DataInstrumentation.getInstance();
	
	// Represents the output common term-frequency information 
	// to be displayed to the console and stored in an external file.
	private StringBuffer output;
	
	// Represents a mapping between each word-term in the text and its occurrence.
	private ConcurrentHashMap<String, Integer> termFrequency;
	
	
	//============================================ CONSTRUCTOR =============================================================
		

	/**
	 * Private Constructor of the PrintTermFrequency class.
	 * 
	 */
	private PrintTermFrequencies(){	}
	
	
	/**
	  * Returns a singleton instance of the PrintTermFrequency class,
	  * ensuring that only one instance of the class is active 
	  * at any single time.
	  * 
	  */
	public synchronized static PrintTermFrequencies getInstance() 
	{
		if (instance == null)
	      {
	          synchronized (PrintTermFrequencies.class)
	          {
	              if (instance == null)
	              {
	                  instance = new PrintTermFrequencies();
	              }
	          }
	      }
	      return instance;
	}
		
		
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Reads the HashMap object from ComputeTermFreq.
	 * class and Processes it by printing in order.
	 */
	public void run()
	{
		try 
		{
			componentWaiter.await();
			// Record the time-taken to print/save the results
			startTime.set(System.nanoTime());
			
			readInput();
			process();
			
			stopTime.set(System.nanoTime());
			logger.recordData("PrintTermFrequencySink", startTime.get(), stopTime.get());
			
			System.out.println("\n======== (Average) RESPONSE-TIMES ========\n");
			// Output the response-times of each component
			instrument.outputAnalysis();
			
			// Finally, shutdown the program.
			controls.shutDownProgram();
		} 
		catch (InterruptedException | IOException e) 
		{
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
	
	
	/**
	 * Reads in the list of computed term frequencies.	
	 * 
	 */
	public synchronized void readInput()  
	{
		termFrequency = textProcessor.getCurrentTermFrequency();	
		
	}


	/**
	 * Prints out the 10 most common terms and their frequency.
	 * 
	 * @throws IOException	if an error occurs while writing out.
	 * 
	 * @throws InterruptedException if the thread execution is interrupted.
	 * 
	 */
	public synchronized void process() throws InterruptedException, IOException 
	{
		printAnalysisToScreen(termFrequency);
	}
	
		
	//============================================ PRIVATE METHODS ==================================================
	
	
	/**
	 * Helper method that prints out the 10 most common terms
	 * and their frequency.
	 * 
	 * @param termMappingData	the text data to print out.
	 * 
	 * @throws IOException if an error occurs while writing out.
	 */
	private synchronized void printAnalysisToScreen(ConcurrentHashMap<String, Integer> termMappingData) throws IOException
	{
		AtomicInteger count = new AtomicInteger(0);
		
		output = new StringBuffer();		
		output.append("\n======== THE 10 MOST FREQUENT WORD-TERMS ========\n");
		
		// Words are ordered by frequency (in the descending order), and words which 
		// have the same frequency count are ordered by lexicographical order (in the ascending order)
		List<Entry<String, Integer>> sortedEntries = termMappingData.entrySet().stream().sorted(Entry.<String, Integer>comparingByValue().reversed().thenComparing(Entry::getKey)).collect(Collectors.toList());
		
		// Display the analysis results.
		System.out.println(output.toString());
		
		output.append("\n");
		for (Entry<String, Integer> s : sortedEntries)
		{
			if (count.get() == 10) { break; }
			output.append(s.toString().replace('=', ' ' ) + "\n");
			System.out.println(s.toString().replace('=', ' ' ) );
			count.incrementAndGet();
		}
	
		// Save the term-frequencies to an external file.
		//printAnalysisToFile(output.toString());
	}
	
	
	/**
	 * Helper method that prints some given
	 * text to an external file.
	 * 
	 * @param data	the text information to store in an external file.
	 * 
	 * @throws IOException	if an error occurs while writing to the output file.
	 */
	private synchronized void printAnalysisToFile(String data) throws IOException
	{
		fhandler.writeToFile(data);	
	}

	
}
