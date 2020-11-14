package PullDownOptionViews;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.suyong.sunshineflat.R;

import Interf.MyItemClickListener;

public class PriceView {
    private Context context;
    private MyItemClickListener listener;

    EditText from_price ;
    EditText to_price ;
    TextView danwei;

    public PriceView(Context context) {
        this.context = context;
    }

    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    public View getPriceView(String dan) {
        View view = LayoutInflater.from(context).inflate(R.layout.option_price_view, null);
        from_price = view.findViewById(R.id.from_price);
        to_price = view.findViewById(R.id.to_price);
        danwei = view.findViewById(R.id.danwei);
        danwei.setText(dan);
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
            String from =from_price.getText().toString().trim() ;
            String to = to_price.getText().toString().trim();
            if(TextUtils.isEmpty(from)||TextUtils.isEmpty(to)){
                Toast.makeText(context,"请完善填写!",Toast.LENGTH_SHORT).show();
                return;
            }
            if(Integer.parseInt(from)>Integer.parseInt(to)){
                Toast.makeText(context,"不符合逻辑哦!",Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onItemClick(v, 1, from+"-"+to);
        }
    }
}
