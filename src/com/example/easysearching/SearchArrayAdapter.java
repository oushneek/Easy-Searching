package com.example.easysearching;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchArrayAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private final String[] values;

	static class ViewHolder {
		public TextView text;
		public ImageView image;
	}

	// initializes the view of listview of categories

	public SearchArrayAdapter(Activity context, String[] values) {
		super(context, R.layout.activity_listview, values);
		this.context = context;
		this.values = values;
	}

	// sets the rowview for list

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.activity_listview, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) rowView.findViewById(R.id.label);
			viewHolder.image = (ImageView) rowView.findViewById(R.id.icon);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		String s = values[position];
		holder.text.setText(s);

		if (s.equals("All Files")) {
			holder.image.setImageResource(R.drawable.allfile);
		} else if (s.equals("Images")) {
			holder.image.setImageResource(R.drawable.image);
		} else if (s.equals("Videos")) {
			holder.image.setImageResource(R.drawable.video);
		} else if (s.equals("Musics")) {
			holder.image.setImageResource(R.drawable.music);
		} else {
			holder.image.setImageResource(R.drawable.apps);
		}
		return rowView;
	}

}
