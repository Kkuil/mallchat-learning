package com.abin.mallchat.common.common.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * @Author Kkuil
 * @Date 2023/09/17 17:00
 * @Description SpringEl表达式工具类
 */
public class SpElUtils {

    /**
     * Spring内置的SpringEl表达式解析器
     */
    private static final ExpressionParser SPRING_EL_PARSER = new SpelExpressionParser();

    /**
     * 获取入参具体名称
     */
    private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    public static String getMethodKey(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }

    /**
     * 解析El表达式
     *
     * @param method 方法对象
     * @param args   参数值
     * @param spEl   el表达式
     * @return 解析值
     */
    public static String parseSpEl(Method method, Object[] args, String spEl) {
        // 使用Optional来进行选择是否有入参，如果没有则返回空字符串数组
        String[] params = Optional.ofNullable(PARAMETER_NAME_DISCOVERER.getParameterNames(method)).orElse(new String[]{});
        // 构造el解析需要的上下文对象
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < params.length; i++) {
            // 所有参数与值一一对应扔进去
            context.setVariable(params[i], args[i]);
        }
        Expression expression = SPRING_EL_PARSER.parseExpression(spEl);
        return expression.getValue(context, String.class);
    }
}
