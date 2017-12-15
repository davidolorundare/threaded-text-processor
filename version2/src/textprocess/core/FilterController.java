package textprocess.core;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import textprocess.interfaces.AbstractDataComponent;
import textprocess.interfaces.IController;
import textprocess.structures.Components;


/**
 * The controller handles all thread operations
 * and management. Internally it makes use of the
 * ExecutorService Framework to manage thread
 * operations.
 * 
 * @author David Olorundare
 *
 */
public class FilterController implements IController
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	
	// Holds an instance to this class
	private volatile static FilterController instance;
		
	// Reference to the data components used in this program.
	Components programComponents;
	
	// Reference to the list of all sources.
	CopyOnWriteArrayList<AbstractDataComponent> sources;
	
	// Reference to the list of all sinks.
	CopyOnWriteArrayList<AbstractDataComponent> sinks;
	
	// Reference to the list of all filters.
	CopyOnWriteArrayList<AbstractDataComponent> filters;
	
	private final int THREADCOUNT = 7;
	
	// Represents a flag to checks if the Source is still producing data
	private AtomicBoolean readingEOFlag = new AtomicBoolean(true);
	
	// Reference to the thread executor that runs all sources, filters, and sinks.
	private volatile ExecutorService execution;
	
	// Reference to the number of threads that should be created by the thread executor.
	private AtomicInteger numOfThreads = new AtomicInteger(1);
	
	// Represents a CountDownLatch object that ensures that the 
	// thread of the 'compute term frequencies' operation finishes 
	// before the thread of the 'printing term frequencies' starts
	// printing the result out begins.
	private CountDownLatch sinkMonitor = new CountDownLatch(1);
	
		
	//============================================ CONSTRUCTOR =============================================================
		

	/**
	 * Private Constructor of the FilterController class.
	 * 
	 */
	private FilterController(){	}
	
	
	/**
	  * Returns a singleton instance of the FilterController class,
	  * ensuring that only one instance of the class is active 
	  * at any single time.
	  * 
	  */
	public synchronized static FilterController getInstance() 
	{
		if (instance == null)
	      {
	          synchronized (FilterController.class)
	          {
	              if (instance == null)
	              {
	                  instance = new FilterController();
	              }
	          }
	      }
	      return instance;
	}
		
		
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Sets the active source indicator flag
	 * 
	 * @param value flag indicator the state of the active source
	 * 
	 */
	public synchronized void setEOFlagFalse() 
	{ 
		readingEOFlag.compareAndSet(true, false); 
	}	
		
	
	/**
	 * Executes an assembled
	 * pipeline of data components
	 * using the Java ExecutorService
	 * Framework.
	 * 
	 * @throws IOException	if an error occurs while writing out.
	 * 
	 * @throws InterruptedException if the thread execution is interrupted.
	 * 
	 */
	public synchronized void operate(Components dataComponents) throws InterruptedException
	{
		execution = Executors.newCachedThreadPool();
		
		programComponents = dataComponents;
		sources = dataComponents.getListOfSources();
		sinks = dataComponents.getListOfSinks();
		filters = dataComponents.getListOfFilters();
		
		sinks.get(0).setThreadCoordinator(sinkMonitor);
		sinks.get(1).setThreadCoordinator(sinkMonitor);
		
		numOfThreads.set( (sources.size() + sinks.size() + filters.size()) );
		
		execute(sources, filters, sinks);
	}
		
	
	/**
	 * Shuts down the program components.
	 * 
	 * @throws InterruptedException if the thread execution is interrupted.
	 * 
	 */
	public synchronized void shutDownProgram() throws InterruptedException
	{
		execution.awaitTermination(100, TimeUnit.MILLISECONDS);
		execution.shutdown();
		execution.shutdownNow();
	}

	
	/**
	 * Returns the active source flag indicator
	 * 
	 * @return flag indicator of an active source
	 * 
	 */
	public synchronized boolean getEOFlag() 
	{ 
		return readingEOFlag.get();
	}
	
	
	//============================================ PRIVATE METHODS ==================================================
		
	
	/**
	 * Iterates through a list of the given source(s), filter(s), 
	 * and sink(s) and executes them on individual threads.
	 * 
	 * @param sourceList		list of sources to be run.
	 * @param filterList		list of filters to be run.
	 * @param sinkList 		list of sinks to be run.
	 */
	private void execute(CopyOnWriteArrayList<AbstractDataComponent> sourceList, CopyOnWriteArrayList<AbstractDataComponent> filterList, CopyOnWriteArrayList<AbstractDataComponent> sinkList) 
	{
		// iterate through the list of sources
		execution.submit(sourceList.get(0));
		
		
		// iterate through the list of filters
		
		// Parallelize the filter directly facing the data-Source,
		// to increase the response-time of the Source.
		if (filterList.size() == 1)
		{
			for (int i = 0; i < THREADCOUNT; i++) 
		    {
				execution.submit(filterList.get(0));   
		    }
		}
		else // if there is more than one active filter.
		{
			for(int i = 0; i < THREADCOUNT; i++ )
			{
				execution.submit(filterList.get(0));  
			}
			
			for(int i = 1; i < filterList.size(); i++)
			{
				execution.submit(filterList.get(i));
			}
		}
		
		// Old non-parallelized filter version
		//for (AbstractDataComponent filter : filterList ) { execution.submit(filter); }
	
		// The compute-term-frequency filter is
		// a sort of sink that collates the data 
		execution.submit(sinkList.get(0)); 
		
		// The print-term-frequency is a sink that prints
		// the final results
		execution.submit(sinkList.get(1));
	}
	
	
}
