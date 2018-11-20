package com.fingerth.selectpic.utils;

import android.os.Environment;

import com.fingerth.selectpic.SelectPictureBaseActivity;

import java.io.File;

/**
 * ======================================================
 * Created by Administrator -周晓明 on 2016/9/21.
 * <p>
 * 版权所有，违者必究！
 * <详情描述/>
 */
public class CachePathUtils {

    public  File getImgFiles() {
        File dir = Environment.getExternalStorageDirectory();
        File destDir = new File(dir, SelectPictureBaseActivity.cache_path);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return destDir;
    }



}
