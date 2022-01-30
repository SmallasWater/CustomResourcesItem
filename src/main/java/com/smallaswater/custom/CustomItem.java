package com.smallaswater.custom;


import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.resourcepacks.ResourcePackManager;
import com.blocklynukkit.loader.script.BlockItemManager;
import com.smallaswater.custom.lib.customitemapi.CustomItemAPI;
import com.smallaswater.custom.resourcespack.ItemResourcePack;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;



import java.util.*;

/**
 * @author SmallasWater
 */
public class CustomItem extends PluginBase {

    private static ItemResourcePack resourcePack;

    private CustomItemAPI itemAPI;

    private static CustomItem c;

    private static LinkedHashMap<String,File> buildImg = new LinkedHashMap<>();

    @Override
    public void onEnable() {
        c = this;
        itemAPI = new CustomItemAPI(this);
        if(c.getServer().getPluginManager().getPlugin("BlocklyNukkit") == null) {
            this.getServer().getPluginManager().registerEvents(itemAPI, this);
        }

    }

    public static CustomItem getItemAPI() {
        return c;
    }

    public static void registerAdapter(Item item, String fileName, String path){
        if(c.getServer().getPluginManager().getPlugin("BlocklyNukkit") != null){
            BlockItemManager blockItemManager = new BlockItemManager(null);
            blockItemManager.registerSimpleItem(item,"items",false,true);
            blockItemManager.addItemTexture(item.getId(),path);
        }else{
            c.itemAPI.registerCustomItem(item.getId(), item.getClass());
            registerResources(fileName,path);
            build();
        }
    }


    private static void registerResources(String fileName, String path){
        File file = new File(path);
        if(file.isFile()){
            buildImg.put(fileName,file);
        }
    }

    private static void build(){
        try {
            resourcePack = new ItemResourcePack(buildImg, UUID.randomUUID(), UUID.randomUUID(), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(resourcePack != null){
            File path = new File(Server.getInstance().getFilePath()+"/resource_packs/自定义物品材质包.mcpack");

            try (OutputStream outputStream = new FileOutputStream(path)) {
                outputStream.write(resourcePack.getData());
            } catch (IOException e) {
                Server.getInstance().getLogger().alert("Unable to dump resource pack", e);
            }

            Server server = Server.getInstance();
            try {
                Field f2 = Server.class.getDeclaredField("resourcePackManager");
                f2.setAccessible(true);
                f2.set(server,new ResourcePackManager(new File(Server.getInstance().getFilePath()+"/resource_packs")));
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
}
