package textprocess.core;


import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

import textprocess.factories.FilterFactory;
import textprocess.filters.ComputeTermFrequencies;
import textprocess.interfaces.AbstractDataComponent;
import textprocess.sink.PrintTermFrequencies;
import textprocess.source.DataSource;
import textprocess.structures.Components;
import textprocess.structures.Pipe;


/**
 * This class represents a pipe-and-filter
 * component assembler. Based on the user 
 * inputs it creates a number of filters, 
 * and their incoming or outgoing
 * accompanying pipes; connects these pieces 
 * together and returns the resulting
 * pipeline to the main-controller of the
 * program, ready to be executed as threads.
 * 
 * @author David Olorundare
 *
 */
public final class Assembler 
{

	//============================================ PRIVATE VARIABLES =============================================================

	
	// Holds an instance to this class.
	private volatile static Assembler instance;

	// Represents an instance of the program configuration.
	ProgramConfig configuration = ProgramConfig.getInstance();
	
	// Represents the number of CPU processors available for use by the program.
	private final int NUM_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
	
	// Represents the number of active text filters.
	private final int FILTER_NUMBERS = ProgramConfig.getInstance().getFiltersToUse().length();
	
	// Represents the order in which filters should be executed.
	private AtomicIntegerArray filterProcessingOrder;
	
	// Represents an array of all filter typeIDs.
	private AtomicIntegerArray allFilters;
	
	// Represents a list of all data sources placed on threads.
	private CopyOnWriteArrayList<AbstractDataComponent> activeDataSources;
	
	// Represents a list of all filters placed on threads.
	private CopyOnWriteArrayList<AbstractDataComponent> activeDataFilters;
	
	// Represents a list of all data sinks placed on threads.
	private CopyOnWriteArrayList<AbstractDataComponent> activeDataSinks;
	
	// Represents a list containing all incoming pipes for the text-filters.
	private CopyOnWriteArrayList<Pipe<String>> filterPipes;
			
	// Represents a list containing all pipes for the data sinks.
	//private CopyOnWriteArrayList<Pipe<String>> sinkPipes;
	
	// Represents an object containing all data components used in the program.
	private volatile Components listOfDataComponents;

	// Represents a data pipe.
	private volatile Pipe<String> dataPipe;
	
	// Represents the outgoing pipe of the Data Source.
	private volatile Pipe<String> sourcePipe;
	
	// Represents the incoming pipe of the Data Computing Sink.
	private volatile Pipe<String> computingSinkPipe;

	// Represents the buffer-size of a pipe.
	private AtomicInteger pipeCapacity = new AtomicInteger((FILTER_NUMBERS * NUM_OF_PROCESSORS));
	
	// Represents the Data Source of the program.
	private volatile AbstractDataComponent source;
	
	// Represents the Data Sink of the program for computing the term frequencies.
	private volatile AbstractDataComponent computingSink;
	
	// Represents the Data Sink of the program for printing the most common term frequencies.
	private volatile AbstractDataComponent printingSink;
	
	
	//============================================ CONSTRUCTOR =============================================================
		
	
	/**
	 *  Private Constructor of the Assembler class.
	 */
	private Assembler(){	}
	
	
  /**
   * Returns a singleton instance of the Assembler class,
   * ensuring that only one instance of the Assembler is active 
   * at any single time.
   * 
   */
	public synchronized static Assembler getInstance() 
	{
      if (instance == null)
      {
          synchronized (Assembler.class)
          {
              if (instance == null)
              {
                  instance = new Assembler();
              }
          }
      }
      return instance;
   }
	
		
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Assembles the pipes-and-filter
	 * pipeline, ready to be executed,
	 * and returns a data-component 
	 * containing all elements (sources,
	 * sinks, and filters)
	 */
	public synchronized Components assemblePipeline()
	{
		createSource();
		createSink();
		createFilters();
		createPipes();
		connectPipesAndFilters();
		
		listOfDataComponents = Components.createComponents();
		listOfDataComponents.setListOfSources(activeDataSources);
		listOfDataComponents.setListOfSinks(activeDataSinks);
		listOfDataComponents.setListOfFilters(activeDataFilters);
		return listOfDataComponents;
	}
	
		
	//============================================ PRIVATE METHODS ==================================================
	
		//====================== HANDLE PIPE PLUMBINGS AND FILTER CONNECTIONS ========================================================
	
	
	/**
	 * Creates one or more data sources.
	 * 
	 */
	private synchronized void createSource()
	{
		activeDataSources = new CopyOnWriteArrayList<AbstractDataComponent>();
		source = DataSource.getInstance();
		activeDataSources.add(source);
	}
	
	
	/**
	 * Creates one or more data sinks.
	 * 
	 */
	private synchronized void createSink()
	{
		// Setup the Sinks where all the data finally goes; compute and print term frequencies.
		activeDataSinks = new CopyOnWriteArrayList<AbstractDataComponent>();
		computingSink = ComputeTermFrequencies.createComputeAndPrintTermFrequency();
		printingSink = PrintTermFrequencies.getInstance();
		activeDataSinks.add(computingSink);
		activeDataSinks.add(printingSink);
	}
	
	
	/**
	 * Creates a set of filters
	 * that will be used to transform
	 * text data.
	 * 
	 * @return	list containing text filters created in the order of their execution.
	 * 
	 */
	private synchronized void createFilters()
	{
		
		activeDataFilters = new CopyOnWriteArrayList<AbstractDataComponent>();
		allFilters = new AtomicIntegerArray(3);
		allFilters.set(0, 1); allFilters.set(1, 2); allFilters.set(2, 3);
		
		if ( configuration.getAllFilters().get() )
		{
			filterProcessingOrder = allFilters;
		}
		else
		{
			filterProcessingOrder = configuration.getFiltersToUse();
		}
		
		for (int i = 0; i < filterProcessingOrder.length(); i++)
		{
			activeDataFilters.add(FilterFactory.createFilter(filterProcessingOrder.get(i)));
		}	
	}
	
	
	/**
	 * For each active filter create
	 * two sets of pipes - one for 
	 * pulling/reading in input data 
	 * and the other for pushing/writing
	 * out data.
	 * 
	 */
	private synchronized void createPipes()
	{
		filterPipes = new CopyOnWriteArrayList<Pipe<String>>();

		// Create filter pipes
		if (FILTER_NUMBERS > 1)
		{
			for (int i = 1; i < FILTER_NUMBERS; i++)
			{
				dataPipe = new Pipe<String>(pipeCapacity.get());
				//dataPipe.setPipeName("Incoming Pipe " + i);
				filterPipes.add(dataPipe);
			}
		}
		
		// Create sink pipes (if there were multiple sinks)
//		sinkPipes = new CopyOnWriteArrayList<Pipe<String>>();
//		for (int i = 1; i <= activeDataSinks.size(); i++)
//		{
//			dataPipe = new Pipe<String>(pipeCapacity.get());
//			dataPipe.setPipeName("Incoming Pipe " + i);
//			sinkPipes.add(dataPipe);
//		}	
	}
	
	
	/**
	 * 
	 * For each active filter object connect
	 * each to its incoming and outgoing pipes
	 * ends, then connect the resulting structure
	 * to a source and a sink; to form the pipeline.
	 */
	private synchronized void connectPipesAndFilters()
	{		
		connectSourceToFirstPipe();
		connectMultipleFilterPipes();
		connectSinkToLastPipe();
	}
	
	
	/**
	 * Set the Data Source component, which has only
	 * an outgoing pipe socket to be the incoming pipe of the first
	 * text-filter. 
	 */
	private synchronized void connectSourceToFirstPipe()
	{
		// source has no incoming pipe, only outgoing pipe.
		// hence no outgoing pipe for the source is defined
		sourcePipe = new Pipe<String>(pipeCapacity.get());
		sourcePipe.setPipeName("Data Source Pipe");
		activeDataSources.get(0).setOutgoingPipe(sourcePipe);
		activeDataFilters.get(0).setIncomingPipe(sourcePipe);
	}
		
	
	/**
	 * Connect any intermediate text-filters between the Data Source and 
	 * Data Sink.
	 *
	 */
	private synchronized void connectMultipleFilterPipes()
	{
		//connect multiple filters with their respective input and output pipes
		if (FILTER_NUMBERS > 1)
		{
			for (int i = 0; i < activeDataFilters.size()-1; i++)
			{
				activeDataFilters.get(i).setOutgoingPipe(filterPipes.get(i));
				activeDataFilters.get(i + 1).setIncomingPipe(filterPipes.get(i));
			}
		}
	}
	
	
	/**
	 * The Data Sink component, has only an 
	 * incoming pipe. Connect the outgoing pipe of
	 * the last text-filter to the incoming pipe
	 * of the DataSink.
	 */
	private synchronized void connectSinkToLastPipe()
	{
		// code handles only one sink entry, but can be
		// modified to handle multiple sinks if needed.
		computingSinkPipe = new Pipe<String>(pipeCapacity.get());
		computingSinkPipe.setPipeName("Data Sink Pipe");
		activeDataSinks.get(0).setIncomingPipe(computingSinkPipe);
		
		// sink has no outgoing pipe, only incoming pipe.
		// connect the sinks incoming pipe to the socket of the last filter's outgoing socket 
		activeDataFilters.get((FILTER_NUMBERS-1)).setOutgoingPipe(computingSinkPipe);		
	}

		
}
