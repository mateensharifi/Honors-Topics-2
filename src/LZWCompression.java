import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
public class LZWCompression {
	/*
	 * Possible optimization procedures:
	 * 	1) Start dictionary's bit size at 9 so we cut out the unnecessary starting loop
	 *  
	 */
	static PrintWriter out;
	static String inputString = "";
	static Scanner in;
	static int bitSize = 8;
	static LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	
	public static void main(String[] args) throws IOException{
		out = new PrintWriter(new File("output.txt"));
		in = new Scanner(new File("lzw-file3.txt"));
		
		init(); //Input/Output stuff
		solve(); //Actually solving stuff
		
		in.close();
		out.close();
	}
	
	static void init() throws IOException{
		while(in.hasNext()) {
			inputString += in.nextLine() + "\n";
		}
		inputString = inputString.trim(); //gets rid of unnecessary newline at string's end
		for(int i = 0; i < 256; i++) { //set up ASCII table with default values
			map.put(Integer.toBinaryString(i), "" + (char)(i));
		}
	}
	
	static void solve() throws IOException{
		
		//Variables:
		int remainingSpaces = 0; //Counts the available spaces left in the dictionary.
		int index = 1; //Will track our 'next' index inside the input string.
		String currentEntry = "" + inputString.charAt(0); //Holds our current string every iteration.
		String ans = ""; //Our answer string to be output at the very end.
		String currentKey = "011111111"; //Holds our latest bit # to use in dictionary.
		String binary = "";
		
		//Loop:
		while(index < inputString.length()) { //Parse through each index of the input string.
			
			if(remainingSpaces == 0) { //If dictionary runs out of space, increase bit size by 1.
				remainingSpaces = (int) Math.pow(2, bitSize++);
				updateMap(); //Updates dictionary with increased bit-size.
			}
			
			//Need to update information every iteration:
			remainingSpaces--; //Subtract one remaining space in the dictionary.
			
			if(map.containsValue(currentEntry + inputString.charAt(index))) { //Is Current + Next in our dictionary?
				currentEntry += inputString.charAt(index); //If so, update our current entry string.
			}else {
				ans += currentEntry; //Add the current entry to the output answer.
				currentKey = updateKey(currentKey); //Update our latest bit key to use.
				binary += currentKey;
				map.put(currentKey, currentEntry + inputString.charAt(index)); //Add Current + Next to dictionary.
				currentEntry =  "" + inputString.charAt(index); //Update Current.
			}
			index++;
		}
		
		
		//Print stuff
//		out.println(inputString.substring(0, inputString.length() - 2));
//		for(String str: map.keySet()) {
//			out.println(str + ": " +  map.get(str));
//		}
		//out.println(ans);
		//out.print(Arrays.toString(byteArray));
		String str = binaryToDecimal(binary);
		out.print(str);
		//out.println(binary);
	}
	
	static String updateKey(String key) { //Adds 1 to the dictionary key in binary.
		int number = Integer.parseInt(key, 2);
		int sum = number + 1;
		return Integer.toBinaryString(sum);
	}
	
	static void updateMap() { //Updating our dictionary with keys 1-bit longer.
		LinkedHashMap<String, String> temporaryMap = new LinkedHashMap<String, String>();
		for(String s: map.keySet()) {
			temporaryMap.put("0" + s, map.get(s));
		}
		map.clear();
		for(String s: temporaryMap.keySet()) {
			map.put(s, temporaryMap.get(s));
		}
	}
	
	static String binaryToDecimal(String binary) {
		String decimal = "";
		for(int i = 0; i < binary.length(); i+= 8) {
			decimal += (char)Integer.parseInt(binary.substring(i, Math.min(i + 8, binary.length())), 2);
		}
		return decimal;
	}
	
	

}
