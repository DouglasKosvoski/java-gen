package br.edu.ifsc.javargexamples;

/**
 *
 * @author douglas
 *
 */

public class G<T> {

  // T stands for "Type"
  public T t;

  public int a;
  // public boolean b;
  // public Double c;


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