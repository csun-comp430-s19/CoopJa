package main.java.CoopJa;

import java.util.ArrayList;

public class N_ParserExample {

    public static String exampletest = "for(1;2;3){4}"; //example statement to test parser
    public static ArrayList<Token> alltokens = Token.tokenize(exampletest); //tokenize var
    //public static Token.TokenType[] receivedtypeslist = Token.extractTokenTypes(alltokens); //get list of tokens names

    public static void main(String[] args) throws ParserException {

        System.out.println("----- Initial Print -----");
        printTokens(alltokens);
        Validator(alltokens);

    }

    public static void printTokens(ArrayList<Token> toPrint) {
        if (toPrint == null) {
            System.out.println("// Printing ArrayList //");
            System.err.println("ArrayList is Empty"); //NOTE EMPTY ARRAYLIST
            System.out.println();
            System.out.println("// End of List //");
        } else {
            System.out.println("// Printing ArrayList //");
            for (int i = 0; i < toPrint.size(); i++) {
                System.out.println(toPrint.get(i).getType().name());
            }
            System.out.println("// End of List //");
        }
    }

    public static void Validator(ArrayList<Token> tokenslist) throws ParserException { /// $$$ need to write something to keep calling this to deal with entire string

        int currentpos = 0; //probably not needed
        //start with first token in list (could use to chop off already dealt with material)
        switch (tokenslist.get(currentpos).getType().name()) {
            case "KEYWORD_IF":
                IfStmt iftest = if_dealwith(tokenslist);
                ParserPrinter(iftest);
                break;
            case "KEYWORD_WHILE":
                WhileLoop whiletest = while_dealwith(tokenslist);
                ParserPrinter(whiletest);
                break;
            case "KEYWORD_FOR":
                ForLoop fortest = for_dealwith(tokenslist);
                ParserPrinter(fortest);
                break;
            default:
                System.err.println("idk yet");
                break;
        }

    }

    public static IfStmt if_dealwith(ArrayList<Token> tokenstuff) throws ParserException {

        //declare if elements
        ArrayList<Token> ifCondition = null;
        ArrayList<Token> ifStmts = null;
        ArrayList<Token> elseStmts = null;

        int currentpos = 0;
        int startpos = 0; //start of if condition
        int endpos = 0; //end of if condition
        //if the list contains "if" followed immediately by "(" -- so "if("
        //tokenstuff[currentpos].name() gives the string name of the token (if: "KEYWORD_IF")
        if (tokenstuff.get(currentpos).getType().name().equals("KEYWORD_IF") && tokenstuff.get(++currentpos).getType().name().equals("SYMBOL_LEFTPAREN")) {
            currentpos++; //increment past 'if('
            startpos = currentpos;
            int extraparenseen = 0;
            boolean done = true;
            try { //check if there is a right paren
                do {
                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTPAREN")) {
                        extraparenseen++; //count the extra left paren
                    }
                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_RIGHTPAREN")) {
                        if (extraparenseen == 0) { //found end of if condition
                            endpos = currentpos; //endpoint
                            done = false; //escape
                        } else { //extraparenseen > 0
                            extraparenseen--;
                        }
                    }
                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTCURLY") || tokenstuff.get(currentpos).getType().name().equals("SYMBOL_RIGHTCURLY")) {
                        //illegal characters
                        //System.err.println("ERROR: illegal characters {} found in if condition");
                        throw new ParserException("ERROR: illegal characters {} found in if condition"); // #### not working correctly
                    }
                    currentpos++;
                } while (done);
            } catch (Exception e) { //no right paren
                throw new ParserException("ERROR: no closing right parethesis is present in if");
            }

            ifCondition = getTokenSubset(tokenstuff, startpos, endpos); //arraylist of tokens for the if condition

            //since we must have "if ( ) { } else { }
            //must now resolve {}, the body of the if stmt

            //currently, the following is resolved: if(cond), must now have a '{'
            if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTCURLY")) { //good
                currentpos++;
                startpos = currentpos;
                extraparenseen = 0;
                done = true;
                try { //check if there is a right curly
                    do {
                        if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTCURLY")) {
                            extraparenseen++; //count the extra left curly
                        }
                        if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_RIGHTCURLY")) {
                            if (extraparenseen == 0) { //found end of if stmts
                                endpos = currentpos; //endpoint
                                done = false; //escape
                            } else { //extraparenseen > 0
                                extraparenseen--;
                            }
                        }
                        currentpos++;
                    } while (done);
                } catch (Exception e4) { //no right curly
                    throw new ParserException("ERROR: no closing right curly is present in if");
                }
                ifStmts = getTokenSubset(tokenstuff, startpos, endpos);

                //must now have an else token followed by '{'

                //currently, the following is resolved if(cond){stmts}
                try {
                    if (tokenstuff.get(currentpos).getType().name().equals("KEYWORD_ELSE")) {
                        if (tokenstuff.get(++currentpos).getType().name().equals("SYMBOL_LEFTCURLY")) {
                            currentpos++; //increment past 'else{'
                            startpos = currentpos;
                            extraparenseen = 0;
                            done = true;
                            try { //check if there is a right curly
                                do {
                                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTCURLY")) {
                                        extraparenseen++; //count the extra left paren
                                    }
                                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_RIGHTCURLY")) {
                                        if (extraparenseen == 0) { //found end of if condition
                                            endpos = currentpos; //endpoint
                                            done = false; //escape
                                        } else { //extraparenseen > 0
                                            extraparenseen--;
                                        }
                                    }
                                    currentpos++;
                                } while (done);
                            } catch (Exception e3) { //no right curly
                                System.err.println("ERROR: no closing right curly is present after else");
                                throw new ParserException("ERROR: no closing right curly is present after else");
                            }

                            elseStmts = getTokenSubset(tokenstuff, startpos, endpos); //arraylist of tokens for the if condition

                        } else {
                            System.err.println("ERROR: else not followed by '{'");
                            throw new ParserException("ERROR: else not followed by '{'");
                        }

                    }
                } catch (Exception e2) {
                    //System.err.println("ERROR: else keyword not present");
                    throw new ParserException("ERROR: else keyword not present");
                }


            } else { //no curly
                //System.err.println("ERROR: if(cond) not followed by '{'");
                throw new ParserException("ERROR: if(cond) not followed by '{'");
            }


        } else { //if keyword not followed immediately by '('
            //System.err.println("ERROR: if keyword not followed by left parenthesis");
            throw new ParserException("ERROR: if keyword not followed by left parenthesis");
        }

        IfStmt outIF = new IfStmt(ifCondition, ifStmts, elseStmts);

        return outIF;

    }

    public static WhileLoop while_dealwith(ArrayList<Token> tokenstuff) throws ParserException {

        ArrayList<Token> whileCondition = null;
        ArrayList<Token> whileStmts = null;

        int currentpos = 0;
        int startpos = 0;
        int endpos = 0;
        //reaching this point means that a while token was detected, so check if there is a '(' after
        //tokenstuff[currentpos].name() gives the string name of the token (while: "KEYWORD_WHILE")
        if (tokenstuff.get(currentpos).getType().name().equals("KEYWORD_WHILE") && tokenstuff.get(++currentpos).getType().name().equals("SYMBOL_LEFTPAREN")) {
            currentpos++;
            startpos = currentpos;
            int extraparenseen = 0;
            boolean done = true;
            try { //check if there is a right paren
                do {
                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTPAREN")) {
                        extraparenseen++; //count the extra left paren
                    }
                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_RIGHTPAREN")) {
                        if (extraparenseen == 0) { //found end of while condition
                            endpos = currentpos; //endpoint
                            done = false; //escape
                        } else { //extraparenseen > 0
                            extraparenseen--;
                        }
                    }
                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTCURLY") || tokenstuff.get(currentpos).getType().name().equals("SYMBOL_RIGHTCURLY")) {
                        //illegal characters
                        //System.err.println("ERROR: illegal characters {} found in while condition");
                        throw new ParserException("ERROR: illegal characters {} found in while condition"); // #### not working correctly
                    }
                    currentpos++;
                } while (done);
            } catch (Exception e) { //no right paren
                throw new ParserException("ERROR: no closing right parethesis is present in while");
            }

            whileCondition = getTokenSubset(tokenstuff, startpos, endpos); //arraylist of tokens for the while condition

            //since we must have "while ( ) { }
            //must now resolve {}, the body of the while stmt

            //currently, the following is resolved: while(cond), must now have a '{'
            if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTCURLY")) { //good
                currentpos++;
                startpos = currentpos;
                extraparenseen = 0;
                done = true;
                try { //check if there is a right curly
                    do {
                        if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTCURLY")) {
                            extraparenseen++; //count the extra left curly
                        }
                        if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_RIGHTCURLY")) {
                            if (extraparenseen == 0) { //found end of while stmts
                                endpos = currentpos; //endpoint
                                done = false; //escape
                            } else { //extraparenseen > 0
                                extraparenseen--;
                            }
                        }
                        currentpos++;
                    } while (done);
                } catch (Exception e4) { //no right curly
                    throw new ParserException("ERROR: no closing right curly is present in while");
                }
                whileStmts = getTokenSubset(tokenstuff, startpos, endpos);

            } else { //no curly
                //System.err.println("ERROR: while(cond) not followed by '{'");
                throw new ParserException("ERROR: while(cond) not followed by '{'");
            }


        } else { //while keyword not followed immediately by '('
            //System.err.println("ERROR: while keyword not followed by left parenthesis");
            throw new ParserException("ERROR: while keyword not followed by left parenthesis");
        }

        WhileLoop outWHILE = new WhileLoop(whileCondition, whileStmts); /// $$$ maybe moving with final validation (where "whileStmts" is finalized)

        return outWHILE;

    }

    public static ForLoop for_dealwith(ArrayList<Token> tokenstuff) throws ParserException {

        ArrayList<Token> forCondition1 = null;
        ArrayList<Token> forCondition2 = null;
        ArrayList<Token> forCondition3 = null;
        ArrayList<Token> forStmts = null;

        int substatements = 0; //count ; found
        boolean thirdsemi = false; //deal with 3rd stmt condition

        int currentpos = 0;
        int startpos = 0;
        int endpos = 0;
        //reaching this point means that a for token was detected, so check if there is a '(' after
        //tokenstuff[currentpos].name() gives the string name of the token (for: "KEYWORD_FOR")
        if (tokenstuff.get(currentpos).getType().name().equals("KEYWORD_FOR") && tokenstuff.get(++currentpos).getType().name().equals("SYMBOL_LEFTPAREN")) {
            currentpos++;
            startpos = currentpos;
            int extraparenseen = 0;
            boolean done = true;
            try { //check if there is a right paren
                do {
                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTPAREN")) {
                        extraparenseen++; //count the extra left paren
                    }
                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_RIGHTPAREN")) {
                        if (extraparenseen == 0 && substatements == 2 && thirdsemi) { //found end of for condition
                            endpos = currentpos; //endpoint
                            done = false; //escape
                            forCondition3 = getTokenSubset(tokenstuff, startpos, endpos); //third & final for condition ie: for(1;2;"3")
                        } else { //extraparenseen > 0
                            extraparenseen--;
                        }
                    }
                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTCURLY") || tokenstuff.get(currentpos).getType().name().equals("SYMBOL_RIGHTCURLY")) {
                        //illegal characters
                        System.err.println("ERROR: illegal characters {} found in for condition");
                        throw new ParserException("ERROR: illegal characters {} found in for condition"); // #### not working correctly
                    }
                    if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_SEMICOLON")) { // $$$ could handle empty stmts here maybe
                        //for condition stmt found
                        if (substatements == 0) { //first for subcond
                            if (tokenstuff.get(currentpos + 1) != null) { //if there is more after the semicolon, 2nd stmt could be there
                                String[] forBad = {"SYMBOL_SEMICOLON", "SYMBOL_LEFTPAREN", "SYMBOL_RIGHTPAREN", "SYMBOL_LEFTCURLY", "SYMBOL_RIGHTCURLY"}; //dont count for symbols
                                boolean bad = false;
                                int testpos = currentpos + 1;
                                for (int i = 0; i < forBad.length && bad == false; i++) { //if the next token is one of the restricted tokens, dont count 2nd stmt as present
                                    if (tokenstuff.get(testpos).getType().name().equals(forBad[i])) {
                                        bad = true;
                                    }
                                }
                                if (bad) {
                                    substatements++; //adding because since there is nothing after, it will break here and not count the semicolon it's currently handling
                                    System.err.println("ERROR: 2nd for condition is empty");
                                    throw new ParserException("ERROR: 2nd for condition is empty");
                                } else {
                                    //continue
                                }
                            }
                            endpos = currentpos;
                            //test if 1st stmt is empty
                            ArrayList<Token> teststmt1;
                            teststmt1 = getTokenSubset(tokenstuff, startpos, endpos); //first for condition ie: for("1";2;3)
                            if (teststmt1 == null) { //if stmt empty
                                substatements++; //adding because since there is nothing after, it will break here and not count the semicolon it's currently handling
                                System.err.println("ERROR: 1st for condition is empty");
                                throw new ParserException("ERROR: 1st for condition is empty");
                            } else { //not empty
                                forCondition1 = teststmt1;
                            }
                        }
                        if (substatements == 1) { //second for subcond
                            if (tokenstuff.get(currentpos + 1) != null) { //if there is more after the semicolon, 3rd stmt could be there
                                String[] forBad = {"SYMBOL_SEMICOLON", "SYMBOL_LEFTPAREN", "SYMBOL_RIGHTPAREN", "SYMBOL_LEFTCURLY", "SYMBOL_RIGHTCURLY"}; //dont count for symbols
                                boolean bad = false;
                                int testpos = currentpos + 1;
                                for (int i = 0; i < forBad.length; i++) { //if the next token is one of the restricted tokens, dont count 3rd stmt as present
                                    if (tokenstuff.get(testpos).getType().name().equals(forBad[i])) {
                                        bad = true;
                                    }
                                }
                                if (bad) {
                                    thirdsemi = false; //no third stmt
                                    substatements++; //adding because since there is nothing after, it will break here and not count the semicolon it's currently handling
                                    System.err.println("ERROR: third for condition is empty");
                                    throw new ParserException("ERROR: third for condition is empty");
                                } else {
                                    thirdsemi = true; //yes there is a 3rd stmt
                                }
                                endpos = currentpos;
                                forCondition2 = getTokenSubset(tokenstuff, startpos, endpos); //second for condition ie: for(1;"2";3)
                            }
                            endpos = currentpos;
                            forCondition2 = getTokenSubset(tokenstuff, startpos, endpos); //second for condition ie: for(1;"2";3)
                        }
                        if (substatements >= 2) { //more semicolons found
                            thirdsemi = false;
                            System.err.println("ERROR: too many semicolons in for cond");
                            throw new ParserException("ERROR: too many semicolons in for cond");
                        }
                        startpos = currentpos + 1; //reposition start at presumed next substatement, but without incrementing currentpos, since this is done at end of do-while loop
                        substatements++;
                    }
                    currentpos++;
                } while (done);
            } catch (Exception e) { //better handling
                if (extraparenseen > 0) { //more '(' seen then ')'
                    System.err.println("more '(' seen then ')'");
                    throw new ParserException("ERROR: no closing right parethesis is present in for");
                }
                if (extraparenseen < 0) { //more ')' found than '('
                    System.err.println("more ')' seen then '('");
                    throw new ParserException("ERROR: no closing right parethesis is present in for");
                }
                if (extraparenseen == 0) {
                    if (substatements > 2) {
                        System.err.println("ERROR: for sub-conditions error. Too many semicolons (" + substatements + ") found.");
                        throw new ParserException("ERROR: for sub-conditions error. Too many semicolons (" + substatements + ") found.");
                    }
                    if (substatements < 2) {
                        System.err.println("ERROR: for sub-conditions error. Too few semicolons (" + substatements + ") found.");
                        throw new ParserException("ERROR: for sub-conditions error. Too few semicolons (" + substatements + ") found.");
                    }
                    if (substatements == 2 && thirdsemi == false) {
                        System.err.println("ERROR: for sub-conditions error. Found " + substatements + " semicolons, but third statement is empty");
                        throw new ParserException("ERROR: for sub-conditions error. Found " + substatements + " semicolons, but third statement is empty");
                    }
                }
            }


            //currently, the following is resolved: while(cond), must now have a '{'
            if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTCURLY")) { //good
                currentpos++;
                startpos = currentpos;
                extraparenseen = 0;
                done = true;
                try { //check if there is a right curly
                    do {
                        if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_LEFTCURLY")) {
                            extraparenseen++; //count the extra left curly
                        }
                        if (tokenstuff.get(currentpos).getType().name().equals("SYMBOL_RIGHTCURLY")) {
                            if (extraparenseen == 0) { //found end of for stmts
                                endpos = currentpos; //endpoint
                                done = false; //escape
                            } else { //extraparenseen > 0
                                extraparenseen--;
                            }
                        }
                        currentpos++;
                    } while (done);
                } catch (Exception e4) { //no right curly
                    throw new ParserException("ERROR: no closing right curly is present in for");
                }
                forStmts = getTokenSubset(tokenstuff, startpos, endpos);

            } else { //no curly
                //System.err.println("ERROR: for(cond) not followed by '{'");
                throw new ParserException("ERROR: for(cond) not followed by '{'");
            }

        } else { //for keyword not followed immediately by '('
            //System.err.println("ERROR: for keyword not followed by left parenthesis");
            throw new ParserException("ERROR: for keyword not followed by left parenthesis");
        }

        ForLoop outFOR = new ForLoop(forCondition1, forCondition2, forCondition3, forStmts); /// $$$ maybe moving with final validation

        return outFOR;

    }

    public static ArrayList<Token> getTokenSubset(ArrayList<Token> global, int start, int end) {
        //note: end contains actual meta character
        //ex: the right paren in "if(~)", the right curly in "if(~){~}", or rightmost curly in "if(~){~}else{~}"
        //because of this, for loop stays before the end int, but includes the start int
        ArrayList<Token> subsetList = null;
        if (start == end) { //empty stmt like "if()"
            return subsetList; //return empty list
        } else {
            int cap = end - start + 1; //capacity = numb of tokens
            subsetList = new ArrayList<Token>(cap);
            int put = 0;
            for (int turn = start; turn < end; turn++) {
                subsetList.add(put, global.get(turn)); //copy token to new arraylist
                put++;
            }
            return subsetList;
        }
    }

    public static void ParserPrinter(Statement input) {

        if (input instanceof IfStmt) {
            System.out.println("----- If -----");
            input.printSelf();
        }
        if (input instanceof WhileLoop) {
            System.out.println("----- While -----");
            input.printSelf();
        }
        if (input instanceof ForLoop) {
            System.out.println("----- For -----");
            input.printSelf();
        }

    }

}

class ParserException extends Exception { //may need to make its own Java file at some point
    public ParserException(final String message) {
        super(message);
    }
}
