package cn.alumik.parsetree;

import cn.alumik.parsetree.lexer.Lexer;
import cn.alumik.parsetree.parser.ParseTree;
import cn.alumik.parsetree.parser.Parser;
import cn.alumik.parsetree.util.Config;

import java.util.Scanner;

public class ParseTreeApp {

    public static void main(String[] args) {
        final Config config = new Config("config.yml");
        try {
            final Lexer lexer = new Lexer(config);
            final Parser parser = new Parser(config);

            final Scanner scanner = new Scanner(System.in);
            final ParseTree parseTree = parser.parse(lexer.lex(scanner.next()));
            parseTree.draw("out/parse-tree.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
