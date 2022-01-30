package com.smallaswater.custom.resourcespack;

import cn.nukkit.resourcepacks.ResourcePack;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ItemResourcePack implements ResourcePack {

    private static final MessageDigest HASHER;

    public ZipOutputStream zipOutputStream;

    static {
        try {
            HASHER = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    private final UUID uuid;
    private final byte[] data;
    private final byte[] sha256;

    public ItemResourcePack(LinkedHashMap<String, File> buildImg, @NotNull UUID header, @NotNull UUID modules, int version) throws Exception {

        StringBuilder builder;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.setLevel(Deflater.BEST_COMPRESSION);
            byte[] buffer;
            uuid = header;
            StringBuilder builder1 = new StringBuilder();
            builder1.append("{\"resource_pack_name\": \"custom Item\",\"texture_name\":\"atlas.items\"," +
                    "\"texture_data\":{");
            zos.putNextEntry(new ZipEntry("manifest.json"));
            builder = new StringBuilder();
            builder.append("{\"format_version\":").append(version).append(",\"header\":{\"uuid\":\"");
            builder.append(header.toString());
            builder.append("\",\"name\":\"custom Item\",\"version\":[0,0,"+version+"],\"min_engine_version\":[1,18,0],\"description\":\"custom item build\"},\"modules\":[{\"description\":\"\",\"version\":[0,0,"+version+"],\"uuid\":\"");
            builder.append(modules);
            builder.append("\",\"type\":\"resources\"}]}");

            buffer = new String(builder.toString().getBytes(), StandardCharsets.UTF_8).getBytes();
            zos.write(buffer, 0, buffer.length);

            buildImg.values().forEach(path->{
            try (InputStream fis = Files.newInputStream(path.toPath(), StandardOpenOption.READ)) {
                byte[] bytes = new byte[fis.available()];
                fis.read(bytes);
                if(i == 0){
                    i++;
                }else{
                    builder1.append(",");
                }
                String n = path.toPath().getFileName().toString().split("\\.")[0];
                builder1.append("\"").append(n.toLowerCase()).append("\":{\"textures\":\"").append("textures/items/").append(path.toPath().getFileName().toString().toLowerCase()).append("\"}");
                zos.putNextEntry(new ZipEntry("textures/items/" +path.toPath().getFileName().toString().toLowerCase()));

                zos.write(bytes, 0, bytes.length);
            } catch (Exception ignore) {

            }

        });
            builder1.append("}}");
//
            zos.putNextEntry(new ZipEntry("textures/item_texture.json"));
            buffer = builder1.toString().getBytes();
            zos.write(buffer, 0, buffer.length);
            zos.finish();
            this.data = baos.toByteArray();

        }

        this.sha256 = HASHER.digest(this.data);
    }

    int i = 0;
    public byte[] getData() {
        return this.data;
    }

    @Override
    public String getPackName() {
        return "自定义物品包";
    }

    @Override
    public UUID getPackId() {
        return this.uuid;
    }

    @Override
    public String getPackVersion() {
        return "0.0.1";
    }

    @Override
    public int getPackSize() {
        return this.data.length;
    }

    @Override
    public byte[] getSha256() {
        return this.sha256;
    }

    @Override
    public byte[] getPackChunk(int off, int len) {
        return Arrays.copyOfRange(this.data, off, off + (Math.min(this.data.length - off, len)));
    }

    @Override
    public String toString() {
        return "ItemResourcePack(" + this.uuid + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }
        if (obj instanceof ItemResourcePack) {
            return this.uuid.equals(((ItemResourcePack) obj).uuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }
}
