package ifsc.java_generator_examples;

public class G<T> {

  public int a;
  public boolean b;
  public Double c;

  // T stands for "Type"
  public T t;

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


// <String> a = () -> "hello";
// INTERFACE<Integer> b = () -> 1 + 3 * 472;
// INTERFACE<Double> c = () -> 3 + 0.14159;
// INTERFACE<Byte> e = () -> 41;
// INTERFACE<Boolean> j = () -> new ifsc.java_generator_examples.A(2, 180, true).a3;
// INTERFACE<ifsc.java_generator_examples.C> f = ()-> new ifsc.java_generator_examples.C();
// INTERFACE<ifsc.java_generator_examples.C> g = () -> new ifsc.java_generator_examples.C();
// INTERFACE<ifsc.java_generator_examples.C> h = () -> new ifsc.java_generator_examples.C();
// INTERFACE<ifsc.java_generator_examples.C> k = () -> new ifsc.java_generator_examples.C();
// INTERFACE<ifsc.java_generator_examples.AextendExtend> i = () -> new ifsc.java_generator_examples.AextendExtend(new ifsc.java_generator_examples.A(5389, -9740, false).getA1(), 2);
// INTERFACE<ifsc.java_generator_examples.C> c = () -> new ifsc.java_generator_examples.C();
