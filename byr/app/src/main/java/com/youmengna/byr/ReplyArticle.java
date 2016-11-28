package com.youmengna.byr;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.youmengna.byr.UtilTools.UtilTool;
import com.youmengna.byr.adapter.GridViewAdapter;
import com.youmengna.byr.sdk.api.ArticleApi;
import com.youmengna.byr.sdk.api.AttachmentApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.DialogUtil;
import com.youmengna.byr.sdk.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;

import cn.byr.bbs.sdkdemo.R;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;

public class ReplyArticle extends FragmentActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    private static final String TAG = ReplyArticle.class.getName();
    protected static final int SUCEESS = 0;
    private ArticleApi mArticleApi;
    private AttachmentApi mAttachmentApi;
    private Oauth2AccessToken mAccessToken;
    private EditText ReplyArticle_Title;
    private EditText ReplyArticle_Content;
    private LinearLayout ReplyArticle_Expression;
    private LinearLayout ReplyArticle_Photograph;
    private LinearLayout ReplyArticle_Picture;
    private LinearLayout ReplyArticle_Attachment;
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<String> imageArrayList = new ArrayList<>();
    private String boardname;
    private int replyId;
    private File imgFile;
    private FrameLayout emojicons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_article);
        initView();
        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mArticleApi = new ArticleApi(mAccessToken);
    }

    public void initView() {
        gridView = (GridView) findViewById(R.id.grid);
        setGridView();
        ReplyArticle_Title = (EditText) findViewById(R.id.ReplyArticle_Title);
        ReplyArticle_Content = (EditText) findViewById(R.id.ReplyArticle_Content);
        //mEditEmojicon = (EmojiconEditText) findViewById(R.id.editEmojicon);
        //是否显示系统表情
        setEmojiconFragment(false);

        emojicons = (FrameLayout) findViewById(R.id.emojicons);

        //ReplyArticle_Content.requestFocus();
        ReplyArticle_Expression = (LinearLayout) findViewById(R.id.ReplyArticle_Expression);
        ReplyArticle_Photograph = (LinearLayout) findViewById(R.id.ReplyArticle_Photograph);
        ReplyArticle_Picture = (LinearLayout) findViewById(R.id.ReplyArticle_Picture);
        ReplyArticle_Attachment = (LinearLayout) findViewById(R.id.ReplyArticle_Attachment);
        ReplyArticle_Title.setText(getIntent().getStringExtra("topTenDatail_Title"));
        ReplyArticle_Content.setText("\r\n" + getIntent().getStringExtra("topTenDatail_Content"));
        ReplyArticle_Expression.setOnClickListener(this);
        ReplyArticle_Photograph.setOnClickListener(this);
        ReplyArticle_Picture.setOnClickListener(this);
        ReplyArticle_Attachment.setOnClickListener(this);
    }

    /**
     * 设置GirdView参数，绑定数据
     */
    private void setGridView() {
        int size = 10;
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        gridView.setColumnWidth(itemWidth); // 设置列表项宽
        gridView.setHorizontalSpacing(2); // 设置列表项水平间距
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(size); // 设置列数量=列表集合数
        gridViewAdapter = new GridViewAdapter(ReplyArticle.this, imageArrayList);
        gridView.setAdapter(gridViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.replyarticle_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ReplyArticle:
                boardname = getIntent().getStringExtra("boardname");
                replyId = getIntent().getIntExtra("ReplyArticle_Id", 0);
                String title = ReplyArticle_Title.getText().toString();
                String content = ReplyArticle_Content.getText().toString();
                mArticleApi.reply(boardname, mListener, title, content, replyId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ReplyArticle_Expression:
                if (emojicons.getVisibility() == View.GONE) {
                    emojicons.setVisibility(View.VISIBLE);
                    //隐藏软件盘
                    //1.得到InputMethodManager对象
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //2.调用hideSoftInputFromWindow方法隐藏软键盘
                    imm.hideSoftInputFromWindow(ReplyArticle_Content.getWindowToken(), 0); //强制隐藏键盘
                } else {
                    emojicons.setVisibility(View.GONE);
                    //显示软键盘
                    //1.得到InputMethodManager对象
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //2.调用showSoftInput方法显示软键盘，其中view为聚焦的view组件
                    imm.showSoftInput(ReplyArticle_Content, InputMethodManager.SHOW_FORCED);
                }
                break;
            case R.id.ReplyArticle_Photograph:
                emojicons.setVisibility(View.GONE);
                Intent iimg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imgFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + UtilTool.getTime() + ".jpg");
                iimg.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
                startActivityForResult(iimg, 3);
                break;
            case R.id.ReplyArticle_Picture:
                emojicons.setVisibility(View.GONE);
                openExplorerFile("image/*", 1);
                break;
            case R.id.ReplyArticle_Attachment:
                emojicons.setVisibility(View.GONE);
                openExplorerFile("*/*", 2);
                break;
        }
    }

    /**
     * 调用Android自带的资源管理器
     *
     * @param type
     * @param requestCode
     */
    public void openExplorerFile(String type, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);//设置类型，这里是任意类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            imageArrayList.add(imgFile + "");
            gridViewAdapter.notifyDataSetChanged();
            return;
        }
        if (resultCode == Activity.RESULT_OK)//判断是否选择，没有选择就不会继续
        {
            Uri uri = data.getData(); //得到uri，下面为将uri转为file的过程
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);
            File file = new File(img_path);
            Toast.makeText(ReplyArticle.this, file.toString(), Toast.LENGTH_SHORT).show();
            Log.e("file", file.toString());
            if (requestCode == 1) {
                imageArrayList.add(file + "");
                gridViewAdapter.notifyDataSetChanged();
            } else if (requestCode == 2) {
                /**
                 * 这里代码有问题，以后写附件部分在修改
                 */
                mAttachmentApi.upload(boardname, replyId, mListener, file);
            }
        }
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Toast.makeText(ReplyArticle.this, response, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onException(BBSException e) {
            // TODO Auto-generated method stub
            LogUtil.e(TAG, e.getMessage());
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            alertInfo();
        }
        return false;
    }

    void alertInfo() {
        DialogUtil.createDialog(ReplyArticle.this, "提示", "确定放弃发送文章!", "确 定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(ReplyArticle_Content, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(ReplyArticle_Content);
    }
}
