package com.searchgutenberg.booksearchengine.utils.regex;


import java.util.*;

public class RegEx{
    //MACROS
    static final int CONCAT = 0xC04CA7;
    static final int ETOILE = 0xE7011E;
    static final int ALTERN = 0xA17E54;
    static final int PROTECTION = 0xBADDAD;

    static final int PARENTHESEOUVRANT = 0x16641664;
    static final int PARENTHESEFERMANT = 0x51515151;
    static final int DOT = 0xD07;

    //REGEX
    private static String regEx;
    private static long startTime;
    private static long endTime;


    //CONSTRUCTOR
    public RegEx(){}

    //MAIN
//    public static void main(String arg[]) throws IOException {
//        String filePath;
//        File inputFile;
//
//        System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
//        if (arg.length>=2) {
//            regEx = arg[0];
//            filePath=arg[1];
//
//        }else {
//            Scanner scanner = new Scanner(System.in);
//            System.out.print("  >> Please enter a regEx ");
//            regEx = scanner.next();
//            System.out.print("  >> Please enter a filePath ");
//            filePath=scanner.next();
//        }
//
//        System.out.println("  >> Parsing regEx \""+regEx+"\".");
//        System.out.println("  >> ...");
//        System.out.println("  >> File path: "+filePath);
//
//
//        if (regEx.length()<1) {
//            System.err.println("  >> ERROR: empty regEx.");
//        }else if(filePath.length()<1){
//            System.err.println("  >> ERROR: empty filePath.");
//        }
//        else {
//            System.out.print("  >> ASCII codes: [" + (int) regEx.charAt(0));
//            for (int i = 1; i < regEx.length(); i++) System.out.print("," + (int) regEx.charAt(i));
//            System.out.println("].");
//
//            // Open the input file to read and store the contents
//            //startTime=System.currentTimeMillis();
//            inputFile = new File(filePath);
//            InputStreamReader reader = new InputStreamReader(new FileInputStream(inputFile));
//            BufferedReader br = new BufferedReader(reader);
//            String line = "";
//            ArrayList<String> inputText = new ArrayList<>();
//            while (line != null) {
//                line = br.readLine();
//                inputText.add(line);
//            }
//            ArrayList<Integer> result = null;
//            //user choose the kmp mode
//            if (arg.length > 2 && arg[2].equals("-kmp")) {
//                //The search string is  a series of concatenations,
//                if (!regEx.contains("*") && !regEx.contains("|")) {
//                    startTime=System.currentTimeMillis();
//                    result = kmp(regEx, inputText);
//                    endTime=System.currentTimeMillis();
//                    System.out.println("kmp result: \n");
//
//                } else {
//                    System.err.println("  >> ERROR: parameters input don't match .");
//                }
//                // in other case,ues the automaton to search the RegEx
//            } else {
//
//                try {
//                    startTime=System.currentTimeMillis();
//                    RegExTree ret = parse();
//                    System.out.println("  >> Tree result: " + ret.toString() + ".");
//                    NDFAutomaton ndf = step2_AhoUllman(ret);
//                    System.out.println("  >> NDF result: " + ndf.toString() + ".");
//                    DFA dfa = step3_ToDFA(ndf);
//                    System.out.println("  >> DFA result: " + dfa.toString() + ".");
//                    result = step5_match(dfa, inputText);
//                    endTime=System.currentTimeMillis();
//                    System.out.println("Automaton search result:\n");
//                } catch (Exception e) {
//                    System.err.println("  >> ERROR: syntax error for regEx \"" + regEx + "\".");
//                }
//
//
//            }
//            // print the found lines
//            if (result != null) {
//                for (int i : result) {
//                    System.out.println(inputText.get(i) + "\n");
//                }
//
//            }
//
//        }
//
//        System.out.println("  >> ...");
//        System.out.println("  >> The search is over.");
//        System.out.println("Time： "+(endTime-startTime)+"ms");
//        System.out.println("Goodbye Mr. Anderson.");
//    }

    //FROM REGEX TO SYNTAX TREE
    private static RegExTree parse() throws Exception {
        //BEGIN DEBUG: set conditionnal to true for debug example
        if (false) throw new Exception();
        RegExTree example = exampleAhoUllman();
        if (false) return example;
        //END DEBUG

        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        for (int i=0;i<regEx.length();i++) result.add(new RegExTree(charToRoot(regEx.charAt(i)),new ArrayList<RegExTree>()));

        return parse(result);
    }
    private static int charToRoot(char c) {
        if (c=='.') return DOT;
        if (c=='*') return ETOILE;
        if (c=='|') return ALTERN;
        if (c=='(') return PARENTHESEOUVRANT;
        if (c==')') return PARENTHESEFERMANT;
        return (int)c;
    }
    private static RegExTree parse(ArrayList<RegExTree> result) throws Exception {
        while (containParenthese(result)) result=processParenthese(result);
        while (containEtoile(result)) result=processEtoile(result);
        while (containConcat(result)) result=processConcat(result);
        while (containAltern(result)) result=processAltern(result);

        if (result.size()>1) throw new Exception();

        return removeProtection(result.get(0));
    }
    private static boolean containParenthese(ArrayList<RegExTree> trees) {
        for (RegExTree t: trees) if (t.root==PARENTHESEFERMANT || t.root==PARENTHESEOUVRANT) return true;
        return false;
    }
    private static ArrayList<RegExTree> processParenthese(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        for (RegExTree t: trees) {
            if (!found && t.root==PARENTHESEFERMANT) {
                boolean done = false;
                ArrayList<RegExTree> content = new ArrayList<RegExTree>();
                while (!done && !result.isEmpty())
                    if (result.get(result.size()-1).root==PARENTHESEOUVRANT) { done = true; result.remove(result.size()-1); }
                    else content.add(0,result.remove(result.size()-1));
                if (!done) throw new Exception();
                found = true;
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(parse(content));
                result.add(new RegExTree(PROTECTION, subTrees));
            } else {
                result.add(t);
            }
        }
        if (!found) throw new Exception();
        return result;
    }

    private static boolean containEtoile(ArrayList<RegExTree> trees) {
        for (RegExTree t: trees) if (t.root==ETOILE && t.subTrees.isEmpty()) return true;
        return false;
    }

    private static ArrayList<RegExTree> processEtoile(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        for (RegExTree t: trees) {
            if (!found && t.root==ETOILE && t.subTrees.isEmpty()) {
                if (result.isEmpty()) throw new Exception();
                found = true;
                RegExTree last = result.remove(result.size()-1);
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(last);
                result.add(new RegExTree(ETOILE, subTrees));
            } else {
                result.add(t);
            }
        }
        return result;
    }
    private static boolean containConcat(ArrayList<RegExTree> trees) {
        boolean firstFound = false;
        for (RegExTree t: trees) {
            if (!firstFound && t.root!=ALTERN) { firstFound = true; continue; }
            if (firstFound) if (t.root!=ALTERN) return true; else firstFound = false;
        }
        return false;
    }
    private static ArrayList<RegExTree> processConcat(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        boolean firstFound = false;
        for (RegExTree t: trees) {
            if (!found && !firstFound && t.root!=ALTERN) {
                firstFound = true;
                result.add(t);
                continue;
            }
            if (!found && firstFound && t.root==ALTERN) {
                firstFound = false;
                result.add(t);
                continue;
            }
            if (!found && firstFound && t.root!=ALTERN) {
                found = true;
                RegExTree last = result.remove(result.size()-1);
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(last);
                subTrees.add(t);
                result.add(new RegExTree(CONCAT, subTrees));
            }else {
                result.add(t);
            }
        }
        return result;
    }
    private static boolean containAltern(ArrayList<RegExTree> trees) {
        for (RegExTree t: trees) if (t.root==ALTERN && t.subTrees.isEmpty()) return true;
        return false;
    }
    private static ArrayList<RegExTree> processAltern(ArrayList<RegExTree> trees) throws Exception {
        ArrayList<RegExTree> result = new ArrayList<RegExTree>();
        boolean found = false;
        RegExTree gauche = null;
        boolean done = false;
        for (RegExTree t: trees) {
            if (!found && t.root==ALTERN && t.subTrees.isEmpty()) {
                if (result.isEmpty()) throw new Exception();
                found = true;
                gauche = result.remove(result.size()-1);
                continue;
            }
            if (found && !done) {
                if (gauche==null) throw new Exception();
                done=true;
                ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
                subTrees.add(gauche);
                subTrees.add(t);
                result.add(new RegExTree(ALTERN, subTrees));
            } else {
                result.add(t);
            }
        }
        return result;
    }
    private static RegExTree removeProtection(RegExTree tree) throws Exception {
        if (tree.root==PROTECTION && tree.subTrees.size()!=1) throw new Exception();
        if (tree.subTrees.isEmpty()) return tree;
        if (tree.root==PROTECTION) return removeProtection(tree.subTrees.get(0));

        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        for (RegExTree t: tree.subTrees) subTrees.add(removeProtection(t));
        return new RegExTree(tree.root, subTrees);
    }

    //EXAMPLE
// --> RegEx from Aho-Ullman book Chap.10 Example 10.25
    private static RegExTree exampleAhoUllman() {
        RegExTree a = new RegExTree((int)'a', new ArrayList<RegExTree>());
        RegExTree b = new RegExTree((int)'b', new ArrayList<RegExTree>());
        RegExTree c = new RegExTree((int)'c', new ArrayList<RegExTree>());
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(c);
        RegExTree cEtoile = new RegExTree(ETOILE, subTrees);
        subTrees = new ArrayList<RegExTree>();
        subTrees.add(b);
        subTrees.add(cEtoile);
        RegExTree dotBCEtoile = new RegExTree(CONCAT, subTrees);
        subTrees = new ArrayList<RegExTree>();
        subTrees.add(a);
        subTrees.add(dotBCEtoile);
        return new RegExTree(ALTERN, subTrees);
    }

    private static NDFAutomaton step2_AhoUllman(RegExTree ret) {

        if (ret.subTrees.isEmpty()) {
            //IMPLICIT REPRESENTATION HERE: INIT STATE IS ALWAYS 0; FINAL STATE IS ALWAYS transitionTable.length-1
            int[][] tTab = new int[2][256];
            ArrayList<Integer>[] eTab = new ArrayList[2];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i=0;i<tTab.length;i++) for (int col=0;col<256;col++) tTab[i][col]=-1;
            for (int i=0;i<eTab.length;i++) eTab[i]=new ArrayList<Integer>();

            if (ret.root!=DOT) tTab[0][ret.root]=1; //transition ret.root from initial state "0" to final state "1"
            else for (int i=0;i<256;i++) tTab[0][i]=1; //transition DOT from initial state "0" to final state "1"

            return new NDFAutomaton(tTab,eTab);
        }

        if (ret.root==CONCAT) {
            //IMPLICIT REPRESENTATION HERE: INIT STATE IS ALWAYS 0; FINAL STATE IS ALWAYS transitionTable.length-1
            NDFAutomaton gauche = step2_AhoUllman(ret.subTrees.get(0));
            int[][] tTab_g = gauche.transitionTable;
            ArrayList<Integer>[] eTab_g = gauche.epsilonTransitionTable;
            NDFAutomaton droite = step2_AhoUllman(ret.subTrees.get(1));
            int[][] tTab_d = droite.transitionTable;
            ArrayList<Integer>[] eTab_d = droite.epsilonTransitionTable;
            int lg=tTab_g.length;
            int ld=tTab_d.length;
            int[][] tTab = new int[lg+ld][256];
            ArrayList<Integer>[] eTab = new ArrayList[lg+ld];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i=0;i<tTab.length;i++) for (int col=0;col<256;col++) tTab[i][col]=-1;
            for (int i=0;i<eTab.length;i++) eTab[i]=new ArrayList<Integer>();

            eTab[lg-1].add(lg); //epsilon transition from old final state "left" to old initial state "right"

            for (int i=0;i<lg;i++) for (int col=0;col<256;col++) tTab[i][col]=tTab_g[i][col]; //copy old transitions
            for (int i=0;i<lg;i++) eTab[i].addAll(eTab_g[i]); //copy old transitions
            for (int i=lg;i<lg+ld-1;i++) for (int col=0;col<256;col++) if (tTab_d[i-lg][col]!=-1) tTab[i][col]=tTab_d[i-lg][col]+lg; //copy old transitions
            for (int i=lg;i<lg+ld-1;i++) for (int s: eTab_d[i-lg]) eTab[i].add(s+lg); //copy old transitions

            return new NDFAutomaton(tTab,eTab);
        }

        if (ret.root==ALTERN) {
            //IMPLICIT REPRESENTATION HERE: INIT STATE IS ALWAYS 0; FINAL STATE IS ALWAYS transitionTable.length-1
            NDFAutomaton gauche = step2_AhoUllman(ret.subTrees.get(0));
            int[][] tTab_g = gauche.transitionTable;
            ArrayList<Integer>[] eTab_g = gauche.epsilonTransitionTable;
            NDFAutomaton droite = step2_AhoUllman(ret.subTrees.get(1));
            int[][] tTab_d = droite.transitionTable;
            ArrayList<Integer>[] eTab_d = droite.epsilonTransitionTable;
            int lg=tTab_g.length;
            int ld=tTab_d.length;
            int[][] tTab = new int[2+lg+ld][256];
            ArrayList<Integer>[] eTab = new ArrayList[2+lg+ld];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i=0;i<tTab.length;i++) for (int col=0;col<256;col++) tTab[i][col]=-1;
            for (int i=0;i<eTab.length;i++) eTab[i]=new ArrayList<Integer>();

            eTab[0].add(1); //epsilon transition from new initial state to old initial state
            eTab[0].add(1+lg); //epsilon transition from new initial state to old initial state
            eTab[1+lg-1].add(2+lg+ld-1); //epsilon transition from old final state to new final state
            eTab[1+lg+ld-1].add(2+lg+ld-1); //epsilon transition from old final state to new final state

            for (int i=1;i<1+lg;i++) for (int col=0;col<256;col++) if (tTab_g[i-1][col]!=-1) tTab[i][col]=tTab_g[i-1][col]+1; //copy old transitions
            for (int i=1;i<1+lg;i++) for (int s: eTab_g[i-1]) eTab[i].add(s+1); //copy old transitions
            for (int i=1+lg;i<1+lg+ld-1;i++) for (int col=0;col<256;col++) if (tTab_d[i-1-lg][col]!=-1) tTab[i][col]=tTab_d[i-1-lg][col]+1+lg; //copy old transitions
            for (int i=1+lg;i<1+lg+ld;i++) for (int s: eTab_d[i-1-lg]) eTab[i].add(s+1+lg); //copy old transitions

            return new NDFAutomaton(tTab,eTab);
        }

        if (ret.root==ETOILE) {
            //IMPLICIT REPRESENTATION HERE: INIT STATE IS ALWAYS 0; FINAL STATE IS ALWAYS transitionTable.length-1
            NDFAutomaton fils = step2_AhoUllman(ret.subTrees.get(0));
            int[][] tTab_fils = fils.transitionTable;
            ArrayList<Integer>[] eTab_fils = fils.epsilonTransitionTable;
            int l=tTab_fils.length;
            int[][] tTab = new int[2+l][256];
            ArrayList<Integer>[] eTab = new ArrayList[2+l];

            //DUMMY VALUES FOR INITIALIZATION
            for (int i=0;i<tTab.length;i++) for (int col=0;col<256;col++) tTab[i][col]=-1;
            for (int i=0;i<eTab.length;i++) eTab[i]=new ArrayList<Integer>();

            eTab[0].add(1); //epsilon transition from new initial state to old initial state
            eTab[0].add(2+l-1); //epsilon transition from new initial state to new final state
            eTab[2+l-2].add(2+l-1); //epsilon transition from old final state to new final state
            eTab[2+l-2].add(1); //epsilon transition from old final state to old initial state

            for (int i=1;i<2+l-1;i++) for (int col=0;col<256;col++) if (tTab_fils[i-1][col]!=-1) tTab[i][col]=tTab_fils[i-1][col]+1; //copy old transitions
            for (int i=1;i<2+l-1;i++) for (int s: eTab_fils[i-1]) eTab[i].add(s+1); //copy old transitions

            return new NDFAutomaton(tTab,eTab);
        }

        return null;
    }
    private static DFA step3_ToDFA(NDFAutomaton ndf) {

        int[][] transitionTable = ndf.transitionTable; //ASCII transition
        ArrayList<Integer>[] epsilonTransitionTable = ndf.epsilonTransitionTable;

        int[][] newTransitionTable = new int[100][256];
        for (int i=0;i<newTransitionTable.length;i++) for (int col=0;col<256;col++) newTransitionTable[i][col]=-1;

        HashSet<Integer> etatI = new HashSet<>(); // état initial, stocker tous les états passer par epsilon à partir d'état initial 0
        ArrayList<HashSet> dfaTransitions = new ArrayList<>(); // Stocker tous les états
        ArrayList<HashSet> setRecord =  new ArrayList<>(); // Enregistrer l'ensemble d'etats qui est déjà présenté
        //HashSet<Integer> etatInital = new HashSet<>(); // Nouveau état initial
        HashSet<Integer> etatFinal = new HashSet<>(); // Nouveau état final

        int index = 0;

        // Chercher tous les états passer par epsilon à partir d'état initial 0
        etatI.add(0);
        for(int state: epsilonTransitionTable[0]){
            etatI.add(state);
            etatI.addAll(getAllEpsilonStates(state,ndf));
        }
        dfaTransitions.add(etatI);

        while(index < dfaTransitions.size()){
            for (int col = 0; col < 256; col++){
                HashSet<Integer> suitEtat =  new HashSet<>(); //Stocker la nouvelle état qui est consisté par plusieur états anciennes
                HashSet<Integer> ndfEtat = dfaTransitions.get(index); // Etat actuel

                //Enregistrer les états finales
                if(ndfEtat.contains(ndf.transitionTable.length-1)){
                    etatFinal.add(index);
                }
                Iterator it = ndfEtat.iterator();
                while (it.hasNext()){
                    Object obj = it.next();
                    if(transitionTable[(int)obj][col] != -1){
                        suitEtat.add(transitionTable[(int)obj][col]); //Stocker les étates qui sont acossié avec obj par le symbol col
                        suitEtat.addAll(getAllEpsilonStates(transitionTable[(int)obj][col],ndf)); //Stocker les étates qui sont acossié avec les étates par le symbol epsilon
                    }
                }
                if(!suitEtat.isEmpty()){
                    //Enregistrer le set de etates qui n'est jamais présenté
                    if(!setExist(suitEtat,setRecord)){
                        dfaTransitions.add(suitEtat);
                        setRecord.add(suitEtat);
                    }
                    //Enregistere les relations de DFA
                    newTransitionTable[index][col] = dfaTransitions.size()-1;
                }
            }
            index = index+1;
        }
        return new DFA(newTransitionTable,etatFinal);
    }

    private static HashSet<Integer> getAllEpsilonStates(int currentState, NDFAutomaton automate){
        HashSet<Integer>res = new HashSet<>();
        HashSet<Integer>tmp = new HashSet<>();
        res.addAll(automate.epsilonTransitionTable[currentState]);
        for(int state : res){
            tmp.addAll(getAllEpsilonStates(state,automate));
        }
        res.addAll(tmp);
        return res;
    }

    private static boolean setExist(HashSet<Integer> set, ArrayList<HashSet> list){
        for(int i = 0; i<list.size();i++){
            if(set.size() == list.get(i).size()){
                if(set.containsAll(list.get(i))){
                    return true;
                }
            }else{
                continue;
            }
        }
        return false;
    }






    private static ArrayList<Integer> step5_match(DFA automate,ArrayList<String> inputText)throws Exception{
        ArrayList<Integer> matchedlines=new ArrayList<>();
        int[][] tarnsitions=automate.getTransition();
        // traverse all the lines of input text
        for( int i=0;i<inputText.size();i++){
            String line=inputText.get(i);
            if(line==null) continue;
            LINE:
            for(int index=0;index<line.length();index++){
                if ((int)line.charAt(index)>=256) continue;
                if (tarnsitions[0][Integer.valueOf(line.charAt(index))]==-1)
                    continue;

                //the next index of char to verify in the current line
                int interIndex=index+1;
                //The next index of line (equals to next state of automaton) to be checked in the transitions table(transitions[line][col])
                int nextIndex=tarnsitions[0][(int) line.charAt(index)];
                while (interIndex<line.length()) {
                    //if we reach the final state of automaton, it means we find a matched word
                    if(automate.getSetFinal().contains(nextIndex)){
                        matchedlines.add(i);
                        break LINE;
                    }
                    //if we can't find the transition specific, that means we should match the word from the next index of the current line(attribute interIndex)
                    if(Integer.valueOf(line.charAt(interIndex))>= tarnsitions[nextIndex].length) break;
                    if(tarnsitions[nextIndex][Integer.valueOf(line.charAt(interIndex))]==-1) break;
                    // if we can find the matched transition, continue to verify the next char
                    nextIndex=tarnsitions[nextIndex][Integer.valueOf(line.charAt(interIndex))];
                    interIndex++;

                }
            }

        }

        return matchedlines;
    }

    private static ArrayList<Integer>kmp(String regEx,ArrayList<String>inputText){
        ArrayList<Integer>matchedlines=new ArrayList<Integer>();
        int [] carryOver=new int[regEx.length()+1];
        carryOver[0]=-1;
        carryOver[1]=0;
        carryOver[regEx.length()]=0;

        //calculate matrix carryOver step1
        for(int i=2;i<regEx.length();i++){
            String currentStr=regEx.substring(0,i);
            ArrayList<String>prefixs =new ArrayList<>();
            HashSet<String>suffixs=new HashSet<>();
            // for the current substring , find all the prefix and suffix
            for(int index=0;index<currentStr.length()-1;index++){
                prefixs.add(currentStr.substring(0,index+1));
                suffixs.add(currentStr.substring(currentStr.length()-index,currentStr.length()));
            }
            //find the longest prefix and suffix in commmon
            for(int j=prefixs.size()-1;j>=0;j--){
                if(suffixs.contains(prefixs.get(j)) ){
                    carryOver[i]=prefixs.get(j).length();
                    break;
                }
            }

        }

        char[] factory=regEx.toCharArray();
        //calculate matrix carryOver step2
        for(int i=0;i< factory.length;i++){
            if(carryOver[i]!=-1 &&factory[carryOver[i]]==factory[i] &&carryOver[carryOver[i]]==-1){
                carryOver[i]=-1;
            }
        }
        //calculate matrix carryOver step3
        for(int i=0;i< factory.length;i++){
            if(carryOver[i]!=-1 &&factory[carryOver[i]]==factory[i] &&carryOver[carryOver[i]]!=-1){
                carryOver[i]=carryOver[carryOver[i]];
            }
        };

        //use carryOver matching
        for( int i=0;i<inputText.size();i++){
            String line=inputText.get(i);
            if(line==null) continue;
            //The index of the curent line being checked
            int currentIndex=0;
            //The index of factory being matched
            int facIndex=0;
            while (currentIndex<line.length()){
                if(line.charAt(currentIndex)==factory[facIndex]){
                    currentIndex++;
                    facIndex++;
                    if(facIndex==factory.length){
                        matchedlines.add(i);
                        break;
                    }
                }else {
                    currentIndex=currentIndex-carryOver[facIndex];
                    facIndex=0;
                }

            }

        }

        return matchedlines;
    }


}