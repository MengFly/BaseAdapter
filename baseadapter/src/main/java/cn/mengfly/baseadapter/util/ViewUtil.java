package cn.mengfly.baseadapter.util;

import android.view.View;
import android.widget.TextView;

public class ViewUtil {

    public static <T extends View> T getView(View view, int res) {
        return view.findViewById(res);
    }

    public static void setText(View rootView, String text, int idRes) {
        TextView view = getView(rootView, idRes);
        view.setText(text);
    }

    public static void setText(View rootView, int strRes, int idRes) {
        TextView view = getView(rootView, idRes);
        view.setText(strRes);
    }

    public static void setVisible(View rootView, int idRes) {
        getView(rootView, idRes).setVisibility(View.VISIBLE);
    }

    public static void setGone(View rootView, int idRes) {
        getView(rootView, idRes).setVisibility(View.GONE);
    }


}
