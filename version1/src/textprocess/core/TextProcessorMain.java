package textprocess.core;


import java.io.IOException;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;


import textprocess.interfaces.IController;
import textprocess.structures.Components;
import textprocess.utils.FileHandler;


/**
 * 					SE480  ASSIGNMENT 4: Active Pipe-and-Filter program
 * 
 * This is the entry main class for the active pipe-and-filter program.
 *
 *
 * 				:EXAMPLE USAGE:
 * 
 * Command Line format:
 * 
 * >> java -cp <library_filepath>: TextProcessorMain <input_filepath>  <stopWord_filepath> <1>, <2>, <3>
 * 
 * >> java -cp lucene-core-3.03.jar: TextProcessorMain <input_filepath>  <stopWord_filepath> <2>, <1>, <3>
 *
 * 
 * >> java -cp bin/lib/*:bin TextProcessorMain “bin/data/usdeclar.txt” “bin/data/stopwords.txt” 1 2
 *
 * This command will run the program on input file ‘usdeclar.txt’ with text-filters 1 and 2, in that order.
 * 
 * 
 * >> java -cp bin/lib/*:bin TextProcessorMain usdeclar.txt stopwords.txt 2 1 3
 *
 * This command will run the program on input file ‘usdeclar.txt’ with text-filters 2, 1, and 3, in that order.
 * 
 *
 *	>> java -cp bin/lib/*:bin TextProcessorMain “bin/data/alice30.txt” “bin/data/stopwords.txt” 1
 *
 * This command will run the program on input file ‘alice30.txt’ with only text-filter 1.
 *
 * 
 * The numbers represent the  text-filters to be used in order of their operation.
 * 
 * 
 * 				:PROGRAM OVERVIEW:
 * 
 * The user enters in from the command line an input file to process,
 * the stop-words file to use, and numbers which represent the filter
 * operations to be performed on the file, in order. During operation,
 * each filter's response-time is measured for later evaluation.
 * 
 * There are 3 available filters, represented by the numbers 1-3:
 * 1 - RemoveStopWords Text Filter
 * 2 - RemoveNonAlphabeticText Filter
 * 3 - StemWordsToRoot Text Filter
 * 
 * The final result of the filter operations is fed into a Data sink 
 * component which computes the frequency of each word-term in the 
 * processed-document and prints out, to the console, the 10 most common 
 * word-terms, along with the average response-times of each component. 
 * 
 * 
 * 			:PROGRAM INTERNAL STRUCTURE/OPERATION:
 * 
 * The program consists of four key components:
 * 
 * - - A Data Source, from which the contents of the input file are read out from
 * - - One or more predefined Filters, which transform the contents of the file 
 * and pass the result along a Pipe (if subsequent filters are in front of it)
 * - - Pipes which are explicit BlockingQueue objects that act as buffers to hold
 * process data as it is passed to the next Filter for processing.
 * - - A Data Sink, that receives the final transformed data, computes its word-term
 * frequency and outputs the 10 most common word-term results to the console and 
 * into an output file. 
 * 
 * 
 * When the user inputs the parameters at the command line, the program
 * the parameters to construct the required pipe-buffers (represented as Blocking
 * queues) and filters; connecting them together and starting the execution.
 *  
 * Each filter is executed as a separate thread and uses an active pull and push model
 * to constantly pull data from its connected upstream pipe, transform the data, 
 * and push the output to a connected downstream pipe.This flow of transformed 
 * data continues till it reaches the Data Sink, where the final result is 
 * outputted to the console and output-file, after which the program operation ends.
 * 
 * The average response-time of each filter, the loading time, overall processing time, 
 * and result-saving time is measured during their operations and the data is recorded 
 * separately and displayed with the final results.
 *  
 *  
 *  				:EXTERNAL LIBRARIES USED:
 * 
 * The ONLY external library that was used is the Apache Lucence Library 
 * (lucene-core-3.0.3.jar), it is employed to perform word-root-stemming 
 * and stop-word removals operations.
 * 
 * All other source-code was written by the Author.
 * 
 * @author David Olorundare
 *
 */
public class TextProcessorMain 
{
	public static void main (String[] args)
	{
		
		//============== SETUP, CONFIGURATION, AND INITIALIZATION ============================================
		
		//=================== SETUP ===============================================
		
		FileHandler handler = FileHandler.getInstance();
		ProgramConfig config = ProgramConfig.getInstance();
		IController controller = FilterController.getInstance();	
		AtomicIntegerArray activeFilters;
		AtomicInteger numberOfFilters = new AtomicInteger(1);
		Components dataComponents;
		
		//=============== CONFIGURATION ========================================================================
		
		if (args.length >= 2)
		{
			config.setInputFile(args[0]); handler.setInputFilePath(args[0]);
			config.setStopWordsFile(args[1]); handler.setStopWordPath(args[1]);
			config.setOutputFile(handler.getOutputFilePath());
						
			// set flags   
			if (args[2].equals("all"))
			{ config.setAllFilters(true); }
			else 
			{ 
				config.setAllFilters(false);
				// load the order of filter operations
				numberOfFilters.set( (args.length - 2) );
				activeFilters = new AtomicIntegerArray(numberOfFilters.get());
				
				for (int i = 2; i < args.length; i++ )
				{
					activeFilters.set( (i - 2), Integer.parseInt(args[i]) );   
				}
				config.setFiltersToUse(activeFilters);
			}
			
		//=============== INITIALIZATION ======================================================================
		
		// Assemble together a pipeline consisting of connected pipes and filters
		Assembler builder = Assembler.getInstance();
		dataComponents = builder.assemblePipeline(); 
			
		//=== STARTUP THE PIPELINE, OPERATE FILTERS, and OUTPUT RESULTS ============================================
		
		// Assembler gives Controller the list of activeFilters to be executed: started/stopped.
		// Pass the list to a Controller, which internally uses 
		// the ExecutorService Framework for all thread management 
			try
			{
				System.out.println("Running Program...");
				controller.operate(dataComponents);
			}
			catch (InterruptedException | IOException e) 
			{
				System.out.println("Error Occurred in running Program..\n...Shutting Down Program");
			}
			finally
			{
				// Attempting shutting down the program.
				try { controller.shutDownProgram(); } 
				catch (InterruptedException error) { error.printStackTrace(); }
			}
		}
		// Print Usage info
		else 
		{
			System.out.println("Program Usage Information:");
			// Show the user some Usage-info.
			System.out.println("From the commandline:\n >> java -cp bin/lib/*:bin TextProcessorMain “bin/data/alice30.txt” “bin/data/stopwords.txt” 1");
			return;
		}
	}
}
