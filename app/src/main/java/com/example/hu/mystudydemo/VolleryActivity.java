package com.example.hu.mystudydemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hu.mystudydemo.tools.BitmapCache;
import com.example.hu.mystudydemo.tools.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Volley使用
 */
public class VolleryActivity extends AppCompatActivity {
    private TextView tv;
    private ImageView iv;
    private NetworkImageView network_image_view;
    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vollery);
        tv = (TextView) findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.iv);
        network_image_view = (NetworkImageView) findViewById(R.id.network_image_view);
        mQueue = Volley.newRequestQueue(this);
//        onStringRequest();
//        onJsonObjectRequest();
//        onImageRequest();
        onImageLoader();
        onNetWorkImageView();
        onXmlRequest();
    }


    private void onXmlRequest() {
        XMLRequest xmlRequest = new XMLRequest("http://flash.weather.com.cn/wmaps/xml/china.xml", new Response.Listener<XmlPullParser>() {
            @Override
            public void onResponse(XmlPullParser response) {
                try {
                    int eventType = response.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                String nodeName = response.getName();
                                if ("city".equals(nodeName)) {
                                    String pName = response.getAttributeValue(0);
                                    Log.d("TAG", "pName is:" + pName);
                                }
                                break;
                        }
                        eventType = response.next();

                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("TAG", volleyError.getMessage(), volleyError);
            }
        });
        mQueue.add(xmlRequest);
    }

    private void onNetWorkImageView() {
        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        network_image_view.setDefaultImageResId(R.mipmap.ic_launcher);
        network_image_view.setErrorImageResId(R.mipmap.ic_launcher);
        network_image_view.setImageUrl("http://p8.qhimg.com/t013cf9b713bea35863.jpg", imageLoader);

    }


    private void onImageLoader() {
        ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader
                .getImageListener(iv, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        imageLoader.get("http://p6.qhimg.com/t01b7331e5256addb1f.jpg", listener, 200, 200);
    }


    private void onImageRequest() {
        ImageRequest imageRequest = new ImageRequest("http://pimages1.tianjimedia.com/resources/product/20150706/MN91APS8TMF0ZX9QRDQM5U1669473467.jpg", new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                iv.setImageBitmap(bitmap);
            }
        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                iv.setImageResource(R.mipmap.ic_launcher);
            }
        });
        mQueue.add(imageRequest);

    }


    private void onJsonObjectRequest() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://ipad.product.yesky.com/iphone/product/detail.json?productid=885143",
                null, new Response.Listener() {
            @Override
            public void onResponse(Object o) {
                tv.setText(o.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                tv.setText("出错啦！" + volleyError.getMessage());
            }
        });
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("http://ipad.product.yesky.com/iphone/product/detail.json?productid=885143", new Response.Listener() {
            @Override
            public void onResponse(Object o) {
                tv.setText(o.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                tv.setText("出错啦！" + volleyError.getMessage());
            }
        });
//      mQueue.add(jsonObjectRequest);
        mQueue.add(jsonArrayRequest);

    }


    /**
     * 字符串请求
     */
    private void onStringRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://www.baidu.com",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        tv.setText(s);
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }) {

            /**
             * 重写getParams()方法
             * 设置POST参数
             * @return
             * @throws AuthFailureError
             */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("params1", "value1");
                map.put("params2", "value2");
                return map;
            }
        };
        mQueue.add(stringRequest);
    }


}
