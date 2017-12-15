package textprocess.source;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import textprocess.interfaces.AbstractDataComponent;
import textprocess.interfaces.IProducer;


/**
 * Represent the source of
 * data for the pipe-and-filter
 * program, reading in text data
 * from a user-defined file and
 * writing each line of the text
 * to an output buffered-pipe
 * for further processing by a
 * subsequent phase.
 * 
 * Data source ends producing when
 * it has read through the whole document.
 * 
 * @author David Olorundare
 *
 */
public class DataSource extends AbstractDataComponent implements IProducer 
{

	//============================================ PRIVATE VARIABLES =============================================================

	
	// Holds an instance to this class.
	private volatile static DataSource instance;

	// Represents an instrumented start operating time for this component.
	public AtomicLong startTime = new AtomicLong(1L);
		
	// Represents an instrumented stop operating time for this component.
	public AtomicLong stopTime = new AtomicLong(2L);
	
	
	//============================================ CONSTRUCTOR =============================================================
		
	
	/**
	 *  Private Constructor of the DataSource class.
	 */
	private DataSource(){ super(); }
	
  /**
   * Returns a singleton instance of the DataSource class,
   * ensuring that only one instance of the DataSource is active 
   * at any single time.
   * 
   */
	public synchronized static DataSource getInstance() 
	{
      if (instance == null)
      {
          synchronized (DataSource.class)
          {
              if (instance == null)
              {
                  instance = new DataSource();
              }
          }
      }
      return instance;
   }


	//========================================= PUBLIC METHODS =============================================================

	
	/**
	 * Runs a loop that reads in each line of
	 * text in the file and writes the content
	 * to an output buffer-pipe, this cycle
	 * continues and terminates when the end 
	 * of the document is reached.
	 * 
	 */
	public void run()  
	{
		try 
		{
			// Record the time-taken to load and process the document
			startTime.set(System.nanoTime());
			
			process();
			writeOutput();
			
			stopTime.set(System.nanoTime());
			logger.recordData("SourceLoading", startTime.get(), 	stopTime.get());
		}
		catch (InterruptedException | IOException e) 
		{ 
			e.printStackTrace(); 
			Thread.currentThread().interrupt(); 
		}
	}

		
	/**
	 * The Data source only reads from the text file
	 * and writes each line of the content to a 
	 * buffer-pipe, hence this method is empty.
	 * 
	 *  @throws InterruptedException if the thread execution is interrupted.
	 * 
	 * @throws IOException if an error occurs while reading the input file.
	 * 
	 */
	public synchronized void process() throws InterruptedException, IOException 
	{ 
		fhandler.readText();
	}

	
	/**
	 * Writes each line of the input text file into
	 * an output buffer-pipe. Terminates when the end of the
	 * document is reached. And this 
	 * 
	 * @throws InterruptedException if the thread execution is interrupted.
	 * 
	 * @throws IOException if an error occurs while writing to the outgoing buffer-pipe.
	 */
	public synchronized void writeOutput() throws InterruptedException, IOException
	{
		String line = fhandler.outputTextLine();
		while (!(line == null))
		{ 
			outgoingPipe.put(line);
			
			// Ensures thread-timing uniformity
			//Thread.sleep(50); 
			line = fhandler.outputTextLine();
		}
			// End of file reached.
			fhandler.closeFileReaders(); 
			controls.setEOFlagFalse();
	}

		
	//============================================ PRIVATE METHODS ==================================================
	
	// No Private Methods
}
