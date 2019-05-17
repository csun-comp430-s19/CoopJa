package CoopJa;

import org.typemeta.funcj.parser.Input;

import java.lang.reflect.Array;
import java.util.*; //ArrayList, HashMap, Set

public class Typechecker {

    public PProgram DuplicateProgram; //holds a copy of the input program to edit later
    public HashMap<String, Storage> ClassListAll = new HashMap(); //holds (Class Name, Storage Object (holds ArrayList<String> of names of Variables and Methods for the Class)
    public String ClassString = ""; //keeps name of the currently typechecking class, used to find this class's Storage object from the ClassListAll var
    public String MethodString = "";
    public ArrayList<String> ClassNamesListAll = new ArrayList<>(); //holds the names of all declared classes (sorted) for easy ref
    public ArrayList<AutoTicket> AutoHandler = new ArrayList<>(); //holds all autotickets
    public ArrayList<AutoTicket> ResolvedAutoTickets = new ArrayList<>(); //holds all resolved autotickets
    public ArrayList<ChildOverride> ChildMethodOverrideList = new ArrayList<>(); //holds all info about a child class replacing a method in parent
    public int ClassNumber; //holds array val of which class we are working on
    public int ClassDeclarationNumber; //holds the array val for which stmt inside the class we are on (auto related)
    public int MethodDeclarationNumber; //if method has a body, used to store the dec number (in array format, starting with 0) for later reference
    public Storage FunctionCallParameterScope; //global for the special case where a recursive function call needs to refer to parameters from its original scope
    public boolean globalAutoOff = false; //used to send to VDT to handle auto in nestings

    public void main(String[] args) throws TypeCheckerException {

//        String foo = "public class one {" +
//                "auto i;" +
//                "auto j;" +
//                "public void main() {" +
//                "auto tempv = 7;" +
//                "i = 0;" +
//                "j = true;" +
//                "}" +
//                "}";

        String foo = "public class one {" +
                "auto i;" +
                "public void main() {" +
                "i = 0;" +
                "}" +
                "}" +
                "public class two extends one {" +
                "int testing;" +
                "public void main() {" +
                "testing = 99;" +
                "}" +
                "}";


        ArrayList<Token> tokenList = Token.tokenize(foo); //tokenize example string
        Input<Token> tokenListInput = new TokenParserInput(tokenList);
        MainParser parsers = new MainParser(); //create MainParser object
        PProgram fooTester = parsers.programParser.parse(tokenListInput).getOrThrow(); //Parse the example var
        System.out.println();

        TypecheckMain(fooTester); //call typechecker with parsed program obj

    } //end Main()

    public PProgram TypecheckMain(PProgram fooTester) throws TypeCheckerException { //typechecker
        DuplicateProgram = fooTester; //create duplicate program for later
        ArrayList<PClassDeclaration> classlist = new ArrayList<PClassDeclaration>(1);

        for (int i = 0; i < fooTester.classDeclarationList.size(); i++) { //load classes into above ArrayList
            //Map classes to their storage.
            classlist.add(i, fooTester.classDeclarationList.get(i));
            ClassListAll.put(fooTester.classDeclarationList.get(i).identifier.getTokenString(), new Storage());
            PClassDeclaration tempClass = classlist.get(i); //assign first class to a temp var
            int x = i + 1; //used for printing
            System.out.println("Current Class: #" + x);

            ClassNumber = i; //x is just i + 1
            String extendscheck = ClassTypecheck(tempClass); //typecheck the current class declaration

            if (extendscheck != null) { //class extends another that exists, grab info
                Storage extendsObj = ClassListAll.get(extendscheck); //get parent obj
                Storage t_S = ClassListAll.get(ClassString); //current class
                t_S.extendsClass = extendsObj; //store parent in child
                ClassListAll.put(ClassString, t_S); //store back new info
            }

            ArrayList<PDeclaration> tempDeclar = tempClass.declarationList; //pull out the declarations for this current class
            System.out.println("Class #" + x + " Declarations Amount: " + tempDeclar.size()); //how many declaration stmts are there?

            System.out.println("Declarations Begin: ");

            for (int j = 0; j < tempDeclar.size(); j++) { //for each declaration, either it is a PVariableDeclaration or a PStatementFunctionDeclaration

                ClassDeclarationNumber = j; //y is just j + 1
                int y = j + 1; //used for printing

                if (tempDeclar.get(j) instanceof PVariableDeclaration) { //handle class variable declarations
                    System.out.println("Declaration #" + y + " is instance of PVariableDeclaration");
                    PVariableDeclaration tempVar = (PVariableDeclaration) tempDeclar.get(j); //cast the PDeclaration obj into its proper form in a temp var

                    Storage t_S = ClassListAll.get(ClassString); //pull out Storage obj of the current class
                    HashMap<String, VarStor> t_VS = t_S.VariableNames; //pull the vars out of the Storage object
                    HashMap<String, VarStor> t_NEWVars; //holds new info after VDT call
                    //returns a map entry for this variable if it passes the typecheck.
                    //class storage is passed since we need to have the scope to check expressions. Might as well not pass t_VS.
                    t_NEWVars = VariableDeclarationTypecheck(t_S, tempVar, false, true); //call VDT with this list of vars (Scope) and get new info
                    t_VS.putAll(t_NEWVars); //add new info to old map
                    t_S.VariableNames = t_VS; //replace Storage object var list with updated copy
                    ClassListAll.put(ClassString, t_S); //replace the old Storage obj by adding it back to class hashmap with class string

                }

                if (tempDeclar.get(j) instanceof PStatementFunctionDeclaration) { //for each method declaration
                    System.out.println("Declaration #" + y + " is instance of PStatementFunctionDeclaration");
                    PStatementFunctionDeclaration tempFunc = (PStatementFunctionDeclaration) tempDeclar.get(j); //cast PDeclaration object to its proper type

                    //Storage tempSendClassStor = ClassListAll.get(ClassString); //retrieve current class's Storage object
                    //Storage replaceClassStor = ;tempSendClassStor
                    HashMap<String, Storage> tempClass111111 = ClassListAll;
                    Storage local111111 = tempClass111111.get(ClassString); //send it a copy
                    MethodDeclarationTypecheck(local111111, tempFunc); //call MDT, send it current class's Stor obj & the PStatementFunctionDeclaration obj, returns an updated Stor obj after typechecking the method
                    //ClassListAll.put(ClassString, replaceClassStor); //update the class's stor obj with method info
                }
                System.out.println("End Declaration #" + y);
                System.out.println();
            }

            ClassNumber = -1;
            ClassDeclarationNumber = -1;

            System.out.println("End of Class #" + x);
            System.out.println();
        }

        //check autohandler to see if it still has stuff in it
        if (AutoHandler.size() == 0) { //good, no open auto tickets
            //nothing
        } else {
            String autoErrOut = "";
            for (int i = 0; i < AutoHandler.size(); i++) {
                AutoTicket tempauto = AutoHandler.get(i);
                autoErrOut = "VarName: " + tempauto.TargetVarName + " in Class: " + tempauto.ClassName + ", Awaited Type: " + tempauto.NewType + "\n";
            }
            throw new TypeCheckerException("Auto Typecheck Error: Could not resolve some Auto Variable Types\n" +
                    "AutoHandler Size: " + AutoHandler.size() + "\n" + autoErrOut);
        }

        //reorder PProgram object
        //info: in order to help CodeGen, because of the way it works (each piece generates its own code in the order its in)
        //only in the case of Inheritance, we need to reorder the variable declarations in the child to align with the Parent & add them together
        //this has to do with how C inheritance works
        //Example: Parent{var1,var2,funct1,var3}
        //child before: {var4,var5,funct2,funct3}
        //Child After: {var1,var2,funct1,var3,var4,var5,funct2,funct3}
        for (int i = 0; i < DuplicateProgram.classDeclarationList.size(); i++) {
            PClassDeclaration tempClassCHILD = DuplicateProgram.classDeclarationList.get(i);
            if (tempClassCHILD.extendsIdentifier != null) { //if the class extends another
                if (ChildMethodOverrideList.size() == 0) {
                    int ParentLoc = ClassNamesListAll.indexOf(tempClassCHILD.extendsIdentifier.getTokenString()); //find the index of the parent class, using the name of the parent, retrieves the pos in arraylist
                    PClassDeclaration tempClassPARENT = DuplicateProgram.classDeclarationList.get(ParentLoc); //grab parent class dec
                    ArrayList<PDeclaration> CombinedDeclarations = tempClassPARENT.declarationList; //make a combined declarations list, insert parent first
                    CombinedDeclarations.addAll(tempClassCHILD.declarationList); //add child declarations
                    tempClassCHILD.declarationList = CombinedDeclarations; //replace child dec with combined dec local copy
                    DuplicateProgram.classDeclarationList.set(i, tempClassCHILD); //REPLACE CHILD IN PPROGRAM, i is pos of child
                    System.err.println("TYPECHECKER REORDER INFO: Found that Class " + tempClassCHILD.identifier.getTokenString() + " has Parent Class: " + tempClassPARENT.identifier.getTokenString() + "" +
                            ", so Typechecker has combined its Declarations in order {Parent,Child}");
                } else { //override stuff needed
                    int TEMPCMO = ChildMethodOverrideList.size();
                    ArrayList<ChildOverride> tempClassO = new ArrayList<>();
                    for (int j = 0; j < TEMPCMO; j++) { //check list of child overrides, collect all pertaining to the current class
                        ChildOverride temp1 = ChildMethodOverrideList.get(j);
                        if (temp1.ChildClassName.equals(tempClassCHILD.identifier.getTokenString())) { //if current working class is the class we need to deal with
                            tempClassO.add(temp1); //add this to temp list
                            //TEMPCMO.remove(j);
                        }
                    }
                    //ChildMethodOverrideList = TEMPCMO; //keep removals
                    int ParentLoc = ClassNamesListAll.indexOf(tempClassCHILD.extendsIdentifier.getTokenString());
                    PClassDeclaration tempClassPARENT = DuplicateProgram.classDeclarationList.get(ParentLoc); //parent decelaration temp
                    int TCTODO = tempClassO.size();
                    int FINALCOUNT = tempClassO.size();
                    ArrayList<Integer> removetheseinchild = new ArrayList<Integer>();
                    for (int k = 0; k < TCTODO; k++) { //for each method override in the class
                        ChildOverride CM = tempClassO.get(k); //pull out the ChildOverride
                        PDeclaration tempDec = tempClassCHILD.declarationList.get(CM.ChildClassDecNumb);
                        if (tempDec instanceof PStatementFunctionDeclaration) { //good, found method
                            PStatementFunctionDeclaration tempFUNC = (PStatementFunctionDeclaration) tempDec; //CHILD METHOD TO REPLACE PARENT
                            if (tempFUNC.identifier.getTokenString().equals(CM.MethodName)) { //found correct method name in child
                                for (int y = 0; y < tempClassPARENT.declarationList.size(); y++) { //iterate through parent and find the method to be replaced
                                    PDeclaration temp155 = tempClassPARENT.declarationList.get(y);
                                    if (temp155 instanceof PStatementFunctionDeclaration) { //dont bother to check if not a functdec
                                        PStatementFunctionDeclaration temp999 = (PStatementFunctionDeclaration) temp155;
                                        if (temp999.identifier.getTokenString().equals(CM.MethodName)) { //found method name in parent
                                            tempClassPARENT.declarationList.set(y, tempFUNC); //***REPLACE PARENT METHOD WITH CHILD, NOW WILL MERGE THEM
                                            //tempClassO.remove(k); //remove completed method name
                                            //tempClassCHILD.declarationList.remove(CM.ChildClassDecNumb); //remove method dec in child
                                            FINALCOUNT--;
                                            removetheseinchild.add(CM.ChildClassDecNumb);
                                            System.err.println("METHOD " + CM.MethodName + " REPLACED");
                                        }
                                    }
                                }
                            } else {
                                throw new TypeCheckerException("Replacement Error: Could not find correct Function named " + CM.MethodName + " in Child Class " + tempClassCHILD.identifier.getTokenString() + " " +
                                        "in pos " + CM.ChildClassDecNumb);
                            }
                        } else {
                            throw new TypeCheckerException("Replacement Error: Could not find Function Declaration in Child Class " + tempClassCHILD.identifier.getTokenString() + " " +
                                    "in pos " + CM.ChildClassDecNumb);
                        }
                    }
                    //tempClassCHILD.declarationList.remove(CM.ChildClassDecNumb); removetheseinchild Integer
                    if (FINALCOUNT == 0) { //success
                        for (int j = removetheseinchild.size() - 1; j >= 0; j--) {
                            tempClassCHILD.declarationList.remove((int) removetheseinchild.get(j));
                        }

                        ArrayList<PDeclaration> CombinedDeclarations = tempClassPARENT.declarationList; //combining, parent first
                        tempClassCHILD.declarationList = CombinedDeclarations; //now child
                        DuplicateProgram.classDeclarationList.set(i, tempClassCHILD); //REPLACE CHILD IN PPROGRAM
                        System.err.println("--REPLACED PARENT METHODS SUCCESSFULLY--");
                    } else {
                        throw new TypeCheckerException("!!_______SOME ERROR_______!! " + tempClassO.size());
                    }
                }
            } //else do nothing
        }

//        System.out.println("testing reorder");
//        for (int xyz = 0; xyz < DuplicateProgram.classDeclarationList.size(); xyz++) { //testing
//            System.out.println(DuplicateProgram.classDeclarationList.get(xyz).identifier.getTokenString());
//            for (int ooo = 0; ooo < DuplicateProgram.classDeclarationList.get(xyz).declarationList.size(); ooo++) {
//                System.out.println(DuplicateProgram.classDeclarationList.get(xyz).declarationList.get(ooo).getIdentiferString());
//                System.out.println(DuplicateProgram.classDeclarationList.get(xyz).declarationList.get(ooo).getClass());
//            }
//        }

        System.out.println("Typechecker has Completed"); //:)
        return DuplicateProgram;
    }

    //entrypoint for checking variable declaration / assignment (more info in body)
    public void CheckVarAss(Object input, Storage varMap) throws TypeCheckerException {
        //still called from VDT(), just name changed and logic moved around and repurposed to be recursive

        Token.TokenType varType;
        Token.TokenType assType; //good comment

        boolean isVD = false;

        if (input instanceof PVariableDeclaration) {
            isVD = true; //we just came from VDT, this is a vardec
            PVariableDeclaration varDec = (PVariableDeclaration) input;
            System.out.println("Checking Variable Declaration Body (UPDATED!)");

            varType = varDec.variableType.getType();
            assType = getExpressionType(varDec.assignment, varMap);

        } else if (input instanceof PVariableAssignment) {
            isVD = false;
            PVariableAssignment varAss = (PVariableAssignment) input;
            //typecheck assignement
            Token.TokenType assignment = getExpressionType(varAss.value, varMap);
            //check if assignee is within the scope
            Token.TokenType assignee = VariableInScope(varAss.identifier.getTokenString(), varMap);
            //weirdness going on for string stuff, was used to fix a bug long ago, but now it looks bizarre
            //possilble failure point for string typechecking
            if (assignment == Token.TokenType.KEYWORD_STRING) { //strings types name return as type identifiers rather than KEYWORD_STRING, this if handles that
                if (!assignee.equals(assignment) && assignee != Token.TokenType.KEYWORD_AUTO)
                    throw new TypeCheckerException("TypeCheck Error: Expected " +
                            assignee + " got " + assignment);
            }
            varType = assignee;
            assType = assignment;
        } else {
            throw new TypeCheckerException("unreachable");
        }

        //working out auto type
        String varName;
        if (input instanceof PVariableDeclaration) {
            varName = ((PVariableDeclaration) input).identifier.getTokenString();
        } else if (input instanceof PVariableAssignment) {
            varName = ((PVariableAssignment) input).identifier.getTokenString();
        } else {
            throw new TypeCheckerException("unreachable");
        }

        if (varType == Token.TokenType.KEYWORD_AUTO && assType != Token.TokenType.KEYWORD_AUTO) { //if left is auto
            System.out.println("AUTO var type detected!");
            //change auto type
            AutoTicket auto = new AutoTicket();
            if (isVD) { //if vardec
                auto.isDeclaration = true;
            } else { //else, varass
                auto.isDeclaration = false;
            }
            auto.ClassName = ClassString;
            auto.ClassNumb = ClassNumber;
            auto.ClassDecNumb = ClassDeclarationNumber;
            auto.inParentClass = true; //unneccessary
            auto.inMethod = true; //probably unneccessary
            auto.MethodName = MethodString;
            auto.MethodDecNum = MethodDeclarationNumber;
            auto.isParam = false; //no
            auto.ParamNum = -1;
            auto.isFunctReturn = false; //no
            auto.TargetVarName = varName;
            auto.NewType = assType;
            AutoHandler.add(auto);
            System.err.println("AUTOTICKET GENERATED"); //will have type

        } else if (assType == Token.TokenType.KEYWORD_AUTO && varType != Token.TokenType.KEYWORD_AUTO) { //right side is auto
            //only certain things could be auto, but let handler deal with it
            System.out.println("Assignment Var has AUTO type!");
            //change auto type of assignment var

            AutoTicket auto = new AutoTicket();
            auto.isDeclaration = false; //right side is not declared
            auto.ClassName = ClassString;
            auto.ClassNumb = ClassNumber;
            auto.ClassDecNumb = ClassDeclarationNumber;
            auto.inParentClass = true; //unneccessary
            auto.inMethod = true; //probably unneccessary
            auto.MethodName = MethodString;
            auto.MethodDecNum = MethodDeclarationNumber;
            auto.isParam = false; //no
            auto.ParamNum = -1;
            auto.isFunctReturn = false; //no
            auto.TargetVarName = varName;
            auto.NewType = assType;
            AutoHandler.add(auto);
            System.err.println("AUTOTICKET GENERATED"); //will have type

        } else if (assType == varType && assType == Token.TokenType.KEYWORD_AUTO) { //both auto
            throw new TypeCheckerException("TypeCheck Error: AUTO Var cannot be assigned to another AUTO Var");
        } else if (assType == varType) { //both equal and not auto
            System.out.println("Good Variable Assignment");
        } else {
            throw new TypeCheckerException("TypeCheck Error: Type mismatch (variable type: " + varType + ", assignment type: " + assType + ")");
        }
    }

    public HashMap<String, VarStor> VariableDeclarationTypecheck(Storage map, PVariableDeclaration input, boolean assignmentAllowed, boolean autoAllowed) throws TypeCheckerException { //take in map of all vars declared in scope, and the declaration stmt

        HashMap<String, VarStor> mapNEW = new HashMap<>(); //used to hold new vars
        AccessModifierTypecheck(input.accessModifier, false); //check if the access modifier is valid or not
        if (input.accessModifier != null) { //this if/else could be removed, mostly for visual output
            System.out.println("Declaration Access Modifier Type: " + input.accessModifier.getType() + " " + input.accessModifier.getTokenString());
        } else {
            System.out.println("Declaration Access Modifier Type: NONE");
        }

        //check if type of var is valid, primitive types tokens: KEYWORD_INT,KEYWORD_DOUBLE,KEYWORD_CHAR,KEYWORD_BOOLEAN,KEYWORD_STRING
        if (input.variableType.getType() == Token.TokenType.KEYWORD_INT || input.variableType.getType() == Token.TokenType.KEYWORD_DOUBLE || input.variableType.getType() == Token.TokenType.KEYWORD_CHAR || input.variableType.getType() == Token.TokenType.KEYWORD_BOOLEAN || input.variableType.getType() == Token.TokenType.KEYWORD_STRING) {
            System.out.println("Primitive Type");
            System.out.println("Declaration Variable Type: " + input.variableType.getType() + " " + input.variableType.getTokenString());
        } else if (input.variableType.getType() == Token.TokenType.KEYWORD_AUTO) { //is type of var AUTO? (token = KEYWORD_AUTO)
            System.out.println("Auto Type");
            //do AUTO stuff later XXXXXXX, maybe a boolean if it is an auto, and before assignment and storing etc, check bool and evaluate the type
            if (autoAllowed) {
                //quick check if we can resolve it now
                Token.TokenType tempType = getExpressionType(input.assignment, map); //check assignment type really quick
                if (tempType == Token.TokenType.KEYWORD_AUTO) { //auto assigned auto type
                    throw new TypeCheckerException("Cannot assign an AUTO type to AUTO Var!");
                } else {
                    if (input.assignment == null) { //if we have NO assignment, generate ticket NOW
                        AutoTicket auto = new AutoTicket();
                        auto.isDeclaration = true;
                        auto.ClassName = ClassString;
                        auto.ClassNumb = ClassNumber;
                        auto.ClassDecNumb = ClassDeclarationNumber;
                        auto.inParentClass = true; //idk
                        auto.inMethod = assignmentAllowed; //in method if assignment is allowed
                        auto.MethodName = MethodString;
                        auto.MethodDecNum = MethodDeclarationNumber;
                        auto.isParam = false; //not in this case
                        auto.ParamNum = -1;
                        auto.isFunctReturn = false; //not allowed
                        auto.TargetVarName = input.identifier.getTokenString();
                        auto.NewType = tempType;
                        AutoHandler.add(auto);
                        System.err.println("AUTOTICKET GENERATED"); //this would not have the type yet, if it was declared in the class
                    }
                    //else, wait to generate autoticket until we have the declaration
                }
            } else { //auto not allowed
                throw new TypeCheckerException("Auto type not allowed here!");
            }
            System.out.println("Declaration Variable Type: " + input.variableType.getType() + " " + input.variableType.getTokenString());
        } else if (input.variableType.getType() == Token.TokenType.IDENTIFIER) { //is the type of the var a Class? (token = IDENTIFIER)
            System.out.println("Variable Declared of a Class");
            if (ClassListAll.containsKey(input.variableType.getTokenString())) { //check all class list for name
                System.out.println("Class Found");
                System.out.println("Declaration Variable Type: " + input.variableType.getType() + " " + input.variableType.getTokenString());
            } else { //class not declared yet
                throw new TypeCheckerException("Variable Declaration Error: Class of Variable Type not defined");
            }
        } else { //if all else fails, invalid type
            throw new TypeCheckerException("Variable Declaration Error: Variable Type unrecognized");
        }

        if (map.extendsClass != null) { //yes extends
            System.out.println("extends 269");
            if (map.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Variable Declaration Error: Variable with same name already defined in scope: \n Inside class as a Variable");
            } else if (map.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Variable Declaration Error: Variable with same name already defined in scope: \n Inside class as a Method");
            } else if (map.extendsClass.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Variable Declaration Error: Variable with same name already defined in scope: \n Inside Parent class as a Variable");
            } else if (map.extendsClass.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Variable Declaration Error: Variable with same name already defined in scope: \n Inside Parent class as a Method");
            } else { //variable is okay
                VarStor tempVS = new VarStor(input.variableType, input.accessModifier); //create a new VarStor obj with the variable's data
                mapNEW.put(input.identifier.getTokenString(), tempVS); //add variable to new list of vars
                System.out.println("Declaration Identifier Type: " + input.identifier.getType() + " " + input.identifier.getTokenString());
            }
        } else { //no extends
            System.out.println("no extends 284");
            if (map.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Variable Declaration Error: Variable with same name already defined in scope: \n Inside class as a Variable");
            } else if (map.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Variable Declaration Error: Variable with same name already defined in scope: \n Inside class as a Method");
            } else { //variable is okay
                VarStor tempVS = new VarStor(input.variableType, input.accessModifier); //create a new VarStor obj with the variable's data
                mapNEW.put(input.identifier.getTokenString(), tempVS); //add variable to new list of vars
                System.out.println("Declaration Identifier Type: " + input.identifier.getType() + " " + input.identifier.getTokenString());
            }
        }

        if (input.assignment != null) { //if there is an assignment
            System.out.println("Assignment is present for Var");
            if (assignmentAllowed) {
                System.out.println("Assignment is allowed to be here");
                CheckVarAss(input, map); //check variable declaration, is it valid?
            } else {
                System.err.println("No Assignment is allowed here!");
                throw new TypeCheckerException("Variable Declaration Error: Variable given an assignment where it is not allowed");
            }
        } else {
            System.out.println("No Assignment is present for Var");
        }

        //variable assignment handled in assignmentAllowed stuff above if stmt

        return mapNEW; //return the updated map of all defined variables in current scope
    }

    public void MethodDeclarationTypecheck(Storage map, PStatementFunctionDeclaration input) throws TypeCheckerException { //input: a class's Storage object & function declaration

        AccessModifierTypecheck(input.accessModifier, false); //check if the access modifier is valid or not
        if (input.accessModifier != null) { //this if/else could be removed, mostly for visual output
            System.out.println("Declaration Access Modifier Type: " + input.accessModifier.getType() + " " + input.accessModifier.getTokenString());
        } else {
            System.out.println("Declaration Access Modifier Type: NONE");
        }

        //check if return type is valid, primitive types tokens: KEYWORD_INT,KEYWORD_DOUBLE,KEYWORD_CHAR,KEYWORD_BOOLEAN,KEYWORD_STRING
        if (input.returnType.getType() == Token.TokenType.KEYWORD_INT || input.returnType.getType() == Token.TokenType.KEYWORD_DOUBLE || input.returnType.getType() == Token.TokenType.KEYWORD_CHAR || input.returnType.getType() == Token.TokenType.KEYWORD_BOOLEAN || input.returnType.getType() == Token.TokenType.KEYWORD_STRING) {
            System.out.println("Primitive Return Type");
            System.out.println("Method Declaration Return Type: " + input.returnType.getType() + " " + input.returnType.getTokenString());
        } else if (input.returnType.getType() == Token.TokenType.KEYWORD_AUTO) { //is return type AUTO? (token = KEYWORD_AUTO)
            System.out.println("Auto Type");
            //do AUTO stuff later XXXXXXX, AUTO return type allowed? --> no
            throw new TypeCheckerException("Cannot declare method to have an AUTO return type!");
            //System.out.println("Method Declaration Return Type: " + input.returnType.getType() + " " + input.returnType.getTokenString());
        } else if (input.returnType.getType() == Token.TokenType.KEYWORD_VOID) {
            System.out.println("Void Type");
            System.out.println("Method Declaration Return Type: " + input.returnType.getType() + " " + input.returnType.getTokenString());
        } else if (input.returnType.getType() == Token.TokenType.IDENTIFIER) { //is return type a Class? (token = IDENTIFIER)
            System.out.println("Method Returns Type of Class");
            if (ClassListAll.containsKey(input.returnType.getTokenString())) { //check all class list for name
                System.out.println("Class Found");
                System.out.println("Method Returns Type of Class: " + input.returnType.getType() + " " + input.returnType.getTokenString());
            } else { //class not declared yet
                throw new TypeCheckerException("Method Declaration Error: Class of Return Type not defined");
            }
        } else { //if all else fails, invalid type
            throw new TypeCheckerException("Method Declaration Error: Return Type unrecognized");
        }

        Storage GOODMETHOD = new Storage();
        FunctStor tempFS = new FunctStor(); //store all function stuff
        //check if method name already exists in scope (given map obj), check both var names and method names
        if (map.extendsClass != null) { //yes extends
            System.out.println("extends");
            if (map.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Method Declaration Error: Var with same name already defined in scope: \n Inside class as a Variable");
            } else if (map.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Method Declaration Error: Var with same name already defined in scope: \n Inside class as a Method");
            } else if (map.extendsClass.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Method Declaration Error: Var with same name already defined in scope: \n Inside Parent class as a Variable");
            } else if (map.extendsClass.MethodNames.containsKey(input.identifier.getTokenString())) { //else if method name is already in parent
                //need to check if the declared method is the same as parent
                FunctStor ParentMethod = map.extendsClass.MethodNames.get(input.identifier.getTokenString());
                if (ParentMethod.AccessModifier.getType() == input.accessModifier.getType()) { //access modifiers are the same
                    if (ParentMethod.Parameters.size() == input.variableDeclarations.size()) { //param amount same
                        if (ParentMethod.ReturnType.getType() == input.returnType.getType()) { //return types same
                            System.err.println("Child allowed to re-define this method from Parent"); //good
                            ChildOverride cor = new ChildOverride();
                            cor.ChildClassName = ClassString;
                            cor.MethodName = input.identifier.getTokenString();
                            cor.ChildClassDecNumb = ClassNumber;
                            cor.ChildClassDecNumb = ClassDeclarationNumber;
                            ChildMethodOverrideList.add(cor);
                            System.err.println("Added ChildOverride to list");
                        } else {
                            throw new TypeCheckerException("Method Declaration Error: Cannot redefine Parent Method \"" + input.identifier.getTokenString() + "\"" +
                                    " since they do not have the same Return Type");
                        }
                    } else {
                        throw new TypeCheckerException("Method Declaration Error: Cannot redefine Parent Method \"" + input.identifier.getTokenString() + "\"" +
                                " since they do not have the same Parameter Amount");
                    }
                } else {
                    throw new TypeCheckerException("Method Declaration Error: Cannot redefine Parent Method \"" + input.identifier.getTokenString() + "\"" +
                            " since they do not have the same Access Modifier");
                }
            } else { //if not, add it as a new var
                System.out.println("Method Declaration Identifier Type: " + input.identifier.getType() + " " + input.identifier.getTokenString());
                tempFS.AccessModifier = input.accessModifier;
                tempFS.ReturnType = input.returnType;
                tempFS.Classname = ClassString;
                //two more things to add: params & stmts, to FunctStor at this point
                GOODMETHOD = ClassListAll.get(ClassString); //pull out class obj
                GOODMETHOD.MethodNames.put(input.identifier.getTokenString(), tempFS);
                ClassListAll.put(ClassString, GOODMETHOD); //replace class
            }
        } else { //no extends
            System.out.println("no extends");
            if (map.VariableNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Method Declaration Error: Var with same name already defined in scope: \n Inside class as a Variable");
            } else if (map.MethodNames.containsKey(input.identifier.getTokenString())) {
                throw new TypeCheckerException("Method Declaration Error: Var with same name already defined in scope: \n Inside class as a Method");
            } else { //if not, add it as a new var
                System.out.println("Method Declaration Identifier Type: " + input.identifier.getType() + " " + input.identifier.getTokenString());
                tempFS.AccessModifier = input.accessModifier;
                tempFS.ReturnType = input.returnType;
                tempFS.Classname = ClassString;
                //two more things to add: params & stmts, to FunctStor at this point
                //map.MethodNames.put(input.identifier.getTokenString(), new FunctStor()); //add method to method names list with Blank FunctStor object for now
                GOODMETHOD = ClassListAll.get(ClassString); //pull out class obj
                GOODMETHOD.MethodNames.put(input.identifier.getTokenString(), tempFS);
                ClassListAll.put(ClassString, GOODMETHOD); //replace class
            }
        }

        GOODMETHOD = ClassListAll.get(ClassString); //pull out class obj
        GOODMETHOD.MethodNames.put(input.identifier.getTokenString(), tempFS);
        ClassListAll.put(ClassString, GOODMETHOD); //replace class

        MethodString = input.identifier.getTokenString(); //used for auto

        //deal with params
        if (input.variableDeclarations != null) { //if method has params
            System.out.println("Method Parameters:");
            for (int i = 0; i < input.variableDeclarations.size(); i++) { //for all parameters in method
                HashMap<String, VarStor> output; //declare var for return of VDT()
                output = VariableDeclarationTypecheck(GOODMETHOD, input.variableDeclarations.get(i), false, false); //send GOODMETHOD to ensure parameters know scope

                String tempVarName = input.variableDeclarations.get(i).identifier.getTokenString(); //name
                System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!" + tempVarName); //debug stuff
                VarStor tempWW = output.get(tempVarName);

                if (tempFS.Parameters == null) { //probably useless now, was because FunctStor didnt declare the hashmap a new hashmap
                    tempFS.Parameters = new ArrayList<VarStor>();
                }

                //***IMPORTANT***: this was the fix to the problem of params not being detected when colliding with themselves, besides that, they are being handled perfectly, this fixes that bug
                if (tempFS.VariableNames.containsKey(tempVarName)) { //if already declared
                    throw new TypeCheckerException("Duplicate Parameter name in Function " + MethodString + " in class " + ClassString);
                }

                tempFS.Parameters.add(i, tempWW); //add param to FunctStor object, ordered

                if (tempFS.VariableNames == null) {
                    tempFS.VariableNames = new HashMap<>();
                }

                tempFS.VariableNames.put(tempVarName, tempWW);

                ///NEW FIX --- THIS SEEMS TO DO NOTHING AT ALL
                GOODMETHOD.MethodNames.put(MethodString, tempFS);
                //System.out.println(tempFS.);
                ClassListAll.put(ClassString, GOODMETHOD);
                //END NEW FIX
            } //end for params

        } else { //no method params
            System.out.println("Method has no Parameters");
        }

        if (input.statementList != null) {
            System.out.println("Method Declaration Body: Statement List");
            Storage statementBlockStorage = GOODMETHOD.Copy();
            statementBlockStorage.VariableNames.putAll(tempFS.VariableNames);//place parameter variables into method scope
            GOODMETHOD = ClassListAll.get(ClassString); //pull out class obj //-----------> updates globalscope with local vars
            tempFS.VariableNames.putAll(tempFS.VariableNames); //------------------------->
            GOODMETHOD.MethodNames.put(input.identifier.getTokenString(), tempFS); //----->
            ClassListAll.put(ClassString, GOODMETHOD); //replace class //----------------->
            for (int k = 0; k < input.statementList.size(); k++) { //for all body stmts (PStmt)
                MethodDeclarationNumber = k;
                PStatement tempStmtExp = input.statementList.get(k);

                String varName = "";
                if (tempStmtExp instanceof PVariableDeclaration) { //surface level vardec inside method
                    globalAutoOff = true; //allow auto
                    varName = ((PVariableDeclaration) tempStmtExp).identifier.getTokenString();
                }

                HashMap<String, VarStor> returnedVDT = TEMP_unused_code_for_PStmts__PSTATEMENT(tempStmtExp, statementBlockStorage); //return vdt output, in the case of Variable Declarations
                if (returnedVDT != null) { //if we added a var (VarDec)
                    statementBlockStorage.VariableNames.put(varName, returnedVDT.get(varName));
                    GOODMETHOD = ClassListAll.get(ClassString); //pull out class obj //----------> updates globalscope with local vars
                    tempFS.VariableNames.put(varName, returnedVDT.get(varName)); //-------------->
                    GOODMETHOD.MethodNames.put(input.identifier.getTokenString(), tempFS); //---->
                    ClassListAll.put(ClassString, GOODMETHOD); //replace class //---------------->
                }

                globalAutoOff = false; //turn off after
                AutoVarChecker(); //check auto here

            } //end for all body stmts
        } else {
            System.out.println("Method Body has no statements");
        }

        if (tempFS.VariableNames == null) {
            tempFS.VariableNames = new HashMap<>();
        }

        GOODMETHOD = ClassListAll.get(ClassString); //pull out class obj
        GOODMETHOD.MethodNames.put(input.identifier.getTokenString(), tempFS);
        ClassListAll.put(ClassString, GOODMETHOD); //replace class

        MethodString = ""; //reset val
        MethodDeclarationNumber = -1; //reset val

        AutoVarChecker(); //check auto here
        //not returning a map anymore, just updating as we go

    }

    public void AutoVarChecker() throws TypeCheckerException {
        //check auto stuff here, after every method stmt
        if (AutoHandler.size() != 0) { //if autohandler is not empty
            HashMap<String, Token.TokenType> orderedlistauto = new HashMap<String, Token.TokenType>(); //will hold name,newtype of things to change
            String[] nameslist = new String[AutoHandler.size()]; //new string array size of current autohandler, will fill with just names at first
            for (int i = 0; i < AutoHandler.size(); i++) { //for all autotickets
                AutoTicket tempauto = AutoHandler.get(i); //grab one of the tickets
                //create array of string (name), check for matches
                nameslist[i] = tempauto.TargetVarName; //load string array with the name
                if (tempauto.NewType != null) { //if the auto type has been RESOLVED (not detected, but determined the new type)
                    //System.err.println("type " + tempauto.NewType + " not equal to null, proceed!");
                    orderedlistauto.put(tempauto.TargetVarName, tempauto.NewType); //put this resolution inside the orderlistauto
                } else { //else, auto has just been detected, not resolved
                    //System.err.println("Found no resolutions for Auto");
                }
            } //end for all autotickets
            nameslist = new HashSet<String>(Arrays.asList(nameslist)).toArray(new String[0]); //removes all duplicates in string array of names
            //System.err.println("After AutoHandler completed...checking for matches");
            System.err.println("size of orderlistauto: " + orderedlistauto.size());
            HashMap<String, Token.TokenType> TEMPLIST = orderedlistauto;
            System.err.println(orderedlistauto.toString()); //!!_!!!_!! only contains resolutions, could be nothing!
            for (int j = 0; j < nameslist.length; j++) { //for all names to resolve
                if (TEMPLIST.containsKey(nameslist[j])) { //if the resolution bank
                    Token.TokenType type = TEMPLIST.get(nameslist[j]); //pull type from hashmap by name
                    //orderedlistauto.put(nameslist[j], type); //ADD IT BACK INTO THE HASHMAP, HASHMAPS ARE WTF
                    System.err.println("()()()()()" + nameslist[j] + " must be type " + type); //-------->>>>here! (we have noooo//oppee the types to resolve)
                    ArrayList<AutoTicket> fix = new ArrayList<>();///////////NEWTYPE
                    for (int y = 0; y < AutoHandler.size(); y++) { //check all autotickets again against a single name resolution, remove all duplicate instances
                        AutoTicket tempauto;
                        if (AutoHandler.get(y).TargetVarName.equals(nameslist[j])) { //if one of the resolved vars is here
                            tempauto = AutoHandler.get(y); //pull out temp
                            AutoHandler.remove(y); //remove it from list
                            if (tempauto.isDeclaration) { //if this ticket is the declaration ticket
                                //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^" + type);
                                tempauto.NewType = type; //update the declaration ticket with type
                                fix.add(tempauto); //store this autoticket (the declaration one with the updated type)
                            } //otherwise, let it go, this will not remove unresolved names
                        } //else if unresolved name is here, leave it
                    } //end for resolutions
                    System.err.println("AUTOHANDLER SIZE IS: " + AutoHandler.size());
                    //System.err.println("New FIXAUTO array:");
                    if (fix.size() > 0) { //if not empty
                        for (int i = 0; i < fix.size(); i++) {
                            AutoTicket tempauto = fix.get(i); //grab one of the tickets
                            System.err.println("####### AutoTicket: Found var named " + tempauto.TargetVarName + " in class " + tempauto.ClassName + " and method " + tempauto.MethodName + "," +
                                    "\nType should be: " + tempauto.NewType + "\n" +
                                    "Class,ClassDec,Method#s: " + tempauto.ClassNumb + "," + tempauto.ClassDecNumb + "," + tempauto.MethodDecNum); //print it
                        }
                        ResolvedAutoTickets.addAll(fix);
                    }
                } else {
                    System.err.println("Cannot resolve name yet : " + nameslist[j]);
                }
            }
        } //end if autohandler is not empty
        //at the end of a stmt, try to resolve auto
        if (ResolvedAutoTickets.size() != 0) { //if we have a resolved auto ticket
            for (int i = 0; i < ResolvedAutoTickets.size(); i++) { //for all resolved auto tickets
                System.err.println("RESOLVED AUTO TICKET FOUND!");
                AutoTicket autoTT = ResolvedAutoTickets.get(i);
                int classnum11 = autoTT.ClassNumb;
                int classdec11 = autoTT.ClassDecNumb;
                int method11 = autoTT.MethodDecNum;
                String varname11 = autoTT.TargetVarName;
                Token.TokenType type11 = autoTT.NewType;
                Storage classDuplicateTemp = ClassListAll.get(ClassNamesListAll.get(classnum11)); //PULLING OUT current class
                //resolve auto keyword in scope----------!
                if (autoTT.MethodName.equals("")) { //if empty method, it is in the class declarations
                    if (classDuplicateTemp.VariableNames.containsKey(varname11)) { //if the var is there
                        VarStor tempReplaceType = classDuplicateTemp.VariableNames.get(varname11);
                        String tokenstringTemp = Token.VarTypeMap.get(type11); //use the stored type's token to extract tokentype, then use to pull the proper string from token.java new hashmap
                        tempReplaceType.Type = new Token(tokenstringTemp); //give new token with proper type to var in storage
                        classDuplicateTemp.VariableNames.put(varname11, tempReplaceType); //replace varstor in local
                        ClassListAll.put(ClassNamesListAll.get(classnum11), classDuplicateTemp); //REPLACE STORAGE WITH UPDATED CLASS
                        System.err.println("AUTO VAR UPDATED IN CLASS " + autoTT.ClassName + " STORAGE: {name: " + varname11 + "; new type: " + type11 + "}");
                    } else {
                        throw new TypeCheckerException("How did this happen? -- Variable DNE"); //dont think this is possible
                    }
                } else { //in method
                    if (classDuplicateTemp.MethodNames.containsKey(autoTT.MethodName)) {
                        FunctStor tempFunctS1 = classDuplicateTemp.MethodNames.get(autoTT.MethodName); //grab method info
                        VarStor tempReplaceType = tempFunctS1.VariableNames.get(varname11);
                        String tempTokenString = Token.VarTypeMap.get(type11); //grab proper string from type
                        tempReplaceType.Type = new Token(tempTokenString); //update type in local varstor
                        tempFunctS1.VariableNames.put(varname11, tempReplaceType); //update var in functstor local
                        classDuplicateTemp.MethodNames.put(autoTT.MethodName, tempFunctS1); //replace functstor in local class stor
                        ClassListAll.put(ClassNamesListAll.get(classnum11), classDuplicateTemp); //REPLACE STORAGE WITH UPDATED FUNCTSTOR
                        System.err.println("AUTO VAR UPDATED IN CLASS " + autoTT.ClassName + " & METHOD " + autoTT.MethodName + " STORAGE: {name: " + varname11 + "; new type: " + type11 + "}");
                    } else {
                        throw new TypeCheckerException("How did this happen? -- Method DNE"); //dont think this is possible
                    }
                }
                //resolve auto keyword in duplicate pprogram----------!
                if (autoTT.MethodName.equals("")) { //if method is empty, look in class declarations (i know this is a duplicate if structure, but doing it to show difference between storage and pprog)
                    PClassDeclaration classPPRog = DuplicateProgram.classDeclarationList.get(classnum11);
                    PDeclaration tempDecl = classPPRog.declarationList.get(classdec11);
                    if (tempDecl instanceof PVariableDeclaration) { //it should be
                        PVariableDeclaration varDecTBD = (PVariableDeclaration) tempDecl;
                        if (varDecTBD.variableType.getType() == Token.TokenType.KEYWORD_AUTO) { //if type is auto, good
                            String tempTokenString = Token.VarTypeMap.get(type11); //grab proper string from type
                            varDecTBD.variableType = new Token(tempTokenString); //update type in local PVariableDeclaration
                            classPPRog.declarationList.set(classdec11, varDecTBD); //replace in local class dec
                            DuplicateProgram.classDeclarationList.set(classnum11, classPPRog); //REPLACE VARDEC IN PPROGRAM
                            System.err.println("AUTO VAR UPDATED IN [PPROGRAM] CLASS " + autoTT.ClassName + " PPROGRAM OBJECT: {name: " + varname11 + "; new type: " + type11 + "}");
                        } else { //auto not detected, error
                            //throw new TypeCheckerException("AUTO Type Error: Variable Replacement Unsuccessful. Did not find AUTO Type in" +
                            //        "Class pos " + classnum11 + " & Class Declaration pos " + classdec11);
                            //theres something where it keeps replacing, but should be fine
                        }
                    } else {
                        throw new TypeCheckerException("AUTO Type Error: Variable Replacement Unsuccessful. Did not find PVariableDeclaration instance in" +
                                "Class pos " + classnum11 + " & Class Declaration pos " + classdec11); //dont think this is possible
                    }
                } else { //inside a method
                    PClassDeclaration classPPRog = DuplicateProgram.classDeclarationList.get(classnum11);
                    PDeclaration tempDecl = classPPRog.declarationList.get(classdec11);
                    if (tempDecl instanceof PStatementFunctionDeclaration) { //found method dec
                        PStatementFunctionDeclaration methodDecTBD = (PStatementFunctionDeclaration) tempDecl;
                        PStatement tempSTMTM = methodDecTBD.statementList.get(method11);
                        if (tempSTMTM instanceof PVariableDeclaration) { //find var dec in method decs
                            PVariableDeclaration varDecTBD = (PVariableDeclaration) tempSTMTM;
                            if (varDecTBD.variableType.getType() == Token.TokenType.KEYWORD_AUTO) { //if type is auto, good
                                String tempTokenString = Token.VarTypeMap.get(type11); //grab proper string from type
                                varDecTBD.variableType = new Token(tempTokenString); //update type in local PVariableDeclaration
                                methodDecTBD.statementList.set(method11, varDecTBD); //set updated vardec in local method
                                classPPRog.declarationList.set(classdec11, methodDecTBD); //replace method in local class dec
                                DuplicateProgram.classDeclarationList.set(classnum11, classPPRog); //REPLACE METHOD IN PPROGRAM
                                System.err.println("AUTO VAR UPDATED IN [PPROGRAM] CLASS " + autoTT.ClassName + " & METHOD " + autoTT.MethodName + " PPROGRAM OBJECT: {name: " + varname11 + "; new type: " + type11 + "}");
                            } else { //auto not detected, error
                                //throw new TypeCheckerException("AUTO Type Error: Variable Replacement Unsuccessful. Did not find AUTO Type in" +
                                //        "Class pos " + classnum11 + " Class Name: " + autoTT.ClassName + ", Class Declaration pos " + classdec11 + " " +
                                //        "Method name: " + autoTT.MethodName + ", Method Dec pos " + autoTT.MethodDecNum);
                                //theres something where it keeps replacing, but should be fine
                            }
                        } else { //not found vardec
                            throw new TypeCheckerException("AUTO Type Error: Variable Replacement Unsuccessful. Did not find PVariableDeclaration instance in" +
                                    "Class pos " + classnum11 + " Class Name: " + autoTT.ClassName + ", Class Declaration pos " + classdec11 + " " +
                                    "Method name: " + autoTT.MethodName + ", Method Dec pos " + autoTT.MethodDecNum);
                        }
                    } else { //didnt find method dec here
                        throw new TypeCheckerException("AUTO Type Error: Variable Replacement Unsuccessful. Did not find PStatementFunctionDeclaration instance in" +
                                "Class pos " + classnum11 + " & Class Declaration pos " + classdec11); //dont think this is possible
                    }
                } //end inside method
            } //end for resolved tickets
        } //end resolved ticket start
    }

    public String ClassTypecheck(PClassDeclaration input) throws TypeCheckerException { //typecheck the class declaration

        String extendsHandler = null; //no
        AccessModifierTypecheck(input.accessModifier, true); //make sure the access modifier is valid
        System.out.println("Class Access Modifier Type: " + input.accessModifier.getType() + " " + input.accessModifier.getTokenString());

        System.out.println("Class Identifier (Name): " + input.identifier.getType() + " " + input.identifier.getTokenString());
        ClassString = input.identifier.getTokenString(); //assign current class name
        ClassNamesListAll.add(ClassNumber, ClassString); //add the name of the class at class number in arraylist (note, both vars are already declared at this point)

        System.out.print("Class Extends a Class?: "); //find out if this current class extends another class (based on its declaration)
        if (input.extendsIdentifier != null) { //the class does extend another
            //Check if the class extends itself.
            if (input.extendsIdentifier.getTokenString().equals(ClassString)) {
                throw new TypeCheckerException("Class Error: Class cannot extend itself.");
            }
            if (ClassListAll.containsKey(input.extendsIdentifier.getTokenString())) { //check if this class (that the working class is supposed to extend) is known yet/exists
                System.out.println("yes " + input.extendsIdentifier.getType() + " " + input.extendsIdentifier.getTokenString()); //it does exist
                extendsHandler = input.extendsIdentifier.getTokenString(); //yes, give name of class
            } else { //class extends class that does not exist (yet)
                throw new TypeCheckerException("Class Error: Class Extends Class that does not exist");
            }
        } else { //the class does not extend another
            System.out.println("no");
        }
        return extendsHandler;

    }

    public void AccessModifierTypecheck(Token input, boolean isClass) throws TypeCheckerException {
        //access modifier required in class, but will fail at parser level if not there
        if (isClass) {
            if (input.getType() == Token.TokenType.KEYWORD_PUBLIC || input.getType() == Token.TokenType.KEYWORD_PRIVATE || input.getType() == Token.TokenType.KEYWORD_PROTECTED) {
                //good
            } else {
                throw new TypeCheckerException("Class Typecheck Error: Class Access Modifier Invalid");
            }
        } else { //not class, access modifier may be blank
            if (input == null || input.getType() == Token.TokenType.KEYWORD_PUBLIC || input.getType() == Token.TokenType.KEYWORD_PRIVATE || input.getType() == Token.TokenType.KEYWORD_PROTECTED) {
                //good
            } else {
                throw new TypeCheckerException("Declaration Error: Access Modifier Invalid");
            }
        }
    }

    //now actually used!
    public HashMap<String, VarStor> TEMP_unused_code_for_PStmts__PSTATEMENT(PStatement tempStmtExp, Storage currentScope) throws TypeCheckerException {
        if (tempStmtExp instanceof PExpressionIdentifierReference) { //done
            System.out.println("Instance of PExpressionIdentifierReference");
            PExpressionIdentifierReference tempExp = (PExpressionIdentifierReference) tempStmtExp;
            //1 token 1 pexpr
            System.err.println("-------LITERALLY NEVER USED, IGNORE----------");
            throw new TypeCheckerException("old unused stuff, not reachable");
        }
        if (tempStmtExp instanceof PIdentifierReference) { //done
            System.out.println("Instance of PIdentifierReference");
            PIdentifierReference tempExp = (PIdentifierReference) tempStmtExp;
            //1 token 1 pstmt
            getExpressionType((PIdentifierReference)tempStmtExp, currentScope);//technically can also be expression so it can be handled as an expression
            return null; //no need to update storage
        }
        if (tempStmtExp instanceof PStatementBreak) { //done
            System.out.println("Instance of PStatementBreak");
            PStatementBreak tempExp = (PStatementBreak) tempStmtExp;
            //1 token
            //typechecker does nothing here, its a codegen thing only
            return null; //no need to update storage
        }
        if (tempStmtExp instanceof PStatementForStatement) { //done
            System.out.println("Instance of PStatementForStatement");
            PStatementForStatement tempExp = (PStatementForStatement) tempStmtExp;
            //1 PStatement ,  1 PExpression,  1 PStatement , 1 ArrayList<PStatement>
            typeCheckForStatement(tempExp, currentScope);
            return null; //no need to update storage
        }
        if (tempStmtExp instanceof PStatementFunctionCall) { //done
            System.out.println("Instance of PStatementFunctionCall");
            PStatementFunctionCall tempExp = (PStatementFunctionCall) tempStmtExp;
            //1 Token , 1 ArrayList<PExpression>
            getExpressionType(tempExp, currentScope);
            return null; //no need to update storage
        }
        if (tempStmtExp instanceof PStatementFunctionDeclaration) { //done
            System.out.println("Instance of PStatementFunctionDeclaration");
            PStatementFunctionDeclaration tempExp = (PStatementFunctionDeclaration) tempStmtExp;
            //handled above
            //should not be able to declare a function within a function, since we only reach here within a function or within some loop (if/for/while), not possible, throw error
            throw new TypeCheckerException("Cannot define function within Function/Loop Statement!");
        }
        if (tempStmtExp instanceof PStatementIfStatement) { //done
            System.out.println("Instance of PStatementIfStatement");
            PStatementIfStatement tempExp = (PStatementIfStatement) tempStmtExp;
            //1 PExpression , 1 ArrayList<PStatement> , 1 ArrayList<PStatement>

            typeCheckIfStatement(tempExp, currentScope);
            return null; //no need to update storage
        }
        if (tempStmtExp instanceof PStatementPrintln) { //done
            System.out.println("Instance of PStatementPrintln");
            PStatementPrintln tempExp = (PStatementPrintln) tempStmtExp;
            //1 token
            Token.TokenType tempToken = getExpressionType(tempExp.printExpression, currentScope); //get type of expression in print stmt
            if (tempToken == Token.TokenType.KEYWORD_INT) {
                //good
            } else if (tempToken == Token.TokenType.KEYWORD_STRING) { //note this also includes if it were a variable that was an int/string
                //good
            } else {
                throw new TypeCheckerException("Cannot print Type " + tempToken);
            }
            return null; //no need to update storage
        }
        if (tempStmtExp instanceof PStatementReturn) { //done
            System.out.println("Instance of PStatementReturn");
            PStatementReturn tempExp = (PStatementReturn) tempStmtExp;
            //1 pexpr
            Token.TokenType tempToken = getExpressionType(tempExp.returnExpression, currentScope); //get type of expression in return stmt
            //pull out current method return type
            Storage copyStor = currentScope.Copy();
            FunctStor tempFucnt = copyStor.MethodNames.get(MethodString);
            Token.TokenType methodReturnType = tempFucnt.ReturnType.getType();

            if (tempToken == methodReturnType) {
                //good
            } else { //types dont match
                throw new TypeCheckerException("Method Return Error: Method Return Type = " + methodReturnType + ", cannot return type " + tempToken);
            }
            //auto should already be fixed by this point
            return null; //no need to update storage
        }
        if (tempStmtExp instanceof PStatementWhileStatement) { //done
            System.out.println("Instance of PStatementWhileStatement");
            PStatementWhileStatement tempExp = (PStatementWhileStatement) tempStmtExp;
            //1 PExpression , 1 ArrayList<PStatement>
            typeCheckWhileStatement(tempExp, currentScope);
            return null; //no need to update storage
        }
        if (tempStmtExp instanceof PVariableAssignment) { //done
            System.out.println("Instance of PVariableAssignment");
            PVariableAssignment tempExp = (PVariableAssignment) tempStmtExp;
            //1 token, 1 pexpr
            CheckVarAss(tempExp, currentScope);
            return null; //no need update storage
        }
        if (tempStmtExp instanceof PVariableDeclaration) { //done
            System.out.println("Instance of PVariableDeclaration");
            PVariableDeclaration tempExp = (PVariableDeclaration) tempStmtExp;
            //already handled
            HashMap<String, VarStor> output; //output of VDT()
            output = VariableDeclarationTypecheck(currentScope, tempExp, true, globalAutoOff); //globalAutoOff should prevent loops from declaring auto types
            //String VarName = tempExp.identifier.getTokenString();
            return output;
        }
        return null; //no need to return anything, since never reached, but whatever
    }

    private void typeCheckWhileStatement(PStatementWhileStatement whileStatement, Storage currentScope) throws TypeCheckerException {
        Storage whileScope = currentScope.Copy();
        HashMap<String, VarStor> blockVariables;//for storing any variables declared in a block
        if (getExpressionType(whileStatement.expression, whileScope) != Token.TokenType.KEYWORD_BOOLEAN)
            throw new TypeCheckerException("While Loop Expression must be of type BOOLEAN");
        for (PStatement statement : whileStatement.statementList) {
            blockVariables = TEMP_unused_code_for_PStmts__PSTATEMENT(statement, whileScope);
            if (blockVariables != null)
                whileScope.VariableNames.putAll(blockVariables);
        }
    }

    private void typeCheckIfStatement(PStatementIfStatement ifStatement, Storage currentScope) throws TypeCheckerException {
        Storage ifScope = currentScope.Copy(); //if statement needs its own scope, anything declared inside stays inside
        Storage elseScope = currentScope.Copy(); //exclusive scope for the else block that wont interfere with anything outside
        HashMap<String, VarStor> blockVariables;//for storing any variables declared in a block
        //check if expression is boolean
        if (getExpressionType(ifStatement.expression, ifScope) != Token.TokenType.KEYWORD_BOOLEAN)
            throw new TypeCheckerException("Expression in IF statement not a Boolean");
        //typecheck elements in if statement
        for (PStatement statement : ifStatement.statementList) {
            blockVariables = TEMP_unused_code_for_PStmts__PSTATEMENT(statement, ifScope);
            if (blockVariables != null)
                ifScope.VariableNames.putAll(blockVariables);
        }
        //typecheck elements in else statement
        blockVariables = null;
        for (PStatement statement : ifStatement.elseStatementList) {
            blockVariables = TEMP_unused_code_for_PStmts__PSTATEMENT(statement, elseScope);
            if (blockVariables != null)
                elseScope.VariableNames.putAll(blockVariables);
        }
    }

    private void typeCheckForStatement(PStatementForStatement forStatement, Storage currentScope) throws TypeCheckerException {
        Storage forScope = currentScope.Copy();//exclusive scope for the For Loop that wont interfere with anything outside
        HashMap<String, VarStor> blockVariables;//for storing any variables declared in a block
        if (!(forStatement.statement1 instanceof PVariableDeclaration))//make sure first statement is a variable declaration
            throw new TypeCheckerException("First Statement in For Loop Must be a variable declaration");
        //remember to add the incrementor declaration to the scope of the for loop
        PVariableDeclaration varDec = (PVariableDeclaration) forStatement.statement1;
        TEMP_unused_code_for_PStmts__PSTATEMENT(forStatement.statement1, forScope); //typecheck variable decleration,....might not be implemented in this function
        VarStor newVariableStore = new VarStor(varDec.variableType, varDec.accessModifier);
        forScope.VariableNames.put(varDec.identifier.getTokenString(), newVariableStore);
        if (getExpressionType(forStatement.expression, forScope) != Token.TokenType.KEYWORD_BOOLEAN) //typecheck continue expression
            throw new TypeCheckerException("For Loop Expression must be of type BOOLEAN");
        if (!(forStatement.statement2 instanceof PVariableAssignment))
            throw new TypeCheckerException("Third statement in for loop must be variable assignement, instead got: " + forStatement.statement2.getClass());
        for (PStatement statement : forStatement.statementList) {
            blockVariables = TEMP_unused_code_for_PStmts__PSTATEMENT(statement, forScope);
            if (blockVariables != null)
                forScope.VariableNames.putAll(blockVariables);
        }
    }

    //idea, recursive methodology also make sure that currentScope is a copy.
    public Token.TokenType getExpressionType(PExpression exp, Storage currentScope) throws TypeCheckerException {
        if (exp instanceof PExpressionAtomNumberLiteral)
            return Token.TokenType.KEYWORD_INT; //Expand here once we have more than just ints
        else if (exp instanceof PExpressionAtomStringLiteral)
            return Token.TokenType.KEYWORD_STRING;
        else if (exp instanceof PExpressionAtomBooleanLiteral)
            return Token.TokenType.KEYWORD_BOOLEAN; //technically not the "boolean" keyword, but lets use this for now
        else if (exp instanceof PExpressionVariable) {
            //oh no, this is getting its own method
            return VariableInScope(((PExpressionVariable) exp).variable.getTokenString(), currentScope);
        }
        else if (exp instanceof PIdentifierReference) {
            PIdentifierReference PIR = (PIdentifierReference) exp;
            return IdentifierReferenceTypeCheckDriver(PIR, currentScope);//moved to own method
        }
        else if (exp instanceof PStatementFunctionCall){
            PStatementFunctionCall functionCall = (PStatementFunctionCall) exp;
            return FunctionCallTypeCheck(functionCall, currentScope);
        }
        else if (exp instanceof PExpressionBinOp) {
            //recursivly do both hands of the expressions
            Token.TokenType lhs = getExpressionType(((PExpressionBinOp) exp).lhs, currentScope);

            Token.TokenType rhs = getExpressionType(((PExpressionBinOp) exp).rhs, currentScope);

            if (lhs != rhs) //The operand types do not match.
            {
                //Allows string & integer concatenation
                if ((lhs == Token.TokenType.KEYWORD_STRING && rhs == Token.TokenType.KEYWORD_INT) ||
                        (lhs == Token.TokenType.KEYWORD_INT && rhs == Token.TokenType.KEYWORD_STRING))
                    return Token.TokenType.KEYWORD_STRING; //concatinating an integer to a string
                else //anything else must fail
                    throw new TypeCheckerException("TypeCheck Error: Expected " +
                            lhs.name() + " got " + rhs.name());
            }

            Token.TokenType output = lhs;//at this point we already determined lhs and rhs are the same type
            //check if the operator is the right type for the expression
            Token.TokenType operator = ((PExpressionBinOp) exp).operatorToken.getType();
            //System.out.println("\t"+rhs.name()+"_"+operator.name()+"_"+lhs.name());
            /********** + *************/
            if (operator == Token.TokenType.SYMBOL_PLUS) //plus operator works w/ ints and strings.
            {

                if ((output != Token.TokenType.KEYWORD_STRING) && (output != Token.TokenType.KEYWORD_INT)) {
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
                }
            }/********** numeric ops *************/
            else if (operator == Token.TokenType.SYMBOL_MINUS || //number operations
                    operator == Token.TokenType.SYMBOL_ASTERISK ||
                    operator == Token.TokenType.SYMBOL_SLASH ||
                    /****** BITWISE OPS ******/
                    operator == Token.TokenType.SYMBOL_SHIFTRIGHT ||
                    operator == Token.TokenType.SYMBOL_SHIFTLEFT ||
                    operator == Token.TokenType.SYMBOL_AMPERSAND ||
                    operator == Token.TokenType.SYMBOL_BAR ||
                    operator == Token.TokenType.SYMBOL_CARET ||
                    operator == Token.TokenType.SYMBOL_TILDE) {
                if (output != Token.TokenType.KEYWORD_INT) /////XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
            }/********* DOUBLE EQUALS *******/
            else if ((operator == Token.TokenType.SYMBOL_DOUBLEEQUALS) || //plus operator works w/ ints and strings.
                    (operator == Token.TokenType.SYMBOL_NOTEQUAL)) {   /*Add more valid operand types for double equals if necessary.*/
                if ((output != Token.TokenType.KEYWORD_INT) &&
                        (output != Token.TokenType.KEYWORD_BOOLEAN)) {
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type: Expected type" +
                            /*Token.TokenType.KEYWORD_INT.name().split("_")[0] + */
                            output.name());
                }
                output = Token.TokenType.KEYWORD_BOOLEAN;
            }/********** Boolean ops ***********/
            else if (operator == Token.TokenType.SYMBOL_DOUBLEAMPERSAND ||
                    operator == Token.TokenType.SYMBOL_DOUBLEBAR) {
                if (output != Token.TokenType.KEYWORD_BOOLEAN) //at this point we already determined lhs and rhs are the same type
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
            }/************ numeric equality ops ***********/
            else if (operator == Token.TokenType.SYMBOL_GREATERTHAN ||
                    operator == Token.TokenType.SYMBOL_GREATERTHANEQUAL ||
                    operator == Token.TokenType.SYMBOL_LESSTHAN ||
                    operator == Token.TokenType.SYMBOL_LESSTHANEQUAL) {
                if (output != Token.TokenType.KEYWORD_INT) //in these cases the lhs rhs are ints and the output is boolean
                    throw new TypeCheckerException("TypeCheck Error: Wrong Operator Type");
                output = Token.TokenType.KEYWORD_BOOLEAN;
            }/*****     *****/
            //else if( operator ==
            //if the two sides match just return the type of one of the sides
            return output;
        }
        return null;
    }

    //for both FunctionCallTypeCheck and IdentifierReferenceTypeCheck, we need to make sure the parameters are typechecked properly as well
    //probably check to make sure we dont use the wrong storage for the internal parameters
    public Token.TokenType FunctionCallTypeCheck (PStatementFunctionCall functionCall, Storage currentScope)throws TypeCheckerException{
        System.out.println("PStatementFunctionCall");
        String identifierName = functionCall.identifier.getTokenString();
        if (currentScope.MethodNames.containsKey(identifierName)){//check if method is within scope
            FunctStor method = currentScope.MethodNames.get(identifierName);
            ArrayList<VarStor> parameters = method.Parameters; //retrieve parameters
            TypeCheckFunctionCallParameters(identifierName, parameters, functionCall, currentScope);
            return method.ReturnType.getType();
        }
        else { //otherwise check if its within its parents scope
            if (currentScope.extendsClass != null) {
                return ExtendedFunctionCallTypeCheck(functionCall, currentScope.extendsClass);
            } else
                throw new TypeCheckerException("Method " + identifierName + " not in scope;");
        }
    }

    //to handle private instances in extended classes
    public Token.TokenType ExtendedFunctionCallTypeCheck (PStatementFunctionCall functionCall, Storage currentScope)throws TypeCheckerException{
        String identifierName = functionCall.identifier.getTokenString();
        if (currentScope.MethodNames.containsKey(identifierName)){//check if method is within scope
            FunctStor method = currentScope.MethodNames.get(identifierName);
            if (method.AccessModifier.equals(Token.TokenType.KEYWORD_PRIVATE))//this is the only part that is different
                throw new TypeCheckerException("Method access violation");
            ArrayList<VarStor> parameters = method.Parameters; //retrieve parameters
            TypeCheckFunctionCallParameters(identifierName, parameters, functionCall, currentScope);
            return method.ReturnType.getType();
        }
        else { //otherwise check if its within its parents scope
            if (currentScope.extendsClass != null) {
                return ExtendedFunctionCallTypeCheck(functionCall, currentScope.extendsClass);
            } else
                throw new TypeCheckerException("Method " + identifierName + " not in scope;");
        }
    }

    //this will work as a recursive method within the recursive method getExpressionType
    //this will run through its own recursive routine to handle
    //foo.foo1.foo2(x, y) is valid foo.foo1().foo2 is not
    public Token.TokenType IdentifierReferenceTypeCheckDriver (PIdentifierReference PIR, Storage currentScope) throws TypeCheckerException{
        Token.TokenType resultingType;
        //set global to keep track of surface storage
        FunctionCallParameterScope = currentScope;
        resultingType = IdentifierReferenceTypeCheck(PIR, currentScope);//call recursive function
        FunctionCallParameterScope = null; //set global scope to null..........just in case
        return resultingType;
    }

    //as this method runs through its recursive calls the FunctionCallParameterScope will remain the scope on its surface
    public Token.TokenType IdentifierReferenceTypeCheck (PIdentifierReference PIR, Storage currentScope) throws TypeCheckerException{
        System.out.println("PIdentifierReference");
        String identifierName = PIR.identifier.getTokenString();
        if (ClassListAll.containsKey(identifierName)) {//make sure the class being called is in the list
            Storage classSpecificStorage = ClassListAll.get(identifierName);//get scope of class being called
            if(PIR.nextStatement == null){//if this of type foo.variable or foo.identifier.identifier2...... and so on
                if (PIR.nextExpression instanceof PExpressionVariable){//variable called from within another method foo.var
                    //use storage from within variables class to determine its validity
                    return  getExpressionType(PIR.nextExpression, classSpecificStorage);
                }
                else if (PIR.nextExpression instanceof PIdentifierReference){//if it is a daisy chained call foo.identifier.identifier2......
                    return IdentifierReferenceTypeCheck((PIdentifierReference)PIR.nextExpression, classSpecificStorage);//
                }
                else {
                    throw new TypeCheckerException("Unexpected result in IdentifierReferenceTypeCheck: " + PIR.nextExpression.getClass());
                }
            }
            else {//PIR.nextExpression == null....... in other words if this is of type foo.method()
                return FunctionCallTypeCheckFromIRef((PStatementFunctionCall) PIR.nextStatement, classSpecificStorage);
            }
        }
        else
            throw new TypeCheckerException("Unrecognized class: " + identifierName);
    }

    //same as FunctionCallTypeCheck but using FunctionCallParameterScope ClassSpecificStorage is still used to determine the method validity
    //for use in IdentifierReferenceTypeCheck
    public Token.TokenType FunctionCallTypeCheckFromIRef (PStatementFunctionCall functionCall, Storage currentScope)throws TypeCheckerException{
        System.out.println("PStatementFunctionCall");
        String identifierName = functionCall.identifier.getTokenString();
        if (currentScope.MethodNames.containsKey(identifierName)){//check if method is within scope
            FunctStor method = currentScope.MethodNames.get(identifierName); // retrieve method
            ArrayList<VarStor> parameters = method.Parameters; //retrieve parameters
            //USE FunctionCallParameterScope to make sure we are not using a nested scope to check for the given variables
            //remember these variables are expected to be declared on the method that is calling this IRef
            //foo.bar(x,y) x and y are declared in the surface scope
            TypeCheckFunctionCallParameters(identifierName, parameters, functionCall, FunctionCallParameterScope);
            //moved for loop to method to Typecheckfunctioncallparameters
            return method.ReturnType.getType();
        }
        else { //otherwise check if its within its parents scope
            if (currentScope.extendsClass != null) {
                //another recursive call to check
                return ExtendedFunctionCallTypeCheckFromIRef(functionCall, currentScope.extendsClass);
            } else
                throw new TypeCheckerException("Method " + identifierName + " not in scope;");
        }
    }

    //for extended class function calls
    public Token.TokenType ExtendedFunctionCallTypeCheckFromIRef (PStatementFunctionCall functionCall, Storage currentScope)throws TypeCheckerException{
        System.out.println("PStatementFunctionCall");
        String identifierName = functionCall.identifier.getTokenString();
        if (currentScope.MethodNames.containsKey(identifierName)){//check if method is within scope
            FunctStor method = currentScope.MethodNames.get(identifierName); // retrieve method
            if (method.AccessModifier.equals(Token.TokenType.KEYWORD_PRIVATE))//this is the only part that is different
                throw new TypeCheckerException("Method access violation");
            ArrayList<VarStor> parameters = method.Parameters; //retrieve parameters
            //USE FunctionCallParameterScope to make sure we are not using a nested scope to check for the given variables
            TypeCheckFunctionCallParameters(identifierName, parameters, functionCall, FunctionCallParameterScope);
            return method.ReturnType.getType();
        }
        else { //otherwise check if its within its parents scope
            if (currentScope.extendsClass != null) {
                //another recursive call to check
                return FunctionCallTypeCheckFromIRef(functionCall, currentScope.extendsClass);
            } else
                throw new TypeCheckerException("Method " + identifierName + " not in scope;");
        }
    }

    public void TypeCheckFunctionCallParameters(String identifierName, ArrayList<VarStor> parameters, PStatementFunctionCall functionCall, Storage currentScope) throws TypeCheckerException{
        for (int i=0; i < parameters.size(); i++){//TypeCheck all of the parameters
            Token.TokenType expectedType = parameters.get(i).Type.getType();//retrive expected Type of parameter (declared param type)
            PExpression givenVariable = functionCall.expressionsInput.get(i);//retrieve given type of parameter
            Token.TokenType givenType = getExpressionType(givenVariable, currentScope); //(given param type)
            if (expectedType == givenType && expectedType != Token.TokenType.KEYWORD_AUTO) { //if they are equal, but not both auto
                //good, do nothing
            } else if (expectedType == givenType && expectedType == Token.TokenType.KEYWORD_AUTO) { //if both auto, fail
                throw new TypeCheckerException("Cannot give an AUTO var an AUTO type!");
            } else if (expectedType == Token.TokenType.KEYWORD_AUTO) { //if param was declared as auto --> FAIL
                throw new TypeCheckerException("Cannot give function param Auto type!"); //probably never reach bc handled earlier
            } else if (givenType == Token.TokenType.KEYWORD_AUTO) { //if given var was declared auto
                AutoTicket auto = new AutoTicket(); //creating an auto ticket to handle later
                auto.isDeclaration = false; //this var was already declared somewhere else
                auto.ClassName = ClassString; //class name currently dealing with
                auto.ClassNumb = ClassNumber; //where in the class array are we
                auto.ClassDecNumb = ClassDeclarationNumber; //where in the declaration list array are we for the above class
                auto.inParentClass = true; //NOT SURE, not even sure if this is necessary, since we can just check it anyway
                auto.inMethod = true; //in this case, since we are dealing with funct params, true
                auto.MethodName = MethodString; //now that i do this again, i think this makes sense, above we use the (potentially) nested function as the name, here we use the outer function being declared (likely where the vars will be) as method name
                auto.MethodDecNum = MethodDeclarationNumber; //keep which method stmt is being worked on to find auto
                auto.isParam = true; //yes, in this case, we are dealing specifically with params
                auto.ParamNum = i; //current param number is in this for loop as "i"
                auto.isFunctReturn = false; //not doing that here
                auto.TargetVarName = "idk"; //not sure the name of this param, but we already know the funct name and param number, hopefully its fine
                auto.NewType = expectedType; //change the given type to the other type, being assigned to it
                AutoHandler.add(auto);
                System.err.println("AUTOTICKET GENERATED"); //should have type
            } else { //neither type is auto, just a type mismatch
                throw new TypeCheckerException("expected type " + expectedType + " got " + givenType);
            }
        }
    }

    //cleanly check for variable in scope
    //if it does not exist throw an TypeCheckerException
    public Token.TokenType VariableInScope (String varName, Storage currentScope) throws TypeCheckerException{
        System.out.println("PExpressionVariable");
        if (currentScope.VariableNames.containsKey(varName)) { //if var is in class
            VarStor tempVarCheck = currentScope.VariableNames.get(varName);
            return tempVarCheck.Type.getType();
        }
        else { //check parent if any
            if (currentScope.extendsClass != null) { //if there is a parent class
                return VariableInExtendedScope(varName, currentScope.extendsClass);
            }
            else { //no parent class
                throw new TypeCheckerException("TypeCheck Error: No var exists");
            }
        }
    }

    //similar to VariableInScope, however will throw TypeCheckerException if Variable has private access type
    public Token.TokenType VariableInExtendedScope (String varName, Storage currentScope) throws TypeCheckerException{
        if (currentScope.VariableNames.containsKey(varName)) { //if var is in class
            VarStor tempVarCheck = currentScope.VariableNames.get(varName);
            if (tempVarCheck.AccessModifier != null)
                if (tempVarCheck.AccessModifier.equals(Token.TokenType.KEYWORD_PRIVATE))//this is the only part that is different
                    throw new TypeCheckerException("Private Access Violation");
            return tempVarCheck.Type.getType();
        }
        else { //check parent if any
            if (currentScope.extendsClass != null) { //if there is a parent class
                return VariableInExtendedScope(varName, currentScope.extendsClass);
            }
            else { //no parent class
                throw new TypeCheckerException("TypeCheck Error: No var exists");
            }
        }
    }

} //End Typechecker class

class Storage { //store all info about a class

    HashMap<String, VarStor> VariableNames; //name, object
    HashMap<String, FunctStor> MethodNames;
    Storage extendsClass;

    public Storage(HashMap<String, VarStor> vars, HashMap<String, FunctStor> funct, Storage exClass) {
        VariableNames = vars;
        MethodNames = funct;
        extendsClass = exClass;
    }

    public Storage() {
        VariableNames = new HashMap();
        MethodNames = new HashMap();
        extendsClass = null;
    }

    public Storage Copy() {
        HashMap<String, VarStor> copyVariableNames = new HashMap<>(VariableNames);
        HashMap<String, FunctStor> coptMethodNames = new HashMap<>(MethodNames);
        Storage copyStorage = extendsClass;
        copyStorage = new Storage(copyVariableNames , coptMethodNames, copyStorage); //need to keep extends i think
        return copyStorage;
    }
}

class VarStor { //stores var info

    Token Type;
    Token AccessModifier;

    public VarStor(Token type_in, Token accessmodifier_in) {
        Type = type_in;
        AccessModifier = accessmodifier_in;
    }

    public VarStor() {
        Type = null;
        AccessModifier = null;
    }
}

class FunctStor { //store method stuff

    Token ReturnType;
    Token AccessModifier;
    String Classname; //name of the class the function is located
    ArrayList<VarStor> Parameters = new ArrayList<VarStor>(); //ordered list of paramteres stored as VarStor objs
    HashMap<String, VarStor> VariableNames; //stores all method vars declared inside it

    public FunctStor(Token Return_temp, Token AM_temp, String class_temp, ArrayList<VarStor> Params_temp, HashMap<String, VarStor> VN_temp) { //convert to tokens?? XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXxx
        ReturnType = Return_temp;
        AccessModifier = AM_temp;
        Classname = class_temp;
        Parameters = Params_temp;
        VariableNames = VN_temp;
    }

    public FunctStor() {
        Parameters = new ArrayList<VarStor>();
        VariableNames = new HashMap<String, VarStor>();
    }
}

class AutoTicket { //store where AUTO type can be found, and under what conditions

    boolean isDeclaration; //***IMPORTANT***: true if the given autoticket is where the var was declared
    String ClassName; //name of the class found in
    int ClassNumb; //array val of which class we are in
    int ClassDecNumb; //array val of which class declaration we are in
    boolean inParentClass; //true if auto type was found in parent
    boolean inMethod; //true if inside a method (found during declaration of, use name since this is the best way)
    String MethodName; //name of funct that was being declared at time ouf encounter
    int MethodDecNum; //holds the number, in array format (starting with 0) of the value in the method declaration list (example, in the 3rd method stmt, auto was found, keep this val)
    boolean isParam; //true if the parameter of a function is auto (not something being assigned to it)
    int ParamNum; //parameter number, in array style (meaning 0 is the first), (may be negative if not applicable, but wont be checked in this case)
    boolean isFunctReturn; //true if the return type of a function is auto
    String TargetVarName; //***IMPORTANT***: name of the variable/method that has Auto type
    Token.TokenType NewType; //***IMPORTANT***: the new type to replace Auto with

    public AutoTicket(boolean temp_dec, String temp_Class, int temp_cN, int temp_cDN, boolean temp_inPar, boolean temp_inM, String temp_Mname, int temp_mNumb, boolean temp_isP, int temp_Pnum, boolean temp_isFR, String temp_Varname, Token.TokenType temp_newType) {
        isDeclaration = temp_dec;
        ClassName = temp_Class;
        ClassNumb = temp_cN;
        ClassDecNumb = temp_cDN;
        inParentClass = temp_inPar;
        inMethod = temp_inM;
        MethodName = temp_Mname;
        MethodDecNum = temp_mNumb;
        isParam = temp_isP;
        ParamNum = temp_Pnum;
        isFunctReturn = temp_isFR;
        TargetVarName = temp_Varname;
        NewType = temp_newType;
    }

    public AutoTicket() {

    }
}

class ChildOverride {
    String ChildClassName;
    String MethodName;
    int ChildClassNumb;
    int ChildClassDecNumb;

    public ChildOverride(String childname_t, String methodname_t, int classn_t, int classdecn_t) {
        ChildClassName = childname_t;
        MethodName = methodname_t;
        ChildClassNumb = classn_t;
        ChildClassDecNumb = classdecn_t;
    }

    public ChildOverride() {

    }
}