/* ************************* *
 * U T I L I T Y   C L A S S *
 * ************************* */

/*
   We use "utility class" ("abstract final" class) simulation as "empty enum"
   described on https://stackoverflow.com/questions/9618583/java-final-abstract-class.
   Empty enum constants list (;) makes impossible to use its non-static methods:
   https://stackoverflow.com/questions/61972971/non-static-enum-methods-what-is-the-purpose-and-how-to-call-them.
 */

package com.company.gametools;

// TODO: Add test to the beginning of the game ot test if all standard functions
// which are not "fixed" here behave as expected and display an error if not
// something like "ERROR: 3rd party API <func_name> was changed, the program will work wrongly!"

public enum MathBugfixes {
    ;

    /* Stupid Java bug. It thinks that Math.sqrt accepts double and expects that the computation result
           returned by the function which we pass into sqrt is also Double. If we clearly define an intermediate variable,
           like this then it works:
           Integer value = functionReturningInteger(); Math.sqrt(value);
           But if we do it without the intermediate variable we have runtime ClassCastException.
     */

    /* Another observation: Very interesting phenomenon found (probably, Java bug!).
           If I change E extends Cortege<T> to E extends Cortege<?> (with indefinite type)
           then I get: java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.Double
           in Unit.java in the expression Math.sqrt(Vector2D_Integer.sumSqr(getAbsDim().to2D())).
           What is weird is that Math.sqrt works perfectly with both Double/double, Integer/int.
           But in case we use <?> the compiler somehow thinks the argument for Math.sqrt MUST BE Double.
           Vector2D_Integer.sumSqr(getAbsDim().to2D()) returns Integer and we get a mismatch.
           The same problem is reproduced if I write:
           double value = Vector2D_Integer.sumSqr(getAbsDim().to2D());
     */
    public static double sqrt(Number a) {
        return Math.sqrt(a.doubleValue());
    }
}
