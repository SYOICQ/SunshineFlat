package PullDownOptionViews;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.suyong.sunshineflat.R;

import Interf.MyItemClickListener;

public class TowardView {
    private Context context;
    private MyItemClickListener listener;

    private String currentSelect="";
    CheckBox f1;CheckBox f2;CheckBox f3;CheckBox f4;CheckBox f5;
    public TowardView(Context context) {
        this.context = context;
    }

    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    public View getTowardView() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toward, null);
         f1 = view.findViewById(R.id.f1);
         f2= view.findViewById(R.id.f2);
         f3= view.findViewById(R.id.f3);
         f4= view.findViewById(R.id.f4);
         f5= view.findViewById(R.id.f5);
        Button confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new mClick(""));
        return view;
    }
    private class mClick implements View.OnClickListener {

        String string;

        private mClick(String string) {
            this.string = string;
        }

        @Override
        public void onClick(View v) {
            currentSelect="";
            if(f1.isChecked()) currentSelect+="朝东:";
            if(f2.isChecked()) currentSelect+="朝南:";
            if(f3.isChecked()) currentSelect+="朝西:";
            if(f4.isChecked()) currentSelect+="朝北:";
            if(f5.isChecked()) currentSelect+="南北:";
            if(TextUtils.isEmpty(currentSelect)) return;
            listener.onItemClick(v, 4, currentSelect);
        }
    }
}
