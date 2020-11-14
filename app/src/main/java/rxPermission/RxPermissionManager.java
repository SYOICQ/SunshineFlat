package rxPermission;

import android.Manifest;

import com.tbruyelle.rxpermissions2.RxPermissions;

public class RxPermissionManager  {
    /**
     * 请求SD卡读写权限
     *
     * @param rxPermissions
     * @param consumer
     */

    public static void requestStoragePermissions(RxPermissions rxPermissions, RxPermissionConsumer consumer) {
        rxPermissions.requestEach(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(consumer);
    }

    /**
     * 请求电话权限
     *
     * @param rxPermissions
     * @param consumer
     */
    public static void requestTelephonePermissions(RxPermissions rxPermissions, RxPermissionConsumer consumer) {
        rxPermissions.requestEach(
                Manifest.permission.CALL_PHONE)
                .subscribe(consumer);
    }


}
