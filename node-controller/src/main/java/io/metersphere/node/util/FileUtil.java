package io.metersphere.node.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 文件工具类
 */
public class FileUtil {

    /**
     * 解压到指定目录
     *
     * @param zipPath
     * @param descDir
     */
    public static void unZipFiles(String zipPath, String descDir) throws IOException {
        unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     * 解压后的文件名，和之前一致
     *
     * @param zipFile 待解压的zip文件
     * @param descDir 指定目录
     */
    @SuppressWarnings("rawtypes")
    public static void unZipFiles(File zipFile, String descDir) {

        ZipFile zip = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            System.setProperty("sun.zip.encoding", System.getProperty("sun.jnu.encoding"));
            zip = new ZipFile(zipFile, Charset.forName("GBK"));//解决中文文件夹乱码
            String name = zip.getName().substring(zip.getName().lastIndexOf('/') + 1, zip.getName().lastIndexOf('.'));
            File pathFile = new File(descDir + name);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }

            for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                in = zip.getInputStream(entry);
                String outPath = (descDir + name + "/" + zipEntryName).replaceAll("\\*", "/");
                // 判断路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!file.exists()) {
                    file.mkdirs();
                }
                // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.error("解压出现异常");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.error("解压出现异常");
            }
        }
        LogUtil.info("解压成功");
        return;
    }

    /**
     * 遍历文件夹寻找debugtalk.py文件
     *
     * @param path
     */
    public static String traverseFolder(String path) {
        String filePath = "";
        int fileNum = 0, folderNum = 0;
        File file = new File(path);
        if (file.exists()) {
            LinkedList<File> list = new LinkedList<File>();
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    list.add(file2);
                    folderNum++;
                } else {
                    if (file2.getName().equals("debugtalk.py")) {
                        filePath = file2.getParent();
                    }
                    fileNum++;
                }
            }
            File temp_file;
            while (!list.isEmpty()) {
                temp_file = list.removeFirst();
                files = temp_file.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        list.add(file2);
                        folderNum++;
                    } else {
                        if (file2.getName().endsWith("debugtalk.py")) {
                            filePath = file2.getParent();
                        }
                        fileNum++;
                    }
                }
            }
        } else {
            LogUtil.error("该目录下没有文件");
        }
        return filePath;
    }

    /**
     * 解析host文件
     *
     * @param filePath
     * @return
     */
    public static List<String> analysisFile(String filePath) {
        List<String> list = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                String newStr = str.trim();
                if (!newStr.equals("")) {
                    if (newStr.startsWith("#")){
                        continue;
                    }
                    if (newStr.contains("#")){
                        newStr = newStr.substring(0,newStr.indexOf("#")).trim();
                    }
                    String [] arr = newStr.split("\\s+");
                    if (arr.length == 0 || arr.length ==1){
                        continue;
                    }
                    String newKey = arr[0];
                    String newValue = arr[1];
                    list.add(newValue + ":" + newKey);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()) {
            LogUtil.error("清理代码库失败");
            return;
        }
        if (file.isFile()){
            file.delete();
        }else {
            //取得这个目录下的所有子文件对象
            File[] files = file.listFiles();
            //遍历该目录下的文件对象
            for (File f : files) {
                //打印文件名
                String name = file.getName();
                //判断子目录是否存在子目录,如果是文件则删除
                if (f.isDirectory()) {
                    deleteFile(f);
                } else {
                    f.delete();
                }
            }
            //删除空文件夹  for循环已经把上一层节点的目录清空。
            file.delete();
        }

    }

    public static void main(String[] args) {
        String path = "D:\\home\\docker\\qhttprunner\\aaa.txt";
        File file = new File(path);
        List<String> list = analysisFile(path);
        for (String s:list){
            System.out.println(s);
        }
        /*String path = "D:\\home\\docker\\qhttprunner\\2dd47965-64b9-4e75-bd9a-040db5bd3f7b.zip";
        deleteFile(new File(path));*/
    }
}