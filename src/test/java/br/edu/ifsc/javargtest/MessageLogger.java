/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifsc.javargtest;

/**
 *
 * @author samuel
 * 
 */
public class MessageLogger {

  public enum Severity {
    ERROR, //         ERROR,
    WARN, //         WARN,
    INFO, //         INFO,
    DEBUG, //         DEBUG,
    TRACE, //         TRACE
  }

  public static Severity logLevel = Severity.ERROR;

  public static void showMessage(Severity s, String msg) {
    if (logLevel.ordinal() >= s.ordinal()) {
      System.out.println(msg);
    }
  }
}
