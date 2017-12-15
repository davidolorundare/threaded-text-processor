package textprocess.interfaces;

/**
 * 
 * Records the time-taken by each
 * data components for each of their
 * (read-process-write) operations, per textline.
 * 
 * @author David Olorundare
 *
 */
public interface IInstrumentation 
{
	
	//============================================ PRIVATE VARIABLES =============================================================

		
	// No Private variables
		
			
	//============================================ CONSTRUCTOR =============================================================
		

	// No Explicit Constructor		
		
	
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Records the response-time of a given data component.
	 * 
	 * @param componentName	name of the data component.
	 * 
	 * @param responseStartTime	the start operating time of the data component.
	 * 
	 * @param responseStopTime	the stop operating time of the data component.
	 * 
	 */
	public void recordData(String componentName, Long responseStartTime, Long responseStopTime);
	
	
	/**
	 * Computes the average recorded response-times of all
	 * the data components in the program.
	 * 
	 */
	public void computeData();
	
	
	/**
	 * Outputs the computed average response-times of each
	 * data component in the program
	 */
	public void outputAnalysis();
		
		
	//============================================ PRIVATE METHODS ==================================================
		
	// No Private methods
		
	
}
