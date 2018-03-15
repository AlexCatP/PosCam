package com.psy.my;

/*
 * Copyright (C) 2014 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import android.app.Application;
import android.graphics.Bitmap;


import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.psy.util.Common;
import com.psy.util.MyImageLoader;


import cn.finalteam.toolsfinal.StorageUtils;
import cn.xdu.poscam.R;

import java.io.File;

import cn.finalteam.galleryfinal.*;


/**
 * Desction:
 * Author:pengjianbo
 * Date:15/12/18 下午1:45
 */
public class IApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(),
                Common.IMG_CACHE_PATH);

        //设置主题
        //ThemeConfig theme = ThemeConfig.CYAN
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(getApplicationContext().getResources().getColor(R.color.shanZhaRed))
                .build();
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();

        CoreConfig coreConfig = new CoreConfig.Builder(this, new MyImageLoader(), theme)
                .setFunctionConfig(functionConfig)
                .setTakePhotoFolder(cacheDir)
                //.setPauseOnScrollListener(new UILPauseOnScrollListener(false, true))
                .build();
        GalleryFinal.init(coreConfig);

        //universal-image-loader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)//图片大小刚好满足控件尺寸
                .showImageForEmptyUri(R.drawable.head) //
                .showImageOnFail(R.drawable.head) //
                .cacheInMemory(true) //
                .cacheOnDisk(true) //
                .build();//
        ImageLoaderConfiguration config = new ImageLoaderConfiguration//
                .Builder(getApplicationContext())//
                .defaultDisplayImageOptions(defaultOptions)//
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存位置
                // default为使用HASHCODE对UIL进行加密命名， 还可以用MD5(new Md5FileNameGenerator())加密
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)//
                .diskCacheFileCount(100)// 缓存一百张图片
                .writeDebugLogs()//
                .build();//
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

    }
}
