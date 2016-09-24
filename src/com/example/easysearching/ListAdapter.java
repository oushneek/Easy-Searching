package com.example.easysearching;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter {
	private List<Data> list;
	private int plength = 0, length = 0;
	private Filter filter;
	private final EasyDB db;
	private final String type;

	public ListAdapter(Context context, String type) {
		list = new ArrayList<Data>();
		db = EasyDB.getInstance(context);
		this.type = type;
	}

	@Override
	public int getCount() {
		if (list == null)
			return 0;
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	// creates view of the list to be shown in result

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.activity_listview, parent,
					false);
		}
		Data data = list.get(position);
		TextView nameView = (TextView) convertView.findViewById(R.id.label);
		nameView.setText(data.getName());
		return convertView;
	}

	// save data in the list

	public void setData(List<Data> data) {
		list = data;
	}

	// does the actual searching

	public class filter_here extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults Result = new FilterResults();
			// if constraint is empty return the original names
			length = constraint.length();
			if (length == 0 || length < plength) {
				if (type.isEmpty())
					list = db.readData();
				else
					list = db.search("", type);
				Result.values = list;
				Result.count = list.size();
				if (length == 0)
					return Result;
			}
			plength = length;
			ArrayList<Data> Filtered_Names = new ArrayList<Data>();
			String filterString = constraint.toString().toLowerCase();
			Data filterableString;

			for (int i = 0; i < list.size(); i++) {
				filterableString = list.get(i);
				if (filterableString.getName().toLowerCase()
						.contains(filterString)) {
					Filtered_Names.add(filterableString);
				}
			}
			Result.values = Filtered_Names;
			Result.count = Filtered_Names.size();

			return Result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			list = (ArrayList<Data>) results.values;
			notifyDataSetChanged();
		}

	}

	public Filter getFilter() {
		if (filter == null) {
			filter = new filter_here();
		}
		return filter;
	}

	public File getFile(int arg3) {
		return new File(list.get(arg3).getPath());
	}

}
