package rxPermission;

import android.util.Log;

import com.tbruyelle.rxpermissions2.Permission;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Description: 统一权限管理订阅封装
 */
public abstract class RxPermissionConsumer implements Observer<Permission> {
    @Override
    public void onSubscribe(Disposable disposable) {

    }

    @Override
    public void onNext(Permission permission) {
            if (permission.granted) {
                //同意后调用
               agree();
            } else if (permission.shouldShowRequestPermissionRationale){
                //禁止，但没有选择“以后不再询问”，以后申请权限，会继续弹出提示
               refuse();
            }else {
                //禁止，但选择“以后不再询问”，以后申请权限，不会继续弹出提示
                handOpen();
            }
    }


    @Override
    public void onError(Throwable throwable) {
        Log.d("Permission",throwable.getMessage());
    }

    @Override
    public void onComplete() {

    }


    public abstract void agree();

    public abstract void refuse();

    public abstract  void handOpen();
}
