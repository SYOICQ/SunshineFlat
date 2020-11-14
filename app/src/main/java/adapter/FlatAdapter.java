package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.suyong.sunshineflat.R;

import java.util.List;

import pojo.Flat;

public class FlatAdapter extends RecyclerView.Adapter<FlatAdapter.MyViewHolder>{

    private List<Flat> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    public FlatAdapter(Context mContext, List<Flat> mDatas) {
        this.mContext=mContext;
        this.mDatas=mDatas;
        inflater= LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.flat_item,parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        setData(holder,mDatas.get(position));
    }

    private void setData(MyViewHolder holder, Flat flat) {
        holder.title.setText(set(flat.getTitle_text()));
        holder.flood.setText(set(flat.getFlood()));
        holder.houseInfo.setText(set(flat.getHouseInfo()));
        holder.description.setText(set(flat.getFollowInfo()));
        holder.price.setText(set(flat.getPrice()));
        Glide.with(mContext)
                .load(flat.getImage())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.loading)
                .error(R.drawable.pic_notfound)
                .into(holder.priview);
        holder.flat_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(flat);
            }
        });
    }


    private String set(String d){
        if(TextUtils.isEmpty(d)) return "未知";
        return d;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView priview;
        TextView title;
        TextView flood;
        TextView houseInfo;
        TextView description;
        TextView price;
        LinearLayout flat_item;
        public MyViewHolder(View view) {
            super(view);
            flat_item = view.findViewById(R.id.flat_item);
            priview  = view.findViewById(R.id.preview);
            title = view.findViewById(R.id.title);
            flood = view.findViewById(R.id.flood);
            houseInfo = view.findViewById(R.id.houseInfo);
            description = view.findViewById(R.id.description);
            price = view.findViewById(R.id.price);
        }
    }

    private OnclickListener listener;
    public interface OnclickListener {
        void onClick(Flat flat);
    }
    public void setOnclicklistener(OnclickListener listener){
        this.listener = listener;
    }
}
