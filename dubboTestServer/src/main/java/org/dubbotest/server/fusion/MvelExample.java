package org.dubbotest.server.fusion;

import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MvelExample {
    public static void main(String[] args) {
        String formula = "a+b+c * Math.exp(Math.exp(d))";
        Map<String, Double> vars = new HashMap<>();
        vars.put("a", 0.24094822);
        vars.put("b", 0.22746295);
        vars.put("c", 0.17299914);
        vars.put("d", -0.8905458);

        ExpressionCompiler compiler = new ExpressionCompiler(formula);
        CompiledExpression compiledExpr = compiler.compile();

        MVEL.executeExpression(compiledExpr, vars);

        int times = 500;
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            if (i == 0) {
                start = System.currentTimeMillis();
            }
            long substart = System.currentTimeMillis();
            vars.put("a", Math.random());
            vars.put("b", Math.random());
            vars.put("c", Math.random());
            vars.put("d", Math.random());

            double v = vars.get("a") + vars.get("b") + vars.get("c") * Math.exp(Math.exp(vars.get("d")));

//            MVEL.eval(formula, vars);
//            MVEL.executeExpression(compiledExpr, vars);
//            System.out.println("result:" + MVEL.executeExpression(compiledExpr, vars));
            if (i == times - 1) {
                System.out.println("time:" + " cost:" + (System.currentTimeMillis() - start));
            }
        }
        long total = System.currentTimeMillis() - start;
        System.out.println("time cost：" + total + " avg:" + total * 1.0 / times);

//        long start1 = System.currentTimeMillis();
//        for (int i = 0; i < times; i++) {
//            vars.put("a", Math.random());
//            vars.put("b", Math.random());
//            vars.put("c", Math.random());
//            vars.put("d", Math.random());
////            MVEL.eval(formula, vars);
//            MVEL.executeExpression(compiledExpr, vars);
////            System.out.println("result:" + MVEL.executeExpression(compiledExpr, vars));
//        }
//        long total1 = System.currentTimeMillis() - start1;
//        System.out.println("[1]time cost：" + total1 + " avg:" + total1 * 1.0 / times);
//
//
//        ExecutorService pool = Executors.newFixedThreadPool(10);
//        for (int i = 0; i < 3; i++) {
//            Future<?> submit = pool.submit(() -> {
//                long start2 = System.currentTimeMillis();
//                for (int j = 0; j < times; j++) {
//                    Map<String, Object> vars2 = Map.of(
//                            "a", Math.random(),
//                            "b", Math.random(),
//                            "c", Math.random(),
//                            "d", Math.random()
//                    );
////                Object result = MvelThreadSafeExample.evaluate(vars2);
//                    Object result = MVEL.executeExpression(compiledExpr, vars2);
////                    System.out.println("线程：" + Thread.currentThread().getName() + "    result:" + result);
//                }
//                long total2 = System.currentTimeMillis() - start2;
//                System.out.println("线程：" + Thread.currentThread().getName() + " time cost:" + total2 + " avg:" + total2 / times);
//            });
//
//            try {
//                submit.get();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//



    }

    public static class MvelThreadSafeExample {

    }

}
