package com.example.h314gm.coolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.h314gm.coolweather.db.City;
import com.example.h314gm.coolweather.db.County;
import com.example.h314gm.coolweather.db.Province;
import com.example.h314gm.coolweather.util.HttpUtil;
import com.example.h314gm.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE=0;

    public static final int LEVEL_CITY=1;

    public static final int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;
    private TextView textView ;
    private Button backbutton;
    private RecyclerView listView;
    //private ArrayAdapter<String> adapter;
    private Adapter adapter;
    private List<String> dataList=new ArrayList<>();
    /** 省列表*/
    private List<Province> provinceList;
    /** 城市列表*/
    private List<City> cityList;
    /** 县列表*/
    private List<County> countyList;
    /** 选中的省*/
    private Province selectedProvince;
    /** 选中的城市*/
    private City selectedcity;
    /** 当前选中的级别*/
    private int currentLevel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        textView = view.findViewById(R.id.title_text);
        backbutton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        listView.setLayoutManager(linearLayoutManager);
        listView.addItemDecoration(new SimplePaddingDecoration(getContext()));
        adapter = new Adapter(dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter.setOnItemClickListener(new Adapter.OnRecyclerViewItemClickListener() {
            public void onItemClick(View view, int position) {
                if (currentLevel==LEVEL_PROVINCE)
                {
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if ((currentLevel==LEVEL_CITY))
                {
                    selectedcity=cityList.get(position);
                    queryCounties();
                }
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel==LEVEL_COUNTY)
                {
                    queryCities();
                }else if ((currentLevel==LEVEL_CITY))
                {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces()
    {
        textView.setText("中国");//selectedProvince.getProvinceName()
        backbutton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size()>0)
        {
            dataList.clear();
            for (Province province:provinceList)
            {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            //listView.setSelected(true);
            //listView.getLayoutManager().smoothScrollToPosition(listView,null,0);
            listView.getLayoutManager().scrollToPosition(0);
            currentLevel = LEVEL_PROVINCE;
        }else
        {
            String address= "http://guolin.tech/api/china";
            queryFormServer(address,ChooseAreaFragment.LEVEL_PROVINCE);
        }
    }

    private void queryCities()
    {
        textView.setText(selectedProvince.getProvinceName());
        backbutton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where(
                "provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size()>0)
        {
            dataList.clear();
            for (City city: cityList)
            {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.getLayoutManager().scrollToPosition(0);
            currentLevel=LEVEL_CITY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFormServer(address,ChooseAreaFragment.LEVEL_CITY);
        }
    }

    private void queryCounties()
    {
        textView.setText(selectedcity.getCityName());
        backbutton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where(
                "cityid = ?", String.valueOf(selectedcity.getId())).find(County.class);
        if (countyList.size()>0)
        {
            dataList.clear();
            for (County county: countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.getLayoutManager().scrollToPosition(0);
            currentLevel=LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedcity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFormServer(address,ChooseAreaFragment.LEVEL_COUNTY);
        }
    }

    private void queryFormServer(String address ,final int type)
    {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                switch (type)
                {
                    case ChooseAreaFragment.LEVEL_PROVINCE:
                        result = Utility.handleProvinceResponse(responseText);
                        break;
                    case ChooseAreaFragment.LEVEL_CITY:
                        result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                        break;
                    case ChooseAreaFragment.LEVEL_COUNTY:
                        result = Utility.handleCountyResponse(responseText,selectedcity.getId());
                        break;
                    default:
                        break;
                }
                if (result)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            switch (type)
                            {
                                case ChooseAreaFragment.LEVEL_PROVINCE:
                                    queryProvinces();
                                    break;
                                case ChooseAreaFragment.LEVEL_CITY:
                                    queryCities();
                                    break;
                                case ChooseAreaFragment.LEVEL_COUNTY:
                                    queryCounties();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog()
    {
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog()
    {
        if (progressDialog!=null)
        {
            progressDialog.dismiss();
        }
    }


}
