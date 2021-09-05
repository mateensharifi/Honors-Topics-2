import java.util.*;
import java.io.*;
public class LZWCompression {
	
	static PrintWriter out;
	static String inputString = "";
	static Scanner in;
	static int bitSize = 8;
	static LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	
	public static void main(String[] args) throws IOException{
		out = new PrintWriter(new File("output.txt"));
		in = new Scanner(new File("input.txt"));
		
		init();
		solve();
		
		in.close();
		out.close();
	}
	
	static void init() throws IOException{
		while(in.hasNext()) {
			inputString += in.nextLine() + "\n";
		}
		for(int i = 0; i < 256; i++) {
			map.put(Integer.toBinaryString(i), "" + (char)(i));
		}
	}
	
	static void solve() {
		
		int remainingSpaces = 0; //Counts the available spaces left in the dictionary.
		int index = 0; //Will track our current index inside the input string.
		String currentEntry = ""; //Holds our current string every iteration.
		String ans = ""; //Our answer string to be output at the very end.
		while(index < inputString.length()) { //Parse through each index of the input string.
			if(remainingSpaces == 0) { //If dictionary runs out of space, increase bit size by 1.
				remainingSpaces = (int) Math.pow(2, bitSize++);
				//Updating our dictionary with keys 1-bit longer:
				LinkedHashMap<String, String> temporaryMap = new LinkedHashMap<String, String>();
				for(String s: map.keySet()) {
					temporaryMap.put("0" + s, map.get(s));
				}
				map.clear();
				for(String s: temporaryMap.keySet()) {
					map.put(s, temporaryMap.get(s));
				}
				//
			}
			//Need to update information every iteration:
			remainingSpaces--; //Subtract one remaining space in the dictionary.
			currentEntry += inputString.charAt(index++); //Update our current entry string.
			System.out.println(currentEntry);
//			if(map.containsValue(currentEntry)) {
//				continue;
//			}else {
//
//			}
		}
		out.print(inputString.substring(0, inputString.length() - 2));
		for(String str: map.keySet()) {
			out.println(str + ": " +  map.get(str));
		}
	}
	

}
