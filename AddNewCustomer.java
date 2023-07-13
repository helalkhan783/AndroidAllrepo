package com.ismos_salt_erp.view.fragment.customers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.ismos_salt_erp.Common;
import com.ismos_salt_erp.CommonNormal;
import com.ismos_salt_erp.R;
import com.ismos_salt_erp.adapter.ContactAdapter;
import com.ismos_salt_erp.clickHandle.AddNewCustomerClickHandle;
import com.ismos_salt_erp.databinding.FragmentAddNewCustomerBinding;
import com.ismos_salt_erp.serverResponseModel.DistrictListResponse;
import com.ismos_salt_erp.serverResponseModel.DivisionResponse;
import com.ismos_salt_erp.serverResponseModel.ThanaList;
import com.ismos_salt_erp.utils.ContactInfo;
import com.ismos_salt_erp.utils.NumberUtil;
import com.ismos_salt_erp.view.fragment.filter.FilterClass;
import com.ismos_salt_erp.viewModel.CustomerViewModel;
import com.ismos_salt_erp.viewModel.DueCollectionViewModel;
import com.ismos_salt_erp.viewModel.MillerProfileInfoViewModel;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;

public class AddNewCustomer extends BaseFragment {
    private FragmentAddNewCustomerBinding binding;
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_customer, container, false);
  
        return binding.getRoot();
    }

private void alertDialog(){
        
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
        @SuppressLint("InflateParams")
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.contact_dialog, null);
        //Set the view
        builder.setView(view);
        EditText searchEt = view.findViewById(R.id.search);
        RecyclerView rv = view.findViewById(R.id.contactRv);
        alertDialog = builder.create();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
             alertDialog.setCanceledOnTouchOutside(false);
 
        //     cross.setOnClickListener(v -> alertDialog.dismiss());//for cancel


        alertDialog.show();
}
  


}
