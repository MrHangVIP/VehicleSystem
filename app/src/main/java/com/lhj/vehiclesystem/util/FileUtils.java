package com.lhj.vehiclesystem.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dawn on 2016/5/2.
 * 文件的相关操作类
 * 包括：
 * 判断sd卡是否存在isSdCardExist，获取根目录getRootPath，删除文件夹deleteDir，uri转换成文件uriToFile，
 * 写入文件（instream）write，写入文件（file）write，读取文件内容read，获取文件的md5值getMd5，
 * 根据地址来获取uri格式getUriByFileDirAndFileName，uri转换成file文件getFileByUri
 */
public class FileUtils {
    /**
     * 判断sd卡是否存在
     *
     * @return
     */
    public static boolean isSdCardExist() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 判断是否已经挂载
            return true;
        }
        return false;
    }

    /**
     * 获取根目录
     *
     * @return
     */
    public static String getRootPath() {
        if (isSdCardExist()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return Environment.getRootDirectory().getAbsolutePath();
        }
    }

    /**
     * 删除文件夹
     *
     * @param dirf
     */
    public static void deleteDir(File dirf) {
        if (dirf.isDirectory()) {
            File[] childs = dirf.listFiles();
            for (int i = 0; i < childs.length; i++) {
                deleteDir(childs[i]);
            }
        }
        dirf.delete();
    }

    /**
     * uri装换文件
     *
     * @param context
     * @param uri
     * @return
     */
    public static File uriToFile(Activity context, Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = context.managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        File file = new File(img_path);
        return file;
    }

    /**
     * 写入文件
     *
     * @param in
     * @param file
     */
    public static void write(InputStream in, File file) {
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            while (in.read(buffer) > -1) {
                out.write(buffer);
            }
            out.flush();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入文件
     *
     * @param in
     * @param file
     * @param append 表示编写时是否在文件末尾续写，true表示不覆盖
     */
    public static void write(String in, File file, boolean append) {
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file, append);
            fw.write(in);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件的内容
     *
     * @param file
     * @return
     */
    public static String read(File file) {
        if (!file.exists()) {
            return "";
        }
        try {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer();
            String s;
            while ((s = br.readLine()) != null) {
                buffer.append(s);
            }
            return buffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取文件的md5值
     *
     * @param file
     * @return
     */
    public static String getMd5(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            MappedByteBuffer mapByteBuffer = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(mapByteBuffer);
            BigInteger bigInt = new BigInteger(1, md5.digest());
            return bigInt.toString(16);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "no file";
        } catch (IOException e) {
            e.printStackTrace();
            return "no file";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "no such algorithm";
        }
    }

    /****
     * 通过目录和文件名来获取Uri
     *
     * @param strFileDir  目录
     * @param strFileName 文件名
     * @return Uri
     * @throws IOException IO异常
     */
    public static Uri getUriByFileDirAndFileName(Context context, String strFileDir, String strFileName) throws IOException {
        Uri uri = null;
        File fileDir = new File(Environment.getExternalStorageDirectory(), strFileDir);  //定义目录
        if (!fileDir.exists()) {   //判断目录是否存在
            fileDir.mkdirs();      //如果不存在则先创建目录
        }
        File file = new File(fileDir, strFileName);   //定义文件
        if (!file.exists()) {  //判断文件是否存在
            file.createNewFile();    //如果不存在则先创建文件
        }
        uri = getUriForFile(context, file);//获取Uri
        return uri;
    }

    private static Uri getUriForFile(Context context, File file) {
        if (file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, "com.lhj.vehiclesystem.fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    /**
     * 通过Uri返回File文件
     * 注意：通过相机的是类似content://media/external/images/media/97596
     * 通过相册选择的：file:///storage/sdcard0/DCIM/Camera/IMG_20150423_161955.jpg
     * 通过查询获取实际的地址
     *
     * @param uri     Uri
     * @param context 上下文对象  Activity
     * @return File
     */
    public static File getFileByUri(Activity context, Uri uri) {
        String path = null;
        if (Build.VERSION.SDK_INT >= 24) {
            return getFilePathFromURI(context, uri);
        }
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        }
        return null;
    }

    public static File getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            String name = System.currentTimeMillis() + fileName;
            File copyFile = new File(Environment.getExternalStorageDirectory() + File.separator + name);
            copy(context, contentUri, copyFile);
            return copyFile;
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            inputstreamtofile(inputStream, dstFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void inputstreamtofile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
