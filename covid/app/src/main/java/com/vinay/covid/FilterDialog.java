package com.vinay.covid;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.vinay.covid.sharedPreference.SettingPreference;

public class FilterDialog extends DialogFragment {
    private Dialog mDialog;
    private Context mContext;
    private RadioButton rb_totalGreater, rb_totalLess, rb_recoverGreater, rb_recoverLess, rb_deathGreater, rb_deathLess;
    private CheckBox cb_total, cb_recover, cb_death;
    private LinearLayout tv_save, tv_cancel, ll_total, ll_recover, ll_death;
    private FilterInterface filterInterface;
    private int totalFilter, recoverFilter, deathFilter;
    private EditText et_totalNumber;
    private EditText et_recoverNumber;
    private EditText et_deathNumber;

    FilterDialog(Context mContext, FilterInterface filterInterface) {
        this.mContext = mContext;
        this.filterInterface = filterInterface;
    }

    public interface FilterInterface {
        void onFilterApplied(long total, long recover, long death, int totalFilter, int recoverFilter, int deathFilter, boolean isTotal, boolean isRecover, boolean isDeath);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new Dialog(mContext);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_filter_dialog);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        initializeId();
        initializeData();
        initializeListener();
        return mDialog;
    }

    private void initializeId() {
        et_totalNumber = mDialog.findViewById(R.id.et_totalNumber);
        et_recoverNumber = mDialog.findViewById(R.id.et_recoverNumber);
        et_deathNumber = mDialog.findViewById(R.id.et_deathNumber);
        rb_totalGreater = mDialog.findViewById(R.id.rb_totalGreater);
        rb_totalLess = mDialog.findViewById(R.id.rb_totalLess);
        rb_recoverGreater = mDialog.findViewById(R.id.rb_recoverGreater);
        rb_recoverLess = mDialog.findViewById(R.id.rb_recoverLess);
        rb_deathGreater = mDialog.findViewById(R.id.rb_deathGreater);
        rb_deathLess = mDialog.findViewById(R.id.rb_deathLess);
        tv_save = mDialog.findViewById(R.id.tv_save);
        tv_cancel = mDialog.findViewById(R.id.tv_cancel);
        cb_death = mDialog.findViewById(R.id.cb_death);
        cb_recover = mDialog.findViewById(R.id.cb_recovered);
        cb_total = mDialog.findViewById(R.id.cb_totalCase);
        ll_death = mDialog.findViewById(R.id.ll_death);
        ll_recover = mDialog.findViewById(R.id.ll_recovered);
        ll_total = mDialog.findViewById(R.id.ll_totalCase);

    }


    private void initializeListener() {
        rb_totalGreater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    totalFilter = Utils.FILTER_TOTAL_GREATER;
                }
                changeTextColor();
            }
        });

        rb_totalLess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    totalFilter = Utils.FILTER_TOTAL_LESS;
                }
                changeTextColor();
            }
        });
        rb_recoverGreater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recoverFilter = Utils.FILTER_RECOVERED_GREATER;
                }
                changeTextColor();
            }
        });

        rb_recoverLess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recoverFilter = Utils.FILTER_RECOVERED_LESS;
                }
                changeTextColor();
            }
        });
        rb_deathGreater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deathFilter = Utils.FILTER_DEATH_GREATER;
                }
                changeTextColor();
            }
        });

        rb_deathLess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    deathFilter = Utils.FILTER_DEATH_LESS;
                }
                changeTextColor();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    long total = 0, recover = 0, death = 0;
                    if (Utils.isStringNotNull(et_totalNumber.getText().toString())) {
                        total = Long.valueOf(et_totalNumber.getText().toString());
                    }
                    if (Utils.isStringNotNull(et_recoverNumber.getText().toString())) {
                        recover = Long.valueOf(et_recoverNumber.getText().toString());
                    }
                    if (Utils.isStringNotNull(et_deathNumber.getText().toString())) {
                        death = Long.valueOf(et_deathNumber.getText().toString());
                    }

                    filterInterface.onFilterApplied(total, recover, death, totalFilter, recoverFilter, deathFilter, cb_total.isChecked(), cb_recover.isChecked(), cb_death.isChecked());
                    dismiss();
                }
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        cb_total.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ll_total.setVisibility(View.VISIBLE);
                } else {
                    ll_total.setVisibility(View.GONE);
                }
            }
        });

        cb_recover.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ll_recover.setVisibility(View.VISIBLE);
                } else {
                    ll_recover.setVisibility(View.GONE);
                }
            }
        });
        cb_death.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ll_death.setVisibility(View.VISIBLE);
                } else {
                    ll_death.setVisibility(View.GONE);
                }
            }
        });
    }

    private void changeTextColor() {
        if (Utils.isAboveKitkat()) {
            if (rb_totalGreater.isChecked()) {
                rb_totalGreater.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                rb_totalGreater.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }

            if (rb_totalLess.isChecked()) {
                rb_totalLess.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                rb_totalLess.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }


            if (rb_recoverGreater.isChecked()) {
                rb_recoverGreater.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                rb_recoverGreater.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }


            if (rb_recoverLess.isChecked()) {
                rb_recoverLess.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                rb_recoverLess.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }

            if (rb_deathGreater.isChecked()) {
                rb_deathGreater.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                rb_deathGreater.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }

            if (rb_deathLess.isChecked()) {
                rb_deathLess.setTextColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                rb_deathLess.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }


        } else {

            if (rb_totalGreater.isChecked()) {
                rb_totalGreater.setTextColor(getResources().getColor(R.color.white));
            } else {
                rb_totalGreater.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            if (rb_totalLess.isChecked()) {
                rb_totalLess.setTextColor(getResources().getColor(R.color.white));
            } else {
                rb_totalLess.setTextColor(getResources().getColor(R.color.colorPrimary));
            }


            if (rb_recoverGreater.isChecked()) {
                rb_recoverGreater.setTextColor(getResources().getColor(R.color.white));
            } else {
                rb_recoverGreater.setTextColor(getResources().getColor(R.color.colorPrimary));
            }


            if (rb_recoverLess.isChecked()) {
                rb_recoverLess.setTextColor(getResources().getColor(R.color.white));
            } else {
                rb_recoverLess.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            if (rb_deathGreater.isChecked()) {
                rb_deathGreater.setTextColor(getResources().getColor(R.color.white));
            } else {
                rb_deathGreater.setTextColor(getResources().getColor(R.color.colorPrimary));
                ;
            }

            if (rb_deathLess.isChecked()) {
                rb_deathLess.setTextColor(getResources().getColor(R.color.white));
            } else {
                rb_deathLess.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        }

    }

    private boolean isValid() {
        if (cb_total.isChecked() && !Utils.isStringNotNull(et_totalNumber.getText().toString())) {
            Toast.makeText(mContext, getString(R.string.error_total), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cb_death.isChecked() && !Utils.isStringNotNull(et_deathNumber.getText().toString())) {
            Toast.makeText(mContext, getString(R.string.error_death), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cb_recover.isChecked() && !Utils.isStringNotNull(et_recoverNumber.getText().toString())) {
            Toast.makeText(mContext, getString(R.string.error_recovered), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initializeData() {
        cb_total.setChecked(SettingPreference.getBooleanField(SettingPreference.KEY_TOTAL_SELECTED, mContext));
        cb_recover.setChecked(SettingPreference.getBooleanField(SettingPreference.KEY_RECOVERED_SELECTED, mContext));
        cb_death.setChecked(SettingPreference.getBooleanField(SettingPreference.KEY_DEATH_SELECTED, mContext));
        if (cb_total.isChecked()) {
            ll_total.setVisibility(View.VISIBLE);
            et_totalNumber.setText(String.valueOf(SettingPreference.getIntField(SettingPreference.KEY_TOTAL_NUMBER, mContext)));
        }
        if (cb_recover.isChecked()) {
            ll_recover.setVisibility(View.VISIBLE);
            et_recoverNumber.setText(String.valueOf(SettingPreference.getIntField(SettingPreference.KEY_RECOVERED_NUMBER, mContext)));
        }
        if (cb_death.isChecked()) {
            ll_death.setVisibility(View.VISIBLE);
            et_deathNumber.setText(String.valueOf(SettingPreference.getIntField(SettingPreference.KEY_DEATH_NUMBER, mContext)));
        }

        setTotalGreaterLess(SettingPreference.getIntField(SettingPreference.KEY_TOTAL_FILTER, mContext));
        setRecoveredGreaterLess(SettingPreference.getIntField(SettingPreference.KEY_RECOVERED_FILTER, mContext));
        setDeathGreaterLess(SettingPreference.getIntField(SettingPreference.KEY_DEATH_FILTER, mContext));
        changeTextColor();

    }

    private void setTotalGreaterLess(int mode) {
        if (mode == Utils.FILTER_TOTAL_GREATER) {
            rb_totalGreater.setChecked(true);
        } else {
            rb_totalLess.setChecked(true);
        }

    }

    private void setRecoveredGreaterLess(int mode) {
        if (mode == Utils.FILTER_RECOVERED_GREATER) {
            rb_recoverGreater.setChecked(true);
        } else {
            rb_recoverLess.setChecked(true);
        }

    }

    private void setDeathGreaterLess(int mode) {
        if (mode == Utils.FILTER_DEATH_GREATER) {
            rb_deathGreater.setChecked(true);
        } else {
            rb_deathLess.setChecked(true);
        }
    }
}

