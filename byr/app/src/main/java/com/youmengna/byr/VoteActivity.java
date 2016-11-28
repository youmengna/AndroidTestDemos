package com.youmengna.byr;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import com.youmengna.byr.adapter.VoteActivityAdapter;
import com.youmengna.byr.bean.Vote;
import com.youmengna.byr.fragment.VoteFragment;
import java.util.ArrayList;
import cn.byr.bbs.sdkdemo.R;

public class VoteActivity extends FragmentActivity {

    private VoteActivityAdapter voteActivityAdapter;
    private ArrayList<Vote> voteArrayList;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        //设置AnctionBar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 生成一个SpinnerAdapter
        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this, R.array.vote_action_list, android.R.layout.simple_spinner_dropdown_item);
        // 得到ActionBar
        // 将ActionBar的操作模型设置为NAVIGATION_MODE_LIST
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        // 为ActionBar设置下拉菜单和监听器
        actionBar.setListNavigationCallbacks(adapter, new DropDownListenser());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * 实现 ActionBar.OnNavigationListener接口
     */
    class DropDownListenser implements ActionBar.OnNavigationListener
    {
        // 得到和SpinnerAdapter里一致的字符数组
        String[] listNames = getResources().getStringArray(R.array.vote_action_list);

        /* 当选择下拉菜单项的时候，将Activity中的内容置换为对应的Fragment */
        public boolean onNavigationItemSelected(int itemPosition, long itemId)
        {
            // 生成自定的Fragment
            VoteFragment voteFragment = new VoteFragment();
            FragmentManager manager =getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle1 = new Bundle();
            if(itemId==0){
                bundle1.putString("votetype", "new");
                voteFragment.setArguments(bundle1);
            }else if(itemId==1){
                bundle1.putString("votetype", "hot");
                voteFragment.setArguments(bundle1);
            }else if(itemId==2){
                bundle1.putString("votetype", "all");
                voteFragment.setArguments(bundle1);
            }else if(itemId==3){
                bundle1.putString("votetype", "me");
                voteFragment.setArguments(bundle1);
            }else if(itemId==4){
                bundle1.putString("votetype", "join");
                voteFragment.setArguments(bundle1);
            }

            // 将Activity中的内容替换成对应选择的Fragment
            transaction.replace(R.id.mainAty, voteFragment, listNames[itemPosition]);
            transaction.commit();
            return true;
        }
    }
}
