package textprocess.structures;

import java.util.concurrent.CopyOnWriteArrayList;

import textprocess.interfaces.AbstractDataComponent;


/**
 * This class represents a storage object
 * containing the list of all sources, sinks,
 * and filters used in the program.
 * 
 * @author David Olorundare
 *
 */
public class Components 
{
	
	//============================================ PRIVATE VARIABLES =============================================================
	

	// Represents a list of all data sources used in the program.
	private CopyOnWriteArrayList<AbstractDataComponent> listOfSources;
	
	// Represents a list of all data sink used in the program.
	private CopyOnWriteArrayList<AbstractDataComponent> listOfSinks;
	
	// Represents a list of all data filters used in the program.
	private CopyOnWriteArrayList<AbstractDataComponent> listOfFilters;
	
	
	//============================================ CONSTRUCTOR =============================================================
		
	
	// No Explicit Constructor
	
	
	//============================================ PUBLIC METHODS =============================================================

	
	/**
	 * Creator method that returns a new Components class instance.
	 * 
	 * @return	newly created Components.
	 */
	public synchronized static Components createComponents() 
	{
		return new Components();
	}
	
	
	/**
	 * Sets the list containing all data filters.
	 * 
	 * @param value the list of data filters to set.
	 * 
	 */
	public synchronized void setListOfFilters(CopyOnWriteArrayList<AbstractDataComponent> value) 
	{ 
		listOfFilters = value; 
	}

	
	/**
	 * Sets the list of all data sources.
	 * 
	 * @param value the list of data sources to set.
	 * 
	 */
	public synchronized void setListOfSources(CopyOnWriteArrayList<AbstractDataComponent> value) 
	{
		listOfSources = value; 
	}

	
	/**
	 * Sets the list of all data sinks.
	 * 
	 * @param value the list of data sources to set.
	 * 
	 */
	public synchronized void setListOfSinks(CopyOnWriteArrayList<AbstractDataComponent> value) 
	{
		listOfSinks = value; 
	}
	
	
	/**
	 * Returns the list of all data sources in use.
	 * 
	 * @return listOfSources list contain all data sources
	 * 
	 */
	public synchronized CopyOnWriteArrayList<AbstractDataComponent> getListOfSources() 
	{
		return listOfSources;
	}


	/**
	 * Returns the list of all data sinks in use.
	 * 
	 * @return list containing all data sinks.
	 * 
	 */
	public synchronized CopyOnWriteArrayList<AbstractDataComponent> getListOfSinks() 
	{
		return listOfSinks; 
	}


	/**
	 * Returns the list of all data filters in use.
	 * 
	 * @return list containing all data filters.
	 * 
	 */
	public synchronized CopyOnWriteArrayList<AbstractDataComponent> getListOfFilters() 
	{
		return listOfFilters; 
	}


	//============================================ PRIVATE METHODS ==================================================
	
	// No Private Methods
}
