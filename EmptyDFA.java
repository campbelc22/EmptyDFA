import java.lang.Comparable;
import java.io.*;
import java.util.*;

/**
This is a program that when given a text file of a DFA returns whether or not
the DFA accepts no strings. It is capable of handling the standard alphabet
and a theoretically infinite amount of states.

No current bugs are known
@11.23

AUTHORS: Chloe Campbell, Jimi Hayes
*/

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
            //reads in each character from the test file
            in = new FileInputStream(file);
            int cInt;
            char c;
            while((cInt = in.read()) != -1){
              c=(char)cInt;
              machine = machine+c;
            }
            in.close();

        }
        finally{
            if(!(isValid(machine))){//checks to see if it is a valid DFA, if not stop
                System.out.println("That DFA is not valid");
                return;
            }
            else{
                System.out.println("That DFA is valid");//if it is a valid input check if it is in EmptyDFA
                markStates();
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

        //retrieves each of the lines of the formal description to read input
        Scanner s = new Scanner(machine);
        statesString = s.nextLine();
        alphabetString = s.nextLine();
        transitionsString = s.nextLine();
        startStateString = s.nextLine();
        acceptStatesString = s.nextLine();
        s.close();

        //checks valid input and initializes instance variables
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

    //parses through a single state to check that is valid and formats it for later use.
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

    //parses through the states to check that they are all valid and formats them for later use.
    private static boolean parseStates(String statesString){
        //peels off curly brackets
        if(statesString.indexOf('{')!=0){
            return false;
        }
        else if(!(statesString.endsWith("}"))){
            return false;
        }
        statesString=statesString.substring(1, statesString.length()-1);

        //retrieves each individual state
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

        //initializes states array
        states = statesList.stream().mapToInt(Integer::intValue).toArray();
        s.close();
        return true;
    }

    //parses through the alphabet to check that it is valid and formats it for later use.
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
            alphStr=alphStr+chr;
        }
        alphabet = new char[alphStr.length()];
        for (int i = 0; i < alphStr.length(); i++) {
            alphabet[i] = alphStr.charAt(i);
        }
        s.close();
        return true;
    }

    //parses through the start state to check that it is valid and formats it for later use.
    private static boolean parseStart(String startString){
        startState = parseState(startString);
        if(startState==-1){
            return false;
        }
        return true;
    }

    //parses through the accept states to check that they are all valid and formats them for later use.
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

    //creates a contains function that is usable on arrays of ints
    public static boolean contains(int[] arr, int num){
        for(int x : arr){
            if(x == num){
                return true;
            }
        }
        return false;
    }

    //a method to mark which states can be accessed through the start state
    public static void markStates(){
        //create a copy of the states that we can edit later
        int[] statesCopy= new int[states.length];
        for(int i=0; i<states.length;i++){
          statesCopy[i] = states[i];
        }
        //find the start state
        for (int i=0; i<statesCopy.length; i++) {
            if(startState == statesCopy[i]){
                statesCopy[i] = -1; //mark the start state
            }
        }
        //create two flags for exit conditions
        boolean flag = true; //exits if accept state is reached
        boolean flag2 =true; //exits if it just starts to loop and doesn't change anything
        while(flag && flag2){
        flag2 = true; //update that nothing has changed
          for(int x =0; x<states.length; x++){ //loop through states
            if(statesCopy[x]==-1){ //find marked states
              for(int row=0; row<transitionArr.length;row++){
                if(states[x] == transitionArr[row][0]){ //check for which transition
                  flag2=false; //change has been made
                  int temp = transitionArr[row][1]; // state to be marked
                  statesCopy[getIndexOfArr(states, temp)]=-1; //mark next state to check
                  if(contains(acceptState,temp)){
                    flag =false; //accept state found! not empty.
                  }
                }
              }
            }
          }
        }
        //print statements for final results
        if(flag==false){
          System.out.println("An accept state is accessible. The DFA accepts at least one string.");
        }
        else {
          System.out.println("No accept state is accessible. The DFA is Empty! Go Team!");
        }

    }

    //gets the index in the array that matches the given value
    public static int getIndexOfArr(int[] arr, int x){
      int index=-1;
      for(int i=0; i<arr.length; i++){
        if(arr[i]==x){
          index=i;
        }
      }
      return index;
    }

    //determines if the transitions are from and to valid states and using
    //a valid alphabet. makes them into a 2d array that contains which state
    //transitions to each other.
    public static boolean parseTransitions(String transitions){
        int count = 1; //counts the amount of transitions
        while(transitions.contains(";")){
            count++;
            transitions = transitions.replaceFirst(";","!");
        }
        if(count % alphabet.length*states.length != 0){
          return false;
        } //checks if there's a transition for each char in alphabet to each state
        String[] sArr = new String[count]; //string array of each of the transitions
        String transCopy = transitions + '!'; //copy with ! at end
        //create transitionArr
        transitionArr = new int[count][alphabet.length]; //the main array!
        char c;
        int startEnd = 0;
        boolean state1Flag= false;
        boolean state2Flag= false;
        boolean alphaFlag= false;
        for (int i=0; i< count; i++){ //make an array of the transitions
            sArr[i]=transCopy.substring(startEnd,transCopy.indexOf('!'));//make substrings
            startEnd=transCopy.indexOf('!')+1;//set new beginning as the end of this one
            transCopy = transCopy.replaceFirst("!",";"); //swap back to ;
        }
        for (int i = 0; i< count; i++) {
          //cuts out the additional characters that aren't needed
            Scanner sc = new Scanner(sArr[i]).useDelimiter(" x | = ");
            String temp1=sc.next(); //gets first state
            String centerChar = sc.next(); //gets second state
            String temp2=sc.next(); //gets third state
            int ti1=parseState(temp1.trim()); //removes the additional characters
            int ti2=parseState(temp2.trim());
            transitionArr[i][0]=ti1; //sets to the main array
            transitionArr[i][1]=ti2;
            if(centerChar.trim().length() != 1) {
              return false; //checks if alphabet char is valid
            }
            centerChar=centerChar.trim();
            c = centerChar.charAt(0); //sets to char from string
            for(int j=0; j<alphabet.length; j++) {
                if(c == alphabet[j]){
                    alphaFlag=true; //if not in alphabet, not valid. 
                }
            }
            sc.close();
        }

        //makes sure transitions to valid states
        for(int row =0; row<count; row++) {
            for (int j=0; j<states.length; j++){
                if(transitionArr[row][0] == states[j]){
                    state1Flag = true;
                }
                if(transitionArr[row][1] == states[j]){
                    state2Flag = true;
                }
            }
        }
        //checks if valid
        if(state1Flag && state2Flag && alphaFlag){
            return true;
        }
        return false;
    }

    //prints 2d array for error checking.
    public static void printTransitionsArray(){
      for(int row =0; row<transitionArr.length; row++) {
          for (int col =0; col<transitionArr[row].length; col++) {
              System.out.print(transitionArr[row][col]+" ");
          }
          System.out.println();
      }
    }
}
