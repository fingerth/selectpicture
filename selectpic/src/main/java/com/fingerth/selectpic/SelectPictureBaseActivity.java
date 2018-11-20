package com.fingerth.selectpic;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fingerth.selectpic.utils.FileSizeUtil;
import com.fingerth.selectpic.utils.CameraUtils;
import com.fingerth.selectpic.utils.FileUtils;
import com.fingerth.selectpic.utils.permission.PermissionUtils;

import java.io.File;

public abstract class SelectPictureBaseActivity extends AppCompatActivity {

    private final String TAG = "SelectPicture";
    private final int REQUEST_CAMERA_CODE = 300;
    private final int REQUEST_CAMERA_PHOTO_CODE = 10002;
    private CameraUtils cameraUtils;
    public static String cache_path = "SelectPicture";


    public String turnCameraPermissions = "去打開相機權限";
    public String youNeedToOpenTheStorageSpace = "去打開文件管理權限";
    public String noSDState = "SD卡不存在，不能拍照";
    public String error = "获取照片失败，请重试";
    public String cancel = "取消";
    public String photo = "拍照";
    public String selectPhotos = "选择照片";
    public String pleaseChoose = "请选择";
    public String help = "帮助";
    public String setting = "設置";

    /**
     * 选择图片上传的方式
     */
    private Dialog dialog_select_pic;

    public void showSelectPictureDialog() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, noSDState, Toast.LENGTH_SHORT).show();
            return;
        }
        dialog_select_pic = new Dialog(this, R.style.MyDialogStyle);
        dialog_select_pic.setContentView(R.layout.dialog_select_pic);
        dialog_select_pic.setCanceledOnTouchOutside(true);
        dialog_select_pic.findViewById(R.id.other_view).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_select_pic.cancel();
                    }
                });
        Button dialogCancel = dialog_select_pic.findViewById(R.id.dialog_cancel);
        dialogCancel.setText(cancel);
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_select_pic.cancel();
            }
        });

        Button chooseByCamera = dialog_select_pic.findViewById(R.id.choose_by_camera);
        chooseByCamera.setText(photo);
        chooseByCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_select_pic.cancel();
                if (cameraUtils == null) {
                    cameraUtils = new CameraUtils();
                }
                cameraUtils.selectPicFromCamera(SelectPictureBaseActivity.this, REQUEST_CAMERA_CODE, REQUEST_CAMERA_PHOTO_CODE);
            }
        });

        Button chooseByLocal = dialog_select_pic.findViewById(R.id.choose_by_local);
        chooseByLocal.setText(selectPhotos);
        chooseByLocal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_select_pic.cancel();
                if (PermissionUtils.checkWriteExternalStorage(SelectPictureBaseActivity.this)) {//一定需要權限，因為圖片要壓縮一下
                    FileUtils.updateFile(SelectPictureBaseActivity.this, pleaseChoose);
                } else {
                    PermissionUtils.requestWriteExternalStorage(SelectPictureBaseActivity.this);
                }
            }
        });
        dialog_select_pic.show();
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case REQUEST_CAMERA_CODE://CAMERA
                boolean cameraAccepted2 = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted2) {
                    //授权成功之后，调用系统相机进行拍照操作等
                    if (cameraUtils == null) {
                        cameraUtils = new CameraUtils();
                    }
                    cameraUtils.selectPicFromCamera(SelectPictureBaseActivity.this, REQUEST_CAMERA_CODE, REQUEST_CAMERA_PHOTO_CODE);
                } else {
                    //PermissionUtils.showOpenPermissionsDialog(this, "幫助", "去打開攝像權限", "設置", "取消");
                    PermissionUtils.showOpenPermissionsDialog(this, help, turnCameraPermissions, setting, cancel);
                }
                break;
            case 200:
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    //授权成功之后
                } else {
                    // PermissionUtils.showOpenPermissionsDialog(this, "幫助", "去打開文件管理權限", "設置", "取消");
                    PermissionUtils.showOpenPermissionsDialog(this, help, youNeedToOpenTheStorageSpace, setting, cancel);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FileUtils.FILE_SELECT_CODE:
                case FileUtils.FILE_SELECT_CODE_KITKAT://API 19以下
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
                    //Log.v(TAG, "选中的文件:" + path);
                    if (!TextUtils.isEmpty(path)) {
                        //这里对图片进行压缩处理
                        File compressFile = new FileSizeUtil().compressBitmapToFile(this, path, Bitmap.CompressFormat.JPEG, false);
                        onLocal(compressFile);
                    }
                    break;
                case REQUEST_CAMERA_PHOTO_CODE:// 拍照上传
                    if (cameraUtils != null && cameraUtils.cameraFile != null && cameraUtils.cameraFile.exists()) {
                        //这里对图片进行压缩处理
                        File compressFile = new FileSizeUtil().compressBitmapToFile(this, cameraUtils.cameraFile.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true);
                        onCamera(compressFile);
                    } else {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public abstract void onCamera(File imageFile);

    public abstract void onLocal(File imageFile);

}
