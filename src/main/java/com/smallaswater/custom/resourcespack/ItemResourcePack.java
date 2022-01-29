package com.smallaswater.custom.resourcespack;

import cn.nukkit.resourcepacks.ResourcePack;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ItemResourcePack implements ResourcePack {

    private static final MessageDigest HASHER;

    static {
        try {
            HASHER = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private UUID uuid;
    private final byte[] data;
    private final byte[] sha256;

    public ItemResourcePack(Path itemPath) throws Exception {
        HashFunction hasher = Hashing.md5();
        String md5 = hasher.hashBytes(itemPath.getFileName().toString().getBytes()).toString();
        Preconditions.checkArgument(md5.length() == 32, "Invalid MD5");
        md5 = md5.toLowerCase();
//        Preconditions.checkNotNull(data, "data");
//        Preconditions.checkArgument(data.length > 0, "Invalid data");

        StringBuilder builder = new StringBuilder(36);
        builder.append(md5, 0, 8);
        builder.append("-");
        builder.append(md5, 8, 12);
        builder.append("-");
        builder.append(md5, 12, 16);
        builder.append("-");
        builder.append(md5, 16, 20);
        builder.append("-");
        builder.append(md5.substring(20));
        String uuid = builder.toString();
        this.uuid = UUID.fromString("691d504c-fd0e-0626-cd3b-058e5682dd1b");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.setLevel(Deflater.BEST_COMPRESSION);
            byte[] buffer;


            zos.putNextEntry(new ZipEntry("manifest.json"));
            builder = new StringBuilder();
            builder.append("{\"format_version\":1,\"header\":{\"uuid\":\"");
            builder.append(uuid);
            builder.append("\",\"name\":\"自定义物品材质\",\"version\":[0,0,1],\"description\":\"\"},\"modules\":[{\"description\":\"\",\"version\":[0,0,1],\"uuid\":\"");
            builder.append("691d504c-fd0e-0626-cd3b-058e5682dd1b");
            builder.append("\",\"type\":\"resources\"}]}");

            buffer = builder.toString().getBytes();
            zos.write(buffer, 0, buffer.length);
            Files.walk(itemPath, 1).filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS) && path.toString().toLowerCase().endsWith(".png")).forEach(path -> {
            try (InputStream fis = Files.newInputStream(path, StandardOpenOption.READ)) {
                byte[] bytes = new byte[fis.available()];
                fis.read(bytes);

                zos.putNextEntry(new ZipEntry("textures/items/" +path.getFileName()));
                zos.write(bytes, 0, bytes.length);
            } catch (Exception ignore) {

            }
        });
//


            zos.finish();
            this.data = baos.toByteArray();
        }

        this.sha256 = HASHER.digest(this.data);
    }

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
