import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
public class LZWCompression {
	/*
	 * Possible optimization procedures:
	 * 	1) Start dictionary's bit size at 9 so we cut out the unnecessary starting loop
	 *  
	 */
	static PrintWriter out;
	static String inputString = "";
	static  ArrayList <String>  decodeInputString = new ArrayList <String> ();
	static String toDecodeInputString = ""; 
	static Scanner in;
	static int bitSizeEncoder = 12;
	static int bitSizeDecoder = 12; 
	static String decoded = ""; 
	static LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	static LinkedHashMap<String, String> decodedMap = new LinkedHashMap<String, String>();


	public static void main(String[] args) throws IOException{
		//		out = new PrintWriter(new File("output.txt"));
		//		in = new Scanner(new File("input.txt"));
		out = new PrintWriter(new File("dummy.txt"));
		in = new Scanner(new File("lzw-file3.txt"));

		init(); //Input/Output stuff
		solve(); //Actually solving stuff
		decode (); 
		out.print(decoded);

		in.close();
		out.close();

	}

	static void init() throws IOException{
		while(in.hasNext()) {
			inputString += in.nextLine() + "\n";
		}
		inputString = inputString.trim(); //gets rid of unnecessary newline at string's end
		for(int i = 0; i < 256; i++) { //set up ASCII table with default values
			
			String binaryValue = Integer.toBinaryString(i); 
			while(binaryValue.length() < bitSizeEncoder)
			{
				binaryValue = "0" + binaryValue;  
			}
			map.put(binaryValue, "" + (char)(i));
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
		String tracker = ""; 

		//Loop:
		while(index < inputString.length() + 1) { //Parse through each index of the input string.

//			if(remainingSpaces == 0) { //If dictionary runs out of space, increase bit size by 1.
//				remainingSpaces = (int) Math.pow(2, bitSizeEncoder++);
//				updateMap(map); //Updates dictionary with increased bit-size.
//			}

			//Need to update information every iteration:
			remainingSpaces--; //Subtract one remaining space in the dictionary.
			if(index == inputString.length())
			{
				ans += convertToBinary(currentEntry,map); //Add the current entry to the output answer.
				currentKey = updateKey(currentKey); //Update our latest bit key to use.
				binary += currentKey; 
			}
			else if(map.containsValue(currentEntry + inputString.charAt(index))) { //Is Current + Next in our dictionary?
				currentEntry += inputString.charAt(index); //If so, update our current entry string.
			}else {
				ans += convertToBinary(currentEntry,map); //Add the current entry to the output answer.
				currentKey = updateKey(currentKey); //Update our latest bit key to use.
//				String searchKey = "000000000"; 
//				for (int i = 0; i < map.size(); i++)
//				{
//					if (map.get(searchKey) == currentEntry)
//					{
//					break; 
//					}
//					updateKey(searchKey); 
//				}
				binary += currentKey; 
				map.put(currentKey, currentEntry + inputString.charAt(index)); //Add Current + Next to dictionary.
				currentEntry =  "" + inputString.charAt(index); //Update Current.
			}
			index++;
		}
	


		//Print stuff
		String str = binaryToDecimal(binary);
		toDecodeInputString = binaryToDecimal(ans); 
		out.println(binaryToDecimal(ans));

		//out.println(binary);
	}

	static String updateKey(String key) { //Adds 1 to the dictionary key in binary.
		int number = Integer.parseInt(key, 2);
		int sum = number + 1;
		String newKey = Integer.toBinaryString(sum); 
		while(newKey.length() < bitSizeEncoder)
		{
			newKey = "0" + newKey;  
		}
		return newKey; 
	}

	static void updateMap( LinkedHashMap<String, String> mapper) { //Updating our dictionary with keys 1-bit longer.
		LinkedHashMap<String, String> temporaryMap = new LinkedHashMap<String, String>();
		for(String s: mapper.keySet()) {
			temporaryMap.put("0" + s, mapper.get(s));
		}
		mapper.clear();
		for(String s: temporaryMap.keySet()) {
			mapper.put(s, temporaryMap.get(s));
		}
	}

	static String binaryToDecimal(String binary) {
		String decimal = "";
		for(int i = 0; i < binary.length(); i+= bitSizeEncoder) {
			decimal += (char)Integer.parseInt(binary.substring(i, Math.min(i + bitSizeEncoder, binary.length())), 2);
		}
		return decimal;
	}

	static void decode () throws IOException
	{
		decodeInputString =  decimalToBinary(toDecodeInputString) ; 
		int l = 0; 
		int r = 1; 
		int remainingSpaces = 0; 
		String currentKey = "000011111111"; 
		String currentEntry = "" + decodeInputString.get(l); 
		String nextEntry = "" + decodeInputString.get(r); 
		
		for(int i = 0; i < 256; i++) { //set up ASCII table with default values
			
			String binaryValue = Integer.toBinaryString(i); 
			while(binaryValue.length() < bitSizeDecoder)
			{
				binaryValue = "0" + binaryValue;  
			}
			decodedMap.put(binaryValue, "" + (char)(i));
		}

//		if(remainingSpaces == 0) { //If dictionary runs out of space, increase bit size by 1.
//			remainingSpaces = (int) Math.pow(2, bitSizeDecoder++);
//			updateMap(decodedMap); //Updates dictionary with increased bit-size.
//		}

		//Need to update information every iteration:
		remainingSpaces--; //Subtract one remaining space in the dictionary.


		while (r < decodeInputString.size()+1)
		{
			if (r ==  decodeInputString.size())
			{
				decoded += decodedMap.get(currentEntry); 
				r++; 
				l++; 
			}
			else if (nextEntry.compareTo(currentKey) >= 0)
			{
				decoded += decodedMap.get(currentEntry);
				currentKey = updateKey(currentKey); 
				decodedMap.put(currentKey, decodedMap.get(currentEntry) + decodedMap.get(getFirstBinaryString(currentEntry))); 
				r++; 
				l++; 
				currentEntry = "" + decodeInputString.get(l); 
				if (r != decodeInputString.size())
				{
					nextEntry = "" + decodeInputString.get(r); 
				}
				else
				{
					nextEntry = "";
				}
			}
			else if(decodedMap.containsValue(decodedMap.get(currentEntry) + decodedMap.get(nextEntry)))//Is Current + Next in our dictionary?	
			{ 
				String searchKey = ""; 
				for (int i= 0; i < bitSizeDecoder; i++)
				{
					searchKey += "0"; 
				}
				for (int i = 0; i < decodedMap.size(); i++)
				{
					if (decodedMap.get(searchKey) != null && decodedMap.get(searchKey).equals(decodedMap.get(currentEntry) + decodedMap.get(nextEntry)))
					{
						break; 
					}
					searchKey = updateKey(searchKey);
				}
				currentEntry = searchKey; 
			}
			else
			{			
				decoded += decodedMap.get(currentEntry); 
				currentKey = updateKey(currentKey); 
				decodedMap.put(currentKey, decodedMap.get(currentEntry) + decodedMap.get(getFirstBinaryString(nextEntry))); 
				r++; 
				l++; 
				currentEntry = "" + decodeInputString.get(l); 
				if (r != decodeInputString.size())
				{
					nextEntry = "" + decodeInputString.get(r); 
				}
				else
				{
					nextEntry = "";
				}
			
				
			}
		}
	}
	


	static ArrayList <String> decimalToBinary (String decimal)
	{
		ArrayList <String> output = new ArrayList <String> (); 
		String binary = ""; 
		for (int i = 0; i < decimal.length(); i++)
		{
			int store = (int)((decimal.charAt(i))); 
		    String newBinary = Integer.toString(store,2); 
			while(newBinary.length()< bitSizeEncoder)
			{
				newBinary = "0" + newBinary; 
			}
			binary = binary + newBinary ; 
		}
		for (int i = 0; i < binary.length(); i+= bitSizeEncoder)
		{
			output.add(binary.substring(i,Math.min(i + bitSizeEncoder, binary.length())));
		}
		return output; 
	}
	
	static String convertToBinary(String toConvert, LinkedHashMap <String, String> mapper)
	{
		
		String searchKey = ""; 
		for (int i= 0; i < bitSizeEncoder; i++)
		{
			searchKey += "0"; 
		}
		for (int i = 0; i < map.size(); i++)
		{
			if (mapper.get(searchKey).equals(toConvert))
			{
				break; 
			}
			searchKey = updateKey(searchKey); 
		}
		return searchKey; 
	}
	
	static String getFirstBinaryString(String binary)
	{
		 String firstBinary;  
		 String firstString = decodedMap.get(binary); 
		firstBinary =  Integer.toBinaryString((int)(firstString.charAt(0)));
		while (firstBinary.length() < bitSizeDecoder)
		{
			firstBinary = "0"+ firstBinary; 
		}		 
		return firstBinary; 
	}



}
