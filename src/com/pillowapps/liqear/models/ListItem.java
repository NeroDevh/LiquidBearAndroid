package com.pillowapps.liqear.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pillowapps.liqear.R;
import com.pillowapps.liqear.components.ModeListAdapter;
import com.pillowapps.liqear.components.UpdateAdapterCallback;
import com.pillowapps.liqear.helpers.Constants;
import com.pillowapps.liqear.helpers.ModeItemsHelper;
import com.pillowapps.liqear.helpers.PreferencesManager;

public class ListItem implements Item {
    final SharedPreferences modePreferences = PreferencesManager.getModePreferences();
    private final Mode mode;
    private final Context context;
    private final UpdateAdapterCallback callback;

    public ListItem(Context context, Mode mode, UpdateAdapterCallback callback) {
        this.mode = mode;
        this.context = context;
        this.callback = callback;
    }

    @Override
    public int getViewType() {
        return ModeListAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        ItemsHolder holder = new ItemsHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.image_thinner_list_item, null, false);
            holder.modeImageView = (ImageView) convertView.findViewById(R.id.image_view_list_item);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_list_item);
            holder.switchVisibilityButton = (ImageButton) convertView.findViewById(R.id.edit_mode_button);
            holder.mainView = convertView.findViewById(R.id.main_view);
            convertView.setTag(holder);
        } else {
            holder = (ItemsHolder) convertView.getTag();
        }

        holder.modeImageView.setImageResource(mode.getIcon());
        holder.titleTextView.setText(context.getString(mode.getTitle()).toLowerCase());
        final boolean modeVisible = mode.isVisible();

        holder.switchVisibilityButton.setVisibility(ModeItemsHelper.isEditMode() ? View.VISIBLE : View.GONE);
        holder.switchVisibilityButton.setImageResource(modeVisible
                        ? R.drawable.minus
                        : R.drawable.plus
        );
        boolean enabled = ModeItemsHelper.isModeEnabled(mode);
        holder.mainView.setBackgroundResource(!enabled ? R.drawable.square_simple_button_states_disabled : R.drawable.square_simple_button_states);
        holder.switchVisibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = modePreferences.edit();
                editor.putBoolean(Constants.MODE_VISIBLE + mode.getModeEnum(), !modeVisible);
                editor.commit();
                callback.onUpdate();
            }
        });
        return convertView;
    }

    public Mode getMode() {
        return mode;
    }

    static class ItemsHolder {
        TextView titleTextView;
        ImageView modeImageView;
        ImageButton switchVisibilityButton;
        View mainView;
    }
}
