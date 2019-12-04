package cn.mengfly.baseadapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cn.mengfly.baseadapter.util.ViewUtil;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
    }

    public BaseViewHolder(Context context, @LayoutRes int layoutRes, ViewGroup parent) {
        this(LayoutInflater.from(context).inflate(layoutRes, parent, false));
    }

    public <T extends View> T getView(@IdRes int resId) {
        View view = mViews.get(resId, null);
        if (view == null) {
            view = ViewUtil.getView(itemView, resId);
            mViews.put(resId, view);
        }
        //noinspection unchecked
        return (T) view;
    }

    public View getContentView() {
        return itemView;
    }


}
