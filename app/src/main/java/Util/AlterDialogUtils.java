package Util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.hb.dialog.dialog.ConfirmDialog;
import com.suyong.sunshineflat.R;

public class AlterDialogUtils {
     public static void showSingleDialog(Context context, String title, String msg, String positive,
                                          DialogInterface.OnClickListener listener) {
        createSingleDialog(context, title, msg, positive, listener).show();
    }

    public static void showDoubleDialog(Context context, String title, String msg, String positive,
                                        String negative, DialogInterface.OnClickListener listener) {

        createDoubleDialog(context, title, msg, positive, negative, listener).show();
    }

    public static AlertDialog createSingleDialog(Context context, String title, String msg, String positive,
                                                 DialogInterface.OnClickListener listener) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positive, listener)
                .create();
        return dialog;
    }

    public static AlertDialog createDoubleDialog(Context context, String title, String msg, String positive,
                                                 String negative, DialogInterface.OnClickListener listener) {

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(positive, listener)
                .setNegativeButton(negative, listener)
                .create();
        return dialog;
    }

    public static ConfirmDialog createTipDialog(Context context,String message){
        ConfirmDialog confirmDialog = new ConfirmDialog(context);
        confirmDialog.setLogoImg(R.mipmap.dialog_notice).setMsg(message);
        return confirmDialog;
    }
}
