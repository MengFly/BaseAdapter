package cn.mengfly.baseadapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.mengfly.baseadapter.provider.LoadMoreProvider;
import cn.mengfly.baseadapter.provider.LoadMoreListener;


/**
 * <h2>BaseAdapter</h2>
 * You can extends this adapter and implement those method {@link #bindData(Object, BaseViewHolder)} to get your custom adapter
 * <p>
 * Through extends this adapter you can get a adapter for {@link RecyclerView} and have some powerful characters than {@link RecyclerView.Adapter} such as
 * <p>
 * 1. <b>emptyView</b>
 * Through call the method {@link #setEmptyView(int)} or the method {@link #setEmptyView(View)} you can set your custom emptyView, It will show when your adapter data is empty
 * <p>
 * 2. <b>loadMore</b>
 * Maybe you want set you data batch on batch, you just need to call the method {@link #setLoadMoreProvider(LoadMoreListener)} to load more data, there provide a default implements for {@link LoadMoreListener} is {@link cn.mengfly.baseadapter.provider.LoadMoreProvider.DefaultLoadMoreListener} use this,you can just achieve the method loadMore
 * <p>
 * 3. <b>onItemClickListener</b>
 * Just like ListView, if you want set itemClickListener,you just call the method {@link #setOnItemClickListener(OnItemClickListener)}
 * <p>
 * 4. <b>OnItemLongClickListener</b>
 * Just like onItemClickListener, you just call the method {@link #setOnItemLongClickListener(OnItemLongClickListener)}
 *
 * @author mengfly
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class BaseAdapter<T>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private List<T> list;
    protected Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private View emptyView;
    private View _emptyViewWrapper;
    @LayoutRes
    private int itemLayoutRes;

    private LoadMoreProvider<T> loadMoreProvider;


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) {
                // load more
                if (!loadMoreProvider.isLoadMoreEnd()) {
                    loadMoreProvider.loadMoreSync(ts -> handler.post(() -> addAll(ts)));
                }
            }
        }
    };

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_EMPTY = 0;


    public BaseAdapter(Context context, @LayoutRes int itemLayoutRes) {
        this.list = new ArrayList<>();
        this.context = context;
        this.itemLayoutRes = itemLayoutRes;
        this.loadMoreProvider = new LoadMoreProvider<>();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public void setEmptyView(int layoutRes) {
        this.emptyView = LayoutInflater.from(context)
                .inflate(layoutRes, null, false);
    }

    public void setLoadMoreProvider(LoadMoreListener<T> loadMoreListener) {
        this.loadMoreProvider.setLoadMoreListener(loadMoreListener);
    }

    public void resetLoadMoreStatus() {
        loadMoreProvider.resetLoad();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY && emptyView != null) {
            if (_emptyViewWrapper == null) {
                _emptyViewWrapper = LayoutInflater.from(context).inflate(R.layout._item_empty_wrapper, parent, false);
                LinearLayout wrapper = _emptyViewWrapper.findViewById(R.id.fl_wrapper);
                wrapper.addView(emptyView);
            }
            return new EmptyViewHolder(_emptyViewWrapper);
        }
        if (viewType == loadMoreProvider.type() && loadMoreProvider.isLoadMoreEnable()) {
            return loadMoreProvider.initLoadMore(parent, context);
        }
        BaseViewHolder baseViewHolder = new BaseViewHolder(context, itemLayoutRes, parent);
        baseViewHolder.itemView.setOnClickListener(this);
        baseViewHolder.itemView.setOnLongClickListener(this);
        return baseViewHolder;
    }

    protected abstract void bindData(T t, BaseViewHolder holder);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position + 1 == getItemCount() && loadMoreProvider.isLoadMoreEnable()) {
            handler.sendEmptyMessageDelayed(0x11, 100);
            loadMoreProvider.bindData(holder);
            return;
        }
        if (holder instanceof BaseViewHolder) {
            holder.itemView.setTag(position);
            T t = list.get(position);
            bindData(t, (BaseViewHolder) holder);
        }
    }

    public int getPosition(T t) {
        return list.indexOf(t);
    }

    /**
     * This method not suggest to call,
     * Because this method cannot reflection the real data size,
     * if you want to know the data size,
     * instead you can call the method {@link #size()} to get real item size
     */
    @Override
    public int getItemCount() {
        int itemCount;
        // 判断empty的逻辑
        if (emptyView != null) {
            itemCount = list.isEmpty() ? 1 : list.size();
        } else {
            itemCount = list.size();
        }
        // 如果存在加载更多的逻辑
        if (loadMoreProvider.isLoadMoreEnable()) {
            itemCount += 1;
        }
        return itemCount;
    }

    public int size() {
        return list.size();
    }

    /**
     * Stop loadMore, if you call this method,
     * adapter will not load more data when the last load more end
     */
    public void stopLoadMore() {
        loadMoreProvider.stopLoadMore();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.isEmpty() && emptyView != null && position < 1) {
            return VIEW_TYPE_EMPTY;
        }
        if (loadMoreProvider.isLoadMoreEnable()
                && (position + 1) == getItemCount()) {
            return loadMoreProvider.type();
        }
        return VIEW_TYPE_ITEM;

    }

    public void removeItem(T t) {
        list.remove(t);
        notifyDataSetChanged();
    }

    public void removePosition(int i) {
        list.remove(i);
        notifyDataSetChanged();
    }

    public void addItem(T t) {
        list.add(t);
        notifyDataSetChanged();
    }

    public void addItem(T t, int index) {
        list.add(index, t);
        notifyDataSetChanged();
    }

    public T getItem(int index) {
        return list.get(index);
    }

    public void addAll(Collection<T> items) {
        list.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (onItemLongClickListener != null) {
            return onItemLongClickListener.onItemLongClick(v, (Integer) v.getTag());
        }
        return false;
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
        resetLoadMoreStatus();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);

    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View itemView, int position);

    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
