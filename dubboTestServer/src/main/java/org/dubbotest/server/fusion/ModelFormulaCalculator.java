package org.dubbotest.server.fusion;

import com.alibaba.fastjson.JSONObject;
import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ExpressionEvaluator;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ModelFormulaCalculator {
    private static ConcurrentHashMap<Object, Object> eeCache = new ConcurrentHashMap<>();

    public ModelFormulaCalculator() {
    }

    public static double calculate(String formula, String paramStr, Float[] variables) throws InvocationTargetException {
        ExpressionEvaluator ee = (ExpressionEvaluator) eeCache.computeIfAbsent(getKey(formula, paramStr), k -> initFormula(formula, paramStr));

        Object[] params = new Object[]{variables};
        return (double) ee.evaluate(variables);

    }

    public static void initModelFormula(JSONObject formulaConfig) {
        try {
            if (null == formulaConfig) {
                return;
            }
            String formula = formulaConfig.getString("formula");
            String paramStr = formulaConfig.getString("paramStr");
            if (StringUtils.isEmpty(formula) || StringUtils.isEmpty(paramStr)) {
                return;
            }

            ExpressionEvaluator ee = initFormula(formula, paramStr);
            eeCache.put(getKey(formula, paramStr), ee);
        } catch (Exception e) {
//            log.error("formulaConfig: {} initModelFormula error", formulaConfig, e);
        }
    }

    private static String getKey(String formula, String paramStr) {
        return formula + "_" + paramStr;
    }

    private static ExpressionEvaluator initFormula(String formula, String paramStr) {
        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
        String[] params = getParams(paramStr);
        Class[] paramTypes = Stream.of(params).map(p -> double.class).toArray(Class[]::new);
        expressionEvaluator.setParameters(params, paramTypes);
        expressionEvaluator.setExpressionType(Double.class);

        try {
            expressionEvaluator.cook(formula);
        } catch (CompileException e) {
            throw new RuntimeException(e);
        }
        return expressionEvaluator;
    }

    private static String[] getParams(String paramStr) {
        if (StringUtils.isEmpty(paramStr)) {
            return null;
        }
        // 修复后的代码
        return Arrays.stream(paramStr.split(","))
                .map(String::trim)
                .filter(x -> !StringUtils.isEmpty(x))
                .toArray(String[]::new);
    }

    public static void main(String[] args) throws InvocationTargetException {
        ModelFormulaCalculator modelFormulaCalculator = new ModelFormulaCalculator();
        JSONObject formulaConfig = new JSONObject();
        formulaConfig.put("formula", "a+b+c * Math.exp(Math.exp(d))");
        formulaConfig.put("params", "a,b,c,d");
        modelFormulaCalculator.initModelFormula(formulaConfig);
        System.out.println(modelFormulaCalculator.calculate("a+b+c * Math.exp(Math.exp(d))", "a,b,c,d", new Float[]{1f, 2f, 3f, 4f}));
    }
}
