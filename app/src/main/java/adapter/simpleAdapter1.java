package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.suyong.sunshineflat.R;

import java.util.List;

import Util.StringAndBitMapTools;
import pojo.ServiceUser;

public class simpleAdapter1 extends BaseAdapter {

    private List<ServiceUser> mData;
    private Context mContext;
    private LayoutInflater mInflater;

    public simpleAdapter1(List<ServiceUser> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        //创建ViewHolder的对象。
        ViewHolder viewHolder = null;
        //获得Item位置上的数据。
        ServiceUser student = mData.get(position);
        //convertview 优化
        if(convertview == null){
            convertview = mInflater.inflate(R.layout.simple_list,null);
            viewHolder = new ViewHolder();
            viewHolder.imagePhoto = convertview.findViewById(R.id.image_photo);
            viewHolder.name =  convertview.findViewById(R.id.name);
            //convertview为空时，ViewHolder将显示在ListView中的数据通过findViewById获取到。
            convertview.setTag(viewHolder);
        }else{
            //convertview不为空时，直接获取ViewHolder的Tag即可。
            viewHolder = (ViewHolder) convertview.getTag();
        }
        viewHolder.imagePhoto.setImageBitmap(StringAndBitMapTools.stringToBitmap(student.getImagePhoto()));
        viewHolder.name.setText(student.getName());
        return convertview;
    }

    /*
   ViewHolder内部类
   */
    class ViewHolder{
        TextView name;
        ImageView imagePhoto;
    }
}
