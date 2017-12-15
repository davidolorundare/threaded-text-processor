package textprocess.filters;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

import textprocess.interfaces.AbstractDataComponent;
import textprocess.interfaces.ITransformer;


/**
 * This class reads from each line of
 * text from an incoming buffer-pipe, 
 * and stemming words in each line to 
 * their root form, and passing the result
 * further down a downstream outgoing buffer-pipe
 * 
 * "BORROWED CODE (External Library Used)"
 * The class makes use of the Apache Lucene String
 * processing library for efficiently stemming
 * the words in each stream of text.
 *
 *
 * @author David Olorundare
 *
 */
public class StemWordsToRoot extends AbstractDataComponent implements ITransformer
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
	
	// Lucene String Processing Classes.
	private volatile TokenStream streamer;
	private volatile TermAttribute tAttribute;
	
	
	//============================================ CONSTRUCTOR =============================================================
		
	
	/**
	 * Constructor of the StemWordsToRoot class.
	 */
	public StemWordsToRoot() { super(); }
		
		
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Creator method that returns a new RemoveStopWords class instance.
	 * 
	 * @return	newly created RemoveStopWords.
	 */
	public synchronized static StemWordsToRoot createStemWordsToRoot() 
	{
		return new StemWordsToRoot();
	}
	
	
	/**
	 * Runs a thread loop that reads in each line of
	 * text in the file, processes it by stemming words 
	 * to their, and writes the result back into an output 
	 * buffer-pipe, this cycle continues and terminates when 
	 * the end of the document is reached.
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
				logger.recordData("WordStemmingFilter", startTime.get(), stopTime.get());
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
	 */
	public synchronized void process() throws IOException 
	{
		processedLine = stemWords(content);
		
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
	 * Stem to root form the words in a given 
	 * stream of text.
	 * 
	 * @param text	line of text to be processed.
	 * 
	 * @return	a clean line of text with all its words stemmed to their root form.
	 * 
	 * @throws IOException if an error occurs while processing the input stream.
	 */
	private synchronized String stemWords(String text) throws IOException
	{
		streamer = new StandardTokenizer(Version.LUCENE_30, new StringReader(text));
		streamer = new PorterStemFilter(streamer);
		tAttribute = streamer.getAttribute(TermAttribute.class);
		StringBuffer textBuffer = new StringBuffer();
		
		while (streamer.incrementToken())
		{
			if (textBuffer.length() > 0) { textBuffer.append(" "); }
			textBuffer.append(tAttribute.term());
		}
		return textBuffer.toString();
	}
	
	
}
