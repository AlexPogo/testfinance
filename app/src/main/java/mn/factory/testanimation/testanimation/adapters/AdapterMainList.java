package mn.factory.testanimation.testanimation.adapters;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

import mn.factory.testanimation.testanimation.R;
import mn.factory.testanimation.testanimation.models.FinanceModel;

/**
 * Created by pogodaev on 17.01.17.
 */

public class AdapterMainList extends RecyclerView.Adapter<AdapterMainList.MainListViewHolder> implements View.OnClickListener {

    private List<FinanceModel> financeModelList = new ArrayList<>();
    private Context context;
    private OnItemClickListener listener;

    public AdapterMainList(Context context, List<FinanceModel> list, OnItemClickListener listener){
        this.financeModelList = list;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MainListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.main_item, null, false);
        return new MainListViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(MainListViewHolder holder, int position) {
        FinanceModel model = financeModelList.get(position);
        holder.financeModelBinding.setVariable(BR.model,model);
        holder.financeModelBinding.notifyChange();
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return financeModelList.size();
    }

    @Override
    public void onClick(View view) {
        listener.OnItemClick(view);
    }

    public class MainListViewHolder extends RecyclerView.ViewHolder{

        ViewDataBinding financeModelBinding;

        public MainListViewHolder(View itemView) {
            super(itemView);
            financeModelBinding = DataBindingUtil.bind(itemView);
        }
    }

    public interface OnItemClickListener{
        void OnItemClick(View view);
    }

    public FinanceModel getItem(int position){
        return financeModelList.get(position);
    }
}
