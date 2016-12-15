package com.podonomy.podonomyplayer;

/**
 *
 */
public class Utils {

  /**
   * Takes an array of things, and return a string representing the concatenation of all these objects.
   */
  public static String mkString(Object ... objects){
    StringBuilder builder = new StringBuilder();
    for (Object o : objects) {
      if (o == null)
        builder.append("null");
      else
        builder.append(o.toString());
    }
    return builder.toString();
  }
}
