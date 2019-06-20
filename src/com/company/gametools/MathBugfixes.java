package com.company.gametools;

public abstract class MathBugfixes {
    /* Stupid Java bug. It thinks that Math.sqrt accepts double and expects that the computation result
           returned by the function which we pass into sqrt is also Double. If we clearly define an intermediate variable,
           like this then it works:
           Integer value = functionReturningInteger(); Math.sqrt(value);
           But if we do it without the intermediate variable we have runtime ClassCastException.
     */

    /* Another observation: Very interesting phenomenon found (probably, Java bug!).
           If I change E extends Cortege<T> to E extends Cortege<?> (with indefinite type)
           then I get: java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.Double
           in Unit.java in the expression Math.sqrt(Vector2D_Integer.sumSqr(getAbsSize().to2D())).
           What is weird is that Math.sqrt works perfectly with both Double/double, Integer/int.
           But in case we use <?> the compiler somehow thinks the argument for Math.sqrt MUST BE Double.
           Vector2D_Integer.sumSqr(getAbsSize().to2D()) returns Integer and we get a mismatch.
           The same problem is reproduced if I write:
           double value = Vector2D_Integer.sumSqr(getAbsSize().to2D());
 */
    public static double sqrt(Number a) {
        return Math.sqrt(a.doubleValue());
    }
}
