import java.util.*;
import java.io.*;
public class LZWCompression {
	
	static BufferedReader in;
	static PrintWriter out;
	static String input = "";
	static Scanner s;
	static LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	
	public static void main(String[] args) throws IOException{
		in = new BufferedReader(new FileReader(new File("lzw-file2.txt")));
		out = new PrintWriter(new File("output.txt"));
		s = new Scanner(new File("input.txt"));
		init();
		solve();
		
		in.close();
		out.close();
	}
	
	static void init() throws IOException{
		while(s.hasNext()) {
			input += s.nextLine() + "\n";
		}
		for(int i = 0; i < 256; i++) {
			map.put(Integer.toBinaryString(i), "" + (char)(i));
		}
		
	}
	
	static void solve() {
		
		out.print(input.substring(0, input.length() - 2));
		for(String str: map.keySet()) {
			out.println(str + ": " +  map.get(str));
		}
	}

}
