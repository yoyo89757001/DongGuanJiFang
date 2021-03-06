package megvii.testfacepass.pa.adapter;

import android.content.Context;
import android.graphics.Bitmap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;


import io.objectbox.query.LazyList;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.Subject;

import megvii.testfacepass.pa.view.GlideRoundTransform;


/**
 * Created by xingchaolei on 2017/12/5.
 */

public class UserListAdapter extends BaseAdapter {

    private List<Subject> mGroupNames;
    private LayoutInflater mLayoutInflater;
    private ItemDeleteButtonClickListener mItemDeleteButtonClickListener;

    private Context context;
    private RequestOptions myOptions2 =null;


    public UserListAdapter(List<Subject> data, Context context) {
        mGroupNames=data;
        this.context=context;
        myOptions2 = new RequestOptions()
                .fitCenter()
                .error(R.drawable.erroy_bg)
                //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
                .transform(new GlideRoundTransform(context, 20));
    }

    public List<Subject> getData() {

        return mGroupNames;
    }

    public void setData(LazyList<Subject> data) {
        mGroupNames = data;
    }

    public void setOnItemDeleteButtonClickListener(ItemDeleteButtonClickListener listener) {
        mItemDeleteButtonClickListener = listener;
    }

    @Override
    public int getCount() {
        return mGroupNames == null ? 0 : mGroupNames.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupNames == null ? null : mGroupNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_item_group_nameuser, parent, false);
            holder = new ViewHolder();
            holder.groupNameTv =  convertView.findViewById(R.id.tv_group_name);
            holder.deleteGroupIv =  convertView.findViewById(R.id.iv_delete_group);
            holder.touxiang =  convertView.findViewById(R.id.touxiang);
            holder.idid =  convertView.findViewById(R.id.idid);
            holder.kahao =  convertView.findViewById(R.id.kahao);
            holder.open =  convertView.findViewById(R.id.open);
            holder.guoqi =  convertView.findViewById(R.id.guoqi);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.deleteGroupIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemDeleteButtonClickListener != null) {
                    mItemDeleteButtonClickListener.OnItemDeleteButtonClickListener(position);
                }
            }
        });

        holder.guoqi.setText("过期时间: "+mGroupNames.get(position).getBirthday());
        holder.groupNameTv.setText(mGroupNames.get(position).getName()+"");
        holder.idid.setText("ID:"+mGroupNames.get(position).getTeZhengMa());
        holder.kahao.setText(mGroupNames.get(position).getWorkNumber()+"");

        if (mGroupNames.get(position).getIsOpen()==1){
            holder.open.setText("是否有进入权限：无权限");// 是否开门  1是关，0是开
        }else if (mGroupNames.get(position).getIsOpen()==0){
            holder.open.setText("是否有进入权限：有权限");// 是否开门  1是关，0是开
        }else {
            holder.open.setText("是否有进入权限：默认值-1");// 是否开门  1是关，0是开
        }

        Bitmap bitmap = null;
        try {
            if (mGroupNames.get(position).getTeZhengMa()!=null){

                Glide.with(context)
                        .load(MyApplication.SDPATH3+ File.separator+mGroupNames.get(position).getTeZhengMa()+".png")
                        .apply(myOptions2)
                        .into(holder.touxiang);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    public static class ViewHolder {
        TextView groupNameTv,idid,kahao,open,guoqi;
        ImageView deleteGroupIv,touxiang;
    }


    public interface ItemDeleteButtonClickListener {

        void OnItemDeleteButtonClickListener(int position);

    }
}
