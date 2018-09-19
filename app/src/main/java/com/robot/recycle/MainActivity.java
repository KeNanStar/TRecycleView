package com.robot.recycle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.robot.recycle.adapter.NewsRecyclerAdapter;
import com.robot.recycle.common.TaskExecutor;
import com.robot.recycle.entity.NewsItem;
import com.robot.recycle.listener.IPullRefresh;
import com.robot.recycle.listener.IPushRefresh;
import com.robot.recycle.view.TRecycleView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private TRecycleView mTRecycleView;
    private NewsRecyclerAdapter mNewsRecyclerAdapter;


    private static  final int  INIT_COUNT = 30;
    private static  final int  PULL_COUNT = 10;
    private static  final int  PUSH_COUNT = 20;

    private static  final int  INIT_MODE = 0;
    private static  final int  PULL_MODE = 1;
    private static  final int  PUSH_MODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTRecycleView = (TRecycleView) findViewById(R.id.t_recycle);

        initTRecycleView();
        ((TextView)findViewById(R.id.load_data)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromNet(INIT_MODE);

            }
        });
        initData();

    }

    private void initData(){
        getDataFromNet(INIT_MODE);
    }


    private void initTRecycleView(){
        mNewsRecyclerAdapter = new NewsRecyclerAdapter(this);
        mTRecycleView.setAdapter(mNewsRecyclerAdapter);
        mTRecycleView.setPullRefresh(mPullRefresh);
        mTRecycleView.setPushRefresh(mPushRefresh);
    }


    private  void getDataFromNet(final int mode){
        TaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //模拟网络请求
                    Thread.sleep(800);
                    final ArrayList data = getData(mode);
                    TaskExecutor.runInUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (mode){
                                case INIT_MODE:
                                    mNewsRecyclerAdapter.setData(data);
                                    break;
                                case PULL_MODE:
                                    mNewsRecyclerAdapter.insertData(data);
                                    mTRecycleView.setRefresh(false);
                                    break;
                                case PUSH_MODE:
                                    mNewsRecyclerAdapter.addData(data);
                                    // mTRecycleView.set(false);
                                    break;
                                 default:

                                     break;

                            }

                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private  IPullRefresh mPullRefresh = new IPullRefresh() {
        @Override
        public void pullRefresh() {
            getDataFromNet(PULL_MODE);
        }
    };


    private IPushRefresh mPushRefresh = new IPushRefresh() {
        @Override
        public void loadMore() {
            getDataFromNet(PUSH_MODE);
        }
    };




    //自定义数据
    public ArrayList getData(int mode) {
        int count = 0;
        ArrayList<NewsItem> list = new ArrayList<NewsItem>();
        String desc = "";
        switch (mode){
            case INIT_MODE:
                count = INIT_COUNT;
                desc = "初始数据";
                break;
            case PULL_MODE:
                count = PULL_COUNT;
                desc = "下拉数据";
                break;
            case PUSH_MODE:
                count = PUSH_COUNT;
                desc = "上滑数据";
                break;
        }

        for (int i = 0; i < count; i++) {
            NewsItem item = new NewsItem();
            item.mTitle = "新闻" + i + "-" + desc;
            item.mContent = "今天晴：一年的第" + i  +"天...";
            list.add(item);
        }
        return  list;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

}
