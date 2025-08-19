package org.dubbotest.server.fusion;

import org.mvel2.MVEL;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FormulaCalculator {
    private final ScriptEngine engine;

    public FormulaCalculator() {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.engine = manager.getEngineByName("js");
    }

    public double calculate(String formula, Map<String, Double> variables) {
        try {
            // 设置变量
            for (Map.Entry<String, Double> entry : variables.entrySet()) {
                engine.put(entry.getKey(), entry.getValue());
            }
            // 计算表达式
            Object result = engine.eval(formula);
            return ((Number) result).doubleValue();
        } catch (Exception e) {
            throw new RuntimeException("公式计算错误: " + formula, e);
        }
    }

    public static void main(String[] args) {
        Map<String, Double> vars = new HashMap<>();
        vars.put("a", 0.24094822);
        vars.put("b", 0.22746295);
        vars.put("c", 0.17299914);
        vars.put("d", -0.8905458);

        double result1 = 0.0;
        double result2 = 0.0;
        long start = System.currentTimeMillis();
        int times = 10;
        for (int i = 0; i < times; i++) {
            vars.put("a", Math.random());
            FormulaCalculator calculator = new FormulaCalculator();
//            result1 = calculator.calculate("0.1 * a + 0.3 * b + 0.01 * c + Math.exp(Math.exp(d))", vars);
            result2 = calculator.calculate("a+b+c * Math.exp(Math.exp(d))", vars);
        }
        long total = System.currentTimeMillis() - start;
        System.out.println("耗时total: " + total + "ms, 平均耗时: " + total*1.0/times);
        System.out.println("result1: " + result1);
        System.out.println("result2: " + result2);
        System.out.println("re: " + Math.exp(Math.exp(1)));

//        FormulaCalculator calculator = new FormulaCalculator();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            pool.submit(() -> {
                Map<String, Double> vars2 = Map.of(
                        "a", Math.random(),
                        "b", Math.random(),
                        "c", Math.random(),
                        "d", Math.random()
                );
                long start2 = System.currentTimeMillis();
                FormulaCalculator calculator = new FormulaCalculator();
                Object result = calculator.calculate("a+b+c * Math.exp(Math.exp(d))", vars2);
                System.out.println("线程：" + Thread.currentThread().getName() + " time cost:" + (System.currentTimeMillis() - start2) + "    result:" + result);
            });
        }
    }
}
