package CustomView;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.suyong.sunshineflat.R;

/**
 * 首先在RefreshableView的构造函数中动态添加了刚刚定义的pull_refresh这个布局作为下拉头，
 * 然后在onLayout方法中将下拉头向上偏移出了屏幕
 * ，再给ListView注册了touch事件。之后每当手指在ListView上滑动时，onTouch方法就会执行
 * 。在onTouch方法中的第一行就调用了setIsAbleToPull方法来判断ListView是否滚动到了最顶部
 * ，只有滚动到了最顶部才会执行后面的代码，否则就视为正常的ListView滚动
 * ，不做任何处理。当ListView滚动到了最顶部时，如果手指还在向下拖动，就会改变下拉头的偏移值
 * ，让下拉头显示出来，下拉的距离设定为手指移动距离的1/2，这样才会有拉力的感觉
 * 。如果下拉的距离足够大，在松手的时候就会执行刷新操作，如果距离不够大，就仅仅重新隐藏下拉头。 具体的刷新操作会在RefreshingTask中进行，
 * 其中在doInBackground方法中回调了PullToRefreshListener接口的onRefresh方法
 * ，这也是大家在使用RefreshableView时必须要去实现的一个接口，因为具体刷新的逻辑就应该写在onRefresh方法中，后面会演示使用的方法。
 * 另外每次在下拉的时候都还会调用updateHeaderView方法来改变下拉头中的数据
 * ，比如箭头方向的旋转，下拉文字描述的改变等。更加深入的理解请大家仔细去阅读RefreshableView中的代码。
 * 
 */
/**
 * @author honey 项目启动执行顺序： 1、首先执行RefreshableView构造方法
 *         2、执行setOnRefreshListener(PullToRefreshListener, int)方法
 *         3、执行onLayout(boolean, int, int, int, int)方法
 */
public class RefreshableView1 extends LinearLayout implements OnTouchListener {

	public String TAG = "RefreshableView";

	public ArrayAdapter<String> adapter;
	/**
	 * 下拉状态
	 */
	public static final int STATUS_PULL_TO_REFRESH = 0;
	/**
	 * 释放立即刷新状态
	 */
	public static final int STATUS_RELEASE_TO_REFRESH = 1;
	/**
	 * 正在刷新状态
	 */
	public static final int STATUS_REFRESHING = 2;
	/**
	 * 刷新完成或未刷新状态
	 */
	public static final int STATUS_REFRESH_FINISHED = 3;
	/**
	 * 下拉头部回滚的速度
	 */
	public static final int SCROLL_SPEED = -20;
	/**
	 * 一分钟的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_MINUTE = 60 * 1000;
	/**
	 * 一小时的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_HOUR = 60 * ONE_MINUTE;
	/**
	 * 一天的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_DAY = 24 * ONE_HOUR;
	/**
	 * 一月的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_MONTH = 30 * ONE_DAY;
	/**
	 * 一年的毫秒值，用于判断上次的更新时间
	 */
	public static final long ONE_YEAR = 12 * ONE_MONTH;
	/**
	 * 上次更新时间的字符串常量，用于作为SharedPreferences的键值
	 */
	private static final String UPDATED_AT = "updated_at";
	/**
	 * 下拉刷新的回调接口
	 */
	private PullToRefreshListener mListener;
	/**
	 * 用于存储上次更新时间
	 */
	private SharedPreferences preferences;
	/**
	 * 下拉头的View
	 */
	private View header;
	/**
	 * 需要去下拉刷新的ListView
	 */
	private ListView listView;
	/**
	 * 刷新时显示的进度条
	 */
	private ProgressBar progressBar;
	/**
	 * 指示下拉和释放的箭头
	 */
	private ImageView arrow;
	/**
	 * 指示下拉和释放的文字描述
	 */
	private TextView description;
	/**
	 * 上次更新时间的文字描述
	 */
	private TextView updateAt;
	/**
	 * 下拉头的布局参数
	 */
	private MarginLayoutParams headerLayoutParams;
	/**
	 * 上次更新时间的毫秒值
	 */
	private long lastUpdateTime;
	/**
	 * 为了防止不同界面的下拉刷新在上次更新时间上互相有冲突，使用id来做区分
	 */
	private int mId = -1;
	/**
	 * 下拉头的高度
	 */
	private int hideHeaderHeight;
	/**
	 * 当前处理什么状态，可选值有STATUS_PULL_TO_REFRESH, STATUS_RELEASE_TO_REFRESH,
	 * STATUS_REFRESHING 和 STATUS_REFRESH_FINISHED
	 */
	private int currentStatus = STATUS_REFRESH_FINISHED;;
	/**
	 * 记录上一次的状态是什么，避免进行重复操作
	 */
	private int lastStatus = currentStatus;
	/**
	 * 手指按下时的屏幕纵坐标
	 */
	private float yDown;
	/**
	 * 在被判定为滚动之前用户手指可以移动的最大值。
	 */
	private int touchSlop;
	/**
	 * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
	 */
	private boolean loadOnce;
	/**
	 * 当前是否可以下拉，只有ListView滚动到头的时候才允许下拉
	 */
	private boolean ableToPull;

	/**
	 * 下拉刷新控件的构造函数，会在运行时动态添加一个下拉头的布局。 项目启动首先进入这个构造方法
	 * 
	 * @param context
	 * @param attrs
	 */
	public RefreshableView1(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "进入RefreshableView构造方法");
		/**
		 * 获得SharedPreferences对象
				*/
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		header = LayoutInflater.from(context).inflate(R.layout.header,
				null, true);
		progressBar = (ProgressBar) header.findViewById(R.id.progress_bar);
		arrow = (ImageView) header.findViewById(R.id.arrow);
		description = (TextView) header.findViewById(R.id.description);
		updateAt = (TextView) header.findViewById(R.id.updated_at);
		/**
		 * getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件
		 */
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		refreshUpdatedAtValue();
		setOrientation(VERTICAL);
		/**
		 * addView 动态给Activity添加View组件
		 */
		addView(header, 0);
		Log.d(TAG, "离开RefreshableView构造方法");
	}

	/**
	 * 1、onLayout()是ViewGroup类的抽象方法，子类中必须重写，作用：
	 * 通过调用其children的layout函数来设置子视图相对与父视图中的位置。
	 * 2、进行一些关键性的初始化操作，比如：将下拉头向上偏移进行隐藏，给ListView注册touch事件。
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		Log.d(TAG, "进入onLayout方法");
		if (changed && !loadOnce) {
			Log.d(TAG, "进入onLayout方法中的if语句");
			hideHeaderHeight = -header.getHeight();
			headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
			headerLayoutParams.topMargin = hideHeaderHeight;
			/**
			 * getChildAt(index)是获取某个指定position的view。
			 * 注意：在ListView中，使用getChildAt(index)的取值，只能是当前可见区域（列表可滚动）的子项！
			 */
			listView = (ListView) getChildAt(1);
			/**
			 * 监听用户触摸屏事件
			 */
			listView.setOnTouchListener(this);
			loadOnce = true;
			Log.d(TAG, "离开onLayout方法中的if语句");
		}
		Log.d(TAG, "离开onLayout方法");
	}

	/**
	 * 当ListView被触摸时调用，其中处理了各种下拉刷新的具体逻辑。
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d(TAG, "进入onTouch方法");
		setIsAbleToPull(event);
		if (ableToPull) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				yDown = event.getRawY();
				Log.d(TAG, "进入MotionEvent.ACTION_DOWN，yDown：" + yDown);
				break;
			case MotionEvent.ACTION_MOVE:
				float yMove = event.getRawY();
				int distance = (int) (yMove - yDown);
				Log.d(TAG, "进入MotionEvent.ACTION_MOVE，yMove：" + yMove);
				// 如果手指是下滑状态，并且下拉头是完全隐藏的，就屏蔽下拉事件
				if (distance <= 0
						&& headerLayoutParams.topMargin <= hideHeaderHeight) {
					return false;
				}
				Log.d(TAG, "touchSlop：" + touchSlop);
				if (distance < touchSlop) {
					return false;
				}
				if (currentStatus != STATUS_REFRESHING) {
					if (headerLayoutParams.topMargin > 0) {
						currentStatus = STATUS_RELEASE_TO_REFRESH;
					} else {
						currentStatus = STATUS_PULL_TO_REFRESH;
					}
					// 通过偏移下拉头的topMargin值，来实现下拉效果
					headerLayoutParams.topMargin = (distance / 2)
							+ hideHeaderHeight;
					header.setLayoutParams(headerLayoutParams);
				}
				break;
			case MotionEvent.ACTION_UP:
			default:
				Log.d(TAG, "进入MotionEvent.ACTION_UP或者是default中");
				if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
					// 松手时如果是释放立即刷新状态，就去调用正在刷新的任务
					new RefreshingTask().execute();
				} else if (currentStatus == STATUS_PULL_TO_REFRESH) {
					// 松手时如果是下拉状态，就去调用隐藏下拉头的任务
					new HideHeaderTask().execute();
				}
				break;
			}
			// 时刻记得更新下拉头中的信息
			if (currentStatus == STATUS_PULL_TO_REFRESH
					|| currentStatus == STATUS_RELEASE_TO_REFRESH) {
				Log.d(TAG, "进入“时刻记得更新下拉头中的信息”的if语句");
				updateHeaderView();
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
		Log.d(TAG, "离开onTouch");
		return false;
	}

	/**
	 * 给下拉刷新控件注册一个监听器。 RefreshableView构造方法执行完成之后，紧接着就执行这个方法
	 * 
	 * @param listener
	 *            监听器的实现。
	 * @param id
	 *            为了防止不同界面的下拉刷新在上次更新时间上互相有冲突， 请不同界面在注册下拉刷新监听器时一定要传入不同的id。
	 */
	public void setOnRefreshListener(PullToRefreshListener listener, int id) {
		Log.d(TAG, "进入setOnRefreshListener方法");
		mListener = listener;
		Log.d(TAG, "离开setOnRefreshListener方法");
		mId = id;
	}

	/**
	 * 当所有的刷新逻辑完成后，记录调用一下，否则你的ListView将一直处于正在刷新状态。
	 */
	public void finishRefreshing() {
		Log.d(TAG, "进入finishRefreshing方法");

		currentStatus = STATUS_REFRESH_FINISHED;
		preferences.edit()
				.putLong(UPDATED_AT + mId, System.currentTimeMillis()).commit();
		new HideHeaderTask().execute();
		Log.d(TAG, "离开finishRefreshing方法");
	}

	/**
	 * 根据当前ListView的滚动状态来设定ableToPull的值，
	 * 每次都需要在onTouch中第一个执行，这样可以判断出当前应该是滚动ListView，还是应该进行下拉。
	 */
	private void setIsAbleToPull(MotionEvent event) {
		Log.d(TAG, "进入setIsAbleToPull方法");
		View firstChild = listView.getChildAt(0);
		if (firstChild != null) {
			int firstVisiblePos = listView.getFirstVisiblePosition();
			if (firstVisiblePos == 0 && firstChild.getTop() == 0) {
				if (!ableToPull) {
					yDown = event.getRawY();
				}
				// 如果首个元素的上边缘，距离父布局值为0，就说明ListView滚动到了最顶部，此时应该允许下拉刷新
				ableToPull = true;
			} else {
				if (headerLayoutParams.topMargin != hideHeaderHeight) {
					headerLayoutParams.topMargin = hideHeaderHeight;
					header.setLayoutParams(headerLayoutParams);
				}
				ableToPull = false;
			}
		} else {
			// 如果ListView中没有元素，也应该允许下拉刷新
			ableToPull = true;
		}
		Log.d(TAG, "离开setIsAbleToPull方法");
	}

	/**
	 * 更新下拉头中的信息。
	 */
	private void updateHeaderView() {
		Log.d(TAG, "进入updateHeaderView方法");
		if (lastStatus != currentStatus) {
			if (currentStatus == STATUS_PULL_TO_REFRESH) {
				description.setText(getResources().getString(
						R.string.pull_to_refresh));
				arrow.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				rotateArrow();
			} else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
				description.setText(getResources().getString(
						R.string.release_to_refresh));
				arrow.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				rotateArrow();
			} else if (currentStatus == STATUS_REFRESHING) {
				description.setText(getResources().getString(
						R.string.refreshing));
				progressBar.setVisibility(View.VISIBLE);
				arrow.clearAnimation();
				arrow.setVisibility(View.GONE);
			}
			refreshUpdatedAtValue();
			Log.d(TAG, "离开updateHeaderView方法");
		}
	}

	/**
	 * 根据当前的状态来旋转箭头。
	 */
	private void rotateArrow() {
		Log.d(TAG, "进入rotateArrow方法");
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

	/**
	 * 刷新下拉头中上次更新时间的文字描述。
	 */
	private void refreshUpdatedAtValue() {
		Log.d(TAG, "进入refreshUpdatedAtValue方法");
		/**
		 * 从SharedPreferences存储文件中取出上次保存的更新时间
		 */
		lastUpdateTime = preferences.getLong(UPDATED_AT + mId, -1);
		/**
		 * 获得当前时间
		 */
		long currentTime = System.currentTimeMillis();
		/**
		 * 获得当前更新时间和上次更新时间的时间差
		 */
		long timePassed = currentTime - lastUpdateTime;

		long timeIntoFormat;
		/**
		 * 此次更新显示的提示信息（如，暂未更新过，或者，5分钟前更新过）
		 */
		String updateAtValue;
		if (lastUpdateTime == -1) {
			// lastUpdateTime =-1指的是还没有更新（暂未更新过），否则就是更新过，判断具体更新的时间
			/**
			 * getResources()得到Resources对象 ,通过该对象可以获得res文件夹下所有的资源，如图片，字符串，布局文件
			 */
			updateAtValue = getResources().getString(R.string.not_updated_yet);
		} else if (timePassed < 0) {
			updateAtValue = getResources().getString(R.string.time_error);
		} else if (timePassed < ONE_MINUTE) {
			updateAtValue = getResources().getString(R.string.updated_just_now);
		} else if (timePassed < ONE_HOUR) {
			/**
			 * %n$ms：代表输出的是字符串，n代表是第几个参数，设置m的值可以在输出之前放置空格 也可简单写成： %s （表示字符串）【
			 * 扩展：%d （表示整数） %f （表示浮点数）】 String str = "上次更新于%1$s前" ; String
			 * string= String.format(str,"23分钟") 就是指将字符串中，第一个要替换的字符，替换为23
			 * string的结果就是：上次更新于23分钟前
			 */
			timeIntoFormat = timePassed / ONE_MINUTE;
			String value = timeIntoFormat + "分钟";
			updateAtValue = String.format(
					getResources().getString(R.string.updated_at), value);
		} else if (timePassed < ONE_DAY) {
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
		Log.d(TAG, "离开refreshUpdatedAtValue方法");
	}

	/**
	 * 正在刷新的任务，在此任务中会去回调注册进来的下拉刷新监听器。
	 */
	class RefreshingTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "---进入RefreshingTask的doInBackground方法");
			int topMargin = headerLayoutParams.topMargin;
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
			/**
			 * 调用publishProgress(0);方法后，紧接着会执行onProgressUpdate这个方法
			 */
			publishProgress(0);
			if (mListener != null) {
				mListener.onRefresh();
			}
			Log.d(TAG, "离开doInBackground方法");
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... topMargin) {
			Log.d(TAG, "---进入RefreshingTask的onProgressUpdate方法");
			
			updateHeaderView();
			headerLayoutParams.topMargin = topMargin[0];
			header.setLayoutParams(headerLayoutParams);
			Log.d(TAG, "离开onProgressUpdate方法");
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			Log.d(TAG, "---进入RefreshingTask的onPostExecute方法");

		}
	}

	/**
	 * 隐藏下拉头的任务，当未进行下拉刷新或下拉刷新完成后，此任务将会使下拉头重新隐藏。
	 */
	class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {
		@Override
		protected Integer doInBackground(Void... params) {
			Log.d(TAG, "------进入HideHeaderTask的doInBackground方法");
			int topMargin = headerLayoutParams.topMargin;
			while (true) {
				topMargin = topMargin + SCROLL_SPEED;
				if (topMargin <= hideHeaderHeight) {
					topMargin = hideHeaderHeight;
					break;
				}
				publishProgress(topMargin);
				sleep(10);
			}
			Log.d(TAG, "离开doInBackground方法");
			return topMargin;
		}

		@Override
		protected void onProgressUpdate(Integer... topMargin) {
			Log.d(TAG, "------进入HideHeaderTask的onProgressUpdate方法");
			headerLayoutParams.topMargin = topMargin[0];
			header.setLayoutParams(headerLayoutParams);
			Log.d(TAG, "离开onProgressUpdate方法");
		}

		@Override
		protected void onPostExecute(Integer topMargin) {
			Log.d(TAG, "-----进入HideHeaderTask的onPostExecute方法");
			/**
			 * 刷新适配器中数据
			 */
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
			headerLayoutParams.topMargin = topMargin;
			header.setLayoutParams(headerLayoutParams);
			currentStatus = STATUS_REFRESH_FINISHED;
			Log.d(TAG, "离开onPostExecute方法");
		}
	}

	/**
	 * 使当前线程睡眠指定的毫秒数。
	 * 
	 * @param time
	 *            指定当前线程睡眠多久，以毫秒为单位
	 */
	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
	 */
	public interface PullToRefreshListener {
		/**
		 * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意此方法是在子线程中调用的， 你可以不必另开线程来进行耗时操作。
		 */
		void onRefresh();
	}

	/**
	 * 
	 * @param adapter
	 */
	public void initAdapter(ArrayAdapter<String> adapter) {
		// TODO Auto-generated method stub
		this.adapter = adapter;
	}
}
