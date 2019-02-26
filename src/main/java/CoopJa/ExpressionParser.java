package main.java.CoopJa;

import java.util.ArrayList;
import java.util.Stack;

public class ExpressionParser {
    ExpressionParser(){

    }
    public static void testExp_dealwith() throws ParserException{
        String testInput = "((1-2)/3)"; //example statement to test parser
        ArrayList<Token> inputTokens = Token.tokenize(testInput); //tokenize var
        System.out.println("----- Expression -----");
        Expression outPut = Exp_dealwith(inputTokens);
        outPut.printSelf();

    }

    //recursive function, takes in a tokenSubset of the expression to be parsed
    public static Expression Exp_dealwith(ArrayList<Token> tokenstuff) throws ParserException {

        //something went horribly wrong
        if (tokenstuff.size() == 0)
            throw new ParserException("ERROR: Empty Expression");

        //in case of single token as expression
        if (tokenstuff.size() == 1){
            Token singleToken = tokenstuff.get(0);
            if (singleToken.getType().name().equals("NUMBER"))
                return new NumExpression(singleToken);
            if (singleToken.getType().name().equals("VARIABLENAME"))
                return new VariableExpression(singleToken);

                //if the singular expression is not either a number or variable it is invalid note: might change later
            else
                throw new ParserException("Error: \"" + singleToken.getType().name() + "\" Is not a valid Expression");
        }

        if (tokenstuff.size() == 2)
            throw new ParserException("ERROR: Expression incomplete");

        //in case of simple 3 token expression
        //this part might be unnesessary, consider it practice
        if (tokenstuff.size() == 3){
            //in case of "(1)" scenario
            if(tokenstuff.get(0).getType().name().equals("SYMBOL_LEFTPAREN")){
                if(tokenstuff.get(2).getType().name().equals("SYMBOL_RIGHTPAREN")){
                    ArrayList<Token> singleTokenArray = new ArrayList<Token>();
                    singleTokenArray.add(tokenstuff.get(1));
                    return Exp_dealwith(singleTokenArray);//recursivly deal with whats between the parens
                }
                //incase there is no matching right paren
                else throw new ParserException("ERROR: Left Paren without a right");
            }

            //assume first token of 3 is an expression and parse it as such
            String operatorName = tokenstuff.get(1).getType().name();
            if (    operatorName.equals("SYMBOL_PLUS") || //has to be one of the 4 operands
                    operatorName.equals("SYMBOL_MINUS") ||
                    operatorName.equals("SYMBOL_ASTERISK") ||
                    operatorName.equals("SYMBOL_SLASH")
            ){
                Expression firstOperand = Exp_dealwith(getSingleTokenArray(tokenstuff, 0));
                Expression secondOperand = Exp_dealwith(getSingleTokenArray(tokenstuff, 2));
                return new ArithmeticExpression(firstOperand, tokenstuff.get(1), secondOperand);
            }
            else throw new ParserException("ERROR: \"" + operatorName + "\" Is not a valid Operator");
        }
        //end of simple cases
        //larger than 3 token scenario
        else {
            Token currentToken;
            //methodology:
            //go through the token string, ignore anything encapsulated in parenthesis
            //find the lowest order function
            //recursively call everything before it and everything afterwards

            int lowestOrderOperator = findLowestOrderOperator(tokenstuff);
            if (lowestOrderOperator == -1)// (1+1+1...) case
                return Exp_dealwith(getTokenSubset(tokenstuff, 1, tokenstuff.size() - 1));

            //make sure that there is something before and after the operator
            if (lowestOrderOperator == 0 || lowestOrderOperator == tokenstuff.size())
                throw new ParserException("ERROR: Operator \"" + tokenstuff.get(lowestOrderOperator) + "\" out of place");

            //recursive calls before and after the operator
            Expression beforeOperator = Exp_dealwith(getTokenSubsetWithSingle(tokenstuff, 0,lowestOrderOperator));
            Expression afterOperator = Exp_dealwith(getTokenSubsetWithSingle(tokenstuff, lowestOrderOperator + 1,
                    tokenstuff.size()));
            return new ArithmeticExpression(beforeOperator, tokenstuff.get(lowestOrderOperator), afterOperator);

        }
    }

    //will return -1 when (1+1+1) case since it will skip anything in a Paren
    public static int findLowestOrderOperator(ArrayList<Token> tokenstuff) throws ParserException{
        int firstAsteriskSlashIndex = -1;
        int firstPlusMinusIndex = -1;

        //find the index of the lowest order operand
        for (int index = 0; index < tokenstuff.size(); index++){
            if (tokenstuff.get(index).getType().name().equals("SYMBOL_LEFTPAREN")){
                index = findEndParen(tokenstuff,index); //skip past anything inside a paren
                continue;
            }
            if (tokenstuff.get(index).getType().name().equals("NUMBER")) continue;
            if (tokenstuff.get(index).getType().name().equals("VARIABLENAME")) continue;
            if (tokenstuff.get(index).getType().name().equals("SYMBOL_PLUS") ||
                    tokenstuff.get(index).getType().name().equals("SYMBOL_MINUS")){
                firstPlusMinusIndex = index;
                continue;
            }
            if (tokenstuff.get(index).getType().name().equals("SYMBOL_ASTERISK") ||
                    tokenstuff.get(index).getType().name().equals("SYMBOL_SLASH")) {
                firstAsteriskSlashIndex = index;
                continue;
            }
            else
                throw new ParserException("ERROR: \"" + tokenstuff.get(index).getType().name() + "\" is not valid");
        }
        if (firstPlusMinusIndex != -1) return firstPlusMinusIndex;
        else if (firstAsteriskSlashIndex != -1) return firstAsteriskSlashIndex;
        else return -1;
    }

    //in case of multiple nested parenthesis, we need to find where the end of a paren expression is
    //this must be done this way in case of (((1+1) + 1) + 1) the encapsulating paren is the one at the very end
    //not the one to the right of 1+1
    //accepts the index of the start paren, returns the index of the end paren
    public static int findEndParen (ArrayList<Token> tokenstuff, int startParen) throws ParserException{
        //To deal with the parenthesis we are going to use a stack
        Stack<Integer> parenStack = new Stack<Integer>();
        int startIndex = startParen;
        Token t;
        for(int currentIndex = startParen; currentIndex < tokenstuff.size(); currentIndex++) {
            t = tokenstuff.get(currentIndex);
            if (t.getType().name().equals("SYMBOL_LEFTPAREN")) {
                parenStack.push(currentIndex);//push the index of the leftParam to the stack
            }
            if (t.getType().name().equals("SYMBOL_RIGHTPAREN")){
                if (!parenStack.isEmpty()){
                    startIndex = parenStack.pop();//when we find a right, pop the stack
                    if (parenStack.isEmpty()){//on an empty stack after a pop we know we are in the outer most paren
                        return currentIndex; //return index of outermost parameter
                    }
                }
                else //RightParen without a corrosponding left
                    throw new ParserException("ERROR: RightParen without a corrosponding left");
            }
        }
        throw new ParserException("ERROR: Paren parse error");
    }

    //return an arraylist with only one token, mostly for recursive method
    public static ArrayList<Token> getSingleTokenArray(ArrayList<Token> global, int index){
        ArrayList<Token> singleTokenArray = new ArrayList<Token>();
        singleTokenArray.add(global.get(index));
        return  singleTokenArray;
    }


    public static ArrayList<Token> getTokenSubset(ArrayList<Token> global, int start, int end) {
        //note: end contains actual meta character
        //ex: the right paren in "if(~)", the right curly in "if(~){~}", or rightmost curly in "if(~){~}else{~}"
        //because of this, for loop stays before the end int, but includes the start int
        ArrayList<Token> subsetList = null;
        if (start == end) { //empty stmt like "if()"
            return subsetList; //return empty list
        } else if (start < end) { //good
            int cap = end - start + 1; //capacity = numb of tokens
            subsetList = new ArrayList<Token>(cap);
            int put = 0;
            for (int turn = start; turn < end; turn++) {
                subsetList.add(put, global.get(turn)); //copy token to new arraylist
                put++;
            }
            return subsetList;
        } else { //start > end
            return subsetList; //return empty list
        }

    }

    //mostly a copy of getTokenSubset, The recursive logic of Exp_dealwith needs this to be able to return size 1 lists
    public static ArrayList<Token> getTokenSubsetWithSingle(ArrayList<Token> global, int start, int end){
        ArrayList<Token> subsetList = null;
        if (start <= end) { //good
            int cap = end - start + 1; //capacity = numb of tokens
            subsetList = new ArrayList<Token>(cap);
            int put = 0;
            for (int turn = start; turn < end; turn++) {
                subsetList.add(put, global.get(turn)); //copy token to new arraylist
                put++;
            }
            return subsetList;
        } else { //start > end
            return subsetList; //return empty list
        }
    }
}
