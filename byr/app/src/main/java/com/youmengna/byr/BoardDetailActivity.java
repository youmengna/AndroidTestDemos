package com.youmengna.byr;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youmengna.byr.adapter.BoardDetailAdapter;
import com.youmengna.byr.bean.Article;
import com.youmengna.byr.bean.Board;
import com.youmengna.byr.sdk.api.BoardApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.byr.bbs.sdkdemo.R;

public class BoardDetailActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener {

    private BoardDetailAdapter boardDetailAdapter;
    private ArrayList<Article> boardArticleArrayList = new ArrayList<>();
    private static final String TAG = BoardDetailActivity.class.getName();
    protected static final int SUCEESS = 0;
    private BoardApi mBoardApi;
    private Oauth2AccessToken mAccessToken;
    private int pagetotal = 0;
    private int curpage = 1;
    private ListView boardDetailListView;
    private TextView boardDetailFirstPage;
    private TextView boardDetailPrePage;
    private TextView boardDetailNextPage;
    private TextView boardDetailLastPage;
    private TextView boardDetailPageOfSum;
    private EditText boardDetailPageEdit;
    private TextView boardDetailPageGo;
    private Board board;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);
        initView();

        Intent intent=getIntent();
        board=(Board) intent.getSerializableExtra("board");
        //设置ActionBar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(board.getName());

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        mBoardApi = new BoardApi(mAccessToken);
        String boardName = board.getName();
        mBoardApi.getBoardInfo(boardName, 1, firstListener);
    }

    public void initView() {
        boardDetailListView = (ListView) findViewById(R.id.boardDetailListView);
        boardDetailListView.setOnItemClickListener(this);
        boardDetailFirstPage = (TextView) findViewById(R.id.boardDetailFirstPage);
        boardDetailPrePage = (TextView) findViewById(R.id.boardDetailPrePage);
        boardDetailNextPage = (TextView) findViewById(R.id.boardDetailNextPage);
        boardDetailLastPage = (TextView) findViewById(R.id.boardDetailLastPage);
        boardDetailPageOfSum = (TextView) findViewById(R.id.boardDetailPageOfSum);
        boardDetailPageEdit = (EditText) findViewById(R.id.boardDetailPageEdit);
        boardDetailPageGo = (TextView) findViewById(R.id.boardDetailPageGo);

        boardDetailFirstPage.setOnClickListener(this);
        boardDetailPrePage.setOnClickListener(this);
        boardDetailNextPage.setOnClickListener(this);
        boardDetailLastPage.setOnClickListener(this);
        boardDetailPageGo.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private RequestListener firstListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Board mBoard = gson.fromJson(response, Board.class);
                firsthandler.obtainMessage(SUCEESS, mBoard).sendToTarget();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    Handler firsthandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                pagetotal = ((Board) (msg.obj)).getPagination().getPage_all_count();
                curpage = ((Board) (msg.obj)).getPagination().getPage_current_count();
                boardArticleArrayList.addAll(((Board) (msg.obj)).getArticle());
                boardDetailAdapter = new BoardDetailAdapter(BoardDetailActivity.this, boardArticleArrayList,curpage,board);
                boardDetailListView.setAdapter(boardDetailAdapter);
                initPage();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.board_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.boardDetailRefresh:
                mBoardApi.getBoardInfo(board.getName(), curpage, mListener);
                //刷新操作
                return true;
            case R.id.boardDetailSendMail:

                return true;
            case R.id.boardDetailFind:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void initPage() {
        //初始化页数
        /*pagetotal = MailboxInfo.getPagination().getPage_all_count();
        curpage = MailboxInfo.getPagination().getPage_current_count();*/
        boardDetailFirstPage.setEnabled(false);
        boardDetailPrePage.setEnabled(false);
        if (pagetotal <= 1) {
            boardDetailNextPage.setEnabled(false);
            boardDetailLastPage.setEnabled(false);
        } else {
            boardDetailNextPage.setEnabled(true);
            boardDetailLastPage.setEnabled(true);
        }
        boardDetailPageOfSum.setText(curpage + "/" + pagetotal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boardDetailFirstPage:
                curpage = 1;
                boardDetailFirstPage.setEnabled(false);
                boardDetailPrePage.setEnabled(false);
                if (pagetotal > 1) {
                    boardDetailNextPage.setEnabled(true);
                    boardDetailLastPage.setEnabled(true);
                }
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    //网络请求
                    mBoardApi.getBoardInfo(board.getName(), curpage, mListener);
                }
                break;
            case R.id.boardDetailPrePage:
                curpage--;
                if (curpage == 1) {
                    boardDetailFirstPage.setEnabled(false);
                    boardDetailPrePage.setEnabled(false);
                }
                if (pagetotal > 1) {
                    boardDetailNextPage.setEnabled(true);
                    boardDetailLastPage.setEnabled(true);
                }
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mBoardApi.getBoardInfo(board.getName(), curpage, mListener);
                }
                break;
            case R.id.boardDetailNextPage:
                curpage++;
                if (curpage == pagetotal) {
                    boardDetailNextPage.setEnabled(false);
                    boardDetailLastPage.setEnabled(false);
                }
                boardDetailFirstPage.setEnabled(true);
                boardDetailPrePage.setEnabled(true);
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mBoardApi.getBoardInfo(board.getName(), curpage, mListener);
                }
                break;
            case R.id.boardDetailLastPage:
                curpage = pagetotal;
                boardDetailNextPage.setEnabled(false);
                boardDetailLastPage.setEnabled(false);
                boardDetailFirstPage.setEnabled(true);
                boardDetailPrePage.setEnabled(true);
                if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mBoardApi.getBoardInfo(board.getName(), curpage, mListener);
                }
                break;
            case R.id.boardDetailPageGo:
                curpage = Integer.parseInt(boardDetailPageEdit.getText().toString());
                boardDetailPageEdit.setText("");
                if (curpage < 1 || curpage > pagetotal) {
                    Toast.makeText(BoardDetailActivity.this, "请输入正确的页码！", Toast.LENGTH_SHORT).show();
                } else if (mAccessToken != null && mAccessToken.isSessionValid()) {
                    mBoardApi.getBoardInfo(board.getName(), curpage, mListener);
                }
                break;
        }
        boardDetailPageOfSum.setText(curpage + "/" + pagetotal);
    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                Gson gson = new Gson();
                Board mBoard = gson.fromJson(response, Board.class);
                List<Article> articleInfo = mBoard.getArticle();
                handler.obtainMessage(SUCEESS, articleInfo).sendToTarget();
            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SUCEESS) {
                boardArticleArrayList.clear();
                boardArticleArrayList.addAll((List<Article>) msg.obj);
                boardDetailAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Article mArticle=boardArticleArrayList.get(position);
        Intent intent=new Intent(BoardDetailActivity.this,TopTenDetail.class);
        intent.putExtra("article",mArticle);
        startActivity(intent);
    }
}
