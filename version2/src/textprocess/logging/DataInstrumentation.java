package textprocess.logging;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import textprocess.interfaces.IInstrumentation;


/**
 *  * Records every time-taken by the
 * data components for each of their
 * read-process-write operations per textline.
 * 
 * Returns to the console and an internal file,
 * the computed response-times of each data component;
 * for later visualization.
 * 
 * 
 * @author David Olorundare
 *
 */
public final class DataInstrumentation implements IInstrumentation
{
	//============================================ PRIVATE VARIABLES =============================================================

	
	// Holds an instance to this class
	private volatile static DataInstrumentation instance;
	
	// Represents each data-components and their response-time for this session.
	ConcurrentHashMap<String, ArrayList<Long>> componentResponseTime = new ConcurrentHashMap<String, ArrayList<Long>>();
	
	// Represents a list of data components and their average response-time for this session.
	ConcurrentHashMap<String, Long> averageResponseTimes = new ConcurrentHashMap<String, Long>();
	
	// Represents a time difference between two response-times.
	AtomicLong timeDiff = new AtomicLong(1L);
	
	// Represents a list containing the execution/response times of a data-component. 
	ArrayList<Long> responseTimeList;

	
	//============================================ CONSTRUCTOR =============================================================
		

	/**
	 * Private Constructor of the DataInstrumentation class.
	 * 
	 */
	private DataInstrumentation(){	}
	
	
	/**
	  * Returns a singleton instance of the DataInstrumentation class,
	  * ensuring that only one instance of the class is active 
	  * at any single time.
	  * 
	  */
	public synchronized static DataInstrumentation getInstance() 
	{
		if (instance == null)
	      {
	          synchronized (DataInstrumentation.class)
	          {
	              if (instance == null)
	              {
	                  instance = new DataInstrumentation();
	              }
	          }
	      }
	      return instance;
	}


	//============================================ PUBLIC METHODS =============================================================
	
		
	/**
	 * Records the response-time (in nanoseconds) of a given data component.
	 * 
	 * @param componentName	name of the data component.
	 * 
	 * @param responseTime	response-time of the data component (in nanoseconds).
	 */
	public synchronized void recordData(String componentName, Long responseStartTime, Long responseStopTime) 
	{
		timeDiff.set((responseStopTime - responseStartTime));
		// If the DataComponent already has a recorded value associate with it then append to that list.
		if (componentResponseTime.keySet().contains(componentName))
		{
			componentResponseTime.get(componentName).add(timeDiff.get());
		}
		// Otherwise associate this DataComponent with a new list.
		else
		{	
			responseTimeList = new ArrayList<Long>();
			responseTimeList.add(timeDiff.get());
			componentResponseTime.put(componentName, responseTimeList);
		}
	}

	
	/**
	 * Computes the average recorded response-times (in nanoseconds) of each
	 * data component in the program.
	 */
	public synchronized void computeData() 
	{
		// For each data-component compute the average response-time from 
		// its list of response-times, in nanoseconds.
		ArrayList<Long> responseList = new ArrayList<Long>();
		Long totalResponseTime;
		Double averageResponseTime;
		
		for (String dataComp : componentResponseTime.keySet())
		{
			totalResponseTime = 0L;
			averageResponseTime = 0.0; 
			responseList = componentResponseTime.get(dataComp);
			 
			for(Long responseTime : responseList)
			{
				totalResponseTime += responseTime;
			}
	
			averageResponseTime = (totalResponseTime.doubleValue() / new Integer(responseList.size()).doubleValue() ); 
			averageResponseTimes.put(dataComp, averageResponseTime.longValue());
		}
	}
	

	/**
	 * Outputs the computed average response-times (in milliseconds)
	 * of each data component in the program.
	 */
	public synchronized void outputAnalysis() 
	{
		computeData();
		Double response;
		
		for (String component : averageResponseTimes.keySet())
		{
			response = 0.0;
			System.out.print(component + ": ");
			// Convert the average response-times from nanoseconds to milliseconds.
			response = (averageResponseTimes.get(component)).doubleValue() / 1000000;
			// Output the average response-times rounded to 2-decimal precision.
			System.out.print( new DecimalFormat("0.00").format(response) + " ms" );
			System.out.println("\n");
		}
	}
	
	
	/**
	 * Returns a list of each component's average execution time (in Nanoseconds).
	 * 
	 * @return list of average nanosecond component-execution times.
	 */
	public synchronized ConcurrentHashMap<String, Long> getAverageResponseTimes() 
	{ 
		return averageResponseTimes; 
	}
	
	
	/**
	 * Returns a list of each component's execution time (in Nanoseconds).
	 * 
	 * @return list of nanosecond component-execution times.
	 */
	public synchronized ConcurrentHashMap<String, ArrayList<Long>> getResponseTimes() 
	{ 
		return componentResponseTime; 
	}

		
	//============================================ PRIVATE METHODS ==================================================
	
	// No Private methods.
}
