package com.kakarote.module.common.expression.func.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.kakarote.common.exception.BusinessException;
import com.kakarote.module.constant.ModuleCodeEnum;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zjj
 * @title: Digit2CN
 * @description: Digit2CN
 * @date 2022/3/8 16:13
 */
public class Digit2CN {
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^(\\-|0|[1-9])(\\d{0,13})\\.?(\\d{0,15})$");
    private static final String DEFAULT_PATH_SEPARATOR = ".";
    private static final String[] CN_NUMBER = {Digit2CN.CN_ZERO, Digit2CN.CN_ONE, Digit2CN.CN_TWO, Digit2CN.CN_THREE,
            Digit2CN.CN_FOUR, Digit2CN.CN_FIVE, Digit2CN.CN_SIX, Digit2CN.CN_SEVEN, Digit2CN.CN_EIGHT, Digit2CN.CN_NINE};
    private static final String[] UNITS1 = {"圆", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆"};
    private static final String[] UNITS2 = {"厘", "毫", "分", "角"};
    private static final String CN_ZHENG = "整";
    private static final String CN_NEGATIVE = "负";
    private static final String CN_ZERO = "零";
    public static final String CN_ONE = "壹";
    public static final String CN_TWO = "贰";
    public static final String CN_THREE = "叁";
    public static final String CN_FOUR = "肆";
    public static final String CN_FIVE = "伍";
    public static final String CN_SIX = "陆";
    public static final String CN_SEVEN = "柒";
    public static final String CN_EIGHT = "捌";
    public static final String CN_NINE = "玖";
    public static final String CN_TEN = "拾";
    public static final String CN_HUNDRED = "佰";
    public static final String CN_THOUSAND = "仟";
    public static final String CN_TEN_THOUSAND = "万";
    public static final String CN_YUAN = "圆";


    public static String getStr(String digit) {
        Matcher matcher = AMOUNT_PATTERN.matcher(digit);
        if (!matcher.find()) {
            throw new BusinessException(ModuleCodeEnum.DIGIT_ERROR);
        }
        StringBuilder builder = new StringBuilder();
        BigDecimal bigDecimal = new BigDecimal(digit).setScale(4, BigDecimal.ROUND_CEILING);
        if (bigDecimal.compareTo(BigDecimal.ZERO) < 0) {
            builder.append(CN_NEGATIVE);
        } else if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
            return builder.append(CN_ZERO).append(CN_YUAN).append(CN_ZHENG).toString();
        }
        digit = bigDecimal.toPlainString();
        List<String> strings = StrUtil.split(digit, DEFAULT_PATH_SEPARATOR);
        int index = 0;
        for (String string : strings) {
            if (ObjectUtil.equal(0, index)) {
                builder.append(formatInt(string));
            } else {
                BigDecimal decimal = new BigDecimal(string);
                if (decimal.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                builder.append(formatDecimal(string));
            }
            index++;
        }
        if (ObjectUtil.equal(1, index)) {
            builder.append(CN_ZHENG);
        }
        return builder.toString();
    }

    private static String formatInt(String decimal) {
        BigDecimal bigDecimal = new BigDecimal(decimal).abs();
        if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }
        decimal = bigDecimal.toPlainString();
        StringBuilder stringBuilder = new StringBuilder();
        int j = 0;
        for (int i = decimal.length() - 1; i >= 0; i--) {
            int n = decimal.charAt(i) - 48;
            String cn = CN_NUMBER[n];
            String un = UNITS1[j++];
            if (ObjectUtil.equals(0, n)) {
                if (StrUtil.isNotEmpty(stringBuilder)
                        && !StrUtil.endWith(stringBuilder.toString(), CN_YUAN)
                        && !StrUtil.endWith(stringBuilder.toString(), CN_ZERO)
                        && !StrUtil.endWith(stringBuilder.toString(), CN_TEN)
                        && !StrUtil.endWith(stringBuilder.toString(), CN_HUNDRED)
                        && !StrUtil.endWith(stringBuilder.toString(), CN_THOUSAND)
                        && !StrUtil.endWith(stringBuilder.toString(), CN_TEN_THOUSAND)) {
                    stringBuilder.append(cn);
                }
                if (Arrays.asList(CN_YUAN, CN_TEN_THOUSAND).contains(un)) {
                    stringBuilder.append(un);
                }
            } else {
                if (StrUtil.endWith(stringBuilder.toString(), CN_ZERO)) {
                    stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 1));
                }
                stringBuilder.append(un).append(cn);
            }
        }
        String str = stringBuilder.reverse().toString();
        if(str.endsWith(CN_ZERO)) {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

    private static String formatDecimal(String decimal) {
        StringBuilder stringBuilder = new StringBuilder();
        int j = 0;
        for (int i = decimal.length() - 1; i >= 0; i--) {
            int n = decimal.charAt(i) - 48;
            String cn = CN_NUMBER[n];
            String un = UNITS2[j++];
            stringBuilder.append(un).append(cn);
        }
        return stringBuilder.reverse().toString();
    }
}
