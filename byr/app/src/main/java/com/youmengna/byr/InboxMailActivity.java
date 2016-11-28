package com.youmengna.byr;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import com.youmengna.byr.fragment.InboxMailFragment;
import cn.byr.bbs.sdkdemo.R;

public class InboxMailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_mail);

        //设置ActionBar
        ActionBar actionBar=getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // 生成一个SpinnerAdapter
        SpinnerAdapter adapter = ArrayAdapter.createFromResource(this, R.array.inbox_action_list, android.R.layout.simple_spinner_dropdown_item);
        // 得到ActionBar
        // 将ActionBar的操作模型设置为NAVIGATION_MODE_LIST
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        // 为ActionBar设置下拉菜单和监听器
        actionBar.setListNavigationCallbacks(adapter, new DropDownListenser());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inbox_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.InboxWriteMail:
                Intent intent = new Intent(InboxMailActivity.this, SendMailActivity.class);
                startActivity(intent);
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
        String[] listNames = getResources().getStringArray(R.array.inbox_action_list);

        /* 当选择下拉菜单项的时候，将Activity中的内容置换为对应的Fragment */
        public boolean onNavigationItemSelected(int itemPosition, long itemId)
        {
            // 生成自定的Fragment
            InboxMailFragment inboxMailFragment = new InboxMailFragment();
            FragmentManager manager =getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Bundle bundle1 = new Bundle();
            if(itemId==0){
                bundle1.putString("boxtype", "inbox");
                inboxMailFragment.setArguments(bundle1);
            }else if(itemId==1){
                bundle1.putString("boxtype", "outbox");
                inboxMailFragment.setArguments(bundle1);
            }else if(itemId==2){
                bundle1.putString("boxtype", "deleted");
                inboxMailFragment.setArguments(bundle1);
            }

            // 将Activity中的内容替换成对应选择的Fragment
            transaction.replace(R.id.inboxContainer, inboxMailFragment, listNames[itemPosition]);
            transaction.commit();
            return true;
        }
    }
}
