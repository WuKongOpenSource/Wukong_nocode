package com.kakarote.module.constant;

/**
 * @author zhangzhiwei
 * 自定义模块的常量
 */
public class ModuleConst {
    /**
     * 默认的应用以及模块数量
     */
    public static final int DEFAULT_MODULE_SIZE = 100;

    /**
     *html转pdf linux wkhtmltopdf路径
     * https://wkhtmltopdf.org/
     */
    public static final String LINUX_TO_PDF_TOOL = "/usr/local/bin/wkhtmltopdf";

    /**
     *html转pdf win wkhtmltopdf路径
     * https://wkhtmltopdf.org/
     */
    public static final String WIN_TO_PDF_TOOL = "D:\\software\\wkhtmltox\\bin\\wkhtmltopdf.exe";

    /**
     * 打印模板缓存key
     */
    public static final String CRM_PRINT_TEMPLATE_CACHE_KEY = "CRM:PRINT:TEMPLATE:";

    /**
     * 导入消息缓存key
     */
    public static final String UPLOAD_EXCEL_MESSAGE_PREFIX = "upload:excel:message:";

}
