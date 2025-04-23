package com.blitz.springboot4.util;

public class Constants {

    public static final int MaxLimit = 4; //在合并托盘or巧固架时,小于该数值的才需要合并
    public static final int MaxNumber = 20; //在统计每个人的拣货单时间间隔时，如果一个人在一个拣货单上拣货不到20件，则不参与统计。


    public static final String Tick = "1";
    public static final String NoTick = "0";


    public static final String ExceptionLocation  = "A-03-01-1"; //该库位特殊，不能参与计算
}
