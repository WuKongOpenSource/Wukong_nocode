package com.kakarote.module.common.expression;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.module.common.expression.func.ICustomFunc;
import com.kakarote.module.constant.ModuleCodeEnum;
import com.kakarote.module.entity.BO.ModuleFormulaBO;
import com.kakarote.module.entity.PO.ModuleField;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zjj
 * @title: ExpressionUtil
 * @description: 表达式工具类
 * @date 2022/3/4 20:16
 */
@Slf4j
public class ExpressionUtil {

    public static final String REGEX = "(?<=\\{)(.*?)(?=\\})";
    public static final String REGEX_ALL = "\\#\\{.*?\\}";

    static {
        Reflections reflections = new Reflections(ICustomFunc.class.getPackage().getName());
        Set<Class<? extends ICustomFunc>> subTypes = reflections.getSubTypesOf(ICustomFunc.class);
        for (Class<? extends ICustomFunc> subType : subTypes) {
            try {
                AviatorEvaluator.getInstance().addFunction(subType.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object execute(String expression, Map<String, Object> args) {
        try {
            Expression compile = AviatorEvaluator.compile(expression);
            return compile.execute(args);
        } catch (Exception e) {
            log.info("expression:{}", expression);
            log.error(e.getMessage());
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_PARSE_ERROR);
        }
    }

    /**
     * 解析表达式参数
     *
     * @param expression
     * @return
     */
    public static Set<String> parseArgs(String expression, String regex) {
        Set<String> args = new HashSet<>();
        if (StrUtil.isNotEmpty(expression)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(expression);
            while (matcher.find()) {
                args.add(matcher.group(0));
            }
        }
        return args;
    }

    /**
     * 执行表达式
     *
     * @param formulaBO
     * @return
     */
    public static Object execute(ModuleFormulaBO formulaBO) {
        if (CollUtil.isNotEmpty(formulaBO.getVars()) && MapUtil.isEmpty(formulaBO.getEnv())) {
            throw new BusinessException(ModuleCodeEnum.EXPRESSION_ARG_ERROR);
        }
        log.info("formula:{}", formulaBO);
        String expression = formulaBO.getExpression();
        Set<String> args =  ExpressionUtil.parseArgs(expression, ExpressionUtil.REGEX_ALL);
        Map<String, Object> env = new HashMap<>(16);
        for (String arg : args) {
            String randomStr = getRandomStr(10);
            expression = expression.replace(arg, randomStr);
            if (MapUtil.isNotEmpty(formulaBO.getEnv()) && formulaBO.getEnv().containsKey(arg)) {
                env.put(randomStr, formulaBO.getEnv().get(arg));
            }
            if (MapUtil.isNotEmpty(formulaBO.getSourceEnv()) && formulaBO.getSourceEnv().containsKey(arg)) {
                env.put(randomStr, formulaBO.getSourceEnv().get(arg));
            }
        }
        return execute(expression, env);
    }

    /**
     * 根据字段实体获取变量名
     *
     * @param field
     * @return
     */
    public static String getArgName(ModuleField field) {
        return getArgName(field.getModuleId(), field.getFieldId());
    }

    /**
     * 获取变量名
     *
     * @param moduleId
     * @param fieldId
     * @return
     */
    public static String getArgName(Long moduleId, Long fieldId) {
        return String.format("#{%s}", StrUtil.join("-", moduleId, fieldId));
    }

    /**
     * 获取变量名
     *
     * @param arg
     * @return
     */
    public static String getArgName(String ... arg) {
        return String.format("#{%s}", StrUtil.join("-", arg));
    }

    public static String getArg(Set<String> args, String arg) {
        return args.stream().filter(a -> StrUtil.contains(arg, a)).findFirst().orElse(null);
    }

    /**
     *  生成随机数字符串
     *
     * @param num
     * @return
     */
    public static String getRandomStr(int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            char c = (char) RandomUtil.randomInt(65, 90);
            builder.append(c);
        }
        return builder.toString();
    }
}
