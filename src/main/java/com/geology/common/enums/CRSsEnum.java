package com.geology.common.enums;

public enum CRSsEnum {
    /**
     * WGS84
     */
    WGS84("WGS84", 0),
    /**
     * GCJ02
     */
    GCJ02("GCJ02", 1),
    /**
     * Baidu
     */
    BD("Baidu", 2);

    private String coordination;

    private Integer code;

    CRSsEnum(String coordination, Integer code)
    {
        this.coordination = coordination;
        this.code = code;
    }
}
