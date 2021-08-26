package com.xxg.xtoolkit;

public class CNYUtil {

    /**
     * 将以分为单位的人民币金额转为元，保留 2 位小数
     *
     * 由于浮点数计算会有精度问题，所以人民币金额通常会以分为单位的整数类型来传输和保存。前端展示一般会以元为单位，保留2位小数到分。
     *
     * CNYUtil.formatFenToYuan(1)     0.01
     * CNYUtil.formatFenToYuan(100)   1.00
     * CNYUtil.formatFenToYuan(12345) 123.45
     */
    public static String formatFenToYuan(int fen) {
        return String.format("%.2f", fen / 100F);
    }
}
