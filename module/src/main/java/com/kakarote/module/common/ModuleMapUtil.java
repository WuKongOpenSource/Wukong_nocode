package com.kakarote.module.common;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.Iterator;
import java.util.Map;

/**
 * @author zjj
 * @title: MapUtil
 * @description: map 工具类
 * @date 2022/4/12 16:15
 */
public class ModuleMapUtil extends MapUtil {

    public static <K, V> Map<K, V> removeEmptyValue(Map<K, V> map) {
        if (isEmpty(map)) {
            return map;
        } else {
            Iterator iter = map.entrySet().iterator();

            while(iter.hasNext()) {
                Map.Entry<K, V> entry = (Map.Entry)iter.next();
                if (ObjectUtil.isEmpty(entry.getValue())) {
                    iter.remove();
                }
            }

            return map;
        }
    }
}
