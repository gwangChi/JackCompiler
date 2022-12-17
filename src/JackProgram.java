import SyntaxAnalyzer.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class JackProgram {
    public static String purifyStr(String str){
        // Removes all comments in a line that begins with //
        String ans = str.replaceAll("//.*[\\r\\n]+","\n");
        // Removes all comments enclosed inside /**/, including multi-line comments
        ans = ans.replaceAll("(?s)/\\*.*?\\*/","");
        // Removes all leading spaces in the first line
        ans = ans.replaceAll("^\\s+","");
        // Removes all leading spaces in the non-first lines
        ans = ans.replaceAll("\\n\\s+","\n");
        // Removes all blank lines or lines with only spaces
        ans = ans.replaceAll("[\\r\\n]+\\s*[\\r\\n]+","\n");
        // Removes first line if blank
        ans = ans.replaceAll("^[\\r\\n]+","");
        // Removes last line if blank
        ans = ans.replaceAll("[\\r\\n]+$","");

        return ans;
    }

    private ArrayList<Token> tokens;
    private String inFileName;
    private ArrayList<String> codeLines;

    public ArrayList<Token> getTokens(){
        return (ArrayList<Token>) this.tokens.clone();
    }

    public String getFileName(){
        return this.inFileName.replaceFirst("[.][^.]+$", "");
    }

    public void readFile() throws IOException{
        // fileName stores <filename>
        String fileName = this.inFileName.replaceFirst("[.][^.]+$", "");

        try {
            // read all lines from <filename>.in at once
            File inFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(inFile);
            byte[] data = new byte[(int) inFile.length()];
            fis.read(data);
            fis.close();
            // inStr stores all chars from <filename>.in
            String inStr = new String(data);

            // Remove blank lines and leading spaces and comments, and stored in outStr
            String outStr = purifyStr(inStr);

            // Use scanner to read each line from ourStr to this.codeLines.
            Scanner scanner = new Scanner(outStr);
            while (scanner.hasNextLine()) {
                this.codeLines.add(scanner.nextLine());
                // process the line
            }

            /**
            // Write outStr to <filename>.out
            FileOutputStream out = new FileOutputStream(fileName+".out");

            for(int i=0; i<outStr.length(); i++){
                out.write(outStr.charAt(i));
            }

            out.close();
             */

        }catch (FileNotFoundException e) {
            System.out.println("A file IO error occurred.");
            e.printStackTrace();
        }
    }

    public void tokenize(){
        for(String line:this.codeLines){
            tokenizeLine(line, line);
        }
    }

    public void writeTokensXML() throws IOException{
        // fileName stores <filename>
        String fileName = this.inFileName.replaceFirst("[.][^.]+$", "");

        try {
            FileOutputStream xmlOut = new FileOutputStream(fileName+"T.xml");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(xmlOut));
            // Write this.tokens.tokenValue to <filenameT>.xml
            bw.write("<tokens>");
            bw.newLine();
            for(Token token:this.tokens){
                bw.write(token.getTokenValue());
                bw.newLine();
            }
            bw.write("</tokens>");
            bw.newLine();
            bw.close();
        }catch (FileNotFoundException e) {
            System.out.println("A file IO error occurred.");
            e.printStackTrace();
        }
    }
    public void printTokens(){
        for(Token token:this.tokens){
            System.out.println(token.getTokenValue());
        }
    }

    public void tokenizeLine(String token, String line){
        if(line.isEmpty()){
            return;
        } else if (Token.isToken(token)) {
            Token newToken = new Token(token);
            this.tokens.add(newToken);
            /** Cut the token from the left on the remaining line string.*/
            tokenizeLine(line.substring(token.length()).replaceAll("^\\s+",""),
                    line.substring(token.length()).replaceAll("^\\s+",""));
        } else if(!token.isEmpty()){
            /** Delete the rightmost character from current token as it is not a valid token.*/
            tokenizeLine(token.substring(0,token.length()-1), line);
        } else{
            /** If the token is depleted and no valid tokens are found, display error msg.*/
            System.out.println("CODE ERROR FOUND: \""+line+"\" is not made of valid tokens!!!");
        }
    }

    public JackProgram(String[] args){
        this.tokens = new ArrayList<Token>();
        this.codeLines = new ArrayList<String>();

        if(args == null || args.length == 0){
            System.out.print("Enter file name including its directory: ");
            Scanner scan = new Scanner(System.in);
            // inFileName stores <filename>.jack from user input
            this.inFileName = scan.nextLine();
        }
        else{
            // take the first argument passed as <filename>.jack
            this.inFileName = args[0];
        }
    }

}
