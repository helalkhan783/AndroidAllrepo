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

public class AddNewCustomer extends AddUpDel implements SmartMaterialSpinner.OnItemSelectedListener, ContactInfo {
    private FragmentAddNewCustomerBinding binding;
    private MillerProfileInfoViewModel millerProfileInfoViewModel;
    private CustomerViewModel customerViewModel;

    MultipartBody.Part image = null;
    /**
     * For Division
     */
    private List<DivisionResponse> divisionResponseList;
    private List<String> divisionNameList;
    /**
     * For District
     */
    private List<DistrictListResponse> districtListResponseList;
    private List<String> districtNameList;
    /**
     * For Thana
     */
    private List<ThanaList> thanaListsResponse;
    private List<String> thanaNameList;
    /**
     * For Selected Customer
     */
    private List<String> selectedCustomerTypeName;
    private List<String> selectedCustomerTypeIdList;

    private String selectedDivision, selectedDistrict, selectedThana, selectedCustomerType;
    private DueCollectionViewModel dueCollectionViewModel;
    public String name, id;


    AlertDialog alertDialog;

    ArrayList<NumberUtil> numberUtils;
    ContactAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_customer, container, false);
        millerProfileInfoViewModel = new ViewModelProvider(this).get(MillerProfileInfoViewModel.class);
        customerViewModel = new ViewModelProvider(this).get(CustomerViewModel.class);
        binding.toolbar.toolbarTitle.setText("Add New Customer");
        dueCollectionViewModel = new ViewModelProvider(this).get(DueCollectionViewModel.class);

        getPageDataFromServer();
        binding.toolbar.setClickHandle(() -> {
            hideKeyboard(getActivity());
            getActivity().onBackPressed();
        });

        binding.setClickHandle(new AddNewCustomerClickHandle() {
            @Override
            public void getImage() {
                forImage();
            }

            @Override
            public void submit() {
                if (binding.companyName.getText().toString().isEmpty()) {
                    binding.companyName.setError("Empty");
                    binding.companyName.requestFocus();
                    return;
                }
                if (binding.ownerName.getText().toString().isEmpty()) {
                    binding.ownerName.setError("Empty");
                    binding.ownerName.requestFocus();
                    return;
                }
                if (binding.phone.getText().toString().isEmpty()) {
                    binding.phone.setError("Empty");
                    binding.phone.requestFocus();
                    return;
                }
                if (!binding.phone.getText().toString().isEmpty()) {
                    if (!isValidPhone(binding.phone.getText().toString())) {
                        binding.phone.setError("Invalid Contact Number");
                        binding.phone.requestFocus();
                        return;
                    }
                }

                if (!binding.altPhone.getText().toString().isEmpty()) {
                    if (!isValidPhone(binding.altPhone.getText().toString())) {
                        binding.altPhone.setError("Invalid Number");
                        binding.altPhone.requestFocus();
                        return;
                    }
                }

                if (!binding.email.getText().toString().isEmpty()) {
                    if (!isValidEmail(binding.email.getText().toString())) {
                        binding.email.setError("Invalid Email");
                        binding.email.requestFocus();
                        return;
                    }
                }

                if (selectedDivision == null) {
                    message(getString(R.string.division_mes));
                    return;
                }
                if (selectedDistrict == null) {
                    message(getString(R.string.district_mes));
                    return;
                }
                if (selectedThana == null) {
                    message(getString(R.string.thana_mes));

                    return;
                }
                if (selectedCustomerType == null) {
                    message(getString(R.string.customer_mes));

                    return;
                }
                showDialog(getString(R.string.customer_add)); // show
            }
        });

        binding.division.setOnItemSelectedListener(this);
        binding.district.setOnItemSelectedListener(this);
        binding.thana.setOnItemSelectedListener(this);
        binding.customerType.setOnItemSelectedListener(this);

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            numberUtils = new ArrayList<>();
            getAllContacts(numberUtils);
        } else {
            requestPermission();
        }

        binding.importContact.setOnClickListener(v -> contactDialog(getActivity()));


        return binding.getRoot();
    }


    private void getCustomerList() {
        dueCollectionViewModel
                .apiCallForGetCustomers(
                        getActivity(),
                        getToken(getActivity().getApplication()),
                        getVendorId(getActivity().getApplication()),
                        binding.companyName.getText().toString().trim()
                );

        dueCollectionViewModel.getCustomerList()
                .observe(getViewLifecycleOwner(), response -> {
                    if (response == null) {
                        errorMessage(getActivity().getApplication(), "Something Wrong");
                        return;
                    }
                    if (response.getStatus() != 200) {
                        errorMessage(getActivity().getApplication(), "Something Wrong");
                        return;
                    }


                    for (int i = 0; i < response.getLists().size(); i++) {
                        name = response.getLists().get(i).getCompanyName() + response.getLists().get(i).getCustomerFname();
                        id = response.getLists().get(i).getCustomerID();

                        Common ref = new CommonNormal();
                        ref.customerKey(name, id);


                        getActivity().onBackPressed();
                    }


                });

    }

    private void getPageDataFromServer() {
        if (!(isInternetOn(getActivity()))) {
            infoMessage(getActivity().getApplication(), "Please Check Your Internet Connection");
            return;
        }
        /**
         * For get Division list from This Api
         */
        millerProfileInfoViewModel.getProfileInfoResponse(getActivity())
                .observe(getViewLifecycleOwner(), response -> {
                    if (response == null) {
                        errorMessage(getActivity().getApplication(), "Something Wrong");
                        return;
                    }
                    /**
                     * now set division list
                     */
                    divisionResponseList = new ArrayList<>();
                    divisionResponseList.clear();
                    divisionResponseList.addAll(response.getDivisions());

                    divisionNameList = new ArrayList<>();
                    divisionNameList.clear();

                    for (int i = 0; i < response.getDivisions().size(); i++) {
                        divisionNameList.add(response.getDivisions().get(i).getName());
                    }
                    binding.division.setItem(divisionNameList);
                });


        /**
         * now set selected customer
         */
        selectedCustomerTypeName = new ArrayList<>();
        selectedCustomerTypeName.clear();
        selectedCustomerTypeIdList = new ArrayList<>();
        selectedCustomerTypeIdList.clear();

        selectedCustomerTypeName.addAll(Arrays.asList("General"));
        selectedCustomerTypeIdList.addAll(Arrays.asList("7"));
        binding.customerType.setItem(selectedCustomerTypeName);

    }

    private void getThanaListByDistrictId(String selectedDistrict) {
        millerProfileInfoViewModel.getThanaListByDistrictId(getActivity(), selectedDistrict)
                .observe(getViewLifecycleOwner(), response -> {
                    if (response == null) {
                        return;
                    }
                    if (response.getStatus() != 200) {
                        return;
                    }
                    thanaListsResponse = new ArrayList<>();
                    thanaListsResponse.clear();
                    thanaNameList = new ArrayList<>();
                    thanaNameList.clear();

                    thanaListsResponse.addAll(response.getLists());
                    for (int i = 0; i < response.getLists().size(); i++) {
                        thanaNameList.add(response.getLists().get(i).getName());
                    }
                    binding.thana.setItem(thanaNameList);
                });
    }

    private void getDistrictListByDivisionId(String selectedDivision) {

        if (!(isInternetOn(getActivity()))) {
            infoMessage(getActivity().getApplication(), "Please Check Your Internet Connection");
            return;
        }

        millerProfileInfoViewModel.getDistrictListByDivisionId(getActivity(), selectedDivision)
                .observe(getViewLifecycleOwner(), response -> {
                    if (response == null) {
                        return;
                    }
                    if (response.getStatus() != 200) {
                        return;
                    }
                    districtListResponseList = new ArrayList<>();
                    districtListResponseList.clear();
                    districtNameList = new ArrayList<>();
                    districtNameList.clear();

                    districtListResponseList.addAll(response.getLists());

                    for (int i = 0; i < response.getLists().size(); i++) {
                        districtNameList.add(response.getLists().get(i).getName());
                    }
                    binding.district.setItem(districtNameList);
                });
    }


    @Override
    public void imageUri(Intent data) {
        binding.image.setImageBitmap(getBitmapImage(data));
        binding.imageName.setText(new File("" + data.getData()).getName());
        image = imageLogobody(data.getData(), "");

    }

    @Override
    public void save() {
        String dueLimit = "0";
        if (!binding.dueLimit.getText().toString().isEmpty()) {
            dueLimit = binding.dueLimit.getText().toString();
        }
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        customerViewModel.addNewCustomer(
                        getActivity(), binding.companyName.getText().toString(),
                        binding.ownerName.getText().toString(),
                        binding.phone.getText().toString(),
                        binding.altPhone.getText().toString(),
                        binding.email.getText().toString(),
                        selectedDivision, selectedDistrict, selectedThana, binding.bazar.getText().toString(),
                        binding.nid.getText().toString(),
                        binding.tin.getText().toString(),
                        "0",
                        "1",
                        selectedCustomerType,
                        binding.address.getText().toString(),
                        dueLimit,
                        image,
                        binding.note.getText().toString())
                .observe(getViewLifecycleOwner(), response -> {
                    progressDialog.dismiss();
                    if (response == null) {
                        errorMessage(getActivity().getApplication(), "ERROR");
                        return;
                    }
                    if (response.getStatus() != 200) {
                        infoMessage(getActivity().getApplication(), "" + response.getMessage());
                        return;
                    }
                    hideKeyboard(getActivity());
                    successMessage(getActivity().getApplication(), "" + response.getMessage());

                    getCustomerList();

                });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.division) {
            selectedDivision = divisionResponseList.get(position).getDivisionId();
            getDistrictListByDivisionId(selectedDivision);
        }
        if (parent.getId() == R.id.district) {
            selectedDistrict = districtListResponseList.get(position).getDistrictId();
            getThanaListByDistrictId(selectedDistrict);
        }
        if (parent.getId() == R.id.thana) {
            selectedThana = thanaListsResponse.get(position).getUpazilaId();

        }
        if (parent.getId() == R.id.customerType) {
            selectedCustomerType = selectedCustomerTypeIdList.get(position);

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void contactDialog(FragmentActivity context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        @SuppressLint("InflateParams")
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.contact_dialog, null);
        //Set the view
        builder.setView(view);
        EditText searchEt = view.findViewById(R.id.search);
        RecyclerView rv = view.findViewById(R.id.contactRv);
        alertDialog = builder.create();

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //     alertDialog.setCancelable(false);
        //     alertDialog.setCanceledOnTouchOutside(false);

        try {
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ContactAdapter(numberUtils, getView(), this);
            rv.setAdapter(adapter);


            searchEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence query, int start, int count, int after) {
                    query = query.toString();
                    adapter.filter(query);


                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        } catch (Exception e) {
        }
        //     cross.setOnClickListener(v -> alertDialog.dismiss());//for cancel


        alertDialog.show();
    }


    @Override
    public void contactInfo(String name, String number) {
        alertDialog.dismiss();
        binding.ownerName.setText("" + name);
        binding.phone.setText("" + number);
    }


}