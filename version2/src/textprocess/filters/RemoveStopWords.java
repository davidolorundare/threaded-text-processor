package textprocess.filters;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

import textprocess.interfaces.AbstractDataComponent;
import textprocess.interfaces.ITransformer;


/**
 * This class reads from a predefined stopwords
 * text file and removes stop-words from the text
 * in an incoming buffer pipe stream.
 *
 * "BORROWED CODE (External Library Used)"
 * This class makes use of the Apache Lucene String
 * processing library for efficiently removing
 * the stopwords from the stream of text.
 *
 *
 * @author David Olorundare
 *
 */
public class RemoveStopWords extends AbstractDataComponent implements ITransformer
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	
	// Represents an instrumented start operating time for this component.
	public AtomicLong startTime = new AtomicLong(1L);
		
	// Represents an instrumented stop operating time for this component.
	public AtomicLong stopTime = new AtomicLong(2L);
	
	// Represents a temporary reference to a line of text.
	private volatile String processedLine;

	// Represents a line of text read from a pipe.
	private volatile String content = " ";
	
	// Apache Lucene String Processing Classes.
	private volatile TokenStream streamer;
	private volatile TermAttribute tAttribute;
	

	//============================================ CONSTRUCTOR =============================================================
		

	/**
	 * Constructor of the RemoveStopWords class.
	 */
	public RemoveStopWords() { super(); }
		
		
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Creator method that returns a new RemoveStopWords class instance.
	 * 
	 * @return	newly created RemoveStopWords.
	 */
	public synchronized static RemoveStopWords createRemoveStopWords() 
	{
		return new RemoveStopWords();
	}
	
	
	/**
	 * Runs a thread loop that reads in each line of
	 * text in the file, processes it by removing
	 * the stopwords, and writes the result back
	 * into an output buffer-pipe, this cycle
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
				logger.recordData("StopWordFilter", startTime.get(), stopTime.get());
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
	 * Process an incoming stream of text by removing
	 * the stop-words from it.
	 * 
	 * @param	a line of text to be processed.
	 * @throws IOException if an error occurs while processing the input stream.
	 */
	public synchronized void process() throws IOException 
	{
		processedLine = removeAllStopWords(content);
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
	 * Remove from a stream of text certain words
	 * that are from a user-defined list of stop-words.
	 * 
	 * @param text	line of text to be processed.
	 * 
	 * @return	a clean line of text with certain stop-words removed. 
	 * @throws IOException if an error occurs while processing the input stream.
	 */
	private synchronized String removeAllStopWords(String text) throws IOException 
	{
		String tempText = " ";
		streamer = new StandardTokenizer(Version.LUCENE_30, new StringReader(text));
		streamer = new StopFilter(true, streamer, fhandler.getStopWordList());
		
		StringBuffer textBuffer = new StringBuffer();
		
		tAttribute = streamer.getAttribute(TermAttribute.class);
		while (streamer.incrementToken())
		{
			if (textBuffer.length() > 0) { textBuffer.append(" "); }
			textBuffer.append(tAttribute.term());
		}
		tempText = textBuffer.toString(); 
		return tempText;
	}
	
	
}
