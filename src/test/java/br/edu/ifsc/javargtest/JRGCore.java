package br.edu.ifsc.javargtest;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Provide;

/**
 *
 * @author lukra
 * 
 */
public class JRGCore {
  private static final int FUEL_START = 10;

  private ClassManager mCT;

  private TypeGenerator mBase;

  private JRGOperator mOperator;

  //private Map<String, String> mCtx;

  private List<String> mValidNames;

  int mFuel;

  public JRGCore(ClassManager ct, TypeGenerator base) {
    mCT = ct;

    mBase = base;

    mOperator = new JRGOperator(mCT, mBase, this);

    //mCtx = new HashMap<String, String>();

    mValidNames = Arrays.asList("a", "b", "c", "d", "e", "f", "g");

    mFuel = FUEL_START;
  }

  @Provide
  public Arbitrary<Expression> genExpression(Map<String, String> ctx, Type t) {
    Arbitrary<Expression> e;
    List<Arbitrary<Expression>> cand = new ArrayList<>();

    try {
      if (mFuel > 0) { // Permite a recursão até certo ponto
        mFuel--;

        if (t.toString().equals(PrimitiveType.booleanType().asString())) {
          cand.add(
            Arbitraries.oneOf(
              mOperator.genLogiExpression(ctx),
              mOperator.genRelaExpression(ctx)
            )
          );
        }

        // Candidatos de tipos primitivos
        if (t.isPrimitiveType()) {
          cand.add(
            Arbitraries.oneOf(mBase.genPrimitiveType(t.asPrimitiveType()))
          );
        }

        if (t.isPrimitiveType() && mBase.isNumericType(t)) {
          cand.add(Arbitraries.oneOf(mOperator.genArithExpression(ctx, t)));
        }

        // Se não for tipo primitivo
        if (!t.isPrimitiveType()) {
          //Candidatos de construtores
          cand.add(Arbitraries.oneOf(genObjectCreation(ctx, t)));
        }

        // Verifica se existem atributos candidatos
        if (!mCT.getCandidateFields(t.asString()).isEmpty()) {
          cand.add(Arbitraries.oneOf(genAttributeAccess(ctx, t)));
        }

        //Verifica se existem candidados methods
        if (!mCT.getCandidateMethods(t.asString()).isEmpty()) {
          cand.add(Arbitraries.oneOf(genMethodInvokation(ctx, t)));
        }

        // Verifica se existem candidados cast
        if (!t.isPrimitiveType() && !mCT.subTypes2(t.asString()).isEmpty()) {
          cand.add(Arbitraries.oneOf(genUpCast(ctx, t)));
        }

        // Verifica se existem candidados Var
        if (ctx.containsValue(t.asString())) {
          cand.add(Arbitraries.oneOf(genVar(ctx, t)));
        }
      } else { // Não permite aprofundar a recursão
        if (t.isPrimitiveType()) {
          cand.add(
            Arbitraries.oneOf(mBase.genPrimitiveType(t.asPrimitiveType()))
          );
        }

        if (!t.isPrimitiveType()) {
          cand.add(Arbitraries.oneOf(genObjectCreation(ctx, t)));
        }

        if (ctx.containsValue(t.asString())) {
          cand.add(Arbitraries.oneOf(genVar(ctx, t)));
        }
        //if (t.toString().equals(PrimitiveType.booleanType().asString())) {
        //    cand.add(Arbitraries.oneOf(mOperator.genLogiExpression(ctx),
        //    mOperator.genRelaExpression(ctx)));
        //}

        //if (t.isPrimitiveType() && mBase.isNumericType(t)){
        //   cand.add(Arbitraries.oneOf(mOperator.genArithExpression(ctx,t)));
        //}
      }
    } catch (ClassNotFoundException ex1) {
      throw new RuntimeException("Error: class not found!");
    }

    return Arbitraries.oneOf(cand);
  }

  @Provide
  public Arbitrary<NodeList<Expression>> genExpressionList(
    Map<String, String> ctx,
    List<Type> types
  ) {
    MessageLogger.showMessage(MessageLogger.Severity.TRACE, "genExpressionList::inicio");

    MessageLogger.showMessage(
      MessageLogger.Severity.TRACE,
      "genExpressionList::types" + types.toString()
    );
    List<Expression> exs = types
      .stream()
      .map(t -> genExpression(ctx, t))
      .map(e -> e.sample())
      .collect(Collectors.toList());

    NodeList<Expression> nodes = new NodeList<>(exs);

    MessageLogger.showMessage(MessageLogger.Severity.TRACE, "genExpressionList::fim");

    return Arbitraries.just(nodes);
  }

  @Provide
  //public Arbitrary<ObjectCreationExpr> genObjectCreation(Type t) throws ClassNotFoundException {
  public Arbitrary<Expression> genObjectCreation(
    Map<String, String> ctx,
    Type t
  )
    throws ClassNotFoundException {
    MessageLogger.showMessage(MessageLogger.Severity.TRACE, "genObjectCreation::inicio");

    List<Constructor> constrs;

    constrs = mCT.getClassConstructors(t.asString());

    Arbitrary<Constructor> c = Arbitraries.of(constrs);

    Constructor constr = c.sample();

    MessageLogger.showMessage(
      MessageLogger.Severity.DEBUG,
      "genObjectCreation::constr : " + constr.toString()
    );

    Class[] params = constr.getParameterTypes();

    List<Class> ps = Arrays.asList(params);

    MessageLogger.showMessage(
      MessageLogger.Severity.DEBUG,
      "genObjectCreation::ps " + ps
    );

    List<Type> types = ps
      .stream()
      .map(
        tname -> JavaTypeTranslator.reflectToParserType(tname.getName())
      )
      .collect(Collectors.toList());

    MessageLogger.showMessage(
      MessageLogger.Severity.DEBUG,
      "genObjectCreation::types " + "[" + types + "]"
    );

    MessageLogger.showMessage(MessageLogger.Severity.TRACE, "genObjectCreation::fim");

    return genExpressionList(ctx, types)
      .map(el -> new ObjectCreationExpr(null, t.asClassOrInterfaceType(), el));
  }

  @Provide
  public Arbitrary<FieldAccessExpr> genAttributeAccess(
    Map<String, String> ctx,
    Type t
  )
    throws ClassNotFoundException {
    MessageLogger.showMessage(
      MessageLogger.Severity.TRACE,
      "genAttributeAccess::inicio"
    );

    Arbitrary<Field> f = genCandidatesField(t.asString());

    Field field = f.sample();

    MessageLogger.showMessage(
      MessageLogger.Severity.TRACE,
      "genAttributeAccess::field: " + field.getName()
    );

    MessageLogger.showMessage(
      MessageLogger.Severity.TRACE,
      "genAttributeAccess::Class: " + field.getDeclaringClass().getName()
    );

    Arbitrary<Expression> e = genExpression(
      ctx,
      JavaTypeTranslator.reflectToParserType(
        field.getDeclaringClass().getName()
      )
    );

    MessageLogger.showMessage(MessageLogger.Severity.TRACE, "genAttributeAccess::fim");

    return e.map(obj -> new FieldAccessExpr(obj, field.getName()));
  }

  @Provide
  public Arbitrary<MethodCallExpr> genMethodInvokation(
    Map<String, String> ctx,
    Type t
  )
    throws ClassNotFoundException {
    MessageLogger.showMessage(
      MessageLogger.Severity.TRACE,
      "genMethodInvokation:" + ":inicio"
    );

    Arbitrary<Method> methods;

    MessageLogger.showMessage(
      MessageLogger.Severity.DEBUG,
      "genMethodInvokation:" + ":t = " + t.asString()
    );

    methods = genCandidatesMethods(t.asString());

    Method method = methods.sample();

    Class[] params = method.getParameterTypes();

    List<Class> ps = Arrays.asList(params);

    MessageLogger.showMessage(
      MessageLogger.Severity.DEBUG,
      "genObjectCreation:" + ":method " + method.toString()
    );

    Arbitrary<Expression> e = genExpression(
      ctx,
      JavaTypeTranslator.reflectToParserType(
        method.getDeclaringClass().getName()
      )
    );

    List<Type> types = ps
      .stream()
      .map(
        tname -> JavaTypeTranslator.reflectToParserType(tname.getName())
      )
      .collect(Collectors.toList());

    MessageLogger.showMessage(MessageLogger.Severity.TRACE, "genMethodInvokation::fim");

    return genExpressionList(ctx, types)
      .map(el -> new MethodCallExpr(e.sample(), method.getName(), el));
  }

  @Provide
  public Arbitrary<NameExpr> genVar(Map<String, String> ctx, Type t) {
    MessageLogger.showMessage(MessageLogger.Severity.TRACE, "genVar::inicio");

    List<NameExpr> collect = ctx
      .entrySet()
      .stream()
      .filter(e -> e.getValue().equals(t.asString()))
      .map(x -> new NameExpr(x.getKey()))
      .collect(Collectors.toList());

    MessageLogger.showMessage(MessageLogger.Severity.TRACE, "genVar::fim");

    return Arbitraries.of(collect);
  }

  @Provide
  public Arbitrary<CastExpr> genUpCast(Map<String, String> ctx, Type t)
    throws ClassNotFoundException {
    List<Class> st = mCT.subTypes2(t.asString());

    Arbitrary<Class> sc = Arbitraries.of(st);

    Class c = sc.sample();

    Arbitrary<Expression> e = genExpression(
      ctx,
      JavaTypeTranslator.reflectToParserType(c.getName())
    );

    return e.map(
      obj ->
        new CastExpr(
          JavaTypeTranslator.reflectToParserType(t.asString()),
          obj
        )
    );
  }

  @Provide
  public Arbitrary<Method> genCandidatesMethods(String type)
    throws ClassNotFoundException {
    List<Method> candidatesMethods;

    candidatesMethods = mCT.getCandidateMethods(type);

    return Arbitraries.of(candidatesMethods);
  }

  @Provide
  public Arbitrary<Field> genCandidatesField(String type)
    throws ClassNotFoundException {
    List<Field> candidatesField;

    candidatesField = mCT.getCandidateFields(type);

    return Arbitraries.of(candidatesField);
  }

  @Provide
  public Arbitrary<Constructor> genCandidatesConstructors(String type)
    throws ClassNotFoundException {
    List<Constructor> candidatesConstructors;

    candidatesConstructors = mCT.getCandidateConstructors(type);

    return Arbitraries.of(candidatesConstructors);
  }

  @Provide
  public Arbitrary<Class> genCandidateUpCast(String type)
    throws ClassNotFoundException {
    List<Class> upCast;

    upCast = mCT.subTypes2(type);

    return Arbitraries.of(upCast);
  }

  @Provide
  public Arbitrary<LambdaExpr> genLambdaExpr(Map<String, String> ctx)
    throws ClassNotFoundException {
    Arbitrary<PrimitiveType> pt = mBase
      .primitiveTypes()
      .map(t -> new PrimitiveType(t));

    Arbitrary<Type> t = Arbitraries.oneOf(mBase.classOrInterfaceTypes(), pt);

    Type tp = t.sample();

    Arbitrary<Expression> e = genExpression(ctx, tp);

    String a = Arbitraries.of(mValidNames).sample();

    return e.map(obj -> new LambdaExpr(new Parameter(tp, a), obj));
  }

  @Provide
  public Arbitrary<Expression> genExpressionOperator(
    Map<String, String> ctx,
    Type t
  ) {
    Arbitrary<Expression> e;

    List<Arbitrary<Expression>> cand = new ArrayList<>();

    if (t.toString().equals(PrimitiveType.booleanType().asString())) {
      cand.add(
        Arbitraries.oneOf(
          mOperator.genLogiExpression(ctx),
          mOperator.genRelaExpression(ctx)
        )
      );
    }
    if (t.isPrimitiveType() && mBase.isNumericType(t)) {
      cand.add(Arbitraries.oneOf(mOperator.genArithExpression(ctx, t)));
    }
    return Arbitraries.oneOf(cand);
  }

  @Provide
  public Arbitrary<Expression> genExpressionOperatorFor(
    Map<String, String> ctx,
    Type t,
    VariableDeclarator e,
    Arbitrary<LiteralExpr> ex
  ) {
    List<Arbitrary<Expression>> cand = new ArrayList<>();

    if (t.toString().equals(PrimitiveType.intType().asString())) {
      cand.add(
        Arbitraries.oneOf(mOperator.genRelacionalBooleanFor(ctx, e, ex))
      );
    }

    return Arbitraries.oneOf(cand);
  }

  public Arbitrary<Expression> genExpressionMatchOperatorFor(
    Map<String, String> ctx,
    Type t,
    VariableDeclarator e,
    Arbitrary<LiteralExpr> ex
  ) {
    List<Arbitrary<Expression>> cand = new ArrayList<>();

    if (t.toString().equals(PrimitiveType.intType().asString())) {
      cand.add(Arbitraries.oneOf(mOperator.genArithBooleanFor(ctx, e, ex)));
    }

    return Arbitraries.oneOf(cand);
  }
}
