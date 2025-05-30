package com.blitz.springboot4.util;

public class Constants {

    public static final int MaxLimit = 5; //在合并托盘or巧固架时,小于该数值的才需要合并
    public static final int MaxNumber = 20; //在统计每个人的拣货单时间间隔时，如果一个人在一个拣货单上拣货不到20件，则不参与统计。

    public static final int MaxLength = 110;//在计算空库位时，有时一个SKU太长，会占据两个库位。该长度就是判断它是否会占用两个库位的标准

    //token有效期
    public static final long EXPIRATION_TIME = 10*60 * 60 * 1000;

    public static final String Tick = "1";
    public static final String NoTick = "0";


    public static final String ExceptionLocation  = "A-03-01-1"; //该库位特殊，不能参与计算


}
