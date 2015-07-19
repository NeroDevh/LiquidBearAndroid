package com.pillowapps.liqear.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.entities.lastfm.LastfmTag;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.SharedPreferencesManager;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LastfmTagAdapter extends ArrayAdapter<LastfmTag> {
    private final Context context;
    private final List<LastfmTag> values;

    public LastfmTagAdapter(Context context, List<LastfmTag> values) {
        super(context, R.layout.image_list_item);
        this.context = context;
        this.values = values;
    }

    @Override
    public LastfmTag getItem(int position) {
        return values.get(position);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    public List<LastfmTag> getItems() {
        return values;
    }

    public void addAll(List<LastfmTag> values) {
        this.values.addAll(values);
        notifyDataSetChanged();
    }

    public void clear() {
        values.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.image_list_item, parent, false);
            holder = new ViewHolder(convertView);
            holder.loadImages = SharedPreferencesManager.getPreferences()
                    .getBoolean(Constants.DOWNLOAD_IMAGES_CHECK_BOX_PREFERENCES, true);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LastfmTag tag = values.get(position);
        holder.tagTextView.setText(Html.fromHtml(tag.getName()));
        holder.imageView.setVisibility(View.INVISIBLE);

        convertView.setBackgroundResource(position % 2 == 0 ?
                R.drawable.list_item_background :
                R.drawable.list_item_background_tinted);

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.text_list_item)
        TextView tagTextView;
        boolean loadImages;
        @InjectView(R.id.image_view_list_item)
        ImageView imageView;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}