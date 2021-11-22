import java.lang.Comparable;
import java.io.*;
import java.util.*;

public class EmptyDFA {
    public static int states[];
    public static char alphabet[];
    public static int startState;
    public static int acceptState[];
    public static int[][] transitionArr;

    public static void main(String[] args) throws IOException {
        FileInputStream in = null;
        String machine = "";
        Scanner s = new Scanner(System.in);
        System.out.println("Please enter the file name of your DFA formal description");
        String file = s.nextLine();
        s.close();
        System.out.println("Thank you for your test file");
        try{
            in = new FileInputStream(file);
            char c;
            while((c = (char)in.read()) != -1){
                machine = machine+c;
            }
            in.close();
        }
        finally{
            if(!(isValid(machine))){
                System.out.println("That DFA is not valid");
                return;
            }
            else{
                System.out.println("That DFA is valid");
                System.out.println(states);
                System.out.println(alphabet);
                System.out.println(startState);
                System.out.println(acceptState);
                System.out.println(transitionArr);
            }
        }
    }
    private static boolean isValid(String machine){
        String statesString;
        boolean statesValid;
        String alphabetString;
        boolean alphabetValid;
        String transitionsString;
        boolean transitionsValid;
        String startStateString;
        boolean startValid;
        String acceptStatesString;
        boolean acceptValid;

        Scanner s = new Scanner(machine);
        statesString = s.nextLine();
        alphabetString = s.nextLine();
        transitionsString = s.nextLine();
        startStateString = s.nextLine();
        acceptStatesString = s.nextLine();
        s.close();

        statesValid = parseStates(statesString.trim());
        alphabetValid = parseAlphabet(alphabetString.trim());
        startValid = parseStart(startStateString.trim());
        acceptValid = parseAccept(acceptStatesString.trim());
        transitionsValid = parseTransitions(transitionsString.trim());

        if(statesValid&&alphabetValid&&transitionsValid&&startValid&&acceptValid){
            return true;
        }
        else{
            return false;
        }
    }

    public static int parseState(String state) {
        Scanner s = new Scanner(state).useDelimiter("q");
        if(s.hasNextInt()){
            int temp = s.nextInt();
            s.close();
            if(temp<0){
                return -1;
            }
            return temp;
        }
        s.close();
        return -1;
    }

    private static boolean parseStates(String statesString){
        if(statesString.indexOf('{')!=0){
            return false;
        }
        else if(!(statesString.endsWith("}"))){
            return false;
        }
        statesString=statesString.substring(1, statesString.length()-1);
        Scanner s = new Scanner(statesString).useDelimiter(", ");
        String state;
        ArrayList<Integer> statesList = new ArrayList<Integer>();
        while(s.hasNext()){
            state=s.next();
            int sNum = parseState(state.trim());
            if(sNum == -1){
                s.close();
                return false;
            }
            statesList.add(Integer.valueOf(sNum));
        }
        states = statesList.stream().mapToInt(Integer::intValue).toArray();
        s.close();
        return true;
    }

    private static boolean parseAlphabet(String alphString){
        if(alphString.indexOf('{')!=0){
            return false;
        }
        else if(!(alphString.endsWith("}"))){
            return false;
        }
        alphString=alphString.substring(1, alphString.length()-1);
        Scanner s = new Scanner(alphString).useDelimiter(", ");
        String chr;
        String alphStr = "";
        while(s.hasNext()){
            chr=s.next();
            chr=chr.trim();
            if(chr.length() > 1){
                s.close();
                return false;
            }
            alphStr=alphString+chr;
        }
        alphabet = new char[alphStr.length()];
        for (int i = 0; i < alphStr.length(); i++) {
            alphabet[i] = alphStr.charAt(i);
        }
        s.close();
        return true;
    }

    private static boolean parseStart(String startString){
        startState = parseState(startString);
        if(startState==-1){
            return false;
        }
        return true;
    }

    private static boolean parseAccept(String acceptString){
        if(acceptString.indexOf('{')!=0){
            return false;
        }
        else if(!(acceptString.endsWith("}"))){
            return false;
        }
        acceptString=acceptString.substring(1, acceptString.length()-1);
        Scanner s = new Scanner(acceptString).useDelimiter(", ");
        String state;
        ArrayList<Integer> statesList = new ArrayList<Integer>();
        while(s.hasNext()){
            state=s.next();
            int sNum = parseState(state.trim());
            if(sNum == -1){
                s.close();
                return false;
            }
            if(contains(states, sNum)){
                statesList.add(Integer.valueOf(sNum));
            }
            else{
                s.close();
                return false;
            }
        }
        acceptState = statesList.stream().mapToInt(Integer::intValue).toArray();
        s.close();
        return true;
    }

    public static boolean contains(int[] arr, int num){
        for(int x : arr){
            if(x == num){
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAcceptStates(){
        boolean bool =false;
        if(acceptState.length >0) { 
            bool =true;
        }
        return bool;
    }
    
    public static void markStates(){ 
        //find the start state 
        int[] statesCopy = states;
        for (int i=0; i<statesCopy.length; i++) { 
            if(startState == statesCopy[i]){
                statesCopy[i] = -1; //mark the start state
            }
        }
        for(int x = 0; x<states.length; x++){ 
            int[] nextStates= new int[alphabet.length];
            for (int row =0; row<transitionArr.length;row++) { 
                for(int j=0; j<alphabet.length;j++) {
                    if(statesCopy[j] == -1){
                        if(states[j] == transitionArr[row][0]){
                            nextStates[j]=transitionArr[row][1];
                            statesCopy[j] = statesCopy[j]-1;
                        }
                    }
                }
            }
        }
    }
    public static boolean parseTransitions(String transitions){ 
        int count = 1;
        while(transitions.contains(";")){
            count++;
            transitions = transitions.replaceFirst(";","!");
        }
        String[] sArr = new String[count]; //string array of each of the transitions
        String transCopy = transitions + '!'; //copy with ! at end
        int startEnd = 0;
        for (int i=0; i< count; i++){ 
            sArr[i]=transCopy.substring(startEnd,transCopy.indexOf('!'));//make substrings
            startEnd=transCopy.indexOf('!')+1;//set new beginning as the end of this one
            transCopy = transCopy.replaceFirst("!",";"); //swap back to ;
        }
        for (int i = 0; i< count; i++) { 
            Scanner sc = new Scanner(sArr[i]).useDelimiter("\\sq\\sx\\s\\s=\\sq\\s;");
            String temp = "";
            while(sc.hasNext()){
                temp = temp.concat(sc.next());
            }
            sArr[i]=temp;
            sc.close();
        }
        boolean state1Flag= false;
        boolean state2Flag= false;
        boolean alphaFlag= false;
        for (int i=0; i<count; i++){
            for (int j=0; j<states.length; j++){
                if(sArr[i].charAt(0) == states[j]){
                    state1Flag=true;
                }
                if(sArr[i].charAt(2) == states[j]){
                    state2Flag=true;
                }
            }
            for(int j=0; j<alphabet.length; j++) {
                if(sArr[i].charAt(1) == alphabet[j]){
                    alphaFlag=true;	
                }
            }
        }
        //create transitionArr
        transitionArr = new int[count][2];
        Scanner s = new Scanner(transitions);
        for(int row =0; row<count; row++) { 
            for (int col =0; col<2; col++) {
                while(!s.hasNextInt()) { 
                    transitionArr[row][col]=s.nextInt();
                }
            }
        }
        s.close();
        if(state1Flag && state2Flag && alphaFlag){
            return true;
        }
        return false;
    }
}
