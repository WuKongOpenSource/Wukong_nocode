package com.kakarote.module.common;

import com.kakarote.common.result.Result;
import com.kakarote.module.entity.PO.ModuleFieldConfig;
import com.kakarote.module.service.IModuleFieldConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InitEsIndexRunner implements ApplicationRunner {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
	private IModuleFieldConfigService fieldConfigService;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, List<Long>> moduleIndexMap = ElasticUtil.getModuleIndexMap();
		moduleIndexMap.forEach((k, v) -> {
			boolean b = ElasticUtil.indexExist(k);;
			if (!b) {
				log.warn("索引{}不存在，正在初始化",k);
				List<ModuleFieldConfig> fieldConfigs = fieldConfigService.list();
				restTemplate.execute(client -> {
					ElasticUtil.init(fieldConfigs, client, k, v);
					return Result.ok();
				});

			}
		});
    }
}
