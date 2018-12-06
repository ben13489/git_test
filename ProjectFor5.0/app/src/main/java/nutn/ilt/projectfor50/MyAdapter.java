package nutn.ilt.projectfor50;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Mu on 2017/10/28.
 */

public class MyAdapter extends BaseAdapter {
    private List mApps;
    private LayoutInflater inflater;

    public MyAdapter(List apps, Context context){
        this.mApps = apps;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mApps.size();
    }

    @Override
    public Object getItem(int position) {
        return mApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.app_item, null);
            view = new ViewHolder();
            view.name =  convertView.findViewById(R.id.app_name);
            view.icon = convertView.findViewById(R.id.app_icon);
            view.rx = convertView.findViewById(R.id.app_rx);
            view.tx =  convertView.findViewById(R.id.app_tx);
            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        ApplicationBean app = (ApplicationBean) mApps.get(position);
        view.name.setText(app.getName());
        view.icon.setImageDrawable(app.getIcon());
        view.rx.setText("接收:" + getFileSize(app.getRx()));
        view.tx.setText("上傳:" + getFileSize(app.getTx()));

        return convertView;
    }
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public static class ViewHolder {
        public TextView name;
        public ImageView icon;
        public TextView rx;
        public TextView tx;
    }
}
