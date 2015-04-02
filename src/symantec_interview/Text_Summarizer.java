package symantec_interview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Text_Summarizer {
	List<String> filter = Arrays.asList("a", "an", "are", "the", "of", "aboard", "about", "above",
			"across", "after", "against", "along", "amid", "among", "anti", "around", "as", "at", "before", 
			"behind", "below", "beneath", "beside", "besides", "between", "beyond", "but", "by", "concerning", 
			"considering", "despite", "down", "during", "except", "excepting", "excluding", "following", "for", 
			"from", "in", "inside", "into", "is", "like", "minus", "near", "of", "off", "on", "onto", "opposite", 
			"outside", "over", "past", "per", "plus", "regarding", "round", "save", "since", "than", "through", 
			"to", "toward", "towards", "under", "underneath", "unlike", "until", "up", "upon", "versus", "via",
			"was", "were", "with", "within", "without");
	
	/* HashMap to save the number of occurrence of the words 
	 * Format
	 * Key = word
	 * Value = occurrence
	 */
	Map<String, Integer> word_freq = new HashMap<String, Integer>();
	
	/* HashMap to save which number of sentence the word is used in 
	 * Format
	 * Key = word
	 * Value = name_of_file:sentence_num
	 */
	Map<String, StringBuffer> word_loc = new HashMap<String, StringBuffer>();
	
	/* HashMap to save which number of sentence the word is used in 
	 * Format
	 * Key = name_of_file:sentence_num
	 * Value = weight
	 */
	Map<String, Integer> sent_weight = new HashMap<String, Integer>();
	
	/* HashMap to save starting index of the sentence 
	 * Format of the table
	 * Key = name_of_file:sentence_num
	 * Value = index_number
	 */
	Map<String, Integer> sent_index = new HashMap<String, Integer>();
	
	/* Set to keep track of words used in the document; sorted in alphabetical order */
	Set<String> words = new TreeSet<String>(); 
	
	int cnt_words = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Text_Summarizer ts = new Text_Summarizer();
		
		Scanner input = new Scanner(System.in);
		System.out.println("Enter the name of file you want to analyze (e.g. test.txt)");
		System.out.println("If there are more than one file, enter all files having whitespace inbetween");
		System.out.println("(e.g. a.txt b.txt c.txt)");
		System.out.print("Enter: ");
		
		/* Parse the input command to read multiple files */
		String[] file_names = input.nextLine().toString().split(" ");
		
		if (ts.parser(file_names)) {
			System.out.println("===================== Parse Complete ======================");

			System.out.println("Target ratio is to print out particular percentage of sentences in the order of weight");
			System.out.print("Enter the target ratio (%): ");
			double target_ratio;
			while(true) {
				target_ratio = input.nextInt();
				if (target_ratio > 100 || target_ratio < 0) {
					System.out.print("Ratio is out of range. Enter again:");
				}
				else
					break;
			} 
				
			ts.calc_weight();
			System.out.println("======= Calculating Weight of Each Sentence Complete ======");

			ts.interpreter(target_ratio);
			System.out.println("==================== Analyzing Complete ====================");

		}
		
		System.out.println("Exit the program");
	}

	public boolean parser(String[] file_names) {	
		for (int i=0; i<file_names.length; i++) {
			File file = new File(file_names[i]);
			
			if (file_names[i].equals("summary.txt")) {
				System.out.println("Input file shouldn't be \"summary.txt\"");
				return false;
			}
					
			/* Variables to count sentence number */ 
			int sentence_cnt = 0;
			boolean period = false;
			int index = 0;
			
			try {
		        Scanner sc = new Scanner(file);
		        
		        while (sc.hasNext()) {
		            if (sentence_cnt == 0) {
		            	sentence_cnt++;
		            	sent_index.put(file_names[i] + ":" + sentence_cnt, index);
		            }
		            	
		        	String s = sc.next();
		        	index++;
		        	cnt_words++;
		        	char first_c = s.charAt(0);
		        	char last_c = s.charAt(s.length()-1);
		        	
		        	/* If a first character is upper case and period was used  
		        	 * at the end of previous word, increment senetence_cnt
		        	 */
		            if ( Character.isUpperCase(first_c) && period == true ) {
		            	sentence_cnt++;
		            	sent_index.put(file_names[i] + ":" + sentence_cnt, index-1);
		            }
		            else 
		            	period = false;
		            
		            /* If a sentence ends with either "." or ".\"" or ".\'", set period to true */
		            if ( last_c == '.' 
		            		|| (last_c == '\"' && s.charAt(s.length()-2) == '.')
		            		|| (last_c == '\'' && s.charAt(s.length()-2) == '.')) period = true;
		            
		            /* Remove all special characters and convert the word into lower case */
		            s = s.replaceAll("[^a-zA-Z0-9]", "");
		            s = s.toLowerCase();

		            /* Only put the words that are not articles or prepositions */
		            if (!filter.contains(s)) {
			            /* Add a new word to the set */
			            if (!words.contains(s))
			            	words.add(s);
			            
			            /* Add or update the word_freq hash map */
			            if (!word_freq.containsKey(s))
			            	word_freq.put(s, 1);
			            else
			            	word_freq.put(s, word_freq.get(s) + 1 );
			            
			            /* Add or update the word_loc hash map */
			            if ( !word_loc.containsKey(s) ) {
			            	StringBuffer sb = new StringBuffer();
			            	sb.append(file_names[i] + ":" + sentence_cnt);
			            	word_loc.put(s, sb);
			            }
			            else {
			            	word_loc.put(s, word_loc.get(s).append("," + file_names[i] + ":" + sentence_cnt));
			            }
		            }
		        }
		        sc.close();
		    } 
		    catch (FileNotFoundException e) {
		        System.out.println("ERROR - File not exist: " + file_names[i]);
		        e.printStackTrace();
		        return false;
		    }
		}
		return true;
	}

	public void calc_weight() {
		for(int i=0; i<words.toArray().length; i++) {
			String word = words.toArray()[i].toString();
			int weight = word_freq.get(word);
			
			StringBuffer word_locs_sb = word_loc.get(word);
			String[] word_locs_parsed = word_locs_sb.toString().split(",");
			Set<String> word_locs = new TreeSet<String>();
			
			for (int j=0; j<word_locs_parsed.length; j++) {
				word_locs.add(word_locs_parsed[j]);
			}
			
			Iterator<String> it = word_locs.iterator();
			while(it.hasNext()) {
				String temp = it.next();
				if (!sent_weight.containsKey(temp)) {
					sent_weight.put(temp, weight);
				}
				else {
					sent_weight.put(temp, sent_weight.get(temp) + weight);
				}
			}
				
		}
	}
	
	public void interpreter(double target_ratio) {
		PrintWriter out;
		Map<String, Integer> word_freq_sorted = SortByValue(word_freq);
		Map<String, Integer> sent_weight_sorted = SortByValue(sent_weight);
		
		int num_sent_to_show = (int) (sent_weight.size() * target_ratio / 100);
		
		Set<String> word_sorted = word_freq_sorted.keySet();
		Set<String> sent_sorted = sent_weight_sorted.keySet();
		
		/* Show at least one sentence if target_ratio is too small */
		if (num_sent_to_show == 0 && sent_sorted.size() != 0) 
			num_sent_to_show = 1;
		
		try {
			out = new PrintWriter("summary.txt");
			out.println("============================= Summary =============================");
			for(int i=0; i<num_sent_to_show; i++) {
				String sent_loc = sent_sorted.toArray()[i].toString();
				out.println(String.format("[%s]", sent_loc));
				out.println(find_sentence(sent_loc));
			}
			
			out.println("================ Statistical Analysis of Sentences ================");
			out.println("Total sentences counts: " + sent_sorted.size() + '\n');
			
			out.println(String.format("%-20s %-15s", "Sentence Location", "Weighted Score"));
			
			for(int i=0; i<sent_sorted.size(); i++) {
				String sent_loc = sent_sorted.toArray()[i].toString();
				out.println(String.format("%-20s %-15s", sent_loc, sent_weight.get(sent_loc)));
			}
			
			out.println("================== Statistical Analysis of Words ==================");
			out.println("Total words counts (including articles and prepositions): " + cnt_words + '\n');
			
			out.println(String.format("%-5s %-20s %-5s %-20s", "Num", "Word", "Freq", "Sentence Location"));
			for(int i=0; i<word_sorted.size(); i++) {
				String word = word_sorted.toArray()[i].toString();
				out.println(String.format("%-5d %-20s %-5d %-20s", (i+1), word, 
						word_freq.get(word), word_loc.get(word).toString()));
			}
		
			out.println("========================= End of Analysis =========================");
			
			out.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public String find_sentence(String sentence_loc) {
		String[] sent_info = sentence_loc.split(":");
		String file_name = sent_info[0];
		int sent_num = Integer.parseInt(sent_info[1]);
		int start_index = sent_index.get(sentence_loc);
		int end_index = -1;
		
		if (sent_index.containsKey(file_name + ":" + (sent_num+1))) {
			end_index = sent_index.get(file_name +":" + (sent_num+1));
		}
	
		StringBuffer sentence = new StringBuffer();
		int cur_index = 0;
		File file = new File(file_name);
		
		if(end_index == -1) {	
			try {
				Scanner sc = new Scanner(file);
		        
		        while (sc.hasNext()) {
		        	String s = sc.next();
		        	if (cur_index++ >= start_index) 
		        		sentence.append(s + " ");
		        }
			} 
			catch (FileNotFoundException e) {
		        System.out.println("ERROR - Fail to retrieve a sentence: " + file_name + ":" + sent_num);
		        e.printStackTrace();
		        return null;
		    }	
		}
		else {
			try {
				Scanner sc = new Scanner(file);
		        
		        while (sc.hasNext() && cur_index < end_index) {
		        	String s = sc.next();
		        	if (cur_index++ >= start_index) 
		        		sentence.append(s + " ");
		        }
			} 
			catch (FileNotFoundException e) {
		        System.out.println("ERROR - Fail to retrieve a sentence: " + file_name + ":" + sent_num);
		        e.printStackTrace();
		        return null;
		    }
		}
		
		return sentence.toString();
	}
	
	public static Map<String, Integer> SortByValue (Map<String, Integer> map) {
		ValueComparator vc =  new ValueComparator(map);
		TreeMap<String,Integer> sortedMap = new TreeMap<String,Integer>(vc);
		sortedMap.putAll(map);
		return sortedMap;
	}
}
