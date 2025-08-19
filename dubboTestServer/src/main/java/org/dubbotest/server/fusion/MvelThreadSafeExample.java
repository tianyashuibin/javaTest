package org.dubbotest.server.fusion;

import org.mvel2.MVEL;
import org.mvel2.compiler.ExpressionCompiler;

import java.io.Serializable;
import java.util.Map;

public class MvelThreadSafeExample {
    private static final Serializable compiledExpr;

    static {
        // 表达式编译一次（可缓存）
        String formula = "price * quantity + tax";
        ExpressionCompiler compiler = new ExpressionCompiler(formula);
        compiledExpr = compiler.compile();
    }

    public static Object evaluate(Map<String, Object> vars) {
        return MVEL.executeExpression(compiledExpr, vars);
    }

    public static void main(String[] args) {
        Map<String, Object> vars = Map.of(
                "price", 100,
                "quantity", 2,
                "tax", 10
        );
        System.out.println("结果：" + evaluate(vars)); // 210
    }
}
