package br.edu.ifsc.javargtest;

import br.edu.ifsc.javargtest.MessageLogger.Severity;
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

  private static final String SKELETON_PATH = "src/main/java/br/edu/ifsc/javarg/MainClass.java";

  private CompilationUnit mSkeleton;

  private ClassManager mCT;

  private InterfaceTable mIT;

  private TypeGenerator mBase;

  private JRGCore mCore;

  private JRGStmt mStmt;

  private JRGOperator mOperator;

  private Map<String, String> mCtx;

  public MainTests() throws FileNotFoundException, IOException {
    mSkeleton = StaticJavaParser.parse(new File(SKELETON_PATH));

    dumpAST();

    MessageLogger.logLevel = Severity.MSG_ERROR;
  }

  @BeforeTry
  public void createObjects() {
    List<String> imp = new ArrayList<>();
    imp = loadImports();

    mCT = new ClassManager(imp);

    mIT = new InterfaceTable(imp);

    mBase = new TypeGenerator(mCT);

    mCore = new JRGCore(mCT, mBase);

    mStmt = new JRGStmt(mCT, mBase, mCore);

    mOperator = new JRGOperator(mCT, mBase, mCore);

    mCtx = new HashMap<String, String>();
  }

  // Auxiliary methods
  private List<String> loadImports() {
    NodeList<ImportDeclaration> imports = mSkeleton.getImports();

    List<String> list = new ArrayList<>();

    Iterator<ImportDeclaration> it = imports.iterator();
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
        FileWriter fileWriter = new FileWriter("ast.dot");
        PrintWriter printWriter = new PrintWriter(fileWriter)) {
      printWriter.print(printer.output(mSkeleton));
    }
  }

  private void imprimirDados(CompilationUnit Classe) throws IOException {
    // DotPrinter printer = new DotPrinter(true);

    PrettyPrinter printer = new PrettyPrinter();

    try (
        FileWriter arq = new FileWriter("MainClass.java");
        PrintWriter gravarArq = new PrintWriter(arq)) {
      // gravarArq.print(mSkeleton.toString());
      gravarArq.print(printer.print(Classe));
    }
  }

  public void compila(String arquivo2) throws IOException {
    PrintWriter saida = new PrintWriter(new FileWriter("logCompilacao.txt"));

    int resultadoCompilacao = com.sun.tools.javac.Main.compile(
        new String[] { arquivo2 },
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
   * types can be found at TypeGenerator.java `primitiveTypes()` which
   * then use 'net.jqwik.api.Arbitraries' to fetch all possible types
   *
   */
  // @Example
  boolean checkGenPrimitiveType() {
    Arbitrary<PrimitiveType.Primitive> t = mBase.primitiveTypes();

    Arbitrary<LiteralExpr> e = t.flatMap(tp -> mBase.genPrimitiveType(new PrimitiveType(tp)));

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
  // @Example
  boolean checkGenPrimitiveString() {
    Arbitrary<LiteralExpr> s = mBase.genPrimitiveString();

    System.out.println("Frase gerada: " + s.sample());

    return true;
  }

  /*
   *
   * Generate a new Class or Interface type from `JRGCore.java`
   *
   */
  // @Example
  boolean checkGenObjectCreation() throws ClassNotFoundException {
    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenObjectCreation" + "::inicio");

    ClassOrInterfaceType c = new ClassOrInterfaceType();

    c.setName("br.edu.ifsc.javargexamples.B");

    // Arbitrary<ObjectCreationExpr> e = mCore.genObjectCreation(c);
    Arbitrary<Expression> e = mCore.genObjectCreation(mCtx, c);

    if (e != null) {
      System.out.println("ObjectCreation gerado: " + e.sample().toString());
    } else {
      MessageLogger.showMessage(
          Severity.MSG_ERROR,
          "Não foi possível gerar " + "criação de objeto");
    }

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenObjectCreation::fim");

    return true;
  }

  /*
   *
   * Generate a random Method from `JRGCore.java` ClassManager
   *
   */
  // @Property(tries = 10)
  boolean checkGenMethodInvokation() throws ClassNotFoundException {
    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenMethodInvokation" + "::inicio");

    ClassOrInterfaceType c = new ClassOrInterfaceType();

    c.setName("br.edu.ifsc.javargexamples.B");
    // Arbitrary<MethodCallExpr> e = mCore.genMethodInvokation(c);
    Arbitrary<MethodCallExpr> e = mCore.genMethodInvokation(
        mCtx,
        JavaTypeTranslator.reflectToParserType("int"));

    if (e != null) {
      System.out.println("Method gerado: " + e.sample().toString());
    } else {
      MessageLogger.showMessage(
          Severity.MSG_ERROR,
          "Não foi possível gerar " + "criação do método");
    }

    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenMethodInvokation" + "::fim");

    return true;
  }

  /*
   *
   * Picks a random Method from a list of avaiable methods
   * from `JRGCore.java` using the given type "int" as a parameter
   *
   */
  // @Example
  boolean checkGenCandidatesMethods() throws ClassNotFoundException {
    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenCandidatesMethods" + "::inicio");

    Arbitrary<Method> b = mCore.genCandidatesMethods("int");

    System.out.println("Candidatos Methods: " + b.sample());

    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenCandidatesMethods" + "::fim");

    return true;
  }

  /*
   *
   * Picks a random Field/Attribute from a list of avaiable fields/attributes
   * from `JRGCore.java` using the given type "int" as a parameter
   *
   */
  // @Example
  boolean checkGenCandidatesFields() throws ClassNotFoundException {
    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenCandidatesFields" + "::inicio");

    Arbitrary<Field> b = mCore.genCandidatesField("int");

    System.out.println("Candidatos Fields: " + b.sample());

    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenCandidatesFields:" + ":fim");

    return true;
  }

  /*
   *
   * Picks a random Constructor from a list of avaiable constructors
   * from `JRGCore.java` using the given type class as a parameter
   *
   */
  // @Example
  boolean checkGenCandidatesConstructors() throws ClassNotFoundException {
    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenCandidatesConstructors" + "::inicio");

    Arbitrary<Constructor> b = mCore.genCandidatesConstructors(
        "br.edu." + "ifsc.javargexamples.B");

    System.out.println("Candidatos Constructors: " + b.sample());

    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenCandidatesConstructors" + "::fim");

    return true;
  }

  /*
   *
   * Generate a selection of random expressions using attributes and literal
   * integers
   *
   */
  // @Property(tries = 10)
  boolean checkGenExpression() {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenExpression::inicio");

    try {
      Arbitrary<Expression> e = mCore.genExpression(
          mCtx,
          JavaTypeTranslator.reflectToParserType("int"));
      System.out.println("Expressão gerada: " + e.sample());
    } catch (Exception ex) {
      System.out.println("Erro: " + ex.getMessage());
      return false;
    }

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenExpression::fim");

    return true;
  }

  /*
   *
   * Generate a statement for accessing an attribute of type 'int'
   * from `JRGCore.java` using 'tname: int' as parameter
   *
   */
  // @Example
  boolean checkGenAttributeAccess() throws ClassNotFoundException {
    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenAtributteAcess" + "::inicio");

    Arbitrary<FieldAccessExpr> e = mCore.genAttributeAccess(
        mCtx,
        JavaTypeTranslator.reflectToParserType("String"));

    System.out.println("Acesso gerado: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenExpression::fim");

    return true;
  }

  /*
   *
   * Generate a Cast expression for convertion
   * from `JRGCore.java` using a Class as parameter
   *
   */
  // @Example
  boolean checkGenUpCast() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenUpCast" + "::inicio");

    Arbitrary<CastExpr> e = mCore.genUpCast(
        mCtx,
        JavaTypeTranslator.reflectToParserType(
            "br.edu.ifsc." + "javargexamples.Aextend"));

    System.out.println("CheckGenUpCast: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenUpCast" + "::final");

    return true;
  }

  /*
   *
   * !ERROR "Jwqik empty set of values"
   *
   */
  // @Example
  boolean checkGenVar() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenVar" + "::inicio");

    Arbitrary<NameExpr> e = mCore.genVar(
        mCtx,
        JavaTypeTranslator.reflectToParserType("int"));

    System.out.println("checkGenVar: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenVar" + "::final");
    return true;
  }

  /*
   *
   * Get all super() from subsequents class inheritance calls
   * from ClassManager given a Class path as a parameter
   *
   */
  // @Example
  boolean checkSuperTypes() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkSuperTypes" + "::inicio");

    List<Class> b = mCT.superTypes(
        "br.edu.ifsc." + "javargexamples.AextendExtend");

    b.forEach(i -> {
      System.out.println("SuperTypes: " + i);
    });

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkSuperTypes" + "::final");

    return true;
  }

  /*
   *
   * Get the subTypes from a given class object
   * from ClassManager given a Class path as a parameter
   *
   */
  // @Example
  boolean checkSubTypes() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkSubTypes" + "::inicio");

    List<Class> b = mCT.subTypes("br.edu.ifsc." + "javargexamples.A");

    b.forEach(i -> {
      System.out.println("subTypes: " + i.toString());
    });

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkSubTypes" + "::final");

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
  // @Example
  boolean checkSubTypes2() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkSubTypes" + "::inicio");

    List<Class> b = mCT.subTypes2("br.edu.ifsc." + "javargexamples.A");

    b.forEach(i -> {
      System.out.println("subTypes: " + i.toString());
    });

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkSubTypes" + "::final");

    return true;
  }

  /*
   *
   * Get the candidates for up-casting
   * from `JRGCore.java` using the class object as a parameter
   *
   */
  // @Example
  boolean checkGenCandidateUpCast() throws ClassNotFoundException {
    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenCandidateUpCast" + "::inicio");

    Arbitrary<Class> b = mCore.genCandidateUpCast(
        "br.edu.ifsc." + "javargexamples.A");

    System.out.println("Candidatos UpCast: " + b.sample().getName());

    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
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
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenBlockStmt::inicio");

    Arbitrary<BlockStmt> e = mStmt.genBlockStmt(mCtx);

    System.out.println("BlockStmt: " + e.sample());

    ClassOrInterfaceDeclaration classe = mSkeleton
        .getClassByName("MainClass")
        .get();

    List<MethodDeclaration> ms = classe.getMethods();

    MethodDeclaration m = ms.get(0);

    m.setBody(e.sample());

//    imprimiDados(mSkeleton);;

    compila("MainClass.java");

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenBlockStmt::fim");

    return true;
  }

  /*
   *
   * Generate a variety of variable declarations and assignments
   * using arbitrary data types and a valid string for the variable
   * label from `JRGStmt.java`
   *
   */
  // @Property(tries = 100)
  boolean checkGenVarDeclAssign() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenVarDeclaration::inicio");

    Arbitrary<VariableDeclarationExpr> e = mStmt.genVarDeclAssign(mCtx);

    System.out.println("checkGengenVarDeclaration: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenVarDeclaration::fim");

    return true;
  }

  /*
   *
   * Generate a variety of ONLY variable declarations using arbitrary
   * data types and a valid string for the variable label from `JRGStmt.java`
   *
   */
  // @Property(tries = 100)
  boolean checkGenVarDecl() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenVarDeclaration::inicio");

    Arbitrary<VariableDeclarationExpr> e = mStmt.genVarDecl(mCtx);

    System.out.println("checkGengenVarDeclaration: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenVarDeclaration::fim");

    return true;
  }

  /*
   *
   * Generate If and Else statements from `JRGStmt.java`
   *
   */
  // @Example
  boolean checkGenIfStmt() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenIfStmt::inicio");

    Arbitrary<IfStmt> e = mStmt.genIfStmt(mCtx);

    System.out.println("checkGenIfStmt: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenIfStmt::fim");

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
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkWhileStmt::inicio");

    Arbitrary<WhileStmt> e = mStmt.genWhileStmt(mCtx);

    System.out.println("checkWhileStmt: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkWhileStmt::fim");

    return true;
  }

  /*
   *
   * Generate conditional statements and a MainClass as well as functions
   * inside it with statements within itself from `JRGStmt.java`
   *
   */
  // @Example
  boolean checkGenStatement() throws ClassNotFoundException, IOException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenStatement::inicio");

    Arbitrary<Statement> e = mStmt.genStatement(mCtx);

    System.out.println("checkGenStatement: " + e.sample());

    System.out.println(mSkeleton.getClassByName("MainClass"));

    ClassOrInterfaceDeclaration classe = mSkeleton
        .getClassByName("MainClass")
        .get();

    classe.addMethod(
        "main",
        Modifier.publicModifier().getKeyword(),
        Modifier.Keyword.STATIC);
    // mSkeleton.addInterface(e.sample().toString());

    classe.addInitializer().addAndGetStatement(e.sample());

    // imprimiDados(mSkeleton.addClass(classe.toString()));

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenStatement::fim");

    return true;
  }

  /*
   *
   * Generate a Logical statement from `JRGStmt.java`
   *
   */
  // @Example
  boolean checkGenExpressionStmt() {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenExpressionStmt::inicio");

    Arbitrary<ExpressionStmt> e = mStmt.genExpressionStmt(mCtx);

    System.out.println("checkGenExpressionStmt: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenExpressionStmt::fim");

    return true;
  }

  /*
   *
   * Generate a Logical Expressions from `JRGOperator.java`
   *
   */
  // @Example
  // @Property(tries = 10)
  boolean checkGenLogiExpression() {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenLogiExpression::inicio");

    Arbitrary<BinaryExpr> e = mOperator.genLogiExpression(mCtx);

    System.out.println("checkGenLogiExpression: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenLogiExpression::fim");

    return true;
  }

  /*
   *
   * Generate a Relational Expressions from `JRGOperator.java`
   * Using comparision signs as <, ==, >= for example
   *
   */
  // @Example
  // @Property(tries = 10)
  boolean checkGenRelaExpression() {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenRelaExpression::inicio");

    Arbitrary<BinaryExpr> e = mOperator.genRelaExpression(mCtx);

    System.out.println("checkGenRelaExpression: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenRelaExpression::fim");

    return true;
  }

  /*
   *
   * Generate a Arithmetic Expressions from `JRGOperator.java`
   * Using %, ==, +, -, * between two or more statements for example
   *
   */
  // @Example
  // @Property(tries = 10)
  boolean checkGenArithExpression() {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenArithExpression::inicio");

    Arbitrary<BinaryExpr> e = mOperator.genArithExpression(
        mCtx,
        JavaTypeTranslator.reflectToParserType("int"));

    System.out.println("checkGenArithExpression: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenArithExpression::fim");

    return true;
  }

  /*
   *
   * Generate statements in a array format from `JRGStmt.java`
   *
   */
  // @Example
  boolean checkGenStatementList() {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenStatementList::inicio");

    Arbitrary<NodeList<Statement>> e = mStmt.genStatementList(mCtx);

    System.out.println("checkGenStatementList: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenStatementList::fim");

    return true;
  }

  /*
   *
   * Generate statements for variable declaration
   * From `JRGStmt.java`
   *
   */
  // @Property(tries = 10)
  boolean checkGenVarDeclarationStmt() throws ClassNotFoundException {
    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenVarDeclarationStmt::inicio");

    Arbitrary<ExpressionStmt> e = mStmt.genVarDeclarationStmt(mCtx);

    System.out.println("checkGenVarDeclarationStmt: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenVarDeclarationStmt::fim");

    return true;
  }

  /*
   *
   * !ERROR - empty set of values
   *
   */
  // @Example
  boolean checkGenVarAssingStmt() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenVarAssingStmt::inicio");

    Arbitrary<VariableDeclarationExpr> e = mStmt.genVarAssingStmt(mCtx);

    System.out.println("checkGenVarAssingStmt: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenVarAssingStmt::fim");

    return true;
  }

  /*
   *
   * !ERROR - empty set of values
   *
   */
  // @Example
  boolean checkGenTypeAssingStmt() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenTypeAssingStmt::inicio");

    Arbitrary<AssignExpr> e = mStmt.genTypeAssingStmt(mCtx);

    System.out.println("checkGenTypeAssingStmt: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenTypeAssingStmt::fim");

    return true;
  }

  /*
   *
   * Generate For Loopings expressions with statements within
   * the loop using `JRGStmt.java`
   *
   */
  // @Example
  // @Property(tries=4)
  boolean checkGenFor() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenTypeAssingStmt::inicio");

    Arbitrary<ForStmt> e = mStmt.genForStmt(mCtx);
    // mStmt.genForStmt(mCtx);
    System.out.println("checkGenTypeAssingStmt: " + e.sample());

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenTypeAssingStmt::fim");

    return true;
  }

  /*
   *
   * !IDK = Generate a selection of variable declarations and assignments
   *
   */
  // @Example
  boolean checkGenList() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenTypeAssingStmt::inicio");

    List<Statement> e = mStmt.genList(mCtx);
    // mStmt.genForStmt(mCtx);
    System.out.println("checkGenTypeAssingStmt: " + e.get(0));

    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenTypeAssingStmt::fim");

    return true;
  }

  /*
   *
   * Generate Lambda expressions from `JRGCore.java`
   *
   */
  //@Example
  boolean checkGenLambdaExpr() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenLambdaExpr::inicio");

    Arbitrary<LambdaExpr> e = mCore.genLambdaExpr(mCtx);
    String expr = e.sample().toString();

    List<String> asd = mIT.getCandidateInterfaces(
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
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkGenLambdaExpr::fim");
    return true;
  }

  // @Example
  boolean checkInterfaceTable() throws ClassNotFoundException {
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkInterfaceTable" + "::inicio");
    mIT.get_methods();
    mIT.get_methods_info();
    mIT.show_imports();
    MessageLogger.showMessage(Severity.MSG_XDEBUG, "checkInterfaceTable" + "::fim");
    return true;
  }

  @Property(tries = 1)
  boolean checkGenCandidatesInterfaces() throws ClassNotFoundException {
    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenCandidatesInterfaces" + "::inicio");

    List<String> candidates = mIT.getCandidateInterfaces(loadImports().iterator().next());

    System.out.println("Candidatos Interfaces: " + candidates.toString());

    MessageLogger.showMessage(
        Severity.MSG_XDEBUG,
        "checkGenCandidatesInterfaces" + "::fim");

    return true;
  }
}
