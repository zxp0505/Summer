package my.summer;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;

import java.util.ArrayList;
import java.util.List;

import customviews.MyWebView;
import utils.Contants;
import utils.DialogCallBack;
import utils.DialogUtil;
import utils.WeiboDialogUtils;

public class MainActivity extends AppCompatActivity {
    private MyWebView mWebView;
    private LinearLayout llWebview;
    String phone = "";
    String currentUrl = "";
    private static final String TAG = "MainActivity";
    private List<String> urlStack = new ArrayList<>();
    private List<String> urlMenberStack = new ArrayList<>();
    private XRefreshView refreshview;
    private RelativeLayout rl_title;
    private TextView tv_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Button bt = (Button) findViewById(R.id.bt);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mywebview.loadUrl(Contants.URL);
//            }
//        });
        initView();
        initWebView();
    }

    private void initView() {
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        ImageView iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        refreshview = (XRefreshView) findViewById(R.id.refreshview);
        refreshview.setPullLoadEnable(true);
        refreshview.setPinnedTime(1000);
//        refreshview.setCustomFooterView(new CustomerFooter(this));
        refreshview.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                mWebView.loadUrl(currentUrl);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
            }
        });
        mWebView = (MyWebView) findViewById(R.id.mywebview);
        mWebView.getSettings().setSupportMultipleWindows(false);
        iv_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webviewGoBack();
            }
        });
    }

    private void initWebView() {
        mWebView.loadUrl(Contants.URL);
        addWebViewClient(mWebView);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
            }

            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }

    private void addWebViewClient(MyWebView webview) {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                WeiboDialogUtils.getInstance().createLoadingDialog(MainActivity.this, "正在加载中...");

                if (url.startsWith("http") || url.startsWith("https")) {
                    Log.e(TAG, "当前的url:" + url);
                    if (url.startsWith(Contants.URL_ONLINE_CHAT)) {
                        rl_title.setVisibility(View.VISIBLE);
                        tv_title.setText("在线咨询");
                    } else {
                        rl_title.setVisibility(View.GONE);
                    }
                } else if (url.startsWith("tel")) {
                    String[] split = url.split(":");
                    final String phone = split[1];
                    DialogUtil.showDialog(MainActivity.this, phone, new DialogCallBack() {
                        @Override
                        public void OnPositiveClick() {
                            MainActivity.this.phone = phone;
                            CallPhone();
                        }

                        @Override
                        public void OnNegativeClick() {

                        }
                    });

                    return;

                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                refreshview.stopRefresh();
                WeiboDialogUtils.getInstance().closeDialog();
                currentUrl = url;
                addUrlStack(url);
                Log.e(TAG, "页面加载完成:" + url + "---currentUrl:" + currentUrl);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                if (url.startsWith("http") || url.startsWith("https")) {
                    Log.e(TAG, "当前的url:" + url);
                    view.loadUrl(url);
                    if (url.startsWith(Contants.URL_ONLINE_CHAT)) {
                        rl_title.setVisibility(View.VISIBLE);
                        tv_title.setText("在线咨询");
                    } else {
                        rl_title.setVisibility(View.GONE);
                    }
                    return false;
                } else if (url.startsWith("tel")) {
                    String[] split = url.split(":");
                    final String phone = split[1];
                    DialogUtil.showDialog(MainActivity.this, phone, new DialogCallBack() {
                        @Override
                        public void OnPositiveClick() {
                            MainActivity.this.phone = phone;
                            CallPhone();
                        }

                        @Override
                        public void OnNegativeClick() {

                        }
                    });


                }

                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.destroyWebview();
            mWebView = null;
        }
        if (urlStack != null) {
            urlStack.clear();
            urlStack = null;
        }
        if (urlMenberStack != null) {
            urlMenberStack.clear();
            urlMenberStack = null;
        }
        WeiboDialogUtils.getInstance().destory();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null) {
//                mWebView.goBack();
                webviewGoBack();

                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void CallPhone() {
        //检查权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {

                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("app需要开启权限才能使用此功能")
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
            } else {

                //申请权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        0);
            }

        } else {
            //已经拥有权限进行拨打
            call();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意了授权
                    call();

                } else {
                    //用户拒绝了授权
                    // Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

    }

    private void call() {
        if (!TextUtils.isEmpty(phone)) {
            Intent phoneIntent = new Intent(
                    "android.intent.action.CALL", Uri.parse("tel:"
                    + phone));
            startActivity(phoneIntent);
        }
    }

    // 添加url栈
    protected void addUrlStack(String url) {
        // TODO Auto-generated method stub
        Log.e(TAG, "size:" + urlStack.size());
        boolean isx = false;
        for (int i = 0; i < urlStack.size(); i++) {
            if (isx) {
                urlStack.remove(i--);
                continue;
            }
            String stack = urlStack.get(i);
            if (stack.equals(url)) {
                isx = true;
                continue;
            }
        }
        if (!isx) {
            if ((url.startsWith("http") || url.startsWith("https"))) {
                urlStack.add(url);
            }
        }

    }



    boolean isExit = false;
    private long currentTimeMillis;

    // GOBACK
    protected void webviewGoBack() {
        if (urlStack.size() < 2) {
            if (System.currentTimeMillis() - currentTimeMillis < 2000) {
                isExit = true;
            }
            currentTimeMillis = System.currentTimeMillis();
            if (isExit) {
                finish();
            } else {
                Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
                mWebView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 2000);
            }
        } else {
            urlStack.remove(urlStack.size() - 1);
            String url = urlStack.get(urlStack.size() - 1);
            mWebView.loadUrl(url);
        }
    }

}
