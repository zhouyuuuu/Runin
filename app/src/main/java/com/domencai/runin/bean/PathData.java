package com.domencai.runin.bean;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 项目名：runin-android
 * 包名：  com.domencai.runin.bean
 * 文件名：PathData
 * 创建者：jun
 * 创建时间  2017/4/22 16:05
 * 描述： TODO
 */

public class PathData {
    private File mFile;
    private RandomAccessFile raf;

    public PathData() throws IOException {
        mFile=new File("record.ruin");
        raf=new RandomAccessFile(mFile, "rw");
        //写入文件头
        byte[] head={0x23,0x33};
        raf.write(head);

    }

    /**
     * @param lon 经度
     * @param lat 维度
     * @param time 时间
     */
    public void addPoint(double lon,double lat,long time,byte flag) throws IOException {
        raf.write(0x20);//点的尺寸
        raf.writeDouble(lon);
        raf.writeDouble(lat);
        raf.writeLong(time);
        raf.write(flag);

    }
    public void update () throws IOException {
        raf.close();
    }
}
