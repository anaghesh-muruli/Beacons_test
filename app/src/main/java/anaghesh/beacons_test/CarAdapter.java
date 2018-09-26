package anaghesh.beacons_test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class CarAdapter extends RecyclerView.Adapter<CarAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<CarData> carList;
    private List<CarData> carListFiltered;
   // private CarAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView carVIN,carChassisNumber,carModel;


        public MyViewHolder(View itemView) {
            super(itemView);
            carVIN = (TextView)itemView.findViewById(R.id.CarVin);
            carChassisNumber=(TextView)itemView.findViewById(R.id.ChassisNumber);
            carModel = (TextView)itemView.findViewById(R.id.VehicleModel);
        }
    }


    public CarAdapter(Context context, List<CarData> carList) {
        this.context = context;
        this.carList = carList;
        this.carListFiltered = carList;
    }

    @Override
    public CarAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card_car_inventory, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final CarData carData = carListFiltered.get(position);
        holder.carVIN.setText(carData.getcarVin());
        holder.carChassisNumber.setText(carData.getCarChassisNumber());
        holder.carModel.setText(carData.getCarModel());

    }

    @Override
    public int getItemCount() {
        return carListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    carListFiltered = carList;
                } else {
                    List<CarData> filteredList = new ArrayList<>();
                    for (CarData row : carList) {

                        if (String.valueOf(row.getcarVin()).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    carListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = carListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                carListFiltered = (ArrayList<CarData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
