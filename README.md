# Natural Language Processing: MultiThreaded Text Processor
Academic Project - A multi-threaded text processor designed and implemented with an active pipe-and-filter-based architecture.

## Overview:
This commandline program reads in a text file, removes stop-words, non-alphabetic text, stems words into their root form using the Apache Lucene Word-Stemming library, and computes the frequency of each word term; printing the 10 most common in order of their frequencies.

Implementing the program with a modular pipe-and-filter architecture enabled easy extensibility of the codebase to include entirely new filters outside of the default three.

Two versions of the program were implemented, the first uses a standard active (multi-threaded) pipe-and-filter architecture, however, the second uses multiple instances of some filters in order to increase throughput but also address performance-bottlenecks at some of the pipes.

Both versions of the program were evaluated with various dataset sizes (of which some are included in the codebase), their executions were instrumented, and their performance characteristics stored in an internal data structure for later processing. Given the modular nature of the program, it could very well be extended to feed into a front-end UI or an analytics back-end if needed.
threaded-text-processor


## Screenshots:

![alt text](https://github.com/davidolorundare/image-repo/blob/master/cli_execution.png "Pipe-and-Filter program running on the terminal")





## Running Demo:








## Usage:



