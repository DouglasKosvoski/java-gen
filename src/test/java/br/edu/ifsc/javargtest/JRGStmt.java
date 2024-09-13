package br.edu.ifsc.javargtest;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

public class JRGStmt {
  private ClassManager mCT;

  private static final int FUEL_START = 10;

  private int mFuel;

  private TypeGenerator mBase;

  private List<String> mValidNames;

  private CoreExpressionGenerator mCore;

  private ExpressionGenerator mOperator;

  public static final int MAX_STMT = 5;
  //WHILE_STMT = 2,
  public static final int IF_STMT = 1, FOR_STMT = 2, VAR_DECL_STMT = 3, VAR_DECLARATION_STMT = 4;

  public JRGStmt(ClassManager ct, TypeGenerator base, CoreExpressionGenerator core) {
    mCT = ct;

    mBase = base;

    mCore = core;

    mOperator = new ExpressionGenerator(mCT, mBase, mCore);

    List<String> tempNames = Arrays.asList(
      "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
    );
    mValidNames = new LinkedList<> (tempNames);

    for (String l1: tempNames) {
      for (String l2: tempNames) {
        String letter = "";
        letter = l1 + l2;
        mValidNames.add(letter);
      }
    }

    mFuel = FUEL_START;
  }

  //ExpressionStmt
  @Provide
  public Arbitrary <VariableDeclarationExpr> genVarDecl(Map <String, String> ctx)
  throws ClassNotFoundException {
    MessageLogger.log(MessageLogger.Severity.TRACE, "genVarDeclaration::start");

    Arbitrary <PrimitiveType> pt = mBase
      .generatePrimitiveTypes()
      .map(t -> new PrimitiveType(t));

    Arbitrary <Type> t = Arbitraries.oneOf(mBase.generateClassOrInterfaceTypes(), pt);

    String v = Arbitraries.of(mValidNames).sample();

    Type tp = t.sample();

    ctx.put(v, tp.asString());

    mValidNames.remove(v);

    MessageLogger.log(MessageLogger.Severity.TRACE, "genVarDeclaration::fim");

    //return t.map(tp -> new VariableDeclarationExpr(tp, v));
    return Arbitraries.just(new VariableDeclarationExpr(tp, v));
  }

  @Provide
  public Arbitrary <Statement> genStatement(Map <String, String> ctx) {
    MessageLogger.log(MessageLogger.Severity.TRACE, "genStatement::start");

    try {
      if (mFuel> 0) {
        mFuel--;
        //IF_STMT, WHILE_STMT,FOR_STMT,
        Arbitrary <Integer> s = Arbitraries.of(
          IF_STMT,
          VAR_DECL_STMT,
          VAR_DECLARATION_STMT
        );
        switch (s.sample()) {
        case IF_STMT:
          return Arbitraries.oneOf(genIfStmt(ctx));
          //case WHILE_STMT: return Arbitraries.oneOf(genWhileStmt(ctx));
        case VAR_DECL_STMT:
          return Arbitraries.oneOf(genVarDeclStmt(ctx));
        case VAR_DECLARATION_STMT:
          return Arbitraries.oneOf(genVarDeclarationStmt(ctx));
          //case FOR_STMT: return Arbitraries.oneOf(genForStmt(ctx));
        }
      } else {
        Arbitrary <Integer> s = Arbitraries.of(
          VAR_DECL_STMT,
          VAR_DECLARATION_STMT
        );
        switch (s.sample()) {
        case VAR_DECLARATION_STMT:
          return Arbitraries.oneOf(genVarDeclarationStmt(ctx));
        case VAR_DECL_STMT:
          return Arbitraries.oneOf(genVarDeclStmt(ctx));
        }
      }
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(JRGStmt.class.getName()).log(Level.SEVERE, null, ex);
    }

    MessageLogger.log(MessageLogger.Severity.TRACE, "genStatement::fim");

    return null;
  }

  @Provide
  public Arbitrary <NodeList<Statement >> genStatementList(
    Map <String, String> ctx
  ) {
    MessageLogger.log(MessageLogger.Severity.TRACE, "genStatementList::start");

    int n = Arbitraries.integers().between(1, MAX_STMT).sample();
    //List<Statement> exs =  new ArrayList<>();

    NodeList<Statement> nodes = new NodeList<> ();
    for (int i = 0; i <n; i++) {
      nodes.add(genStatement(ctx).sample());
    }

    MessageLogger.log(MessageLogger.Severity.TRACE, "genStatementList::fim");

    return Arbitraries.just(nodes);
  }

  @Provide
  public Arbitrary <BlockStmt> genBlockStmt(Map <String, String> ctx) {
    MessageLogger.log(MessageLogger.Severity.TRACE, "genBlockStmt::start");

    Arbitrary <NodeList<Statement >> l = genStatementList(ctx);

    BlockStmt b = new BlockStmt(l.sample());

    MessageLogger.log(MessageLogger.Severity.TRACE, "genBlockStmt::fim");

    return Arbitraries.just(b);
  }

  //ExpressionStmt
  @Provide
  public Arbitrary <VariableDeclarationExpr> genVarDeclAssign(
    Map <String, String> ctx
  )
  throws ClassNotFoundException {
    MessageLogger.log(MessageLogger.Severity.TRACE, "genVarDeclarator::start");
    Arbitrary <PrimitiveType> pt = mBase
      .generatePrimitiveTypes()
      .map(t -> new PrimitiveType(t));

    Arbitrary <Type> t = Arbitraries.oneOf(mBase.generateClassOrInterfaceTypes(), pt);

    Type tp = t.sample();

    Arbitrary <Expression> e = mCore.genExpression(ctx, tp);

    String v = Arbitraries.of(mValidNames).sample();

    ctx.put(v, tp.asString());

    mValidNames.remove(v);

    MessageLogger.log(MessageLogger.Severity.TRACE, "genVarDeclarator::fim");

    return e.map(
      obj -> new VariableDeclarationExpr(new VariableDeclarator(tp, v, obj))
    );
  }

  //
  @Provide
  public Arbitrary <VariableDeclarationExpr> genVarAssignStmt(
    Map <String, String> ctx
  )
  throws ClassNotFoundException {
    String key = Arbitraries.of(ctx.keySet()).sample();

    String value = ctx.get(key);

    Type tp = JavaTypeTranslator.convertToParserType(value);

    Arbitrary <Expression> e = mCore.genExpression(ctx, tp);

    return e.map(
      obj -> new VariableDeclarationExpr(new VariableDeclarator(tp, key, obj))
    );
  }

  @Provide
  public Arbitrary <AssignExpr> genTypeAssignStmt(Map <String, String> ctx)
  throws ClassNotFoundException {
    String key = Arbitraries.of(ctx.keySet()).sample();

    String value = ctx.get(key);

    Type tp = JavaTypeTranslator.convertToParserType(value);

    Arbitrary <Expression> e = mCore.genExpression(ctx, tp);

    return e.map(
      obj -> new AssignExpr(e.sample(), obj, AssignExpr.Operator.ASSIGN)
    );
  }

  @Provide
  public Arbitrary <IfStmt> genIfStmt(Map <String, String> ctx) {
    MessageLogger.log(MessageLogger.Severity.TRACE, "genIfStmt::start");

    Map <String, String> newCtxIf = new HashMap <String, String> (ctx);
    Map <String, String> newCtxElse = new HashMap <String, String> (ctx);

    Arbitrary <Expression> e = mCore.genExpressionOperator(
      ctx,
      PrimitiveType.booleanType()
    );

    MessageLogger.log(MessageLogger.Severity.TRACE, "genIfStmt::fim");

    return e.map(
      exp ->
      new IfStmt(
        exp,
        genBlockStmt(newCtxIf).sample(),
        genBlockStmt(newCtxElse).sample()
      )
    );
  }

  @Provide
  public Arbitrary <WhileStmt> genWhileStmt(Map <String, String> ctx) {
    MessageLogger.log(MessageLogger.Severity.TRACE, "genWhileStmt::start");

    Map <String, String> newCtx = new HashMap <String, String> (ctx);

    Arbitrary <Expression> e = mCore.genExpression(
      newCtx,
      PrimitiveType.booleanType()
    );

    MessageLogger.log(MessageLogger.Severity.TRACE, "genWhileStmt::fim");
    return e.map(
      exp ->
      new WhileStmt(
        exp.asVariableDeclarationExpr(),
        genBlockStmt(newCtx).sample()
      )
    );
  }

  @Provide
  public Arbitrary <ExpressionStmt> genExpressionStmt(Map <String, String> ctx) {
    //@TODO: Sortear o tipo aleatoriamente e passar para genExpression
    Arbitrary <PrimitiveType.Primitive> t = mBase.generatePrimitiveTypes();

    Arbitrary <Expression> e = mCore.genExpression(
      ctx,
      JavaTypeTranslator.convertToParserType(t.sample().toString())
    );

    return e.map(exp -> new ExpressionStmt(exp));
  }

  @Provide
  public Arbitrary <ExpressionStmt> genVarDeclarationStmt(
    Map <String, String> ctx
  )
  throws ClassNotFoundException {
    Arbitrary <VariableDeclarationExpr> e = genVarDeclAssign(ctx);

    return e.map(exp -> new ExpressionStmt(exp));
  }

  @Provide
  public Arbitrary <ExpressionStmt> genVarDeclStmt(Map <String, String> ctx)
  throws ClassNotFoundException {
    Arbitrary <VariableDeclarationExpr> e = genVarDecl(ctx);

    return e.map(exp -> new ExpressionStmt(exp));
  }

  @Provide
  //Arbitrary<ForStmt>
  public Arbitrary <ForStmt> genForStmt(Map <String, String> ctx)
  throws ClassNotFoundException {
    MessageLogger.log(MessageLogger.Severity.TRACE, "genForStmt::start");

    Arbitrary <VariableDeclarationExpr> variableD = genVarDeclAssignInt(ctx);

    VariableDeclarationExpr variable = variableD.sample();

    Arbitrary <LiteralExpr> intAll = Arbitraries
      .integers()
      .between(25, 100)
      .map(i -> new IntegerLiteralExpr(String.valueOf(i)));

    Arbitrary <LiteralExpr> intLimited = Arbitraries
      .integers()
      .between(1, 5)
      .map(i -> new IntegerLiteralExpr(String.valueOf(i)));

    Map <String, String> newCtx = new HashMap <String, String> (ctx);

    System.out.println(variable.getVariable(0));

    Arbitrary <Expression> compare = mCore.genExpressionOperatorFor(
      newCtx,
      PrimitiveType.intType(),
      variable.getVariable(0),
      intAll
    );

    Arbitrary <Expression> e = mCore.genExpression(ctx, PrimitiveType.intType());

    Arbitrary <BlockStmt> a = genBlockStmt(newCtx);

    NodeList<Expression> nodes = new NodeList<> (variable);

    Arbitrary <Expression> update = mCore.genExpressionMatchOperatorFor(
      newCtx,
      PrimitiveType.intType(),
      variable.getVariable(0),
      intLimited
    );

    NodeList<Expression> nodesAtu = new NodeList<> (update.sample());

    MessageLogger.log(MessageLogger.Severity.TRACE, "genForStmt::fim");
    return e.map(
      exp -> new ForStmt(nodes, compare.sample(), nodesAtu, a.sample())
    );
  }

  @Provide
  public Arbitrary <VariableDeclarationExpr> genVarDeclAssignInt(
    Map <String, String> ctx
  )
  throws ClassNotFoundException {
    MessageLogger.log(MessageLogger.Severity.TRACE, "genVarDeclarator::start");
    Arbitrary <PrimitiveType> pt = mBase
      .generatePrimitiveIntTypes()
      .map(t -> new PrimitiveType(t));

    Arbitrary <Type> t = Arbitraries.oneOf(pt);

    Type tp = t.sample();

    Arbitrary <Expression> e = mCore.genExpression(ctx, tp);

    String v = Arbitraries.of(mValidNames).sample();

    Arbitrary <LiteralExpr> integer = Arbitraries
      .integers()
      .between(1, 10)
      .map(i -> new IntegerLiteralExpr(String.valueOf(i)));

    ctx.put(v, tp.asString());

    mValidNames.remove(v);

    MessageLogger.log(MessageLogger.Severity.TRACE, "genVarDeclarator::fim");

    return e.map(
      obj ->
      new VariableDeclarationExpr(
        new VariableDeclarator(tp, v, integer.sample())
      )
    );
  }

  @Provide
  public List<Statement> genList(Map <String, String> ctx)
  throws ClassNotFoundException {
    MessageLogger.log(MessageLogger.Severity.TRACE, "genVarDeclarator::start");

    List<Statement> a = new ArrayList<> ();

    for (int i = 0; i <100; i++) {
      Arbitrary <Statement> e = genStatement(ctx);
      a.add(e.sample());
    }

    for (int i = 0; i <100; i++) {
      System.out.println("Position: " + a.get(i));
    }

    MessageLogger.log(MessageLogger.Severity.TRACE, "genVarDeclarator::fim");

    return a;
  }
}