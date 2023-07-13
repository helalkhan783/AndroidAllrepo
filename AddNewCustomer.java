 

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
