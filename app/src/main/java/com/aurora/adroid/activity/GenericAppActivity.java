/*
 * Aurora Droid
 * Copyright (C) 2019, Rahul Kumar Patel <whyorean@gmail.com>
 *
 * Aurora Droid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Aurora Droid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Aurora Droid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aurora.adroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.adroid.R;
import com.aurora.adroid.Sort;
import com.aurora.adroid.manager.RepoListManager;
import com.aurora.adroid.model.App;
import com.aurora.adroid.model.Repo;
import com.aurora.adroid.section.GenericAppSection;
import com.aurora.adroid.viewmodel.AppsViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class GenericAppActivity extends FragmentActivity {

    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.chip_group)
    ChipGroup chipGroup;
    @BindView(R.id.sort_name_az)
    Chip chipNameAZ;
    @BindView(R.id.sort_name_za)
    Chip chipNameZA;
    @BindView(R.id.sort_size_min)
    Chip chipSizeMin;
    @BindView(R.id.sort_size_max)
    Chip chipSizeMax;
    @BindView(R.id.sort_date_updated)
    Chip chipDateUpdated;
    @BindView(R.id.sort_date_added)
    Chip chipDateAdded;
    @BindView(R.id.txt_input_search)
    TextInputEditText txtInputSearch;
    @BindView(R.id.sort_view)
    HorizontalScrollView sortView;
    @BindView(R.id.container)
    CoordinatorLayout container;

    private SectionedRecyclerViewAdapter adapter;
    private GenericAppSection section;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_generic_apps);
        ButterKnife.bind(this);
        Intent arguments = getIntent();
        if (arguments != null) {
            int listType = arguments.getIntExtra("LIST_TYPE", 0);

            AppsViewModel appsViewModel = new ViewModelProvider(this).get(AppsViewModel.class);

            switch (listType) {
                case 0:
                    appsViewModel.getNewAppsLiveData().observe(this, this::setupApps);
                    chipGroup.check(R.id.sort_date_added);
                    break;
                case 1:
                    appsViewModel.getUpdatedAppsLiveData().observe(this, this::setupApps);
                    chipGroup.check(R.id.sort_date_updated);
                    break;
                case 2:
                    String categoryName = arguments.getStringExtra("CATEGORY_NAME");
                    txtInputSearch.setHint(categoryName);
                    appsViewModel.getCategoryAppsLiveData(categoryName).observe(this, this::setupApps);
                    break;
                case 3:
                    String repoId = arguments.getStringExtra("REPO_ID");
                    Repo repo = RepoListManager.getRepoById(this, repoId);
                    txtInputSearch.setHint(repo.getRepoName());
                    appsViewModel.getRepoAppsLiveData(repoId).observe(this, this::setupApps);
                    break;
            }

            setupChip();
            setupSearchBar();
        }
    }

    private void setupChip() {
        chipNameAZ.setOnClickListener(v -> sortAppsBy(Sort.NAME_AZ));
        chipNameZA.setOnClickListener(v -> sortAppsBy(Sort.NAME_ZA));
        chipSizeMax.setVisibility(View.GONE);
        chipSizeMin.setVisibility(View.GONE);
        chipDateUpdated.setOnClickListener(v -> sortAppsBy(Sort.DATE_UPDATED));
        chipDateAdded.setOnClickListener(v -> sortAppsBy(Sort.DATE_ADDED));
    }

    private void sortAppsBy(Sort sort) {
        if (section != null) {
            section.sortBy(sort);
            adapter.getAdapterForSection("GENERIC").notifyAllItemsChanged();
        }
    }

    private void setupApps(List<App> appList) {
        adapter = new SectionedRecyclerViewAdapter();
        section = new GenericAppSection(this, appList);
        adapter.addSection("GENERIC", section);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    private void setupSearchBar() {
        txtInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterApps(String query) {
        section.filter(query);
        adapter.notifyDataSetChanged();
    }
}