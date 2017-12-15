package textprocess.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;


/**
 * This utility class represents consists
 * methods used for file handling and processing.
 * 
 * @author David Olorundare
 *
 */
public final class FileHandler 
{
	
	//============================================ PRIVATE VARIABLES =============================================================
	
	
	// Holds an instance to this class.
	private volatile static FileHandler instance;
	
	// Represents the filepath of the input text file to be processed 
	private AtomicReference<String> source = new AtomicReference<String>(" ");
	
	// Represents the stop-words to be used by the stop-word-removal filter
	private AtomicReference<String> stopWordsPath = new AtomicReference<String>(" ");

	// Represents the filepath of a file containing the results of the processing.
	private AtomicReference<String> destination = new AtomicReference<String>("src/data/output_results.txt");
	
	// Represents the external file to which the processed results are stored in.
	private volatile File outputData;
	
	// Represents a File read object for reading-in data from the input text file.
	private volatile BufferedReader inputReader;
	
	// Represents a File read object for reading-in data from the stop-words text file.
	private volatile BufferedReader stopWordsReader;
	
	// Represents a list of stop-words
	private ConcurrentHashMap<String, Integer> stopWordsMap;
	
	// Represents a set of stop-words
	private volatile Set<String> stopWordsList;
		
	// Represents a line of the read-in input text file
	private AtomicReference<String> textLine = new AtomicReference<String>(" ");
	
	
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 *  Private Constructor of the FileHandler class.
	 */
	private FileHandler(){	}
	
	
  /**
   * Returns a singleton instance of the FileHandler class,
   * ensuring that only one instance of the Handler is active 
   * at any single time.
   * 
   */
	public synchronized static FileHandler getInstance() 
	{
      if (instance == null)
      {
          synchronized (FileHandler.class)
          {
              if (instance == null)
              {
                  instance = new FileHandler();
              }
          }
      }
      return instance;
   }
	
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Helper method that sets the current filepath
	 * of the input text-file to be processed.
	 * 
	 * @param filePath	current filepath of the input text-file.
	 * 
	 */
	public synchronized void setInputFilePath(String filePath)
	{
		source.set(filePath);
	}
	
	
	/**
	 * Helper method that sets the current filepath
	 * of the stopwords text-file.
	 * 
	 * @param filePath	current filepath of the stopwords text-file.
	 * 
	 */
	public synchronized void setStopWordPath(String filePath)
	{
		stopWordsPath.set(filePath);
	}
	
	
	/**
	 * Helper method that sets the current filepath
	 * of the output text-file used for storing processed
	 * results.
	 * 
	 * @param filePath	current filepath of the output text-file.
	 * 
	 */
	public synchronized void setOutputFilePath(String filePath)
	{
		destination.set(filePath);
	}
	
	
	/**
	 * Returns the output file path.
	 * 
	 * @param filePath	location of the outputfile
	 * 
	 */
	public synchronized String getOutputFilePath()
	{
		return destination.get();
	}
	
	
	/**
	 * Loads data from an input file containing text into memory. 
	 *
	 * @throws IOException	if an error occurs while reading the input file.
	 * 
	 * @throws FileNotFoundException	if either input, output, or stopword files are not found.
	 *
	 */
	public synchronized void readText() throws IOException, FileNotFoundException
	{
		File inputData = new File(source.get());
        if (!inputData.exists()) 
        { throw new FileNotFoundException("Input File Doesn't Exist"); }
		
        File stopWordData = new File(stopWordsPath.get());
        if (!stopWordData.exists()) 
        { throw new FileNotFoundException("Stop Words File Doesn't Exist"); }
        
        // Ensure the output_results file exists.
//        outputData = new File(destination.get());
//        if (!outputData.exists()) 
//        { throw new FileNotFoundException("Output File Doesn't Exist"); }
           
        // Input file exists so read in data.
        inputReader = new BufferedReader(new FileReader(inputData)); 
        stopWordsReader = new BufferedReader(new FileReader(stopWordData));
        
        //Read stop words into a Set
        readStopWords(stopWordsReader);
	}
	

	/**
	 * Reads a line of text from the input text file
	 * and returns it as a Line object.
	 * 
	 * @return object containing information about a line of text.
	 * 
	 * @throws IOException	if an error occurs while reading the input file.
	 */
	public synchronized String outputTextLine() throws IOException
	{
	
		// check if stream is ready for reading; read a line, set its ID mapping, and return it. 
        	textLine.set(inputReader.readLine());
        	return textLine.get();
	}
	
	
	/**
	 *  Returns a reference to the stop-words
	 *  file that has been read into memory.
	 *  
	 * @return stop-words file reader.
	 */
	public synchronized BufferedReader getStopWords()
	{
		return stopWordsReader;
	}
	
	
	/**
	 * Ensures the file-readers are closed.
	 * 
	 * @throws IOException	if an error occurs while attempting to close the file-readers.
	 */
	public synchronized void closeFileReaders() throws IOException
	{
		inputReader.close();
		stopWordsReader.close();
	}
	

	/**
	 * Helper method that writes some string data
	 * to the given external output file.
	 * 
	 * @param data	the string data to be written to 
	 * 				a given external file.
	 * 
	 * @throws IOException if an error occurs while writing to the output file.
	 */
	public synchronized void writeToFile(String data) throws IOException
	{
		Writer textFileWriter = new FileWriter(destination.get());
		textFileWriter.write(data);
		textFileWriter.close();
	}
	
	
	/**
	 * Returns the list of stop-words.
	 * 
	 * @return list of stop-words
	 */
	public synchronized Set<String> getStopWordList() 
	{ 
		return stopWordsList;
	}
	
	
	//============================================ PRIVATE METHODS =============================================================

	
	/**
	 * Copy the stop-words into a HashSet to use for searching.
	 * 
	 * @param reader	  stopwords file stream 
	 * @throws IOException if an error occurs while reading from the stream.
	 */
	private synchronized void readStopWords(BufferedReader reader) throws IOException 
	{
		stopWordsMap = new ConcurrentHashMap<String, Integer>();
		String text = " ";
		
		while( (text = reader.readLine()) != null )
		{
			stopWordsMap.put(text, 1);
		}
		stopWordsList = ConcurrentHashMap.newKeySet(stopWordsMap.size());
	}
	
	
}
