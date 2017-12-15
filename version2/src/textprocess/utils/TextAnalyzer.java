package textprocess.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.BreakIterator;



import java.util.Locale;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * This utility class performs various text analysis
 * operations on a given input text data.
 * It has methods for performing paragraph, 
 * sentence, and word analysis using the built-in 
 * Java BufferedReader readline() method, built-in
 * Java BreakIterator Sentence segementation method,
 * and Java Regex methods, respectively.
 * 
 * @author David Olorundare
 *
 */
public final class TextAnalyzer 
{
	
	//============================================ PRIVATE VARIABLES =============================================================
	
	
	// Represents an instance to this class.
	private volatile static TextAnalyzer instance;

	// Represents a buffer holding the input text data in a stream.
	private volatile BufferedReader inputData;
	
	// Represents a list of sentences in a given paragraph.
	private CopyOnWriteArrayList<String> sentences;
	
	// Represents a temporary store of the current text paragraph being processed.
	private AtomicReference<String> tempParagraph;
	
	// Represents the current sentence in a paragraph being segmented.
	private AtomicInteger currSentenceIndex = new AtomicInteger(0);
	
	// Represents the previous sentence in a paragraph that was segmented.
 	private AtomicInteger  prevSentenceIndex = new AtomicInteger(0);
 	
 	// Represents the number of tokens in the analyzed text.
 	private AtomicInteger tokenCount = new AtomicInteger(0);
 	
 	// Represents the number of paragraphs in the analyzed text.
 	private AtomicInteger paragraphCount = new AtomicInteger(0);
 	
 	// Represents the number of sentences in the analyzed text.
 	private AtomicInteger sentenceCount = new AtomicInteger(0); 
 	
	// Represents a mapping between each word-token in the text and its occurrence.
	private ConcurrentHashMap<String, Integer> wordCount = new ConcurrentHashMap<String, Integer>();
 	
 	// Represents a mapping between each distinct word (types) in the text and their frequency.
 	private ConcurrentHashMap<String, Integer> typeCount = new ConcurrentHashMap<String, Integer>();
 	
 	
	//============================================ CONSTRUCTOR =============================================================
	
	
	/**
	 * Private Constructor of the TextAnalyzer class.
	 * 
	 */
	private TextAnalyzer() {	}
	
	
	/**
	  * Returns a singleton instance of the TextAnalyzer class,
	  * ensuring that only one instance is active 
	  * at any single time.
	  * 
	  */
	public static TextAnalyzer getInstance() 
	{
	      if (instance == null)
	      {
	          synchronized (TextAnalyzer.class)
	          {
	              if (instance == null)
	              {
	                  instance = new TextAnalyzer();
	              }
	          }
	      }
	      return instance;
	}
	
	
	//============================================ PUBLIC METHODS =============================================================
	
	
	/**
	 * Return the current mapping of word-term to occurrence rate.
	 * 
	 * @return	word-term to occurrence rate mapping.
	 * 
	 */
	public synchronized ConcurrentHashMap<String, Integer> getCurrentTermFrequency()
	{
		return wordCount;
	}
	
	
	/**
	 * Performs text analysis on a stream of text  data.
	 * 
	 * @param textStream	the text data to be analyzed.
	 * 
	 * @return	structure containing the results of text analysis.
	 * 
	 * @throws	IOException	if an error occurs while reading the input file.
	 * @throws	PatternSyntaxException  if the regex syntax of the pattern used is wrong.
	 * @throws	IllegalArgumentException if one of the arguments supplied to the regex methods is wrong.
	 */
	private synchronized void analyzeText(BufferedReader textStream) throws IOException, PatternSyntaxException, IllegalArgumentException
	{
		
		//=========================  ANALYZE INPUT TEXT FROM A STREAM =============
		
		inputData = textStream;
		String lineOfText;
		lineOfText = inputData.readLine();			
		
		while (true) 
		{
			 // Detect paragraphs in text.
			 if (lineOfText == null || lineOfText.trim().length() == 0) 
			 {
			     paragraphCount.incrementAndGet(); 
			     if(lineOfText == null ) break;
			 } 
			 else 
			 {	 
				// do sentence segmentation
				CopyOnWriteArrayList<String> result = sentenceSegmementation(lineOfText);
				
				// do word-tokenization operation
				tokenizeAndCount(result);
			 }
			 lineOfText = inputData.readLine();
		}
		inputData.close();
	}
	
	
	/**
	 * Helper method that performs sentence segmentation
	 * on a given paragraph of text, tracking the number
	 * of sentences in the paragraph.
	 * 
	 * @param textParagraph	the text paragraph on which sentence segmentation is to be performed.
	 * 
	 * @return	list containing the segmented sentences.
	 * 
	 */
	 public synchronized CopyOnWriteArrayList<String> sentenceSegmementation(String textParagraph) 
	 {
		tempParagraph = new AtomicReference<String>(" ");
		tempParagraph.set(textParagraph);
		sentences = new CopyOnWriteArrayList<String>();
		
		//do sentence segmentation operation.
		Locale locale = Locale.US;
		BreakIterator breaker = BreakIterator.getSentenceInstance(locale);
	 	breaker.setText(tempParagraph.get());
	 	
		AtomicInteger boundaryInd = new AtomicInteger(breaker.first());
		while (boundaryInd.get() != BreakIterator.DONE)
		{
			prevSentenceIndex.set(breaker.current());
			boundaryInd.set(breaker.next());
			currSentenceIndex.set(breaker.current());
						
			sentences.add(tempParagraph.get().substring(prevSentenceIndex.get(), currSentenceIndex.get()));
		}
		return sentences;
	 }
	
	
	/**
	 * Helper method that tokenize's a list of sentences, 
	 * using Regular expressions and Java code, into words.
	 * 
	 *  Ensure each sentence is not null or empty, ignore if it is.
	 *		
	 *	For each sentence, iterate through it and use regex-patterns to do a search and replace, 
	 *  searching for pre-defined contractions and replacing them with their proper form; as separate strings.
     *  Use regex-patterns to also look for punctuation marks, separate them as separate-strings. 
	 *  Update the token-count and (distinct words) type-count.
     *  
	 * @param sentence	structure containing the list of sentences to be tokenized.
	 * 
	 * @throws	PatternSyntaxException  if the regex syntax of the pattern used is wrong.
	 * @throws	IllegalArgumentException if one of the arguments supplied to the regex methods is wrong.
	 * 
	 */
	public synchronized void tokenizeAndCount(CopyOnWriteArrayList<String> sentences) throws PatternSyntaxException, IllegalArgumentException
	{ 
		ConcurrentMap<String, String> contractions = new ConcurrentHashMap<String, String>();
		// Handle if the preceding word is a personal pronoun e.g. "he", "she", and "it".
		contractions.put("(^|[^a-zA-Z])([Hh]e)'s", "$2 is");
		contractions.put("(^|[^a-zA-Z])([Ss]e)'s", "$2 is");
		contractions.put("(^|[^a-zA-Z])([Ii]t)'s", "$2 is");
		
		//Handle other forms of word-contraction.
		//contractions.put("([a-zA-Z]+)('s)", "$1 $2");
		contractions.put("'s", " 's");
		contractions.put("'d", " would");
		contractions.put("'re", " are");
		contractions.put("'ll", " will");
		contractions.put("n't", " not");
		contractions.put("'nt", " not");
		contractions.put("'ve", " have");
		contractions.put("'m", " am");
		
		// Handle numbers followed by letters e.g. "80s" , "90s".
		contractions.put("([0-9]+)([a-zA-Z]+)", "$1 $2");

		// For each sentence containing words, tokenize the words using Regex.
		for (String sentence: sentences)
		{
			if (!sentence.equals(" ") || !(sentence == null) )
			{
				// First expand any word-contractions.
//				for (String key : contractions.keySet())
//				{
//					sentence = sentence.replaceAll(key + "\\b", contractions.get(key));
//				}
				
				// Next, tokenize the sentence and its punctuations; into a list of tokens.
				CopyOnWriteArrayList<String> tokenizedSentence = splitter(sentence);

				// Finally, count all tokens and types.
				mapWords(tokenizedSentence);
			}
		}
		// Free up memory.
		sentences.clear();
   }
	 
	
	//===============================  PRIVATE METHODS ========================================================
   
	
   /**
    * Helper method that iterates through an 
    * array of words, counting the number of
    * word-tokens and word-types present.
    * 
    * @param wordsList	an array containing word-tokens.
    * 
    */
   private synchronized void mapWords(CopyOnWriteArrayList<String> wordsList)
   {
	   // iterate through the array of words counts both tokens and types.
	   for (String word : wordsList)
	   {
		   // Count number of tokens 
		   // and store their word-to-frequency mapping in a list.
		   Integer num = wordCount.get(word);
	        num = (num == null) ? 1 : ++num;
	        wordCount.put(word, num);
		   
		   // Count number of types (distinct words) 
		   if ( typeCount.keySet().contains(word))
		   {
			   typeCount.put(word, (typeCount.get(word)+1) );
		   }
		   typeCount.put(word, 1);
	   }
   }


	/**
	* Helper method that counts the number of tokens
    * in the an analyzed text
    * 
    * @return	a count of the tokens in the analyzed text.
	*/
	private synchronized int countTokens() 
	{
		// Calculate the total number of tokens.
		   int tokenCount = 0;
		   for (String s: wordCount.keySet())
		   {
			   tokenCount += wordCount.get(s);
		   }
		   return tokenCount;
	}
	
	
   /**
    * Helper method that counts the number of distinct words
    * in the an analyzed text.
    * 
    * @return	a count of the distinct words in the analyzed text.
    */
   private synchronized int countTypes()
   {
 	   // Calculate the total number of tokens.
	   int numOfTypes = 0;
	   for (String s: typeCount.keySet())
	   {
		   numOfTypes += typeCount.get(s);
	   }
	   return numOfTypes;
   }
   
	
   /**
    * Helper method that performs regular expression
    * operations to split a sentence into tokens, taking
    * note of punctuations.
    * 
    * @param sentence	the sentence to be split into tokens.
    * 
    * @return	a list of tokens.
    */
   private synchronized CopyOnWriteArrayList<String> splitter(String sentence) 
   {
	    Pattern pattern = Pattern.compile("(\\w+)|(\\.{3})|(\\'s)|[^\\s]");
	    Matcher matcher = pattern.matcher(sentence);
	    CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
	    while (matcher.find()) { list.add(matcher.group()); }
	    return list;
	}
   
   	
}
