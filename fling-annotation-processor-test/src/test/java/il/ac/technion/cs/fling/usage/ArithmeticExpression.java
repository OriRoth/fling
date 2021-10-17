package il.ac.technion.cs.fling.usage;

import static il.ac.technion.cs.fling.generated.ArithmeticExpression.begin;
import static il.ac.technion.cs.fling.generated.ArithmeticExpression.n;

import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Add_;
import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Add_1;
import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Add_2;
import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Expression;
import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Mul;
import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Mul_;
import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Mul_1;
import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Mul_2;
import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Term;
import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Term1;
import il.ac.technion.cs.fling.generated.ArithmeticExpressionAST.Term2;

public class ArithmeticExpression {
	public static void main(String[] args) {
		Expression e1 = begin().n(1).plus().n(2).end().times().n(3).$();
		System.out.println(eval(e1));
		Expression e2 = n(1).plus().n(2).times().n(3).$();
		System.out.println(eval(e2));
		Expression e3 = n(1).plus().n(1).times().n(6).divide().n(3).minus().n(3).$();
		System.out.println(eval(e3));
	}

	private static int eval(Expression e) {
		return eval(eval(e.mul), e.add_);
	}

	private static int eval(int n, Add_ a) {
		return a instanceof Add_1 ? n + eval(eval(((Add_1) a).mul), ((Add_1) a).add_)
				: a instanceof Add_2 ? n - eval(eval(((Add_2) a).mul), ((Add_2) a).add_) : n;
	}

	private static int eval(Mul e) {
		return eval(eval(e.term), e.mul_);
	}

	private static int eval(int n, Mul_ m) {
		return m instanceof Mul_1 ? n * eval(eval(((Mul_1) m).term), ((Mul_1) m).mul_)
				: m instanceof Mul_2 ? n / eval(eval(((Mul_2) m).term), ((Mul_2) m).mul_) : n;
	}

	private static int eval(Term e) {
		return e instanceof Term1 ? eval(((Term1) e).expression) : ((Term2) e).n;
	}
}
