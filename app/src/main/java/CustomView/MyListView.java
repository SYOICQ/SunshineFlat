package CustomView;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.suyong.sunshineflat.R;

public class MyListView extends ListView implements AbsListView.OnScrollListener {

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

    private View bottomView;//尾文件
    private View headView;//头文件
    private int bottomHeight;
    private int headHeight;
    private int firstY;
    private int moveY;
    private TextView headDescription;
    private TextView headTime;
    private ProgressBar progressBar;
    private ImageView arrow;
    private final String UPDATE_AT= "UPDATE_AT";

    private final int DOWN_REFRESH = 1;
    private final int RELEASE_REFRESH = 2;
    private final int REFRESHING = 3;
    private int currentState = DOWN_REFRESH;
    private int mId;
    private SharedPreferences preferences;
    private long lastUpdateTime;

    //footerView的2种状态
    public static final int LOAD_MORE = 4;
    public static final int LOAD_MORE_ING = 5;
    public int currentStateFooter = LOAD_MORE;

    public MyListView(Context context) {
        super(context);
        Init(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    private void Init(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        bottomView = View.inflate(getContext(),R.layout.footer_view,null);
        headView = View.inflate(getContext(), R.layout.header, null);
        headDescription = headView.findViewById(R.id.description);
        headTime = headView.findViewById(R.id.updated_at);
        arrow = headView.findViewById(R.id.arrow);
        progressBar = headView.findViewById(R.id.progress_bar);
        bottomView.measure(0,0);
        bottomHeight = bottomView.getMeasuredHeight();
        bottomView.setPadding(0,-bottomHeight,0,0);
        headView.measure(0,0);
        headHeight = headView.getMeasuredHeight();
        headView.setPadding(0,-headHeight,0,0);
        this.addFooterView(bottomView);
        this.addHeaderView(headView);
        this.setOnScrollListener(this);
        refreshUpdateAtValue();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //开始滚动（SCROLL_STATE_FLING）
        // 正在滚动(SCROLL_STATE_TOUCH_SCROLL)
        // 已经停止（SCROLL_STATE_IDLE）
//        if (scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_IDLE) {
//            if (getLastVisiblePosition() == getCount() - 1 && currentStateFooter == LOAD_MORE) {
//                currentStateFooter = LOAD_MORE_ING;
//                bottomView.setPadding(0, 0, 0, 0);
//                setSelection(getCount());
//                if (onLoadMoreListener != null) {
//                    onLoadMoreListener.loadMore();
//                }
//            }
//        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        switch(ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                firstY = (int)ev.getY();
                if(currentState==REFRESHING){
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                moveY=(int)ev.getY();
                int distance = moveY - firstY;
                if(distance>0&&getFirstVisiblePosition()==0&&currentState!=REFRESHING){

                    int paddingTop = -headHeight + distance;
                    if(paddingTop<0 && currentState != DOWN_REFRESH){
                        currentState = DOWN_REFRESH;
                        headDescription.setText("下拉刷新");
                        progressBar.setVisibility(View.GONE);
                        rotateArrow();
                    }else if(paddingTop>0 && currentState != RELEASE_REFRESH){
                        currentState = RELEASE_REFRESH;
                        headDescription.setText("释放刷新");
                        progressBar.setVisibility(View.GONE);
                        rotateArrow();
                    }
                    if(paddingTop>0){
                        headView.setPadding(0,0,0,0);
                    }else{
                        headView.setPadding(0,paddingTop,0,0);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(currentState==RELEASE_REFRESH){
                    currentState = REFRESHING;
                    headView.setPadding(0,0,0,0);
                    headDescription.setText("刷新中...");
                    arrow.setVisibility(View.GONE);
                    arrow.clearAnimation();
                    progressBar.setVisibility(View.VISIBLE);
                    if(onRefreshListener!=null){
                        onRefreshListener.refresh();
                    }
                    return false;
                }else if(currentState ==DOWN_REFRESH){
                    headView.setPadding(0,-headHeight,0,0);
                }
                break;
        }
        if (currentState == RELEASE_REFRESH||currentState==DOWN_REFRESH) {
            refreshUpdateAtValue();
        }
        return super.onTouchEvent(ev);
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
        headTime.setText(updateAtValue);
    }

    //旋转箭头
    private void rotateArrow() {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        float fromDegrees = 0f;
        float toDegrees = 0f;
        if (currentState== DOWN_REFRESH) {
            fromDegrees = 180f;
            toDegrees = 360f;
        } else if (currentState == RELEASE_REFRESH) {
            fromDegrees = 0f;
            toDegrees = 180f;
        }
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees,
                pivotX, pivotY);
        animation.setDuration(100);
        animation.setFillAfter(true);
        arrow.startAnimation(animation);
    }

    public interface OnRefreshListener {
        void refresh();
    }

    private OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener,int mId) {
        this.onRefreshListener = onRefreshListener;
        this.mId = mId;
    }

    public interface OnLoadMoreListener {
        void loadMore();
    }

    private OnLoadMoreListener onLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void finishRefresh(){
        arrow.setVisibility(VISIBLE);
        arrow.clearAnimation();
        headDescription.setText("下拉刷新");
        progressBar.setVisibility(GONE);
        headView.setPadding(0,-headHeight,0,0);
        currentState = DOWN_REFRESH;
        preferences.edit()
                .putLong(UPDATE_AT + mId, System.currentTimeMillis()).commit();
    }

    public void finishLoadMore() {
        currentStateFooter = LOAD_MORE;
        bottomView.setPadding(0, -bottomHeight, 0, 0);
    }
}
