package com.sadpandapp;

import java.util.List;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.Constants;
import com.sadpandapp.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GalleryAdapter extends ArrayAdapter<Gallery>{

	
	public static final int THUMBNAIL_WIDTH = 100;
	
	private List<Gallery> objects;
	public GalleryAdapter(Context context, int textViewResourceId,
			List<Gallery> objects) {
		super(context, textViewResourceId, objects);
		this.objects = objects;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, null);
        }

        Gallery item = objects.get(position);
        if (item!= null) {
        	//Doujin name
            TextView tvName = (TextView) view.findViewById(R.id.tvDoujinName);
            if (tvName != null) {
                // do whatever you want with your string and long
            	tvName.setText(item.getName());
            }
            
            //Doujin tags
            TextView tvTags = (TextView) view.findViewById(R.id.tvDoujinTags);
            List<String> tags = item.getTags();
            if (tvTags != null && tags.size()>0) {
            	String tagsString = tags.get(0);
            	for(int i=1; i<tags.size();i++){
            		tagsString = tagsString.concat(", "+tags.get(i));
            	}
            	tvTags.setText(tagsString);
            }
            
            //Doujin thumb
            Log.i(this.getClass().getName(), "Grabbing Thumb: "+item.getThumb());
            AQuery aq = new AQuery(view);

            //create a bitmap ajax callback object
         	BitmapAjaxCallback cb = new BitmapAjaxCallback();
         	
         	//configure the callback
         	cb.url(item.getThumb()).animation(Constants.FADE_IN).targetWidth(getContext().getResources().getDimensionPixelSize(R.dimen.thumbnail_width));
         	
         	aq.id(R.id.ivDoujinThumb).image(cb);
            
            
         }

        return view;
    }
}
