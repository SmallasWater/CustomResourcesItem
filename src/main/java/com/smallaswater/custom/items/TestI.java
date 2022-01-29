package com.smallaswater.custom.items;


import com.smallaswater.custom.lib.customitemapi.item.ItemCustomTool;

/**
 * @author SmallasWater
 * 2022/1/29
 */
public class TestI extends ItemCustomTool {


    public TestI() {
        this(0,1);
    }
    public TestI(Integer meta) {
        this(meta,1);
    }

    public TestI(Integer meta,int count) {
        super(1011,meta,count,"测试","npcAirElement".toLowerCase());
    }


}
