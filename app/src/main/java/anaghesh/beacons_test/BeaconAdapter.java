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


public class BeaconAdapter extends RecyclerView.Adapter<BeaconAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<BeaconData> beaconList;
    private List<BeaconData> beaconListFiltered;
    // private beaconAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView BeaconPublicId,MacId,UUID,Battery;


        public MyViewHolder(View itemView) {
            super(itemView);
            BeaconPublicId = (TextView)itemView.findViewById(R.id.BeaconPublicId);
            MacId=(TextView)itemView.findViewById(R.id.MacId);
            UUID = (TextView)itemView.findViewById(R.id.UUID);
            Battery = (TextView)itemView.findViewById(R.id.Battery);
        }
    }


    public BeaconAdapter(Context context, List<BeaconData> beaconList) {
        this.context = context;
        this.beaconList = beaconList;
        this.beaconListFiltered = beaconList;
    }

    @Override
    public BeaconAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_card_beacon_inventory, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final BeaconData beaconData = beaconListFiltered.get(position);
        holder.BeaconPublicId.setText(beaconData.getBeaconPublicId());
        holder.MacId.setText(beaconData.getMacId());
        holder.UUID.setText(beaconData.getUUID());
        holder.Battery.setText(beaconData.getBattery());

    }

    @Override
    public int getItemCount() {
        return beaconListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    beaconListFiltered = beaconList;
                } else {
                    List<BeaconData> filteredList = new ArrayList<>();
                    for (BeaconData row : beaconList) {

                        if (String.valueOf(row.getBeaconPublicId()).toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    beaconListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = beaconListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                beaconListFiltered = (ArrayList<BeaconData>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
