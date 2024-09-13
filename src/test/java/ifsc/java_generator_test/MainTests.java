package ifsc.java_generator_test;

import ifsc.java_generator_test.MessageLogger.Severity;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.printer.DotPrinter;
import com.github.javaparser.printer.PrettyPrinter;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeTry;

/**
 *
 * @author samuel
 *
 */
public class MainTests {

  private static final String SKELETON_PATH = "src/main/java/ifsc/java_generator/MainClass.java";

  private CompilationUnit mSkeleton;

  private ClassManager mCT;

  private InterfaceTable mIT;

  private TypeGenerator mBase;

  private CoreExpressionGenerator mCore;

  private JRGStmt mStmt;

  private ExpressionGenerator mOperator;

  private Map < String, String > mCtx;

  public MainTests() throws FileNotFoundException, IOException {
    mSkeleton = StaticJavaParser.parse(new File(SKELETON_PATH));

    dumpAST();

    MessageLogger.setLogLevel(Severity.ERROR);
  }

  @BeforeTry
  public void createObjects() {
    List < String > imp = new ArrayList < > ();
    imp = loadImports();

    mCT = new ClassManager(imp);

    mIT = new InterfaceTable(imp);

    mBase = new TypeGenerator(mCT);

    mCore = new CoreExpressionGenerator(mCT, mBase);

    mStmt = new JRGStmt(mCT, mBase, mCore);

    mOperator = new ExpressionGenerator(mCT, mBase, mCore);

    mCtx = new HashMap < String, String > ();
  }

  // Auxiliary methods
  private List < String > loadImports() {
    NodeList < ImportDeclaration > imports = mSkeleton.getImports();

    List < String > list = new ArrayList < > ();

    Iterator < ImportDeclaration > it = imports.iterator();
    while (it.hasNext()) {
      ImportDeclaration i = it.next();
      list.add(i.getName().asString());
    }

    System.out.println("DEBUG: MainTests (loadImports) -> " + list.toString());
    return list;
  }

  /*
   *
   * Write AST - Arbitrary Sintax Tree to file
   * using FileWriter output filename is `ast.dot`
   *
   */
  private void dumpAST() throws IOException {
    DotPrinter printer = new DotPrinter(true);

    try (
      FileWriter fileWriter = new FileWriter("ast.dot"); PrintWriter printWriter = new PrintWriter(fileWriter)) {
      printWriter.print(printer.output(mSkeleton));
    }
  }

  private void showData(CompilationUnit cl) throws IOException {
    // DotPrinter printer = new DotPrinter(true);

    PrettyPrinter printer = new PrettyPrinter();

    try (
      FileWriter arq = new FileWriter("MainClass.java"); PrintWriter writer = new PrintWriter(arq)) {
      // writer.print(mSkeleton.toString());
      writer.print(printer.print(cl));
    }
  }

  public void compile(String file2) throws IOException {
    PrintWriter saida = new PrintWriter(new FileWriter("compilation_log.txt"));

    int compilationResult = com.sun.tools.javac.Main.compile(
      new String[] {
        file2
      },
      saida);
  }

  /**********************************
   * *
   * Tests start here *
   * *
   *
   * @throws ClassNotFoundException
   **********************************/

  /*
   *
   * Generate a random primitive type all available primitive
   * types can be found at TypeGenerator.java `generatePrimitiveTypes()` which
   * then use 'net.jqwik.api.Arbitraries' to fetch all possible types
   *
   */
  // @Example
  boolean checkGeneratePrimitiveType() {
    Arbitrary < PrimitiveType.Primitive > t = mBase.generatePrimitiveTypes();

    Arbitrary < LiteralExpr > e = t.flatMap(tp -> mBase.generatePrimitiveType(new PrimitiveType(tp)));

    System.out.println(
      "Expressão gerada (tipo primitivo): " + e.sample().toString());

    return true;
  }

  /*
   *
   * Generate a random String literal with min_length = 1
   * and max_length = 5, ranging chars from 'a' to 'z'
   *
   */
  @Example
  boolean CheckGeneratePrimitiveString() {
    Arbitrary < LiteralExpr > s = mBase.generatePrimitiveString();

    System.out.println("Frase gerada: " + s.sample());

    return true;
  }

  /*
   *
   * Generate a new Class or Interface type from `CoreExpressionGenerator.java`
   *
   */
  @Example
  boolean checkGenObjectCreation() throws ClassNotFoundException {
    MessageLogger.log(
      Severity.TRACE,
      "checkGenObjectCreation" + "::start");

    ClassOrInterfaceType c = new ClassOrInterfaceType();

    c.setName("ifsc.java_generator_examples.B");

    // Arbitrary<ObjectCreationExpr> e = mCore.genObjectCreation(c);
    Arbitrary < Expression > e = mCore.genObjectCreation(mCtx, c);

    if (e != null) {
      System.out.println("ObjectCreation gerado: " + e.sample().toString());
    } else {
      MessageLogger.log(
        Severity.ERROR,
        "Não foi possível gerar " + "criação de objeto");
    }

    MessageLogger.log(Severity.TRACE, "checkGenObjectCreation::fim");

    return true;
  }

  /*
   *
   * Generate a random Method from `CoreExpressionGenerator.java` ClassManager
   *
   */
  @Property(tries = 2)
  boolean genMethodInvocation() throws ClassNotFoundException {
    MessageLogger.log(
      Severity.TRACE,
      "genMethodInvocation" + "::start");

    ClassOrInterfaceType c = new ClassOrInterfaceType();

    c.setName("ifsc.java_generator_examples.B");
    // Arbitrary<MethodCallExpr> e = mCore.genMethodInvocation(c);
    Arbitrary < MethodCallExpr > e = mCore.genMethodInvocation(
      mCtx,
      JavaTypeTranslator.convertToParserType("int"));

    if (e != null) {
      System.out.println("Method gerado: " + e.sample().toString());
    } else {
      MessageLogger.log(
        Severity.ERROR,
        "Não foi possível gerar " + "criação do método");
    }

    MessageLogger.log(
      Severity.TRACE,
      "genMethodInvocation" + "::fim");

    return true;
  }

  /*
   *
   * Picks a random Method from a list of avaiable methods
   * from `CoreExpressionGenerator.java` using the given type "int" as a parameter
   *
   */
  @Example
  boolean checkGenCandidatesMethods() throws ClassNotFoundException {
    MessageLogger.log(
      Severity.TRACE,
      "checkGenCandidatesMethods" + "::start");

    Arbitrary < Method > b = mCore.genCandidatesMethods("int");

    System.out.println("Candidates Methods: " + b.sample());

    MessageLogger.log(
      Severity.TRACE,
      "checkGenCandidatesMethods" + "::fim");

    return true;
  }

  /*
   *
   * Picks a random Field/Attribute from a list of avaiable fields/attributes
   * from `CoreExpressionGenerator.java` using the given type "int" as a parameter
   *
   */
  @Example
  boolean checkGenCandidatesFields() throws ClassNotFoundException {
    MessageLogger.log(
      Severity.TRACE,
      "checkGenCandidatesFields" + "::start");

    Arbitrary < Field > b = mCore.genCandidatesField("int");

    System.out.println("Candidates Fields: " + b.sample());

    MessageLogger.log(
      Severity.TRACE,
      "checkGenCandidatesFields:" + ":fim");

    return true;
  }

  /*
   *
   * Picks a random Constructor from a list of avaiable constructors
   * from `CoreExpressionGenerator.java` using the given type class as a parameter
   *
   */
  @Example
  boolean checkGenCandidatesConstructors() throws ClassNotFoundException {
    MessageLogger.log(
      Severity.TRACE,
      "checkGenCandidatesConstructors" + "::start");

    Arbitrary < Constructor > b = mCore.genCandidatesConstructors(
      "" + "ifsc.java_generator_examples.B");

    System.out.println("Candidates Constructors: " + b.sample());

    MessageLogger.log(
      Severity.TRACE,
      "checkGenCandidatesConstructors" + "::fim");

    return true;
  }

  /*
   *
   * Generate a selection of random expressions using attributes and literal
   * integers
   *
   */
  @Property(tries = 2)
  boolean checkGenExpression() {
    MessageLogger.log(Severity.TRACE, "checkGenExpression::start");

    try {
      Arbitrary < Expression > e = mCore.genExpression(
        mCtx,
        JavaTypeTranslator.convertToParserType("int"));
      System.out.println("Expressão gerada: " + e.sample());
    } catch (Exception ex) {
      System.out.println("Erro: " + ex.getMessage());
      return false;
    }

    MessageLogger.log(Severity.TRACE, "checkGenExpression::fim");

    return true;
  }

  /*
   *
   * Generate a statement for accessing an attribute of type 'int'
   * from `CoreExpressionGenerator.java` using 'tname: int' as parameter
   *
   */
  // @Example
  boolean checkGenAttributeAccess() throws ClassNotFoundException {
    MessageLogger.log(
      Severity.TRACE,
      "checkGenAttributeAccess" + "::start");

    Arbitrary < FieldAccessExpr > e = mCore.genAttributeAccess(
      mCtx,
      JavaTypeTranslator.convertToParserType("String"));

    System.out.println("Access generated: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenExpression::fim");

    return true;
  }

  /*
   *
   * Generate a Cast expression for conversion
   * from `CoreExpressionGenerator.java` using a Class as parameter
   *
   */
  // @Example
  boolean checkGenUpCast() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkGenUpCast" + "::start");

    Arbitrary < CastExpr > e = mCore.genUpCast(
      mCtx,
      JavaTypeTranslator.convertToParserType(
        "ifsc." + "java_generator_examples.Aextend"));

    System.out.println("CheckGenUpCast: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenUpCast" + "::final");

    return true;
  }

  /*
   *
   * !ERROR "Jwqik empty set of values"
   *
   */
  // @Example
  boolean checkGenVar() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkGenVar" + "::start");

    Arbitrary < NameExpr > e = mCore.genVar(
      mCtx,
      JavaTypeTranslator.convertToParserType("int"));

    System.out.println("checkGenVar: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenVar" + "::final");
    return true;
  }

  /*
   *
   * Get all super() from subsequent class inheritance calls
   * from ClassManager given a Class path as a parameter
   *
   */
  @Example
  boolean checkSuperTypes() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkSuperTypes" + "::start");

    List < Class > b = mCT.superTypes(
      "ifsc." + "java_generator_examples.AextendExtend");

    b.forEach(i -> {
      System.out.println("SuperTypes: " + i);
    });

    MessageLogger.log(Severity.TRACE, "checkSuperTypes" + "::final");

    return true;
  }

  /*
   *
   * Get the subTypes from a given class object
   * from ClassManager given a Class path as a parameter
   *
   */
  @Example
  boolean checkSubTypes() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkSubTypes" + "::start");

    List < Class > b = mCT.subTypes("ifsc." + "java_generator_examples.A");

    b.forEach(i -> {
      System.out.println("subTypes: " + i.toString());
    });

    MessageLogger.log(Severity.TRACE, "checkSubTypes" + "::final");

    return true;
  }

  /*
   *
   * Get all superTypes from subsequent class calls
   * from ClassManager given a Class path as a parameter
   *
   * In fact, should be called `checkSuperTypes()`
   *
   */
  @Example
  boolean checkSubTypes2() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkSubTypes" + "::start");

    List < Class > b = mCT.subTypes2("ifsc." + "java_generator_examples.A");

    b.forEach(i -> {
      System.out.println("subTypes: " + i.toString());
    });

    MessageLogger.log(Severity.TRACE, "checkSubTypes" + "::final");

    return true;
  }

  /*
   *
   * Get the candidates for up-casting
   * from `CoreExpressionGenerator.java` using the class object as a parameter
   *
   */
  // @Example
  boolean checkGenCandidateUpCast() throws ClassNotFoundException {
    MessageLogger.log(
      Severity.TRACE,
      "checkGenCandidateUpCast" + "::start");

    Arbitrary < Class > b = mCore.genCandidateUpCast(
      "ifsc." + "java_generator_examples.A");

    System.out.println("Candidates UpCast: " + b.sample().getName());

    MessageLogger.log(
      Severity.TRACE,
      "checkGenCandidateUpCast" + "::final");

    return true;
  }

  /*
   *
   * Generate a BlockStmt containing a random program
   * from `JRGStmt.java` using the imports from `MainClass.java`
   * the code is generated from a list variables up to conditional statements
   *
   * # A further improvement would be to write this BlockStmt to a file instead
   * of writing to console everytime the test is ran
   *
   */
  @Example
  boolean checkGenBlockStmt() throws ClassNotFoundException, IOException {
    MessageLogger.log(Severity.TRACE, "checkGenBlockStmt::start");

    Arbitrary < BlockStmt > e = mStmt.genBlockStmt(mCtx);

    System.out.println("BlockStmt: " + e.sample());

    ClassOrInterfaceDeclaration cl = mSkeleton
      .getClassByName("MainClass")
      .get();

    List < MethodDeclaration > ms = cl.getMethods();

    MethodDeclaration m = ms.get(0);

    m.setBody(e.sample());

    compile("MainClass.java");

    MessageLogger.log(Severity.TRACE, "checkGenBlockStmt::fim");

    return true;
  }

  /*
   *
   * Generate a variety of variable declarations and assignments
   * using arbitrary data types and a valid string for the variable
   * label from `JRGStmt.java`
   *
   */
  @Property(tries = 2)
  boolean checkGenVarDeclAssign() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkGenVarDeclaration::start");

    Arbitrary < VariableDeclarationExpr > e = mStmt.genVarDeclAssign(mCtx);

    System.out.println("checkGenVarDeclAssign: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenVarDeclaration::fim");

    return true;
  }

  /*
   *
   * Generate a variety of ONLY variable declarations using arbitrary
   * data types and a valid string for the variable label from `JRGStmt.java`
   *
   */
  @Property(tries = 2)
  boolean checkGenVarDecl() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkGenVarDeclaration::start");

    Arbitrary < VariableDeclarationExpr > e = mStmt.genVarDecl(mCtx);

    System.out.println("checkGenVarDecl: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenVarDeclaration::fim");

    return true;
  }

  /*
   *
   * Generate If and Else statements from `JRGStmt.java`
   *
   */
  @Example
  boolean checkGenIfStmt() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkGenIfStmt::start");

    Arbitrary < IfStmt > e = mStmt.genIfStmt(mCtx);

    System.out.println("checkGenIfStmt: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenIfStmt::fim");

    return true;
  }

  /*
   *
   * !ERROR - It's using a binaryExpr and looping conditional for some reason
   * Idk if it is supposed to be like this
   *
   */
  // @Example
  boolean checkWhileStmt() {
    MessageLogger.log(Severity.TRACE, "checkWhileStmt::start");

    Arbitrary < WhileStmt > e = mStmt.genWhileStmt(mCtx);

    System.out.println("checkWhileStmt: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkWhileStmt::fim");

    return true;
  }

  /*
   *
   * Generate conditional statements and a MainClass as well as functions
   * inside it with statements within itself from `JRGStmt.java`
   *
   */
  @Example
  boolean checkGenStatement() throws ClassNotFoundException, IOException {
    MessageLogger.log(Severity.TRACE, "checkGenStatement::start");

    Arbitrary < Statement > e = mStmt.genStatement(mCtx);

    System.out.println("checkGenStatement: " + e.sample());

    System.out.println(mSkeleton.getClassByName("MainClass"));

    ClassOrInterfaceDeclaration cl = mSkeleton
      .getClassByName("MainClass")
      .get();

    cl.addMethod(
      "main",
      Modifier.publicModifier().getKeyword(),
      Modifier.Keyword.STATIC);
    // mSkeleton.addInterface(e.sample().toString());

    cl.addInitializer().addAndGetStatement(e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenStatement::fim");

    return true;
  }

  /*
   *
   * Generate a Logical statement from `JRGStmt.java`
   *
   */
  @Example
  boolean checkGenExpressionStmt() {
    MessageLogger.log(Severity.TRACE, "checkGenExpressionStmt::start");

    Arbitrary < ExpressionStmt > e = mStmt.genExpressionStmt(mCtx);

    System.out.println("checkGenExpressionStmt: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenExpressionStmt::fim");

    return true;
  }

  /*
   *
   * Generate a Logical Expressions from `ExpressionGenerator.java`
   *
   */
  // @Example
  @Property(tries = 2)
  boolean checkGenLogicExpression() {
    MessageLogger.log(Severity.TRACE, "checkGenLogicExpression::start");

    Arbitrary < BinaryExpr > e = mOperator.genLogicExpression(mCtx);

    System.out.println("checkGenLogicExpression: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenLogicExpression::fim");

    return true;
  }

  /*
   *
   * Generate a Relational Expressions from `ExpressionGenerator.java`
   * Using comparision signs as <, ==, >= for example
   *
   */
  // @Example
  @Property(tries = 2)
  boolean checkGenRelaExpression() {
    MessageLogger.log(Severity.TRACE, "checkGenRelaExpression::start");

    Arbitrary < BinaryExpr > e = mOperator.genRelaExpression(mCtx);

    System.out.println("checkGenRelaExpression: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenRelaExpression::fim");

    return true;
  }

  /*
   *
   * Generate a Arithmetic Expressions from `ExpressionGenerator.java`
   * Using %, ==, +, -, * between two or more statements for example
   *
   */
  // @Example
  @Property(tries = 2)
  boolean checkGenArithExpression() {
    MessageLogger.log(Severity.TRACE, "checkGenArithExpression::start");

    Arbitrary < BinaryExpr > e = mOperator.genArithExpression(
      mCtx,
      JavaTypeTranslator.convertToParserType("int"));

    System.out.println("checkGenArithExpression: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenArithExpression::fim");

    return true;
  }

  /*
   *
   * Generate statements in a array format from `JRGStmt.java`
   *
   */
  @Example
  boolean checkGenStatementList() {
    MessageLogger.log(Severity.TRACE, "checkGenStatementList::start");

    Arbitrary < NodeList < Statement >> e = mStmt.genStatementList(mCtx);

    System.out.println("checkGenStatementList: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenStatementList::fim");

    return true;
  }

  /*
   *
   * Generate statements for variable declaration
   * From `JRGStmt.java`
   *
   */
  @Property(tries = 2)
  boolean checkGenVarDeclarationStmt() throws ClassNotFoundException {
    MessageLogger.log(
      Severity.TRACE,
      "checkGenVarDeclarationStmt::start");

    Arbitrary < ExpressionStmt > e = mStmt.genVarDeclarationStmt(mCtx);

    System.out.println("checkGenVarDeclarationStmt: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenVarDeclarationStmt::fim");

    return true;
  }

  /*
   *
   * !ERROR - empty set of values
   *
   */
  // @Example
  boolean checkGenVarAssignStmt() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkGenVarAssignStmt::start");

    Arbitrary < VariableDeclarationExpr > e = mStmt.genVarAssignStmt(mCtx);

    System.out.println("checkGenVarAssignStmt: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenVarAssignStmt::fim");

    return true;
  }

  /*
   *
   * !ERROR - empty set of values
   *
   */
  // @Example
  boolean checkGenTypeAssignStmt() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkGenTypeAssignStmt::start");

    Arbitrary < AssignExpr > e = mStmt.genTypeAssignStmt(mCtx);

    System.out.println("checkGenTypeAssignStmt: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenTypeAssignStmt::fim");

    return true;
  }

  /*
   *
   * Generate For Looping expressions with statements within
   * the loop using `JRGStmt.java`
   *
   */
  // @Example
  @Property(tries=4)
  boolean checkGenFor() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkGenFor::start");

    Arbitrary < ForStmt > e = mStmt.genForStmt(mCtx);
    // mStmt.genForStmt(mCtx);
    System.out.println("checkGenFor: " + e.sample());

    MessageLogger.log(Severity.TRACE, "checkGenFor::fim");

    return true;
  }

  /*
   *
   * !IDK = Generate a selection of variable declarations and assignments
   *
   */
  @Example
  boolean checkGenList() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkGenList::start");

    List < Statement > e = mStmt.genList(mCtx);
    // mStmt.genForStmt(mCtx);
    System.out.println("checkGenList: " + e.get(0));

    MessageLogger.log(Severity.TRACE, "checkGenList::fim");

    return true;
  }

  /*
   *
   * Generate Lambda expressions from `CoreExpressionGenerator.java`
   *
   */
  // @Example
  boolean checkGenLambdaExpr() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkGenLambdaExpr::start");

    Arbitrary < LambdaExpr > e = mCore.genLambdaExpr(mCtx);
    String expr = e.sample().toString();

    List < String > asd = mIT.getCandidateInterfaces(
      loadImports().iterator().next());

    // System.out.println(expr.toString());
    // for (String a : asd) {
    // System.out.println(a);
    // }

    String ret = asd.toString().replace("[", "").replace("]", "");

    for (int i = 0; i < expr.split(" ").length; i++) {
      switch (i) {
      case 0:
        ret = ret.concat("<" + expr.split(" ")[i] + ">");
        break;
      case 1:
        ret = ret.concat(" " + expr.split(" ")[i] + " ");
        break;
      case 2:
        ret = ret.concat("= () ->");
        break;
      default:
        ret = ret.concat(" " + expr.split(" ")[i]);
        break;
      }
    }
    ret = ret.concat(";");
    // System.out.println("checkGenLambdaExpr: " + expr);
    System.out.println("checkGenLambdaExpr: " + ret);
    MessageLogger.log(Severity.TRACE, "checkGenLambdaExpr::fim");
    return true;
  }

  @Example
  boolean checkInterfaceTable() throws ClassNotFoundException {
    MessageLogger.log(Severity.TRACE, "checkInterfaceTable" + "::start");
    mIT.printMethods();
    mIT.printMethodDetails();
    mIT.showImports();
    MessageLogger.log(Severity.TRACE, "checkInterfaceTable" + "::fim");
    return true;
  }

  @Property(tries = 1)
  boolean checkGenCandidatesInterfaces() throws ClassNotFoundException {
    MessageLogger.log(
      Severity.TRACE,
      "checkGenCandidatesInterfaces" + "::start");

    List < String > candidates = mIT.getCandidateInterfaces(loadImports().iterator().next());

    System.out.println("Candidates Interfaces: " + candidates.toString());

    MessageLogger.log(
      Severity.TRACE,
      "checkGenCandidatesInterfaces" + "::fim");

    return true;
  }
}