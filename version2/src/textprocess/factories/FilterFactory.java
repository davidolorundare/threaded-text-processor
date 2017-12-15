package textprocess.factories;

import textprocess.filters.RemoveNonAlphaText;
import textprocess.filters.RemoveStopWords;
import textprocess.filters.StemWordsToRoot;
import textprocess.interfaces.AbstractDataComponent;


/**
 * Represents a factory-class
 * that creates new Text-Filter objects
 * for use.
 * 
 * @author David Olorundare
 *
 */
public final class FilterFactory 
{
	
	//============================================ PRIVATE VARIABLES =============================================================

	
	// No Private variables

		
	//============================================ PUBLIC METHODS =============================================================
		
	
	/**
	 * Creates a new text-filter given
	 * its typeID.
	 * 
	 * @param typeID	  the ID of the filter to create.
	 * 
	 * @return	the newly created text-filter.
	 * 
	 */
	public synchronized static AbstractDataComponent createFilter(int typeID) 
    {
        switch (typeID)
        {
            // RemoveStopWord Filter
        		case 1:
                return RemoveStopWords.createRemoveStopWords();
            // RemoveNonAlphabeticText Filter
            case 2:
                return RemoveNonAlphaText.createRemoveNonAlphaText();
            // WordStemming Filter
            case 3:
            		return StemWordsToRoot.createStemWordsToRoot();
            default:
                return null;
        }
    }
	
			
	//============================================ PRIVATE METHODS ==================================================
		
	// No Private methods
}
