/* ************************* *
 * U T I L I T Y   C L A S S *
 * ************************* */

/*
   We use "utility class" ("abstract final" class) simulation as "empty enum"
   described on https://stackoverflow.com/questions/9618583/java-final-abstract-class.
   Empty enum constants list (;) makes impossible to use its non-static methods:
   https://stackoverflow.com/questions/61972971/non-static-enum-methods-what-is-the-purpose-and-how-to-call-them.
 */

package com.company.gamecontrollers;

//import com.company.gamethread.M_Thread;

// TODO: Think about GameManager or GameState
/*
enum Choice {
    EXIT_IMMEDIATELY,
    EXIT,
    PAUSE,
    RESUME,
    CANCEL
}*/

/*
public enum GameStateController {
    ; // utility class

    // This function handles ESC key press (it runs in a special unnamed thread automatically by Java mechanisms)
    // I think that this must be handled in the main thread, because it is the highest priority action - game state managing.
    public static void escKeyHandling() {
        M_Thread.suspendAll();
        M_Thread.terminateNoGiveUp(null, 1000, "User quit action.");

        // TODO: add all of this
        // The window with 4 buttons: Exit Immediately, Exit, Pause, Cancel
//        Choice choice = handlePauseWindow();
//        if (choice == Choice.EXIT_IMMEDIATELY) {
//            terminate(0);
//        } else if (choice == Choice.EXIT) {
//            terminate(1000);
//        } else if (choice == Choice.PAUSE) {
//            // Just keep being suspended...
//        } else if (choice == Choice.RESUME) {
//            resumeAll();
//        } else if (choice == Choice.CANCEL) {
//            // Do nothing
//        } else {
//            // TODO: Display a new window
//            //ErrWindow ew = displayErrorWindow("No such choice: " + choice.toString() + ". Exiting...(if this window does not disappear for a long time, kill the game process manually from OS.)");
//            terminateNoGiveUp(null, 1000, null);
//            //ew.close();
//        }
    }
}*/
