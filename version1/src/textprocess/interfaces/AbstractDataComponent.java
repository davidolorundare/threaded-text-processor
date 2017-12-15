package textprocess.interfaces;

import java.util.concurrent.CountDownLatch;

import textprocess.core.FilterController;
import textprocess.logging.DataInstrumentation;
import textprocess.utils.FileHandler;
import textprocess.utils.TextAnalyzer;


/**
 * An abstract class representing
 * a Data object which performs
 * one or more of these operations:
 * reads data from am incoming-buffer, 
 * transforms the data in some way, 
 * and writes the result out in an 
 * outgoing-buffer.
 * 
 * Every data-component is Runnable
 * on a Thread.
 * 
 * @author David Olorundare
 *
 */
public abstract class AbstractDataComponent implements Runnable
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	// Represents a file-handling object
	public volatile FileHandler fhandler = FileHandler.getInstance();
	
	// Represents a data-instrumentation variable used for logging response times.
	public volatile DataInstrumentation logger = DataInstrumentation.getInstance();
	
	// Represents the incoming buffer-pipe of this data component.
	public volatile AbstractPipe<String> incomingPipe;
	
	// Represents the outgoing buffer-pipe of this data component.
	public volatile AbstractPipe<String> outgoingPipe;

	// Represents the active producer flag.
	public volatile FilterController controls = FilterController.getInstance();
	
	// Represents a text-processor instance.
	public volatile TextAnalyzer textProcessor = TextAnalyzer.getInstance();
	
	// References a variable used for inter-component thread-coordination.
	public CountDownLatch componentWaiter = null;

	
	//============================================ CONSTRUCTOR =============================================================
	
	
	// No Explicit Constructor

	
	//============================================ PUBLIC METHODS =============================================================
	

		/**
		 * Set this DataComponent's incoming buffer-pipe.
		 * 
		 * @param in		incoming buffer of the DataComponent.
		 * 
		 */
		public synchronized void setIncomingPipe(AbstractPipe<String> in) 
		{ 
			incomingPipe = in; 
		}
		
		
		/**
		 * Set this DataComponent's outgoing buffer-pipe.
		 * 
		 * @param out	outgoing buffer of the DataComponent.
		 * 
		 */
		public synchronized void setOutgoingPipe(AbstractPipe<String> out) 
		{ 
			outgoingPipe = out; 
		}
	
		
		/**
		 * Returns the incoming buffer-pipe of this DataComponent.
		 * 
		 * @return	incoming DataComponent buffer-pipe.
		 */
		public synchronized AbstractPipe<String> getIncomingPipe()
		{
			return incomingPipe;
		}
		
		
		/**
		 * Returns the outgoing buffer-pipe of this DataComponent.
		 * 
		 * @return outgoing DataComponent buffer-pipe
		 */
		public synchronized AbstractPipe<String> getOutgoingPipe() 
		{ 
			return outgoingPipe; 
		}
		
		
		/**
		 * Returns the active source status flag.
		 * 
		 * @return flag value indicating the status of the active source
		 */
		public synchronized boolean getDataSourceEOFlag()
		{ 
			return controls.getEOFlag(); 
		}
		
		
		/**
		 * Sets the thread-coordinator variable of this data component.
		 * 
		 * @param timer number of operations required before this data component's thread is unlocked.
		 * 
		 * @throws InterruptedException if the thread execution is interrupted.
		 */
		public void setThreadCoordinator(CountDownLatch timer) throws InterruptedException
		{
			componentWaiter = timer;
		}
		
		
		/**
		 * Defines how the Response-time of this DataComponent is measured.
		 * Passes the component's name, start and end operation times to
		 * a logging/instrumentation object.
		 * 
		 * @param nameOfComponent	name of the data component.
		 * 
		 * @param startExecutionTime	the start operating time of the data component.
		 * 
		 * @param stopExecutionTime	the stop operating time of the data component.
		 * 
		 */
		public synchronized void instrument(String nameOfComponent, Long startExecutionTime, Long stopExecutionTime)
		{ 
			logger.recordData(nameOfComponent, startExecutionTime, stopExecutionTime);
		}
		
		
		//============================================ PRIVATE METHODS ==================================================
			
		// No Private Methods
}
