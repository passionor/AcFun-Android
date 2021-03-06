package com.hubertyoung.common.utils.manager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.Stack;


/**
 * @desc:activity管理
 * @author:HubertYoung
 * @date 2019/3/19 14:55
 * @since:
 * @see AppActivityManager
 */
public class AppActivityManager {
	private static          Stack< Activity >  activityStack;
	private volatile static AppActivityManager instance;

	private AppActivityManager() {

	}

	/**
	 * 单一实例
	 */
	public static AppActivityManager getAppManager() {
		if ( instance == null ) {
			synchronized ( AppActivityManager.class ) {
				if ( instance == null ) {
					instance = new AppActivityManager();
					instance.activityStack = new Stack();
				}
			}

		}
		return instance;
	}

	public void init( Application application ) {
		application.registerActivityLifecycleCallbacks( new Application.ActivityLifecycleCallbacks() {
			@Override
			public void onActivityCreated( Activity activity, Bundle savedInstanceState ) {
				addActivity( activity );
			}

			@Override
			public void onActivityStarted( Activity activity ) {

			}

			@Override
			public void onActivityResumed( Activity activity ) {

			}

			@Override
			public void onActivityPaused( Activity activity ) {

			}

			@Override
			public void onActivityStopped( Activity activity ) {

			}

			@Override
			public void onActivitySaveInstanceState( Activity activity, Bundle outState ) {

			}

			@Override
			public void onActivityDestroyed( Activity activity ) {
				AppActivityManager.getAppManager().finishActivity( activity );
			}
		} );
	}

	/**
	 * 添加Activity到堆栈
	 */
	private void addActivity( Activity activity ) {
		if ( null == activityStack ) {
			activityStack = new Stack<>();
		}
		activityStack.add( activity );
	}


	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		try {
			Activity activity = activityStack.lastElement();
			return activity;
		} catch ( Exception e ) {
			//            e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取当前Activity的前一个Activity
	 */
	public Activity preActivity() {
		int index = activityStack.size() - 2;
		if ( index < 0 ) {
			return null;
		}
		Activity activity = activityStack.get( index );
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity( activity );
	}

	/**
	 * 结束指定的Activity
	 */
	private void finishActivity( Activity activity ) {
		if ( activity != null ) {
			activityStack.remove( activity );
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 移除指定的Activity
	 */
	public void removeActivity( Activity activity ) {
		if ( activity != null ) {
			activityStack.remove( activity );
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity( Class< ? > cls ) {
		for (Activity activity : activityStack) {
			if ( activity.getClass().equals( cls ) ) {
				finishActivity( activity );
				return;
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if ( null != activityStack.get( i ) ) {
				activityStack.get( i ).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 返回到指定的activity
	 *
	 * @param cls
	 */
	public void returnToActivity( Class< ? > cls ) {
		while ( activityStack.size() != 0 )
			if ( activityStack.peek().getClass() == cls ) {
				break;
			} else {
				finishActivity( activityStack.peek() );
			}
	}


	/**
	 * 是否已经打开指定的activity
	 *
	 * @param cls
	 * @return
	 */
	public boolean isOpenActivity( Class< ? > cls ) {
		if ( activityStack != null ) {
			for (int i = 0, size = activityStack.size(); i < size; i++) {
				if ( cls == activityStack.peek().getClass() ) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 退出应用程序 不推荐此做法
	 *
	 * @param context      上下文
	 * @param isBackground 是否开开启后台运行
	 */
	@Deprecated
	public void AppExit( Context context, Boolean isBackground ) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = ( ActivityManager ) context.getSystemService( Context.ACTIVITY_SERVICE );
			activityMgr.restartPackage( context.getPackageName() );
		} catch ( Exception e ) {

		} finally {
			// 注意，如果您有后台程序运行，请不要支持此句子
			if ( !isBackground ) {
				System.exit( 0 );
			}
		}
	}
}