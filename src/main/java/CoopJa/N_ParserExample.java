package main.java.CoopJa;

import java.util.ArrayList;

public class N_ParserExample {

    public static String exampletest = "if ( stuff ) { more stuff } else { other stuff }"; //example statement to test parser
    public static ArrayList<Token> alltokens = Token.tokenize(exampletest); //tokenize var
    //public static Token.TokenType[] receivedtypeslist = Token.extractTokenTypes(alltokens); //get list of tokens names

    public static void main(String[] args) {

        System.out.println("----- Initial Print -----");
        printTokens(alltokens);
        Validator(alltokens);

    }

    public static void printTokens(ArrayList<Token> toPrint) {
        System.out.println("// Printing ArrayList //");
        for (int i = 0; i < toPrint.size(); i++) {
            System.out.println(toPrint.get(i).getType().name());
        }
        System.out.println("// End of List //");
    }

    public static void Validator(ArrayList<Token> tokenslist) {

        int currentpos = 0; //probably not needed
        //start with first token in list (could use to chop off already dealt with material)
        switch (tokenslist.get(currentpos).getType().name()) {
            case "KEYWORD_IF":
                IfStmt iftest = if_dealwith(tokenslist);
                ParserPrinter(iftest); // TEST
                break;
            default:
                System.out.println("idk yet");
                break;
        }

    }

    public static IfStmt if_dealwith(ArrayList<Token> tokenstuff) {

        //declare if elements
        ArrayList<Token> ifCondition = null;
        ArrayList<Token> ifStmts = null;
        ArrayList<Token> elseStmts = null;

        //while will be similar to beginning of if
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
                        // THROW ERROR & BREAK
                        System.out.println("ERROR: illegal characters {} found in if condition");
                    }
                    currentpos++;
                } while (done);
            } catch (Exception e) { //no right paren
                // THROW ERROR & BREAK
                System.out.println("ERROR: no closing right parethesis is present");
            }

            ifCondition = getTokenSubset(tokenstuff, startpos, endpos); //arraylist of tokens for the if condition
            //send somewhere to resolve the if condition

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
                } catch (Exception e) { //no right curly
                    // THROW ERROR & BREAK
                    System.out.println("ERROR: no closing right curly is present");
                }
                ifStmts = getTokenSubset(tokenstuff, startpos, endpos);

                //must now have an else token followed by '{'

                //currently, the following is resolved if(cond){stmts}
                if (tokenstuff.get(currentpos).getType().name().equals("KEYWORD_ELSE")) { //good
                    if (tokenstuff.get(++currentpos).getType().name().equals("SYMBOL_LEFTCURLY")) { //have "else{"
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
                        } catch (Exception e) { //no right curly
                            // THROW ERROR & BREAK
                            System.out.println("ERROR: no closing right curly is present");
                        }

                        elseStmts = getTokenSubset(tokenstuff, startpos, endpos);

                    } else { //no left curly after else
                        // THROW ERROR & BREAK
                        System.out.println("ERROR: else not followed by '{'");
                    }

                } else { //no else keyword
                    // THROW ERROR & BREAK
                    System.out.println("ERROR: else keyword not present");
                }


            } else { //no curly
                // THROW ERROR & BREAK
                System.out.println("ERROR: if(cond) not followed by '{'");
            }


        } else { //if keyword not followed immediately by '('
            // THROW ERROR & BREAK
            System.out.println("ERROR: if keyword not followed by left parenthesis");
        }

        IfStmt outIF = new IfStmt(ifCondition, ifStmts, elseStmts);

        return outIF;

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

    public static void ParserPrinter(IfStmt inputIf) { //testing, just taking if right now

        System.out.println("----- Print If Conditions -----");
        printTokens(inputIf.Conditions);
        System.out.println("----- Print If Statements -----");
        printTokens(inputIf.Statements);
        System.out.println("----- Print Else Statements -----");
        printTokens(inputIf.ElseStmts);

    }

}

class IfStmt {

    ArrayList<Token> Conditions;
    ArrayList<Token> Statements;
    ArrayList<Token> ElseStmts;

    public IfStmt(ArrayList<Token> in_Cond, ArrayList<Token> in_Stmts, ArrayList<Token> in_ElseStmts) {
        Conditions = in_Cond;
        Statements = in_Stmts;
        ElseStmts = in_ElseStmts;
    }
}
