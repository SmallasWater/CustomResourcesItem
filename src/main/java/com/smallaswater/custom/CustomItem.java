package com.smallaswater.custom;


import cn.nukkit.plugin.PluginBase;
import cn.nukkit.resourcepacks.ResourcePack;
import cn.nukkit.resourcepacks.ResourcePackManager;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.smallaswater.custom.items.TestI;
import com.smallaswater.custom.lib.customitemapi.CustomItemAPI;
import com.smallaswater.custom.lib.customitemapi.ZipUtils;
import com.smallaswater.custom.lib.customitemapi.item.ItemCustomArmor;
import com.smallaswater.custom.resourcespack.ItemResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import java.util.*;

/**
 * @author SmallasWater
 */
public class CustomItem extends PluginBase {


    @Override
    public void onEnable() {
       initFile();
        Path itemPath = this.getDataFolder().toPath().resolve("items");
        try {
            if (!Files.isDirectory(itemPath, LinkOption.NOFOLLOW_LINKS)) {
                Files.deleteIfExists(itemPath);
                Files.createDirectory(itemPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<ResourcePack> packs = new ObjectArrayList<>();
        try {
            packs.add(new ItemResourcePack(itemPath));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        if (!packs.isEmpty()) {
            ResourcePackManager manager = this.getServer().getResourcePackManager();
            try {
                Field f1 = ResourcePackManager.class.getDeclaredField("resourcePacksById");
                f1.setAccessible(true);
                Map<UUID, ResourcePack> byId = (Map<UUID, ResourcePack>) f1.get(manager);
                packs.forEach(pack -> byId.put(pack.getPackId(), pack));

                Field f2 = ResourcePackManager.class.getDeclaredField("resourcePacks");
                f2.setAccessible(true);
                packs.addAll(Arrays.asList((ResourcePack[]) f2.get(manager)));
                f2.set(manager, packs.toArray(new ResourcePack[0]));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }




        }

        CustomItemAPI itemAPI = new CustomItemAPI(this);
        itemAPI.registerCustomItem(1011, TestI.class);
        this.getServer().getPluginManager().registerEvents(itemAPI, this);

    }
    public void initFile(){
        this.getLogger().info("读取资源中");
        if(!new File(this.getDataFolder()+"/items").exists()) {
            this.getLogger().info("正在解压资源包");
            this.saveResource("items/items.zip", false);
            if (new File(this.getDataFolder() + "/items/items.zip").exists()) {
                try {
                    ZipUtils.unzip(this.getDataFolder() + "/items/items.zip", this.getDataFolder() + "/items");
                } catch (Exception e) {
                    this.getLogger().info("解压资源失败");
                }
                new File(this.getDataFolder() + "/items/items.zip").delete();
                this.getLogger().info("解压完成");
            }
        }
    }
}
