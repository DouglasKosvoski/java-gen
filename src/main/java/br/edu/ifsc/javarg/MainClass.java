package br.edu.ifsc.javarg;

import br.edu.ifsc.javargexamples.A;
import br.edu.ifsc.javargexamples.Aextend;
import br.edu.ifsc.javargexamples.AextendExtend;
import br.edu.ifsc.javargexamples.B;
import br.edu.ifsc.javargexamples.C;

/**
 *
 * @author unknown
 *
 */

@FunctionalInterface
interface GFI<t> {
  t func();
}

@SuppressWarnings("all")
public class MainClass {

  public static void main(String[] args) {

    GFI<String> a = () -> "hello";
    GFI<Integer> b = () -> 1 + 3 * 472;
    GFI<Double> c = () -> 3 + 0.14159;
    GFI<Byte> e = () -> 41;
    
    GFI<Boolean> j = () -> new br.edu.ifsc.javargexamples.A(2, 180, true).a3;
    GFI<br.edu.ifsc.javargexamples.C> f = ()-> new br.edu.ifsc.javargexamples.C();
    GFI<br.edu.ifsc.javargexamples.C> g = () -> new br.edu.ifsc.javargexamples.C();
    GFI<br.edu.ifsc.javargexamples.C> h = () -> new br.edu.ifsc.javargexamples.C();
    GFI<br.edu.ifsc.javargexamples.C> k = () -> new br.edu.ifsc.javargexamples.C();
    GFI<br.edu.ifsc.javargexamples.AextendExtend> i = () -> new br.edu.ifsc.javargexamples.AextendExtend(new br.edu.ifsc.javargexamples.A(5389, -9740, false).getA1(), 2);

  }
}
