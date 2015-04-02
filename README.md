# text_summarizer
I worked on the project on Eclipse environment. It would be ideal to use Eclipse when running the program.
Just for your information, my lap top is macbook, and JRE1.7 was used to compile the program.

There are couple assumptions I made regarding the unit testing:
1. I assumed that all documents would have the right grammar syntax, such as a new sentence starts with a upper case letter etc.
2. Input text files are having the right format, meaning the files are not having binary or hexadecimal numbers etc.

As for the unit testing, these are the tests I thought I should have to ensure my program is working correctly:
[Direction: when testing the cases, just enter the name of the file/files on the console of Eclipse]
- test1.txt: Check word count is done correctly. 
- test2.txt: Check sentence count is done correctly. 
- test3.txt: Check if the program returns proper output if the text file is empty. 
- test4.txt: Check if occurrence of the word is calculated correctly. 
- test5-1.txt: Check if weighted score of each sentence is calculated correctly. 
- test5-2.txt: Check if weighted score of each sentence is calculated correctly. 
- test6.txt: Check if target ratio is used correctly when printing out the summary; enter 50% for the target ratio
- test1.txt test1.txt: Check the same scenario as test1.txt, but enter the same file twice
- test2.txt test2.txt: Check the same scenario as test2.txt, but enter the same file twice
- aaaa.txt (doesn't exist): Check if the program terminates if the user feeds in the file name that doesn't exist
- summary.txt: Check if the program terminates if an input file has the name "summary.txt," which is supposed to be the output file

In addition to the test cases, I have included two example files, which are "example1.txt" and "example2.txt." 
These two files are for you to test the program how effectively analyzing and summarizing the document, if you are interested.
