package com.kakarote.module.service;

import com.kakarote.module.entity.BO.UploadExcelBO;
import org.springframework.web.multipart.MultipartFile;

public interface IModuleUploadExcelService {

    /**
     * 导入excel
     *
     * @param file F
     * @param uploadExcelBO B
     * @return D
     */
    Long uploadExcel(MultipartFile file, UploadExcelBO uploadExcelBO);
}
