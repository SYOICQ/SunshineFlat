package PullDownOptionViews;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.suyong.sunshineflat.R;

import Interf.MyItemClickListener;

public class AreaView {
    private Context context;
    private MyItemClickListener listener;
    EditText from_area ;
    EditText to_area ;
    public AreaView(Context context) {
        this.context = context;
    }

    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    public View getAreaView() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_area, null);
        from_area = view.findViewById(R.id.from_area);
        to_area = view.findViewById(R.id.to_area);
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
            String from =from_area.getText().toString().trim();
             String to=to_area.getText().toString().trim();
            if(TextUtils.isEmpty(from)||TextUtils.isEmpty(to)){
                Toast.makeText(context,"请完善填写!",Toast.LENGTH_SHORT).show();
                return;
            }
            if(Integer.parseInt(from)>Integer.parseInt(to)){
                Toast.makeText(context,"不符合逻辑哦!",Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onItemClick(v, 2, from+"-"+ to);
        }
    }
}
