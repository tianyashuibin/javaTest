package org.dubbotest.server.fusion;

import org.codehaus.janino.ExpressionEvaluator;

public class JaninoExample {
    public static void main(String[] args) throws Exception {
        ExpressionEvaluator ee = new ExpressionEvaluator();
        ee.setParameters(new String[]{"a", "b", "c", "d", "e"}, new Class[]{double.class, double.class, double.class, double.class, double.class});
        ee.setExpressionType(double.class);
        ee.cook("a+b+c * Math.exp(Math.exp(d))");  // 从配置文件读取

        long start = System.currentTimeMillis();
        int times = 500;
        for (int i = 0; i < times; i++) {
            double result = (Double) ee.evaluate(new Object[]{Math.random(), Math.random(), Math.random(), Math.random(), Math.random()});
        }
        long total = System.currentTimeMillis() - start;
        System.out.println("耗时total: " + total + "ms, 平均耗时: " + total*1.0/times);

    }

}
