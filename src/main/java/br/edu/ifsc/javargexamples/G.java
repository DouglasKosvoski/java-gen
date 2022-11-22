package br.edu.ifsc.javargexamples;

/**
 *
 * @author douglas
 *
 */

@FunctionalInterface
interface GenericFunctionalInterface<t> {
  t func();
}

/**
 *
 * @param <T> the type of the value being boxed
 *
 */

public class G<T> {

  int a;
  boolean b;
  Double c;

  // T stands for "Type"
  private T t;

  public void set(T t) {
    this.t = t;
  }

  public T get() {
    return t;
  }

  public int teste() {
    return 1;
  }
}

// GFI<String> a = () -> "hello";
// GFI<Integer> b = () -> 1 + 3 * 472;
// GFI<Double> c = () -> 3 + 0.14159;
// GFI<Byte> e = () -> 41;
// GFI<Boolean> j = () -> new br.edu.ifsc.javargexamples.A(2, 180, true).a3;
// GFI<br.edu.ifsc.javargexamples.C> f = ()-> new br.edu.ifsc.javargexamples.C();
// GFI<br.edu.ifsc.javargexamples.C> g = () -> new br.edu.ifsc.javargexamples.C();
// GFI<br.edu.ifsc.javargexamples.C> h = () -> new br.edu.ifsc.javargexamples.C();
// GFI<br.edu.ifsc.javargexamples.C> k = () -> new br.edu.ifsc.javargexamples.C();
// GFI<br.edu.ifsc.javargexamples.AextendExtend> i = () -> new br.edu.ifsc.javargexamples.AextendExtend(new br.edu.ifsc.javargexamples.A(5389, -9740, false).getA1(), 2);
// GFI<br.edu.ifsc.javargexamples.C> c = () -> new br.edu.ifsc.javargexamples.C();
