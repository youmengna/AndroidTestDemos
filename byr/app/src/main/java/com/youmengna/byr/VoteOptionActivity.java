package com.youmengna.byr;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.youmengna.byr.UtilTools.UtilTool;
import com.youmengna.byr.adapter.VoteOptionActivityAdapter;
import com.youmengna.byr.bean.Vote;
import com.youmengna.byr.bean.VoteOption;
import com.youmengna.byr.sdk.api.VoteApi;
import com.youmengna.byr.sdk.auth.Oauth2AccessToken;
import com.youmengna.byr.sdk.exception.BBSException;
import com.youmengna.byr.sdk.net.RequestListener;
import com.youmengna.byr.sdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.byr.bbs.sdkdemo.R;

public class VoteOptionActivity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private VoteOptionActivityAdapter mVoteOptionActivityAdapter;
    private ListView voteoptionlv;
    private Button VoteSubmit;
    private Vote voteDetail;
    private TextView vote_option_title;
    private TextView vote_option_endtime;
    private int Vid;
    private boolean isMulti;
    private VoteApi mVoteApi;
    private ArrayList<VoteOption> VoteOptionList = new ArrayList<>();
    private Oauth2AccessToken mAccessToken;
    private static final String TAG = VoteOptionActivity.class.getName();
    protected static final int SUCEESS = 0;
    private List<VoteOption> voted;
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                JSONObject jsonObject = null;
                Gson gson = new Gson();
                Vote mVote=gson.fromJson(response,Vote.class);
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject.has("code")) {
                    Toast.makeText(VoteOptionActivity.this, "No such id.", Toast.LENGTH_LONG).show();
                }

            }
        }

        @Override
        public void onException(BBSException e) {
            LogUtil.e(TAG, e.getMessage());

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_option);

        //设置ActionBar
        ActionBar actionBar=getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initView();
        Bundle bundle = this.getIntent().getExtras();
        Vid = bundle.getInt("Vid");
        voteDetail = (Vote) bundle.getSerializable("voteDatail");
        /*voted=voteDetail.getVoted();
        if(voted!=null){
            VoteSubmit.setEnabled(false);
        }else {
            VoteSubmit.setEnabled(true);
        }*/
        vote_option_title.setText(voteDetail.getTitle());
        Date d = new Date((long)voteDetail.getEnd()*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        vote_option_endtime.setText("截止日期："+sdf.format(d));

        VoteOptionList.addAll(voteDetail.getOptions());
        mVoteOptionActivityAdapter=new VoteOptionActivityAdapter(VoteOptionActivity.this,VoteOptionList);
        voteoptionlv.setAdapter(mVoteOptionActivityAdapter);
        int temp = bundle.getInt("isMulti");
        if (temp == 0) {
            isMulti = false;
            mVoteOptionActivityAdapter.setisMulti(isMulti);
        } else if (temp == 1) {
            isMulti = true;
            mVoteOptionActivityAdapter.setisMulti(isMulti);
        }
        mVoteOptionActivityAdapter.setvote_count(voteDetail.getVote_count());
        voteoptionlv.setOnItemClickListener(this);

    }

    void initView(){
        vote_option_title= (TextView) findViewById(R.id.vote_option_title);
        vote_option_endtime= (TextView) findViewById(R.id.vote_option_endtime);
        voteoptionlv = (ListView) findViewById(R.id.voteoptionlist);
        VoteSubmit = (Button) findViewById(R.id.VoteSubmit);
        VoteSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.VoteSubmit:
                int size = VoteOptionList.size();
                ArrayList<Integer> viidarray=new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    if(VoteOptionList.get(i).isChecked()){
                        viidarray.add(VoteOptionList.get(i).getViid());
                    }
                }
                int[] viid = UtilTool.ArrayListToArray(viidarray);
                mAccessToken = AccessTokenKeeper.readAccessToken(VoteOptionActivity.this);
                mVoteApi = new VoteApi(mAccessToken);
                mVoteApi.vote(Vid, viid, isMulti, mListener);
                VoteSubmit.setEnabled(false);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VoteOption voteOption = VoteOptionList.get(position);
        if (isMulti) {
            if(voteOption.isChecked())
            {
                voteOption.setChecked(false);
            }else {
                voteOption.setChecked(true);
            }
        }else {
            for (int i=0;i<VoteOptionList.size();i++){
                VoteOptionList.get(i).setChecked(false);
            }
            voteOption.setChecked(true);
        }
        mVoteOptionActivityAdapter.notifyDataSetChanged();
    }
}
