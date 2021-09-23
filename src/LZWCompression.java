import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LZWCompression {
	/*
	 * Possible optimization procedures: 1) Start dictionary's bit size at 9 so we
	 * cut out the unnecessary starting loop
	 * 
	 */
	static PrintWriter out;
	static String inputString = "";
	static ArrayList<String> decodeInputString = new ArrayList<String>();
	static String toDecodeInputString = "";
	static Scanner in;
	static int bitSizeEncoder = 12;
	static int bitSizeDecoder = 12;
	static String decoded = "";
	static LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	static LinkedHashMap<String, String> decodedMap = new LinkedHashMap<String, String>();

	public static void main(String[] args) throws IOException { //tester
		out = new PrintWriter(new File("dummy.txt"));
		in = new Scanner(new File("lzw-file3.txt"));
		long startTime = System.currentTimeMillis();
		init(); // Input/Output stuff
		solve(); // Actually solving stuff
		decode();
		out.print(decoded);
		in.close();
		out.close();
		System.out.println("Time elapsed: " + (System.currentTimeMillis() - startTime) + " milliseconds");

	}
	public static void aSCIICreator(LinkedHashMap <String, String> input) { //Sets up ASCII table with values of input
		for (int i = 0; i < 256; i++) { // set up ASCII table with default values
			String binaryValue = Integer.toBinaryString(i);
			while (binaryValue.length() < bitSizeEncoder) { //while loop in for loop is very time consuming, might be able to get rid of one loop (requires possibly rewriting convertToBinary as well) 
				binaryValue = "0" + binaryValue;
			}
			input.put(binaryValue, "" + (char) (i));
		}
	}
	static void init() throws IOException { // (British method)
		while (in.hasNext()) {
			inputString += in.nextLine() + "\n";
		}
		inputString = inputString.trim(); // gets rid of unnecessary newline at string's end
		aSCIICreator(map);
	}

	static void solve() throws IOException {

		// Variables:
		int remainingSpaces = 0; // Counts the available spaces left in the dictionary.
		int nextIndex = 1; // Will track our 'next' index inside the input string.
		String currentEntry = "" + inputString.charAt(0); // Holds our current string every iteration.
		String ans = ""; // Our answer string to be output at the very end.
		String currentKey = "011111111"; // Holds our latest bit # to use in dictionary.
		String binary = "";
		String tracker = "";

		// Loop:
		while (nextIndex < inputString.length() + 1) { // Parse through each index of the input string.
			// Need to update information every iteration:
			remainingSpaces--; // Subtract one remaining space in the dictionary.
			if (nextIndex == inputString.length()) {
				ans += convertToBinary(currentEntry, map); // Add the current entry to the output answer.
				currentKey = updateKey(currentKey); // Update our latest bit key to use.
				binary += currentKey;
			} else if (map.containsValue(currentEntry + inputString.charAt(nextIndex))) { // Is Current + Next in our
																						// dictionary?
				currentEntry += inputString.charAt(nextIndex); // If so, update our current entry string.
			} else {
				ans += convertToBinary(currentEntry, map); // Add the current entry to the output answer.
				currentKey = updateKey(currentKey); // Update our latest bit key to use.

				binary += currentKey;
				map.put(currentKey, currentEntry + inputString.charAt(nextIndex)); // Add Current + Next to dictionary.
				currentEntry = "" + inputString.charAt(nextIndex); // Update Current.
			}
			nextIndex++;
		}

		// Print stuff
		String str = binaryToDecimal(binary);
		toDecodeInputString = binaryToDecimal(ans);
		out.println(binaryToDecimal(ans));
	}

	static String updateKey(String key) { // Adds 1 to the dictionary key in binary.
		int number = Integer.parseInt(key, 2);
		int sum = number + 1;
		String newKey = Integer.toBinaryString(sum);
		while (newKey.length() < bitSizeEncoder) {
			newKey = "0" + newKey;
		}
		return newKey;
	}

	static String binaryToDecimal(String binary) {
		String decimal = "";
		for (int i = 0; i < binary.length(); i += bitSizeEncoder) {
			decimal += (char) Integer.parseInt(binary.substring(i, Math.min(i + bitSizeEncoder, binary.length())), 2);
		}
		return decimal;
	}

	// This is da code (decode)
	// This method changes the value of the decoded String from "" to the decoded
	// version of the encoded String

	static void decode() throws IOException {
		decodeInputString = decimalToBinary(toDecodeInputString);
		int left = 0;
		int right = 1;
		int remainingSpaces = 0;
		String currentKey = "000011111111";
		String currentEntry = "" + decodeInputString.get(left);
		String nextEntry = "" + decodeInputString.get(right);
		aSCIICreator(decodedMap);

		// Need to update information every iteration:
		remainingSpaces--; // Subtract one remaining space in the dictionary.

		while (right < decodeInputString.size() + 1) {
			if (right == decodeInputString.size()) {
				decoded += decodedMap.get(currentEntry);
				right++;
				left++;
			} else if (nextEntry.compareTo(currentKey) >= 0) {
				decoded += decodedMap.get(currentEntry);
				currentKey = updateKey(currentKey);
				decodedMap.put(currentKey, decodedMap.get(currentEntry) + decodedMap.get(getFirstBinaryString(currentEntry)));
				right++;
				left++;
				currentEntry = "" + decodeInputString.get(left);
				if (right != decodeInputString.size()) {
					nextEntry = "" + decodeInputString.get(right);
				} else {
					nextEntry = "";
				}
			} else if (decodedMap.containsValue(decodedMap.get(currentEntry) + decodedMap.get(nextEntry)))// Is Current
																											// + Next in
																											// our
																											// dictionary?
			{
				String searchKey = "";
				for (int i = 0; i < bitSizeDecoder; i++) {
					searchKey += "0";
				}
				for (int i = 0; i < decodedMap.size(); i++) {
					if (decodedMap.get(searchKey) != null && decodedMap.get(searchKey)
							.equals(decodedMap.get(currentEntry) + decodedMap.get(nextEntry))) {
						break;
					}
					searchKey = updateKey(searchKey);
				}
				currentEntry = searchKey;
			} else {
				decoded += decodedMap.get(currentEntry);
				currentKey = updateKey(currentKey);
				decodedMap.put(currentKey,
						decodedMap.get(currentEntry) + decodedMap.get(getFirstBinaryString(nextEntry)));
				right++;
				left++;
				currentEntry = "" + decodeInputString.get(left);
				if (right != decodeInputString.size()) {
					nextEntry = "" + decodeInputString.get(right);
				} else {
					nextEntry = "";
				}

			}
		}
	}

//Input a String
// This method will convert that String to binary and then break the String up into ArrayList components each with size equal to the bitSize and return that ArrayList
	static ArrayList<String> decimalToBinary(String decimal) {
		ArrayList<String> output = new ArrayList<String>();
		String binary = "";
		for (int i = 0; i < decimal.length(); i++) {
			int store = (int) ((decimal.charAt(i)));
			String newBinary = Integer.toString(store, 2);
			while (newBinary.length() < bitSizeEncoder) {
				newBinary = "0" + newBinary;
			}
			binary = binary + newBinary;
		}
		for (int i = 0; i < binary.length(); i += bitSizeEncoder) {
			output.add(binary.substring(i, Math.min(i + bitSizeEncoder, binary.length())));
		}
		return output;
	}

	// Input a String value toConvert to binary and a LinkedHashMap of Strings
	// This method finds and returns the binary String "key" that is associated with
	// some value ToConvert in the map
	static String convertToBinary(String toConvert, LinkedHashMap<String, String> mapper) {

		String searchKey = "";
		for (int i = 0; i < bitSizeEncoder; i++) {
			searchKey += "0";
		}
		for (int i = 0; i < mapper.size(); i++) {
			if (mapper.get(searchKey).equals(toConvert)) {
				break;
			}
			searchKey = updateKey(searchKey);
		}
		return searchKey;
	}

	// Input a key.
	// Given inputed key, finds associated value, gets first character of value as a
	// String, and finds key associated with that string value
	// returns that non-inputed key
	static String getFirstBinaryString(String binary) {
		String firstBinary;
		String firstString = decodedMap.get(binary);
		firstBinary = Integer.toBinaryString((int) (firstString.charAt(0)));
		while (firstBinary.length() < bitSizeDecoder) {
			firstBinary = "0" + firstBinary;
		}
		return firstBinary;
	}

}
