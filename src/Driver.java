import SyntaxAnalyzer.*;
import Compiler.Compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Driver {
    public static void main(String[] args) throws IOException {
        String directoryName = "";
        System.out.print("Enter directory that has all the .jack files that you want to compile: ");
        Scanner scan = new Scanner(System.in);
        directoryName = scan.nextLine();

        Path currentRelativePath = Paths.get("");
        String currDir = currentRelativePath.toAbsolutePath().toString();

        //take current directory as input if empty directory entered
        directoryName = (directoryName.equals("")) ? currDir : directoryName;
        File directory = new File(directoryName);
        File[] files = directory.listFiles();   //get all files under non-empty directoryName/

        //rootTree stores and combines all class files' tree under root tree node
        SyntaxTree tree = new SyntaxTree("rootTree", null);

        for (File f:files){
            //go through every file that ends with .jack
            if (f.getName().endsWith(".jack")){
                JackProgram jack = new JackProgram(new String[]{directoryName+"//"+f.getName()});
                jack.readFile();
                jack.tokenize();

                Parser pr = new Parser(jack.getFileName()+".xml");
                pr.parse(jack.getTokens());

                /** parses the class xml content into root tree's new class-level child */
                pr.parseTree(tree, 0);
                pr.closeFile();
            }
        }

        /**
        JackProgram jack = new JackProgram(args);
        jack.readFile();
        jack.tokenize();
        //jack.printTokens();
        //jack.writeTokensXML();
        Parser pr = new Parser(jack.getFileName()+"_parse.xml");
        pr.parse(jack.getTokens());

        SyntaxTree tree = new SyntaxTree("rootTree", null);
        //pr.printParsedXML();
        // parses the xml content into a parsed syntax tree
        pr.parseTree(tree, 0);
        pr.closeFile();

        //Parser.printTree(tree, 0);
        */

        Compiler cp = new Compiler(directoryName);
        /** pass parse tree to compiler */
        cp.compileRootTree(tree);
        //cp.compileClass(tree.getChildren().get(0));
        //cp.writeVM();
        //cp.closeFile();
    }
}
