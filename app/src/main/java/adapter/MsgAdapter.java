package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suyong.sunshineflat.R;

import java.util.List;

import pojo.Msg;

public class MsgAdapter extends RecyclerView.Adapter <MsgAdapter.ViewHolder>{
    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        ImageView leftpic;
        ImageView rightpic;
        public ViewHolder(View view){
            super(view);
            leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
            leftMsg = (TextView)view.findViewById(R.id.left_msg);
            rightMsg = (TextView)view.findViewById(R.id.right_msg);
            leftpic = view.findViewById(R.id.receive_pic);
            rightpic = view.findViewById(R.id.send_pic);
        }
    }

    public MsgAdapter(List<Msg> mMsgList){
        this.mMsgList = mMsgList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if(msg.getType()==Msg.Type_Recived){
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            holder.leftpic.setImageBitmap(msg.getReceive_pic());
        }else if(msg.getType()==Msg.Type_Send){
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMsg.setText(msg.getContent());
            holder.rightpic.setImageBitmap(msg.getSend_pic());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }


}

