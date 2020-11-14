package CustomView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.suyong.sunshineflat.R;

import java.security.PublicKey;

public class PullToRefreshLayout extends LinearLayout implements View.OnTouchListener {

    public String TAG = "PullToRefreshLayout";

    public ArrayAdapter<String> adapter;

    //下拉头
    private View headView;
    //需要下拉刷新的listView
    private ListView listView;
    //刷新时的进度条
    private ProgressBar progressBar;
    private TextView description;
    //指示下拉和释放的箭头
    private ImageView arrow;
    //下拉的释放的文字描述
    private TextView updateAt;
    //下拉头的布局参数
    private MarginLayoutParams params;
    //上次更新时间的毫秒指
    private long lastUpdateTime;

    private int mId = -1;

    //下拉头的高度
    private int hideHeadHeight;
    //当前的状态
    private int currentStatus = STATUS_REFRESH_FINISHED;
    //上一次的状态
    private int lastStatus = currentStatus;

    //手机按下的纵坐标
    private float yDown;
    //可以判断为移动的最大值
    private int touchSlop;
    //
    private boolean loadOnce ;
    //是否可以下拉的标志
    private boolean ablePullFlag;

    //下拉刷新的回调接口
    private PullToRefreshListener mListener;

    //下拉状态
    public static final int STATUS_PULL_TO_REFRESH = 0;
    //释放立即刷新状态
    public static final int STATUS_RELEASE_TO_REFRESH = 1;
    //正在刷新状态
    public static final int STATUS_REFRESHING = 2;
    //刷新完成或未刷新状态
    public static final int STATUS_REFRESH_FINISHED = 3;
    //下拉头部的回滚速度
    public static final int SCROLL_SPEED = -20;
    //一分钟的毫秒值
    public static final long ONE_MINUTE = 60 * 1000;
    //一小时的毫秒值
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    //一天的毫秒值
    public static final long ONE_DAY = 24 * ONE_HOUR;
    //一月的毫秒值
    public static final long ONE_MONTH = 30 * ONE_DAY;
    //一年的毫秒值
    public static final long ONE_YEAR = 12 * ONE_MONTH;
    //上次更新事件的字符串常量
    private static final String UPDATE_AT = "updated_at";
    //存储上次的更新时间
    private SharedPreferences preferences;
    //下拉的距离
    private float pullLength = 0;
    //是否可以下拉
    private boolean pullFlag = true;


    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "进入RefreshableView构造方法");
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        headView = LayoutInflater.from(context).inflate(R.layout.header,null,true);
        progressBar = (ProgressBar)headView.findViewById(R.id.progress_bar);
        arrow = (ImageView)headView.findViewById(R.id.arrow);
        description = headView.findViewById(R.id.description);
        updateAt = headView.findViewById(R.id.updated_at);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        refreshUpdateAtValue();
        setOrientation(VERTICAL);
        addView(headView,0);
    }

    //刷新下拉头中的更跟新时间的文字描述
    private void refreshUpdateAtValue() {
        lastUpdateTime = preferences.getLong(UPDATE_AT+mId,-1);
        long currenTime = System.currentTimeMillis();
        long timePassed = currenTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue = "";
        if(lastUpdateTime == -1){
            updateAtValue = getResources().getString(R.string.not_updated_yet);
        }else if(timePassed<0){
            updateAtValue = getResources().getString(R.string.time_error);
        }else if (timePassed < ONE_MINUTE) {
            updateAtValue = getResources().getString(R.string.updated_just_now);
        }else if(timePassed < ONE_HOUR){
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + "分钟";
            updateAtValue = String.format(
                    getResources().getString(R.string.updated_at), value);
        }else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            String value = timeIntoFormat + "小时";
            updateAtValue = String.format(
                    getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_MONTH) {
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + "天";
            updateAtValue = String.format(
                    getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + "个月";
            updateAtValue = String.format(
                    getResources().getString(R.string.updated_at), value);
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + "年";
            updateAtValue = String.format(
                    getResources().getString(R.string.updated_at), value);
        }
        updateAt.setText(updateAtValue);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean changed,int l,int t,int r,int b){
        super.onLayout(changed,l,t,r,b);
        Log.d(TAG, "进入onLayout方法");
        if(changed && !loadOnce){
            Log.d(TAG,"进入onLayout方法中的if语句");
            hideHeadHeight = - headView.getHeight();
            params = (MarginLayoutParams)headView.getLayoutParams();
            params.topMargin = hideHeadHeight;
            listView = (ListView) getChildAt(1);
            listView.setOnTouchListener(this);
            loadOnce = true;
            Log.d(TAG, "离开onLayout方法中的if语句");
        }
        Log.d(TAG, "离开onLayout方法");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIsAbleToPull(event);
        if(ablePullFlag){
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    Log.d(TAG, "进入MotionEvent.ACTION_DOWN，yDown：" + yDown);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = event.getRawY();
                    int distance = (int)(yMove-yDown);
                    Log.d(TAG, "进入MotionEvent.ACTION_MOVE，yMove：" + yMove);
                    if(distance<=0&&params.topMargin<=hideHeadHeight) return false;
                    if(distance<touchSlop) return false;
                    if (currentStatus != STATUS_REFRESHING) {
                        if (params.topMargin > 0) {
                            currentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            currentStatus = STATUS_PULL_TO_REFRESH;
                        }
                        // 通过偏移下拉头的topMargin值，来实现下拉效果
                        params.topMargin = (distance / 2)
                                + hideHeadHeight;
                        headView.setLayoutParams(params);
                    }
                     break;
                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        // 松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                        new RefreshingTask().execute();
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        // 松手时如果是下拉状态，就去调用隐藏下拉头的任务
                        new HideHeaderTask().execute();
                    }
                    break;
            }
            if (currentStatus == STATUS_PULL_TO_REFRESH
                    || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                Log.d(TAG, "进入“时刻记得更新下拉头中的信息”的if语句");
                updateHeadView();
                // 当前正处于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态
                listView.setPressed(false);
                listView.setFocusable(false);
                listView.setFocusableInTouchMode(false);
                lastStatus = currentStatus;
                // 当前正处于下拉或释放状态，通过返回true屏蔽掉ListView的滚动事件
                Log.d(TAG, "离开“时刻记得更新下拉头中的信息”的if语句");
                return true;
            }
        }
        return false;
    }

    public void setOnRefreshListener(PullToRefreshListener listener, int id) {
        mListener = listener;
        mId = id;
    }

    /**
     * 更新头布局
     */
    private void updateHeadView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH) {
                //下拉可以刷新
                description.setText(getResources().getString(
                        R.string.pull_to_refresh));
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                //释放以刷新
                description.setText(getResources().getString(
                        R.string.release_to_refresh));
                arrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                rotateArrow();
            } else if (currentStatus == STATUS_REFRESHING) {
                //正在刷新
                description.setText(getResources().getString(
                        R.string.refreshing));
                progressBar.setVisibility(View.VISIBLE);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
            }
            refreshUpdateAtValue();
            Log.d(TAG, "离开updateHeaderView方法");
        }
    }

    //旋转箭头
    private void rotateArrow() {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentStatus == STATUS_PULL_TO_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees,
                pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
        Log.d(TAG, "离开rotateArrow方法");
    }

    public void finishRefreshing() {
        Log.d(TAG, "进入finishRefreshing方法");
        currentStatus = STATUS_REFRESH_FINISHED;
        preferences.edit()
                .putLong(UPDATE_AT + mId, System.currentTimeMillis()).commit();
        new HideHeaderTask().execute();
        Log.d(TAG, "离开finishRefreshing方法");
    }

    private void setIsAbleToPull(MotionEvent event) {
        View firstChild = listView.getChildAt(0);
        if(firstChild!=null){
            int firstVisiblePos = listView.getFirstVisiblePosition();
            if(firstVisiblePos == 0 && firstChild.getTop() == 0){
                if(!ablePullFlag){
                    yDown = event.getRawY();
                }
                // 如果首个元素的上边缘，距离父布局值为0，
                // 就说明ListView滚动到了最顶部，此时应该允许下拉刷新
                ablePullFlag = true;
            }else{
                if(params.topMargin!=hideHeadHeight){
                    params.topMargin = hideHeadHeight;
                    headView.setLayoutParams(params);
                }
                ablePullFlag = false;
            }
        }else{
            ablePullFlag = true;
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas){
        super.dispatchDraw(canvas);
    }

    /**
     * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
     */
    public interface PullToRefreshListener {
        //刷新时会去回调此方法
        void onRefresh();
    }

    class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            int topMargin = params.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= hideHeadHeight) {
                    topMargin = hideHeadHeight;
                    break;
                }
                publishProgress(topMargin);
                sleep(10);
            }
            return topMargin;
        }
        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            params.topMargin = topMargin[0];
            headView.setLayoutParams(params);
        }
        @Override
        protected void onPostExecute(Integer topMargin) {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            params.topMargin = topMargin;
            headView.setLayoutParams(params);
            currentStatus = STATUS_REFRESH_FINISHED;
        }
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private class RefreshingTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            int topMargin = params.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    topMargin = 0;
                    break;
                }
                publishProgress(topMargin);
                sleep(10);
            }
            currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            if (mListener != null) {
                mListener.onRefresh();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            updateHeadView();
            params.topMargin = topMargin[0];
            headView.setLayoutParams(params);
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            Log.d(TAG, "---进入RefreshingTask的onPostExecute方法");
        }


    }
    public void initAdapter(ArrayAdapter<String> adapter) {
        // TODO Auto-generated method stub
        this.adapter = adapter;
    }
}
