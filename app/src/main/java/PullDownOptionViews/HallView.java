package PullDownOptionViews;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.suyong.sunshineflat.R;

import Interf.MyItemClickListener;

public class HallView {
    private Context context;
    private MyItemClickListener listener;

    private String currentSelect="";

    CheckBox l1;CheckBox l2;CheckBox l3;CheckBox l4;
    CheckBox l5;CheckBox l6;

    public HallView(Context context) {
        this.context = context;
    }

    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    public View getHallView() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_hall, null);
         l1 = view.findViewById(R.id.l1);
         l2= view.findViewById(R.id.l2);
         l3= view.findViewById(R.id.l3);
         l4= view.findViewById(R.id.l4);
         l5= view.findViewById(R.id.l5);
         l6= view.findViewById(R.id.l6);
        Button confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new mClick(currentSelect));
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
            if(l1.isChecked()) currentSelect+="l1";
            if(l2.isChecked()) currentSelect+="l2";
            if(l3.isChecked()) currentSelect+="l3";
            if(l4.isChecked()) currentSelect+="l4";
            if(l5.isChecked()) currentSelect+="l5";
            if(l6.isChecked()) currentSelect+="l6";
            if(TextUtils.isEmpty(currentSelect)) return;
            listener.onItemClick(v, 3, currentSelect);
        }
    }
}
