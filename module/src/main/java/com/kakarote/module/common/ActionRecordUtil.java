package com.kakarote.module.common;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kakarote.common.constant.Const;
import com.kakarote.ids.provider.utils.UserCacheUtil;
import com.kakarote.module.constant.ModuleFieldEnum;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ActionRecordUtil {

    public static Object parseValue(Object value, Integer type,boolean isNeedStr) {
        if (ObjectUtil.isEmpty(value) && !type.equals(ModuleFieldEnum.DETAIL_TABLE.getType())) {
            return isNeedStr ? "空" : "";
        }
        ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(type);
        switch (fieldEnum) {
            case BOOLEAN_VALUE: {
                return "1".equals(value) ? "是" : "否";
            }
            case TAG:{
                String eq="[]";
                if (ObjectUtil.equals(value,eq)){
                    return "空";
                }
                if (value instanceof CharSequence) {
                    value = JSON.parseArray(value.toString());
                }
                if (value instanceof List) {
                    return ((List<?>) value).stream().map(data-> ((Map<String,String>)data).get("name")).collect(Collectors.joining(Const.SEPARATOR));
                }
                return "空";
            }
            case AREA_POSITION: {
                StringBuilder stringBuilder = new StringBuilder();
                if (value instanceof CharSequence) {
                    value = JSON.parseArray(value.toString());
                }
                for (Map<String, Object> map : ((List<Map<String, Object>>) value)) {
                    stringBuilder.append(map.get("name")).append(" ");
                }
                return stringBuilder.toString();
            }
            case CURRENT_POSITION: {
                if (value instanceof CharSequence) {
                    value = JSON.parseObject(value.toString());
                }
                return Optional.ofNullable(((Map<String, Object>) value).get("address")).orElse("").toString();
            }
            case DATE_INTERVAL: {
                if (value instanceof Collection) {
                    value = StrUtil.join(Const.SEPARATOR,value);
                }
                return isNeedStr ? value.toString() : value;
            }
            case SINGLE_USER:
            case USER:
                List<String> ids = StrUtil.splitTrim(value.toString(),Const.SEPARATOR);
                return ids.stream().map(id -> UserCacheUtil.getUserName(Long.valueOf(id))).collect(Collectors.joining(Const.SEPARATOR));
            case STRUCTURE:
                List<String> deptIds = StrUtil.splitTrim(value.toString(),Const.SEPARATOR);
                return deptIds.stream().map(id -> UserCacheUtil.getDeptName(Convert.toLong(id))).collect(Collectors.joining(Const.SEPARATOR));
            case DETAIL_TABLE: {
                if(value == null) {
                    value = new ArrayList<>();
                }
                if(value instanceof String){
                    if("".equals(value)) {
                        value = new ArrayList<>();
                    } else {
                        value = JSON.parseArray((String) value);
                    }
                }
                List<Map<String,Object>> list = new ArrayList<>();
                JSONArray array = new JSONArray((List<Object>) value);
                for (int i = 0; i < array.size(); i++) {
                    JSONArray objects = array.getJSONArray(i);
                    for (int j = 0; j < objects.size(); j++) {
                        Map <String,Object> map = new HashMap<>();
                        JSONObject data = objects.getJSONObject(j);
                        map.put("name",data.get("name"));
                        map.put("value",parseValue(data.get("value"),data.getInteger("type"),false));
                        list.add(map);
                    }
                }
                return list;
            }
            default:
                return isNeedStr ? value.toString() : value;
        }

    }

    public static Object parseExportValue(Object value, Integer type,boolean isNeedStr) {
        if (ObjectUtil.isEmpty(value) && !type.equals(ModuleFieldEnum.DETAIL_TABLE.getType())) {
            return isNeedStr ? "空" : "";
        }
        ModuleFieldEnum fieldEnum = ModuleFieldEnum.parse(type);
        switch (fieldEnum) {
            case BOOLEAN_VALUE: {
                return "1".equals(value) ? "是" : "否";
            }
            case TAG:{
                if (value instanceof CharSequence) {
                    value = JSON.parseArray(value.toString());
                }
                if (value instanceof List) {
                    return ((List<?>) value).stream().map(data-> ((Map<String,String>)data).get("name")).collect(Collectors.joining(Const.SEPARATOR));
                }
                return "";
            }
            case AREA_POSITION: {
                StringBuilder stringBuilder = new StringBuilder();
                if (value instanceof CharSequence) {
                    value = JSON.parseArray(value.toString());
                }
                for (Map<String, Object> map : ((List<Map<String, Object>>) value)) {
                    stringBuilder.append(map.get("name")).append(" ");
                }
                return stringBuilder.toString();
            }
            case CURRENT_POSITION: {
                if (value instanceof CharSequence) {
                    value = JSON.parseObject(value.toString());
                }
                return Optional.ofNullable(((Map<String, Object>) value).get("address")).orElse("").toString();
            }
            case DATE_INTERVAL: {
                if (value instanceof Collection) {
                    value = StrUtil.join(Const.SEPARATOR,value);
                }
                return isNeedStr ? value.toString() : value;
            }
            case SINGLE_USER:
            case DETAIL_TABLE: {
                if(value == null) {
                    value = new ArrayList<>();
                }
                if(value instanceof String){
                    if("".equals(value)) {
                        value = new ArrayList<>();
                    } else {
                        value = JSON.parseArray((String) value);
                    }
                }
                List<Map<String,Object>> list = new ArrayList<>();
                JSONArray array = new JSONArray((List<Object>) value);
                for (int i = 0; i < array.size(); i++) {
                    JSONArray objects = array.getJSONArray(i);
                    for (int j = 0; j < objects.size(); j++) {
                        Map <String,Object> map = new HashMap<>();
                        JSONObject data = objects.getJSONObject(j);
                        map.put("name",data.get("name"));
                        map.put("value", parseValue(data.get("value"),data.getInteger("type"),false));
                        list.add(map);
                    }
                }
                return list;
            }
            default:
                return isNeedStr ? value.toString() : value;
        }

    }


}
