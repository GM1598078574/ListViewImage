package com.gaomin.listviewimage;

/**
 * Created by gaomin on 2016/7/2.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageAdapter extends ArrayAdapter<String> {
    private ListView mListView;
    private Context context;

    /**
     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
     */

    private LruCache<String, BitmapDrawable> mMemoryCache;


    public ImageAdapter(Context context, int resourse, String[] objects) {
        super(context, resourse, objects);
        this.context = context;
        //获取应用最大内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, BitmapDrawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getBitmap().getByteCount();
            }
        };
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        String url = getItem(position);
        if(mListView == null){
            mListView = (ListView) parent;
        }
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.images_item,parent,false);
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.image);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.imageView.setTag(url);
        }
        BitmapDrawable bitmapDrawable = getBitmapFromMemoryCache(url);
        if(bitmapDrawable != null){
            viewHolder.imageView.setImageDrawable(bitmapDrawable);
        }else {
            BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask();
            bitmapWorkerTask.execute(url);
        }
        return convertView;
    }

    /**
     * 将一张图片存储到LruCache中。
     *
     * @param key      LruCache的键，这里传入图片的URL地址。
     * @param drawable LruCache的值，这里传入从网络上下载的BitmapDrawable对象。
     */

    public void addBitmapToMemoryCache(String key,BitmapDrawable drawable){
        if(getBitmapFromMemoryCache(key) == null){
            mMemoryCache.put(key,drawable);
        }
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。
     *
     * @param key LruCache的键，这里传入图片的URL地址。
     * @return 对应传入键的BitmapDrawable对象，或者null。
     */
    public BitmapDrawable getBitmapFromMemoryCache(String key){
        return mMemoryCache.get(key);
    }

    /**
     * 异步下载图片的任务。
     *
     * @author guolin
     */


        class BitmapWorkerTask extends AsyncTask<String,Void,BitmapDrawable>{
            String ImgUrl;
            @Override
            protected BitmapDrawable doInBackground(String... param) {
                ImgUrl = param[0];
                Bitmap bitmap = downloadBitmap(ImgUrl);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(getContext().getResources(),bitmap);
                addBitmapToMemoryCache(ImgUrl,bitmapDrawable);
                return bitmapDrawable;
            }

            @Override
            protected void onPostExecute(BitmapDrawable drawable) {
                ImageView imageView = (ImageView) mListView.findViewWithTag(ImgUrl);
                if (imageView != null && drawable != null) {
                    imageView.setImageDrawable(drawable);
                    System.out.println("1111111111111111------> 加载成功");
                }else{
                    System.out.println("1111111111111111------> 加载错乱");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageView.setImageDrawable(getContext().getDrawable(R.drawable.empty_photo));
                    }
                }
            }
        /**
         * 建立HTTP请求，并获取Bitmap对象。
         *
         * @param imageUrl 图片的URL地址
         * @return 解析后的Bitmap对象
         */
            private  Bitmap downloadBitmap(String imageUrl){
            Bitmap drawable = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                if(conn.getResponseCode()==200){
                    InputStream in = conn.getInputStream();
                     drawable = BitmapFactory.decodeStream(in);

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return drawable;
        }

        }
    class ViewHolder{
        ImageView imageView;
    }
}

