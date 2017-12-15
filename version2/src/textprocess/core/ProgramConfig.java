package textprocess.core;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;


/**
 * This class represents the program
 * configuration that holds all the
 * various configuration information
 * and settings of the program.
 * 
 * @author David Olorundare
 *
 */
public final class ProgramConfig 
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	
	// Holds an instance to this class.
	private volatile static ProgramConfig instance;

	// Represents the filepath of the input text file.
	private AtomicReference<String> inputFile = new AtomicReference<String>(" ");
	
	// Represents the filepath used internally to store the processed results
	private AtomicReference<String> outputFile = new AtomicReference<String>(" ");
	
	// Represents a flag that indicates if all text-filters are to be used.
	private AtomicBoolean useAllFilters = new AtomicBoolean(true);
	
	// Represents a list of text-filters types to be used.
	private AtomicIntegerArray filtersToUse;
	
	// Represents the filepath of the stop-words file. 
	private AtomicReference<String> stopWordsFile = new AtomicReference<String>(" ");
	
	// Represents the number of active filters
	private AtomicInteger filterSize = new AtomicInteger(0);
	
	
	//============================================ CONSTRUCTOR =============================================================
		

	/**
	 *  Private Constructor of the ProgramConfig class.
	 */
	private ProgramConfig(){	}
	
	
  /**
   * Returns a singleton instance of the ProgramConfig class,
   * ensuring that only one instance of the Configuration is active 
   * at any single time.
   * 
   */
	public static ProgramConfig getInstance() 
	{
      if (instance == null)
      {
          synchronized (ProgramConfig.class)
          {
              if (instance == null)
              {
                  instance = new ProgramConfig();
              }
          }
      }
      return instance;
   }
	
		
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Sets the flag that indicates whether or not all filters 
	 * should be used.
	 * 
	 * @param value	true if all text-filters should be used, otherwise false.
	 * 
	 */
	public synchronized void setAllFilters(boolean value) 
	{ 
		useAllFilters.set(value); 
	}
	
	
	/**
	 * Assigns the text filters to be used in order of their typeID.
	 * 
	 * @param value	list containing typeID of text filters to be used, in order. 
	 * 
	 */
	public synchronized void setFiltersToUse(AtomicIntegerArray value) 
	{ 
		filtersToUse = value; 
	}
	
	
	/**
	 * Sets the filepath of the input text file.
	 * 
	 * @param value	input text filepath.
	 * 
	 */
	public synchronized void setInputFile(String value) 
	{ 
		inputFile.set(value); 
	}
	
	
	/**
	 * Sets the filepath of the output text file containing the processed
	 * results.
	 * 
	 * @param value filepath of the output file.
	 * 
	 */
	public synchronized void setOutputFile(String value) 
	{ 
		outputFile.set(value); 
	}

	
	/**
	 * Sets the filepath of the stop-words file containing the
	 * stop-words the stop-word-text-filter should use.
	 * 
	 * @param value filepath of the stopwords file used by a text filter.
	 * 
	 */
	public synchronized void setStopWordsFile(String value) 
	{ 
		stopWordsFile.set(value); 
	}	
	
	
	/**
	 * Sets the number of active filters.
	 * 
	 * @param size number of active filters.
	 * 
	 */
	public synchronized void setFilterSize(AtomicInteger size) 
	{
		filterSize = size;
	}
		
		
	/**
	 * Returns a list of the text filters in use, in order of their typeID.
	 * 
	 * @return list containing typeID's of active text filters.
	 * 
	 */
	public synchronized AtomicIntegerArray getFiltersToUse() 
	{ 
		return filtersToUse; 
	}
	
	
	/**
	 * Returns the filepath of the input text file.
	 * 
	 * @return input text file filepath.
	 * 
	 */
	public synchronized AtomicReference<String> getInputFile() 
	{ 
		return inputFile; 
	}


	/**
	 * Returns flag value that indicates if all text filters
	 * should be used for text processing.
	 * 
	 * @return	true if all text filters are to be used, otherwise false.	
	 * 
	 */
	public synchronized AtomicBoolean getAllFilters() 
	{ 
		return useAllFilters; 
	}


	/**
	 * Returns the filepath of the output file where the processed
	 * results are stored.
	 * 
	 * @return filepath of output file.
	 * 
	 */
	public synchronized AtomicReference<String> getOutputFile() 
	{ 
		return outputFile; 
	}


	/**
	 * Returns the filepath of the stopwords file.
	 * 
	 * @return filepath of file containing stopwords.
	 * 
	 */
	public synchronized AtomicReference<String> getStopWordsFile() 
	{ 
		return stopWordsFile; 
	}

	
	/**
	 * Returns the number of active filters.
	 * 
	 * @return number of active filters.
	 * 
	 */
	public synchronized AtomicInteger getFilterSize() 
	{
		return filterSize;
	}

	
	//============================================ PRIVATE METHODS ==================================================
		
	// No Private Methods.
}
