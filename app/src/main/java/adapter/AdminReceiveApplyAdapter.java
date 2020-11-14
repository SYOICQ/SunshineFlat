package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.suyong.sunshineflat.R;

import java.util.List;

import Util.StringAndBitMapTools;
import pojo.Apply;

public class AdminReceiveApplyAdapter extends RecyclerView.Adapter<AdminReceiveApplyAdapter.MyViewHolder>{

    private List<Apply> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    public AdminReceiveApplyAdapter(Context mContext, List<Apply> mDatas) {
        this.mContext=mContext;
        this.mDatas=mDatas;
        inflater= LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.admin_receive_applylist,parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        setData(holder,mDatas.get(position));
    }

    private void setData(MyViewHolder holder, Apply flat) {
        holder.phone.setText(flat.getPhone());
        String str="";
        if(flat.getAdminFlag()==1) str="管理员认证请求";
        if(flat.getLawerFlag()==1) str="律师认证请求";
        if(flat.getAgentFlag()==1) str="经纪人认证请求";
        holder.type.setText(str);
        holder.reson.setText(flat.getReson());
        holder.pic_yulan.setImageBitmap(StringAndBitMapTools.stringToBitmap(flat.getPic()));
        holder.agree.setOnClickListener((V)->{
            listener.onClick(flat,"agree");
        });
        holder.refuse.setOnClickListener((v)->{
            listener.onClick(flat,"refuse");
        });
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView type;
        TextView phone;
        TextView reson;
        ImageView pic_yulan;
        Button refuse;
        Button agree;
        public MyViewHolder(View view) {
            super(view);
             type = view.findViewById(R.id.type);
             phone = view.findViewById(R.id.phone);
             reson =view.findViewById(R.id.reson);
             pic_yulan =view.findViewById(R.id.pic_yulan);
             refuse =view.findViewById(R.id.refuse);
             agree =view.findViewById(R.id.agree);
        }
    }

    private OnclickListener listener;
    public interface OnclickListener {
        void onClick(Apply apply,String flag);
    }
    public void setOnclicklistener(OnclickListener listener){
        this.listener = listener;
    }

}
