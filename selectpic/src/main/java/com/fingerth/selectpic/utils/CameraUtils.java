package com.fingerth.selectpic.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;


import com.fingerth.selectpic.R;
import com.fingerth.selectpic.SelectPictureBaseActivity;
import com.fingerth.selectpic.utils.fileprovider.FileProvider7;
import com.fingerth.selectpic.utils.permission.PermissionUtils;

import java.io.File;

/**
 * ======================================================
 * Created by Administrator able_fingerth on 2016/11/10.
 * <p/>
 * 版权所有，违者必究！
 * <详情描述/>
 */
public class CameraUtils {
    public File cameraFile;

//    public static String turnCameraPermissions = "去打開相機權限";
//    public static String youNeedToOpenTheStorageSpace = "去打開文件管理權限";
//    public static String noSDState = "SD卡不存在，不能拍照";

    public void selectPicFromCamera(Activity mContext, int requestCameraCode, int requestCameraPhotoCode) {
        if (PermissionUtils.checkWriteExternalStorage(mContext)) {//一定需要權限，因為圖片要壓縮一下
            if (PermissionUtils.checkCamera(mContext)) {
                //授权成功之后，调用系统相机进行拍照操作等
                picFromCamera(mContext, requestCameraPhotoCode);
            } else {
                //申请摄像权限
                PermissionUtils.requestCamera(mContext, requestCameraCode);
            }
        } else {
            PermissionUtils.requestWriteExternalStorage(mContext);
        }

    }

    /**
     * 照相获取图片
     */
    private void picFromCamera(Activity mContext, int requestCameraPhotoCode) {
        cameraFile = new File(new CachePathUtils().getImgFiles(), mContext.getString(R.string.app_name) + System.currentTimeMillis() + ".png");
        try {
            deleteFile(cameraFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cameraFile.getParentFile().mkdirs();
            //Android N 使用FileProvider
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                Uri imageUri = FileProvider7.getUriForFile(mContext, cameraFile);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                } else {
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                // 指定照片保存路径（SD卡）
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                mContext.startActivityForResult(intent, requestCameraPhotoCode);
            }

        }
    }

    /**
     * @param file 设定文件
     *             TODO(删除文件)
     */
    private void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }

            }
            file.delete();
        }
    }

}
