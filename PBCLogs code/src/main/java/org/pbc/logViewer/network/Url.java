package org.pbc.logViewer.network;


import org.pbc.logViewer.launcher.AppConstants;

public class Url {

    public static String TARGET_NODE = AppConstants.NODE_1;

    public static String getLogUrl() {
        return "http://" + TARGET_NODE + ":8080/PrivateBlockChain/apis/getLog?pointerLocation=";
    }

    public static String getStatisticsUrl(final int pageNumber) {
        return "http://" + TARGET_NODE + ":8080/PrivateBlockChain/apis/getStatistics?pageNo=" + pageNumber;
    }
}
