package com.aurora.adroid.model.items;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.aurora.adroid.GlideApp;
import com.aurora.adroid.R;
import com.aurora.adroid.model.App;
import com.aurora.adroid.util.DatabaseUtil;
import com.aurora.adroid.util.TextUtil;
import com.aurora.adroid.util.Util;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericItem extends AbstractItem<GenericItem.ViewHolder> {

    private App app;
    private String packageName;

    public GenericItem(App app) {
        this.app = app;
        this.packageName = app.getPackageName();
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_installed;
    }

    @Override
    public int getType() {
        return R.id.fastadapter_item;
    }

    public static class ViewHolder extends FastItemAdapter.ViewHolder<GenericItem> {
        @BindView(R.id.img)
        AppCompatImageView img;
        @BindView(R.id.line1)
        AppCompatTextView line1;
        @BindView(R.id.line2)
        AppCompatTextView line2;
        @BindView(R.id.line3)
        AppCompatTextView line3;

        ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void bindView(@NotNull GenericItem item, @NotNull List<?> list) {
            final Context context = itemView.getContext();
            final App app = item.getApp();

            line1.setText(app.getName());
            line2.setText(StringUtils.joinWith(".", Util.getDateFromMilli(app.getLastUpdated()),
                    app.getSuggestedVersionCode(),
                    app.getRepoName(),
                    app.getAuthorName()));

            String summary;
            if (app.getLocalized() != null
                    && app.getLocalized().getEnUS() != null
                    && app.getLocalized().getEnUS().getSummary() != null) {
                summary = TextUtil.emptyIfNull(app.getLocalized().getEnUS().getSummary());
            } else
                summary = TextUtil.emptyIfNull(app.getSummary());

            summary = StringUtils.capitalize(summary);

            line3.setText(summary);

            if (app.getIcon() == null)
                img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_placeholder));
            else
                GlideApp
                        .with(context)
                        .asBitmap()
                        .load(DatabaseUtil.getImageUrl(app))
                        .placeholder(R.drawable.ic_placeholder)
                        .into(img);
        }

        @Override
        public void unbindView(@NotNull GenericItem item) {
            line1.setText(null);
            line2.setText(null);
            line3.setText(null);
            img.setImageDrawable(null);
        }
    }
}
