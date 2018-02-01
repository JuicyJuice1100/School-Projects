/** Server program for the MM app
 *
 *  @author YOUR FULL NAME GOES HERE
 *
 *  @version CS 391 - Spring 2018 - A1
 **/

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.Arrays;

public class MMServer {

  static ServerSocket serverSocket = null;  // listening socket
  static int portNumber = 5555;             // port on which server listens
  static Socket clientSocket = null;        // socket to a client
  static DataInputStream in = null;         // input stream from client
  static DataOutputStream out = null;       // output stream to client

  /*  Start the server then wait for a connection request. After accepting a
      connection request, use an instance of MM to send to the client an initial
      request for his or her next guess and then repeatedly:
          1. wait for and read in a request
          2. compute the reply
          3. send the reply to the client
      until the reply is "Thank you for playing!"
      The amount and format of the console output (e.g., client request,
      server replies) are imposed as part of the problem statement in the
      handout (as shown in the provided session trace).
   */
  public static void main(String[] args) {

      String request, reply;
      MM masterMind = null;
      try {
        serverSocket = new ServerSocket(portNumber);
        System.out.println("Server started: " + serverSocket);
        System.out.println("Waiting for a client...");
        clientSocket = serverSocket.accept();
        System.out.println("Connection established: " + clientSocket);
        openStreams();
        masterMind = new MM();
        while(true){
            request = in.readUTF();
            System.out.println("Client: " + request);
            reply = masterMind.getReply(request);
            out.writeUTF(reply);
            if(reply.equals("Thank you for playing!")){ break; }
        }
        close();
      } catch (IOException e){
        System.out.println("Server encountered an error. Please try connecting again");
      }

  }// main method

  /* open the necessary I/O streams and initializes the in and out
     static variables; this method does not catch any exceptions.
   */
  static void openStreams() throws IOException {

    in = new DataInputStream(clientSocket.getInputStream());
    out = new DataOutputStream(clientSocket.getOutputStream());

  }// openStreams method

  /* close all open I/O streams and sockets
   */
  static void close() {
    try{
        if(in != null){ in.close(); }
        if(out != null) { out.close(); }
        if(clientSocket != null) {clientSocket.close();}
    } catch (IOException e){
        System.err.println("Error in close(): " + e.getMessage());
    }
  }// close method

}//MMServer class

/* this class implements the MM FSM
**/
class MM {
  private static final int PLAY = 0;      // P state
  private static final int GAMEOVER = 1;  // GO state
  private int state;                      // current state
  private String answer;                  // pattern to discover
  private ArrayList<String> trace;
  static String youWin = "    You win!";
  static String nextGuess = "    Type your next guess:";
  static String playAgain = "    Another game? (Y/N)";
  static String thankYou = "    Thank you for playing!";
  static FINAL String AVAILABLELETTERS = "ABCDEF";

  /*  the FSM starts in the P state and the trace is empty
   */
  MM() {
    state = PLAY;
    answer = pickRandomAnswer();
  }// constructor

  /* return a 4-character string in which each character is a uniformly
     randomly selected character in the range A-F. This string must be printed
     to the console before it is returned.
  */
  private String pickRandomAnswer() {
    StringBuilder pattern = new StringBuilder();
    for(int i = 0; i < 4; i++){
        pattern.append(AVAILABLELETTERS.charAt(new Random().nextInt(5)));
    }
    return pattern.toString();
  }// pickRandomAnswer method

/*  return a string that encodes the codemaker's feedback to the given guess.
    The format of the feedback is fully specified in the handout.
 */
private String getFeedback(String guess) {

      // to be completed

      return null;
}// getFeedback method

  /*  Implement the transition function of the FSM. In other words, return
      the server's reply as a function of the client's query. Also update
      the current state and other variables as needed.
   */
  public String getReply(String query) {
    String reply = null;

    switch(state){
        case PLAY:
            reply = ""
    }
    return reply;
  }// getReply method

}// MM class
