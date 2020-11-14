package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suyong.sunshineflat.R;

import java.util.List;

import pojo.City;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyViewHolder> {
    private List<City> mDatas;
    private Context mContext;
    private LayoutInflater inflater;


    public CityAdapter(Context mContext, List<City> mDatas) {
        this.mContext=mContext;
        this.mDatas=mDatas;
        inflater=LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.city_item,parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        City city = mDatas.get(position);
        holder.tv.setText(city.getName());
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(city);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        public MyViewHolder(View view) {
            super(view);
            tv = view.findViewById(R.id.love_item);
        }
    }

    private OnClickListener clickListener;

    public void setOnClickListener(OnClickListener clickListener){
        this.clickListener = clickListener;
    }

    public interface OnClickListener {
        void onClick(City city);
    }
}
