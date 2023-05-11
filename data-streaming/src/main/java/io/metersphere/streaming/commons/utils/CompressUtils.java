package io.metersphere.streaming.commons.utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class CompressUtils {

    /***
     * 压缩Zip
     *
     * @param data
     * @return
     */
    public static Object zip(Object data) {
        if (!(data instanceof byte[])) {
            return data;
        }

        byte[] temp = (byte[]) data;
        byte[] b = (byte[]) data;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(bos);
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(temp.length);
            zip.putNextEntry(entry);
            zip.write(temp);
            zip.closeEntry();
            zip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            LogUtil.error(ex);
        }

        return b;
    }

    /***
     * 解压Zip
     *
     * @param data
     * @return
     */
    public static Object unzip(Object data) {
        if (!(data instanceof byte[])) {
            return data;
        }
        byte[] temp = (byte[]) data;
        byte[] b = (byte[]) data;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(temp);
            ZipInputStream zip = new ZipInputStream(bis);
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                b = baos.toByteArray();
                baos.flush();
                baos.close();
            }
            zip.close();
            bis.close();
        } catch (Exception ex) {
            LogUtil.error(ex);
        }
        return b;
    }


    public static void zipFiles(File srcfile, File targetFile) {

        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new FileOutputStream(targetFile));

            if (srcfile.isFile()) {
                zipFile(srcfile, out, "");
            } else {
                File[] list = srcfile.listFiles();
                for (int i = 0; i < list.length; i++) {
                    compress(list[i], out, "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void compress(File file, ZipOutputStream out, String basedir) {
        /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            zipDirectory(file, out, basedir);
        } else {
            zipFile(file, out, basedir);
        }
    }

    public static void zipDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists())
            return;

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            /* 递归 */
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }


    public static void zipFile(File srcfile, ZipOutputStream out, String basedir) {
        if (!srcfile.exists())
            return;

        byte[] buf = new byte[8192];
        FileInputStream in = null;

        try {
            int len;
            in = new FileInputStream(srcfile);
            out.putNextEntry(new ZipEntry(basedir + srcfile.getName()));

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.closeEntry();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
