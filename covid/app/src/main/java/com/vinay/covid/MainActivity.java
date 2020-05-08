package com.vinay.covid;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.vinay.covid.adapter.CovidDataListAdapter;
import com.vinay.covid.apiConnection.ApiClient;
import com.vinay.covid.fieldCompartor.CountryNameComparator;
import com.vinay.covid.fieldCompartor.DeathCaseComparator;
import com.vinay.covid.fieldCompartor.RecoveredCaseComparartor;
import com.vinay.covid.fieldCompartor.TotalCaseComparator;
import com.vinay.covid.model.CovidEntity;
import com.vinay.covid.sharedPreference.SettingPreference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements FilterDialog.FilterInterface {
    private static final int PERMISSION_ID = 40;
    private RecyclerView rv_countyDataList;
    private ArrayList<CovidEntity> covidEntityArrayList;
    private ArrayList<CovidEntity> duplicateCovidList;
    private CovidDataListAdapter covidDataListAdapter;
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView tv_totalCase, tv_totalDeath, tv_totalRecovered, tv_filterTotalTile, tv_filterRecoverTitle,
            tv_filterDeathFilter;
    private LinearLayout ll_filter, ll_totalAppliedFilter, ll_recoverAppliedFilter, ll_deathAppliedFilter, ll_statisticData,
            ll_sortCountry, ll_sortTotal, ll_sortDeath, ll_sortRecoverd;
    private ImageView iv_country, iv_total, iv_deaths, iv_recovered;
    private ProgressBar progressBar;
    private ImageView placeHolderImage;
    private Button btn_retry;
    Handler handler = null;
    Runnable runnableCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeIds();
        initializeObject();
        initializeListener();
        initializeTimer();
    }

    private void initializeObject() {
        covidEntityArrayList = new ArrayList<>();
        duplicateCovidList = new ArrayList<>();
        LinearLayoutManager linearLayoutManger = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_countyDataList.setLayoutManager(linearLayoutManger);
        covidDataListAdapter = new CovidDataListAdapter(MainActivity.this, covidEntityArrayList);
        rv_countyDataList.setAdapter(covidDataListAdapter);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
        if (covidEntityArrayList.size() <= 0) {
            placeHolderImage.setVisibility(View.VISIBLE);
            ll_statisticData.setVisibility(View.GONE);
        }
        sortList(SettingPreference.getIntField(SettingPreference.KEY_SORT_OPTION, MainActivity.this), true);
    }


    private void initializeTimer() {
        handler = new Handler();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                fetchDataFromServer();
                handler.postDelayed(this, 1000 * 60 * 2);
            }
        };
        handler.post(runnableCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnableCode);
    }

    private void initializeIds() {
        rv_countyDataList = findViewById(R.id.rv_countryList);
        tv_totalCase = findViewById(R.id.tv_totalCase);
        tv_totalDeath = findViewById(R.id.tv_totalDeath);
        tv_totalRecovered = findViewById(R.id.tv_recovered);
        ll_filter = findViewById(R.id.ll_filterTop);

        tv_filterTotalTile = findViewById(R.id.tv_totalFilterApplied);
        tv_filterRecoverTitle = findViewById(R.id.tv_recoveredFilterApplied);
        tv_filterDeathFilter = findViewById(R.id.tv_deathFilterApplied);
        ll_totalAppliedFilter = findViewById(R.id.ll_totalApplied);
        ll_deathAppliedFilter = findViewById(R.id.ll_deathApplied);
        ll_recoverAppliedFilter = findViewById(R.id.ll_recoveredApplied);

        ll_sortCountry = findViewById(R.id.ll_sort_country);
        ll_sortRecoverd = findViewById(R.id.ll_sort_recovered);
        ll_sortDeath = findViewById(R.id.ll_sort_death);
        ll_sortTotal = findViewById(R.id.ll_sort_total);

        iv_country = findViewById(R.id.iv_country);
        iv_deaths = findViewById(R.id.iv_death);
        iv_recovered = findViewById(R.id.iv_recoverd);
        iv_total = findViewById(R.id.iv_total);
        btn_retry = findViewById(R.id.btn_retry);
        ll_statisticData = findViewById(R.id.ll_statistics);

        progressBar = findViewById(R.id.progressBar);
        placeHolderImage = (ImageView) findViewById(R.id.iv_placeholder);
        Glide.with(this).load(R.drawable.loading).asGif().into(placeHolderImage);
    }

    private void initializeListener() {
        covidDataListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                calculateTotalStatistic();
            }
        });
        ll_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog filterDialog = new FilterDialog(MainActivity.this, MainActivity.this);
                filterDialog.show(getSupportFragmentManager(), "Filter");
            }
        });

        ll_totalAppliedFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingPreference.setBooleanField(MainActivity.this, SettingPreference.KEY_TOTAL_SELECTED, false);
                SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_TOTAL_NUMBER, 0);
                SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_TOTAL_FILTER, Utils.FILTER_TOTAL_GREATER);
                new RefreshAsynTask().execute("");

            }
        });

        ll_deathAppliedFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingPreference.setBooleanField(MainActivity.this, SettingPreference.KEY_DEATH_SELECTED, false);
                SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_DEATH_NUMBER, 0);
                SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_DEATH_FILTER, Utils.FILTER_DEATH_GREATER);
                new RefreshAsynTask().execute("");

            }
        });
        ll_recoverAppliedFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingPreference.setBooleanField(MainActivity.this, SettingPreference.KEY_RECOVERED_SELECTED, false);
                SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_RECOVERED_NUMBER, 0);
                SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_RECOVERED_FILTER, Utils.FILTER_RECOVERED_GREATER);
                new RefreshAsynTask().execute("");

            }
        });

        ll_sortCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, ll_sortCountry);
                popupMenu.getMenuInflater().inflate(R.menu.menu_sort_country, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_ascending) {
                            SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_SORT_OPTION, Utils.SORT_COUNTRY_ASC);
                            sortList(Utils.SORT_COUNTRY_ASC, true);

                            covidDataListAdapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.action_descending) {
                            SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_SORT_OPTION, Utils.SORT_COUNTRY_DES);
                            sortList(Utils.SORT_COUNTRY_DES, true);
                            covidDataListAdapter.notifyDataSetChanged();
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        ll_sortTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, ll_sortTotal);
                popupMenu.getMenuInflater().inflate(R.menu.menu_sort_total, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_total_aes) {
                            SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_SORT_OPTION, Utils.SORT_TOTAL_ASC);
                            sortList(Utils.SORT_TOTAL_ASC, true);
                            covidDataListAdapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.action_total_des) {
                            SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_SORT_OPTION, Utils.SORT_TOTAL_DSC);
                            sortList(Utils.SORT_TOTAL_DSC, true);
                            covidDataListAdapter.notifyDataSetChanged();
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });
        ll_sortDeath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, ll_sortDeath);
                popupMenu.getMenuInflater().inflate(R.menu.menu_sort_death, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_death_aes) {
                            SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_SORT_OPTION, Utils.SORT_DEATH_ASC);
                            sortList(Utils.SORT_DEATH_ASC, true);
                            covidDataListAdapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.action_death_des) {
                            SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_SORT_OPTION, Utils.SORT_DEATH_DES);
                            sortList(Utils.SORT_DEATH_DES, true);
                            covidDataListAdapter.notifyDataSetChanged();
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });
        ll_sortRecoverd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, ll_sortRecoverd);
                popupMenu.getMenuInflater().inflate(R.menu.menu_sort_recover, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.action_recover_aes) {
                            SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_SORT_OPTION, Utils.SORT_RECOVERED_ASC);
                            sortList(Utils.SORT_RECOVERED_ASC, true);
                            covidDataListAdapter.notifyDataSetChanged();
                        } else if (menuItem.getItemId() == R.id.action_recover_des) {
                            SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_SORT_OPTION, Utils.SORT_RECOVERED_DES);
                            sortList(Utils.SORT_RECOVERED_DES, true);
                            covidDataListAdapter.notifyDataSetChanged();
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(MainActivity.this)) {
                    fetchDataFromServer();
                } else {
                    ll_statisticData.setVisibility(View.GONE);
                    placeHolderImage.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void fetchDataFromServer() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            if (covidEntityArrayList.size() <= 0) {
                placeHolderImage.setVisibility(View.VISIBLE);
            }
            ApiClient.ApiInterface apiService = new ApiClient().getClient().create(ApiClient.ApiInterface.class);
            Call<ArrayList<CovidEntity>> call = apiService.getAllData(ApiClient.HEADER_KEY, ApiClient.HEADER_HOST);
            call.enqueue(new Callback<ArrayList<CovidEntity>>() {
                @Override
                public void onResponse(Call<ArrayList<CovidEntity>> call, Response<ArrayList<CovidEntity>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        btn_retry.setVisibility(View.GONE);
                        covidEntityArrayList.clear();
                        duplicateCovidList.clear();
                        covidEntityArrayList.addAll(response.body());
                        duplicateCovidList.addAll(covidEntityArrayList);
                        new RefreshAsynTask().execute("");
                    } else {
                        progressBar.setVisibility(View.GONE);
                        placeHolderImage.setVisibility(View.GONE);
                        if (covidEntityArrayList.size() <= 0) {
                            ll_statisticData.setVisibility(View.GONE);
                            btn_retry.setVisibility(View.VISIBLE);
                            rv_countyDataList.setVisibility(View.GONE);
                        }
                    }

                }

                @Override
                public void onFailure(Call<ArrayList<CovidEntity>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    if (covidEntityArrayList.size() <= 0) {
                        ll_statisticData.setVisibility(View.GONE);
                        btn_retry.setVisibility(View.VISIBLE);
                        placeHolderImage.setVisibility(View.GONE);
                        rv_countyDataList.setVisibility(View.GONE);
                    }
                    if (!Utils.isNetworkAvailable(MainActivity.this)) {
                        Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, t.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void calculateTotalStatistic() {
        long totalCase = 0;
        long death = 0;
        long recovered = 0;
        for (CovidEntity covidEntity : covidEntityArrayList) {
            totalCase = totalCase + covidEntity.getConfirmed();
            death = death + covidEntity.getDeaths();
            recovered = recovered + covidEntity.getRecovered();
        }
        tv_totalRecovered.setText(String.valueOf(recovered));
        tv_totalDeath.setText(String.valueOf(death));
        tv_totalCase.setText(String.valueOf(totalCase));
    }

    @Override
    public void onFilterApplied(long total, long recover, long death, int totalFilter, int recoverFilter, int deathFilter, boolean isTotal, boolean isRecover, boolean isDeath) {
        SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_TOTAL_FILTER, totalFilter);
        SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_DEATH_FILTER, deathFilter);
        SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_RECOVERED_FILTER, recoverFilter);

        SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_TOTAL_NUMBER, (int) total);
        SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_DEATH_NUMBER, (int) death);
        SettingPreference.setIntField(MainActivity.this, SettingPreference.KEY_RECOVERED_NUMBER, (int) recover);

        SettingPreference.setBooleanField(MainActivity.this, SettingPreference.KEY_TOTAL_SELECTED, isTotal);
        SettingPreference.setBooleanField(MainActivity.this, SettingPreference.KEY_DEATH_SELECTED, isDeath);
        SettingPreference.setBooleanField(MainActivity.this, SettingPreference.KEY_RECOVERED_SELECTED, isRecover);

        new RefreshAsynTask().execute("");


    }

    private void setAppliedFilters(long total, long recover, long death, int totalFilter, int recoverFilter, int deathFilter, boolean isTotal, boolean isRecover, boolean isDeath) {
        if (isTotal) {
            ll_totalAppliedFilter.setVisibility(View.VISIBLE);
            if (totalFilter == Utils.FILTER_TOTAL_GREATER) {
                tv_filterTotalTile.setText(getString(R.string.total_case) + " " + getString(R.string.greater) + " " + total);
            } else {
                tv_filterTotalTile.setText(getString(R.string.total_case) + " " + getString(R.string.less) + " " + total);
            }
        } else {
            ll_totalAppliedFilter.setVisibility(View.GONE);
        }
        if (isRecover) {
            ll_recoverAppliedFilter.setVisibility(View.VISIBLE);
            if (recoverFilter == Utils.FILTER_RECOVERED_GREATER) {
                tv_filterRecoverTitle.setText(getString(R.string.recovered) + " " + getString(R.string.greater) + " " + recover);
            } else {
                tv_filterRecoverTitle.setText(getString(R.string.recovered) + " " + getString(R.string.less) + " " + recover);
            }

        } else {
            ll_recoverAppliedFilter.setVisibility(View.GONE);
        }
        if (isDeath) {
            ll_deathAppliedFilter.setVisibility(View.VISIBLE);
            if (deathFilter == Utils.FILTER_DEATH_GREATER) {
                tv_filterDeathFilter.setText(getString(R.string.death) + " " + getString(R.string.greater) + " " + death);
            } else {
                tv_filterDeathFilter.setText(getString(R.string.death) + " " + getString(R.string.less) + " " + death);
            }
        } else {
            ll_deathAppliedFilter.setVisibility(View.GONE);
        }

    }

    public class RefreshAsynTask extends AsyncTask<String, String, String> {
        boolean isTotal, isRecover, isDeath, condition1, condition2, condition3;
        int totalFilter, recoverFilter, deathFilter, total, recover, death;
        int sortOption;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            totalFilter = SettingPreference.getIntField(SettingPreference.KEY_TOTAL_FILTER, MainActivity.this);
            recoverFilter = SettingPreference.getIntField(SettingPreference.KEY_RECOVERED_FILTER, MainActivity.this);
            deathFilter = SettingPreference.getIntField(SettingPreference.KEY_DEATH_FILTER, MainActivity.this);

            isTotal = SettingPreference.getBooleanField(SettingPreference.KEY_TOTAL_SELECTED, MainActivity.this);
            isRecover = SettingPreference.getBooleanField(SettingPreference.KEY_RECOVERED_SELECTED, MainActivity.this);
            isDeath = SettingPreference.getBooleanField(SettingPreference.KEY_DEATH_SELECTED, MainActivity.this);


            total = SettingPreference.getIntField(SettingPreference.KEY_TOTAL_NUMBER, MainActivity.this);
            recover = SettingPreference.getIntField(SettingPreference.KEY_RECOVERED_NUMBER, MainActivity.this);
            death = SettingPreference.getIntField(SettingPreference.KEY_DEATH_NUMBER, MainActivity.this);

            sortOption = SettingPreference.getIntField(SettingPreference.KEY_SORT_OPTION, MainActivity.this);


        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                covidEntityArrayList.clear();
                if (isTotal && isRecover && isDeath) {
                    for (CovidEntity covidEntity : duplicateCovidList) {
                        if (totalFilter == Utils.FILTER_TOTAL_GREATER) {
                            condition1 = covidEntity.getConfirmed() >= total;

                        } else {
                            condition1 = covidEntity.getConfirmed() <= total;

                        }

                        if (deathFilter == Utils.FILTER_DEATH_GREATER) {
                            condition2 = covidEntity.getDeaths() >= death;

                        } else {
                            condition2 = covidEntity.getDeaths() <= death;

                        }

                        if (recoverFilter == Utils.FILTER_RECOVERED_GREATER) {
                            condition3 = covidEntity.getRecovered() >= recover;

                        } else {
                            condition3 = covidEntity.getRecovered() <= recover;

                        }

                        if (condition1 && condition2 && condition3) {
                            covidEntityArrayList.add(covidEntity);
                        }

                    }
                } else if (isTotal && isDeath) {
                    for (CovidEntity covidEntity : duplicateCovidList) {
                        if (totalFilter == Utils.FILTER_TOTAL_GREATER) {
                            condition1 = covidEntity.getConfirmed() >= total;

                        } else {
                            condition1 = covidEntity.getConfirmed() <= total;

                        }

                        if (deathFilter == Utils.FILTER_DEATH_GREATER) {
                            condition2 = covidEntity.getDeaths() >= death;


                        } else {
                            condition2 = covidEntity.getDeaths() <= death;


                        }

                        if (condition1 && condition2) {
                            covidEntityArrayList.add(covidEntity);
                        }

                    }
                } else if (isTotal && isRecover) {
                    for (CovidEntity covidEntity : duplicateCovidList) {
                        if (totalFilter == Utils.FILTER_TOTAL_GREATER) {
                            condition1 = covidEntity.getConfirmed() >= total;

                        } else {
                            condition1 = covidEntity.getConfirmed() <= total;

                        }


                        if (recoverFilter == Utils.FILTER_RECOVERED_GREATER) {
                            condition3 = covidEntity.getRecovered() >= recover;

                        } else {
                            condition3 = covidEntity.getRecovered() <= recover;

                        }

                        if (condition1 && condition3) {
                            covidEntityArrayList.add(covidEntity);
                        }

                    }
                } else if (isDeath && isRecover) {
                    for (CovidEntity covidEntity : duplicateCovidList) {


                        if (deathFilter == Utils.FILTER_DEATH_GREATER) {
                            condition2 = covidEntity.getDeaths() >= death;

                        } else {
                            condition2 = covidEntity.getDeaths() <= death;

                        }

                        if (recoverFilter == Utils.FILTER_RECOVERED_GREATER) {
                            condition3 = covidEntity.getRecovered() >= recover;

                        } else {
                            condition3 = covidEntity.getRecovered() <= recover;

                        }

                        if (condition2 && condition3) {
                            covidEntityArrayList.add(covidEntity);
                        }

                    }
                } else if (isDeath) {
                    for (CovidEntity covidEntity : duplicateCovidList) {
                        if (deathFilter == Utils.FILTER_DEATH_GREATER) {
                            condition2 = covidEntity.getDeaths() >= death;

                        } else {
                            condition2 = covidEntity.getDeaths() <= death;

                        }
                        if (condition2)
                            covidEntityArrayList.add(covidEntity);
                    }
                } else if (isRecover) {
                    for (CovidEntity covidEntity : duplicateCovidList) {
                        if (recoverFilter == Utils.FILTER_RECOVERED_GREATER) {
                            condition3 = covidEntity.getRecovered() >= recover;

                        } else {
                            condition3 = covidEntity.getRecovered() <= recover;

                        }
                        if (condition3)
                            covidEntityArrayList.add(covidEntity);


                    }
                } else if (isTotal) {
                    for (CovidEntity covidEntity : duplicateCovidList) {
                        if (totalFilter == Utils.FILTER_TOTAL_GREATER) {
                            condition1 = covidEntity.getConfirmed() >= total;

                        } else {
                            condition1 = covidEntity.getConfirmed() <= total;

                        }
                        if (condition1) {
                            covidEntityArrayList.add(covidEntity);
                        }

                    }
                } else {
                    for (CovidEntity covidEntity : duplicateCovidList) {
                        if (covidEntity.getConfirmed() != 0) {
                            covidEntityArrayList.add(covidEntity);
                        }
                    }
                }


                sortList(sortOption, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            covidDataListAdapter.notifyDataSetChanged();
            setAppliedFilters(total, recover, death, totalFilter, recoverFilter, deathFilter, isTotal, isRecover, isDeath);
            progressBar.setVisibility(View.GONE);
            if (covidEntityArrayList.size() > 0) {
                ll_statisticData.setVisibility(View.VISIBLE);
                placeHolderImage.setVisibility(View.GONE);
                rv_countyDataList.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void sortList(int sortOption, boolean isUIThread) {

        if (isUIThread) {
            resetImageResource();
        }
        switch (sortOption) {
            case Utils.SORT_TOTAL_ASC:
                Collections.sort(covidEntityArrayList, new TotalCaseComparator(Utils.ACS));
                if (isUIThread) {
                    iv_total.setRotation(180);
                    iv_total.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                break;
            case Utils.SORT_TOTAL_DSC:
                Collections.sort(covidEntityArrayList, new TotalCaseComparator(Utils.DES));
                if (isUIThread) {
                    iv_total.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                break;
            case Utils.SORT_RECOVERED_ASC:
                Collections.sort(covidEntityArrayList, new RecoveredCaseComparartor(Utils.ACS));
                if (isUIThread) {
                    iv_recovered.setRotation(180);
                    iv_recovered.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                break;
            case Utils.SORT_RECOVERED_DES:
                Collections.sort(covidEntityArrayList, new RecoveredCaseComparartor(Utils.DES));
                if (isUIThread) {
                    iv_recovered.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                break;
            case Utils.SORT_DEATH_ASC:
                Collections.sort(covidEntityArrayList, new DeathCaseComparator(Utils.ACS));
                if (isUIThread) {
                    iv_deaths.setRotation(180);
                    iv_deaths.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                break;
            case Utils.SORT_DEATH_DES:
                Collections.sort(covidEntityArrayList, new DeathCaseComparator(Utils.DES));
                if (isUIThread) {
                    iv_deaths.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                break;
            case Utils.SORT_COUNTRY_ASC:
                Collections.sort(covidEntityArrayList, new CountryNameComparator(Utils.ACS));
                if (isUIThread) {
                    iv_country.setRotation(180);
                    iv_country.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                break;
            case Utils.SORT_COUNTRY_DES:
                Collections.sort(covidEntityArrayList, new CountryNameComparator(Utils.DES));
                if (isUIThread) {
                    iv_country.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
                }
                break;
        }

        CovidEntity selectedEntity;
        CovidEntity tempEntity = new CovidEntity();
        tempEntity.setCountry(SettingPreference.getCountry(MainActivity.this, SettingPreference.KEY_COUNTRY_NAME));
        int index = covidEntityArrayList.indexOf(tempEntity);
        if (index > -0) {
            selectedEntity = covidEntityArrayList.get(index);
            if (selectedEntity != null) {
                covidEntityArrayList.remove(selectedEntity);
                covidEntityArrayList.add(0, selectedEntity);
            }
        }
    }

    private void resetImageResource() {
        iv_country.setRotation(0);
        iv_country.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);

        iv_total.setRotation(0);
        iv_total.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);

        iv_recovered.setRotation(0);
        iv_recovered.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);

        iv_deaths.setRotation(0);
        iv_deaths.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN);

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            setCountry(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    };

    private void setCountry(double latitude, double longitude) {
        SettingPreference.setCountry(MainActivity.this, latitude + "", SettingPreference.KEY_COUNTRY_LAT);
        SettingPreference.setCountry(MainActivity.this, longitude + "", SettingPreference.KEY_COUNTRY_LONG);
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            SettingPreference.setCountry(MainActivity.this, addresses.get(0).getCountryName(), SettingPreference.KEY_COUNTRY_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    setCountry(location.getLatitude(), location.getLongitude());
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

}
