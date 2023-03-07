package br.edu.ifsc.javargexamples;

// import java.util.function.Supplier;

/**
 *
 * @author douglas
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


// INTERFACE<String> a = () -> "hello";
// INTERFACE<Integer> b = () -> 1 + 3 * 472;
// INTERFACE<Double> c = () -> 3 + 0.14159;
// INTERFACE<Byte> e = () -> 41;
// INTERFACE<Boolean> j = () -> new br.edu.ifsc.javargexamples.A(2, 180, true).a3;
// INTERFACE<br.edu.ifsc.javargexamples.C> f = ()-> new br.edu.ifsc.javargexamples.C();
// INTERFACE<br.edu.ifsc.javargexamples.C> g = () -> new br.edu.ifsc.javargexamples.C();
// INTERFACE<br.edu.ifsc.javargexamples.C> h = () -> new br.edu.ifsc.javargexamples.C();
// INTERFACE<br.edu.ifsc.javargexamples.C> k = () -> new br.edu.ifsc.javargexamples.C();
// INTERFACE<br.edu.ifsc.javargexamples.AextendExtend> i = () -> new br.edu.ifsc.javargexamples.AextendExtend(new br.edu.ifsc.javargexamples.A(5389, -9740, false).getA1(), 2);
// INTERFACE<br.edu.ifsc.javargexamples.C> c = () -> new br.edu.ifsc.javargexamples.C();
