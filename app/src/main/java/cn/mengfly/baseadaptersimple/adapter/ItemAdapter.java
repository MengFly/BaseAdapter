package cn.mengfly.baseadaptersimple.adapter;

import android.content.Context;
import android.widget.TextView;

import cn.mengfly.baseadapter.BaseAdapter;
import cn.mengfly.baseadapter.BaseViewHolder;
import cn.mengfly.baseadaptersimple.R;

public class ItemAdapter extends BaseAdapter<String> {

    public ItemAdapter(Context context, int itemLayoutRes) {
        super(context, itemLayoutRes);
    }

    @Override
    protected void bindData(String s, BaseViewHolder holder) {
        ((TextView) holder.getView(R.id.tv_content)).setText(s);
    }
}
