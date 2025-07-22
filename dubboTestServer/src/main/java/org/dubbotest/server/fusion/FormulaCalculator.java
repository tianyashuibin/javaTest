package org.dubbotest.server.fusion;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;

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
        vars.put("a", 10.0);
        vars.put("b", 20.0);
        vars.put("c", 30.0);
        vars.put("d", 0.0);

        FormulaCalculator calculator = new FormulaCalculator();
        double result1 = calculator.calculate("0.1 * a + 0.3 * b + 0.01 * c + Math.exp(Math.exp(d))", vars);
        double result2 = calculator.calculate("c * Math.exp(Math.exp(d))", vars);
        System.out.println("result1: " + result1);
        System.out.println("result2: " + result2);
        System.out.println("re: " + Math.exp(Math.exp(1)));
    }
}
